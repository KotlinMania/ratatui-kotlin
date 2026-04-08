// port-lint: source ratatui-core/src/backend.rs
package ratatui.backend

import ratatui.buffer.BufferDiff
import ratatui.layout.Position
import ratatui.layout.Size

/**
 * Defines which region of the terminal's visible display area is cleared.
 *
 * Transliteration of `ratatui_core::backend::ClearType`.
 */
enum class ClearType {
    /** Clears all character cells in the visible display area. */
    All,

    /** Clears from the cursor position (inclusive) through the end of the display area. */
    AfterCursor,

    /** Clears from the start of the display area through the cursor position (inclusive). */
    BeforeCursor,

    /** Clears all character cells in the cursor's current line. */
    CurrentLine,

    /** Clears from the cursor position (inclusive) to the end of the current line. */
    UntilNewLine;

    companion object {
        fun fromString(value: String): ClearType? {
            return when (value) {
                "All" -> All
                "AfterCursor" -> AfterCursor
                "BeforeCursor" -> BeforeCursor
                "CurrentLine" -> CurrentLine
                "UntilNewLine" -> UntilNewLine
                else -> null
            }
        }
    }
}

/**
 * The window size in characters (columns / rows) as well as pixels.
 *
 * Transliteration of `ratatui_core::backend::WindowSize`.
 */
data class WindowSize(
    /** Size of the window in characters (columns / rows). */
    val columnsRows: Size,
    /** Size of the window in pixels. */
    val pixels: Size
)

/**
 * The `Backend` interface provides an abstraction over different terminal libraries.
 *
 * Transliteration of `ratatui_core::backend::Backend`.
 */
interface Backend {
    /**
     * Draw the given content to the terminal screen.
     *
     * The content is provided as an iterator over `(x, y, cell)` tuples.
     */
    fun draw(content: Iterator<BufferDiff.Item>)

    /**
     * Insert `n` line breaks to the terminal screen.
     *
     * This method is optional and may not be implemented by all backends.
     */
    fun appendLines(n: UShort) {
        // default: no-op
        if (n == 0.toUShort()) return
    }

    /** Hide the cursor on the terminal screen. */
    fun hideCursor()

    /** Show the cursor on the terminal screen. */
    fun showCursor()

    /** Get the current cursor position on the terminal screen. */
    fun getCursorPosition(): Position

    /** Set the cursor position on the terminal screen. */
    fun setCursorPosition(position: Position)

    /** Deprecated tuple-based cursor getter to mirror Rust. */
    @Deprecated("use getCursorPosition() instead")
    fun getCursor(): Pair<UShort, UShort> {
        val pos = getCursorPosition()
        return Pair(pos.x.toUShort(), pos.y.toUShort())
    }

    /** Deprecated tuple-based cursor setter to mirror Rust. */
    @Deprecated("use setCursorPosition(Position(x, y)) instead")
    fun setCursor(x: UShort, y: UShort) {
        setCursorPosition(Position(x.toInt(), y.toInt()))
    }

    /**
     * Clears all character cells in the terminal's visible display area.
     *
     * This operation preserves the cursor position.
     *
     * This is equivalent to calling [clearRegion] with [ClearType.All].
     */
    fun clear() {
        clearRegion(ClearType.All)
    }

    /** Clear a region of the terminal's visible display area. */
    fun clearRegion(clearType: ClearType)

    /** Get the terminal size. */
    fun size(): Size

    /**
     * Get the size of the terminal screen in columns/rows and pixels.
     *
     * Pixel dimensions may be unavailable on some backends; the default implementation reports
     * `0,0` pixels (matching the Rust documentation note).
     */
    fun windowSize(): WindowSize {
        val columnsRows = size()
        return WindowSize(columnsRows = columnsRows, pixels = Size(0, 0))
    }

    /** Flush any buffered output. */
    fun flush()
}
