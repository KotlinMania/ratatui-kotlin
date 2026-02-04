package ratatui.terminal

import ratatui.buffer.Buffer
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.layout.Size
import ratatui.style.Color
import ratatui.style.Modifier

/**
 * An interface to interact and draw [Frame]s on the user's terminal.
 *
 * This is the main entry point for Ratatui. It is responsible for drawing and maintaining the
 * state of the buffers, cursor and viewport.
 *
 * The [Terminal] class requires a [Backend] implementation which is used to interface with
 * the underlying terminal library.
 *
 * The Terminal maintains two buffers: the current and the previous.
 * When the widgets are drawn, the changes are accumulated in the current buffer.
 * At the end of each draw pass, the two buffers are compared, and only the changes
 * between these buffers are written to the terminal, avoiding any redundant operations.
 * After flushing these changes, the buffers are swapped to prepare for the next draw cycle.
 *
 * The terminal also has a viewport which is the area of the terminal that is currently visible to
 * the user. It can be either fullscreen, inline or fixed. See [Viewport] for more information.
 *
 * # Example
 *
 * ```kotlin
 * val backend = TestBackend(80, 24)
 * val terminal = Terminal(backend)
 * terminal.draw { frame ->
 *     val area = frame.area()
 *     frame.renderWidget(Paragraph.new("Hello World!"), area)
 * }
 * ```
 */
class Terminal<B : Backend>(
    /** The backend used to interface with the terminal */
    private val backend: B,
    /** Terminal options including viewport configuration */
    options: TerminalOptions = TerminalOptions()
) {
    /** Holds the results of the current and previous draw calls */
    private val buffers: Array<Buffer>

    /** Index of the current buffer */
    private var current: Int = 0

    /** Whether the cursor is currently hidden */
    private var hiddenCursor: Boolean = false

    /** Viewport configuration */
    private val viewport: Viewport = options.viewport

    /** Area of the viewport */
    private var viewportArea: Rect

    /** Last known area of the terminal */
    private var lastKnownArea: Rect

    /** Last known position of the cursor */
    private var lastKnownCursorPos: Position

    /** Number of frames rendered */
    private var frameCount: Int = 0

    init {
        val area = when (viewport) {
            is Viewport.Fullscreen, is Viewport.Inline -> Rect.from(backend.size())
            is Viewport.Fixed -> viewport.area
        }

        viewportArea = when (viewport) {
            is Viewport.Fullscreen -> area
            is Viewport.Inline -> computeInlineSize(viewport.height, area.asSize())
            is Viewport.Fixed -> viewport.area
        }

        lastKnownCursorPos = when (viewport) {
            is Viewport.Fullscreen -> Position.ORIGIN
            is Viewport.Inline -> Position(0, viewportArea.y)
            is Viewport.Fixed -> viewportArea.asPosition()
        }

        lastKnownArea = area
        buffers = arrayOf(Buffer.empty(viewportArea), Buffer.empty(viewportArea))
    }

    /**
     * Get a Frame object which provides a consistent view into the terminal state for rendering.
     *
     * Note: This exists to support more advanced use cases. Most cases should use [draw].
     */
    fun getFrame(): Frame {
        return Frame(
            viewportArea = viewportArea,
            buffer = buffers[current],
            frameCount = frameCount
        )
    }

    /**
     * Gets the current buffer as a mutable reference.
     */
    fun currentBuffer(): Buffer = buffers[current]

    /**
     * Gets the backend.
     */
    fun backend(): B = backend

    /**
     * Obtains a difference between the previous and the current buffer and passes it to the
     * current backend for drawing.
     */
    fun flush() {
        val previousBuffer = buffers[1 - current]
        val currentBuffer = buffers[current]
        val updates = previousBuffer.diff(currentBuffer)
        if (updates.isNotEmpty()) {
            val last = updates.last()
            lastKnownCursorPos = Position(last.x, last.y)
        }
        backend.draw(updates)
    }

    /**
     * Updates the Terminal so that internal buffers match the requested area.
     *
     * Requested area will be saved to remain consistent when rendering. This leads to a full clear
     * of the screen.
     */
    fun resize(area: Rect) {
        val nextArea = when (viewport) {
            is Viewport.Inline -> {
                val offsetInPreviousViewport = (lastKnownCursorPos.y - viewportArea.top())
                    .coerceAtLeast(0)
                computeInlineSize(viewport.height, area.asSize(), offsetInPreviousViewport)
            }
            is Viewport.Fixed, is Viewport.Fullscreen -> area
        }
        setViewportArea(nextArea)
        clear()
        lastKnownArea = area
    }

    private fun setViewportArea(area: Rect) {
        buffers[current].resize(area)
        buffers[1 - current].resize(area)
        viewportArea = area
    }

    /**
     * Queries the backend for size and resizes if it doesn't match the previous size.
     */
    fun autoresize() {
        if (viewport is Viewport.Fullscreen || viewport is Viewport.Inline) {
            val area = Rect.from(size())
            if (area != lastKnownArea) {
                resize(area)
            }
        }
    }

    /**
     * Draws a single frame to the terminal.
     *
     * Returns a [CompletedFrame] if successful.
     *
     * This method will:
     * - autoresize the terminal if necessary
     * - call the render callback, passing it a [Frame] reference to render to
     * - flush the current internal state by copying the current buffer to the backend
     * - move the cursor to the last known position if it was set during the rendering closure
     * - return a [CompletedFrame] with the current buffer and the area of the terminal
     *
     * # Example
     *
     * ```kotlin
     * terminal.draw { frame ->
     *     val area = frame.area()
     *     frame.renderWidget(Paragraph.new("Hello World!"), area)
     *     frame.setCursorPosition(0, 0)
     * }
     * ```
     */
    fun draw(renderCallback: (Frame) -> Unit): CompletedFrame {
        // Autoresize - otherwise we get glitches if shrinking
        autoresize()

        val frame = getFrame()
        renderCallback(frame)

        // Get cursor position before dropping frame
        val cursorPosition = frame.cursorPosition

        // Draw to stdout
        flush()

        // Handle cursor visibility
        if (cursorPosition == null) {
            hideCursor()
        } else {
            showCursor()
            setCursorPosition(cursorPosition)
        }

        swapBuffers()

        // Flush backend
        backend.flush()

        val completedFrame = CompletedFrame(
            buffer = buffers[1 - current],
            area = lastKnownArea,
            count = frameCount
        )

        // increment frame count before returning from draw
        frameCount = (frameCount + 1).let { if (it < 0) 0 else it }

        return completedFrame
    }

    /**
     * Hides the cursor.
     */
    fun hideCursor() {
        backend.hideCursor()
        hiddenCursor = true
    }

    /**
     * Shows the cursor.
     */
    fun showCursor() {
        backend.showCursor()
        hiddenCursor = false
    }

    /**
     * Gets the current cursor position.
     */
    fun getCursorPosition(): Position = backend.getCursorPosition()

    /**
     * Sets the cursor position.
     */
    fun setCursorPosition(position: Position) {
        backend.setCursorPosition(position)
        lastKnownCursorPos = position
    }

    /**
     * Sets the cursor position.
     */
    fun setCursorPosition(x: Int, y: Int) {
        setCursorPosition(Position(x, y))
    }

    /**
     * Clear the terminal and force a full redraw on the next draw call.
     */
    fun clear() {
        when (viewport) {
            is Viewport.Fullscreen -> backend.clearRegion(ClearType.All)
            is Viewport.Inline -> {
                backend.setCursorPosition(viewportArea.asPosition())
                backend.clearRegion(ClearType.AfterCursor)
            }
            is Viewport.Fixed -> {
                for (y in viewportArea.top() until viewportArea.bottom()) {
                    backend.setCursorPosition(Position(0, y))
                    backend.clearRegion(ClearType.AfterCursor)
                }
            }
        }
        // Reset the back buffer to make sure the next update will redraw everything
        buffers[1 - current].reset()
    }

    /**
     * Clears the inactive buffer and swaps it with the current buffer.
     */
    fun swapBuffers() {
        buffers[1 - current].reset()
        current = 1 - current
    }

    /**
     * Queries the real size of the backend.
     */
    fun size(): Size = backend.size()

    private fun computeInlineSize(height: UShort, size: Size, offset: Int = 0): Rect {
        val h = minOf(height.toInt(), size.height)
        val y = (size.height - h - offset).coerceAtLeast(0)
        return Rect(x = 0, y = y, width = size.width, height = h)
    }

    companion object {
        /**
         * Creates a new [Terminal] with the given [Backend] with a full screen viewport.
         */
        fun <B : Backend> new(backend: B): Terminal<B> {
            return Terminal(backend, TerminalOptions(viewport = Viewport.Fullscreen))
        }

        /**
         * Creates a new [Terminal] with the given [Backend] and [TerminalOptions].
         */
        fun <B : Backend> withOptions(backend: B, options: TerminalOptions): Terminal<B> {
            return Terminal(backend, options)
        }
    }
}

/**
 * Options to pass to [Terminal].
 */
data class TerminalOptions(
    /** Viewport used to draw to the terminal */
    val viewport: Viewport = Viewport.Fullscreen
)

/**
 * Type of region to clear.
 */
enum class ClearType {
    /** Clear the entire screen */
    All,
    /** Clear from cursor to end of screen */
    AfterCursor,
    /** Clear from start of screen to cursor */
    BeforeCursor,
    /** Clear the current line */
    CurrentLine,
    /** Clear from cursor to end of line */
    UntilNewLine
}

/**
 * A cell update to be drawn to the terminal.
 */
data class CellUpdate(
    val x: Int,
    val y: Int,
    val symbol: String,
    val fg: Color,
    val bg: Color,
    val modifiers: Modifier
)

/**
 * Backend interface for terminal operations.
 *
 * Implementations of this interface are responsible for the actual terminal I/O operations.
 */
interface Backend {
    /**
     * Draw the given updates to the terminal.
     */
    fun draw(updates: List<CellUpdate>)

    /**
     * Hide the cursor.
     */
    fun hideCursor()

    /**
     * Show the cursor.
     */
    fun showCursor()

    /**
     * Get the current cursor position.
     */
    fun getCursorPosition(): Position

    /**
     * Set the cursor position.
     */
    fun setCursorPosition(position: Position)

    /**
     * Clear a region of the terminal.
     */
    fun clearRegion(clearType: ClearType)

    /**
     * Get the terminal size.
     */
    fun size(): Size

    /**
     * Flush any buffered output.
     */
    fun flush()
}
