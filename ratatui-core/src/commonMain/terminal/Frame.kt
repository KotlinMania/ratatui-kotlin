package ratatui.terminal

import ratatui.buffer.Buffer
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.widgets.StatefulWidget
import ratatui.widgets.Widget

/**
 * A consistent view into the terminal state for rendering a single frame.
 *
 * This is obtained via the closure argument of [Terminal.draw]. It is used to render widgets
 * to the terminal and control the cursor position.
 *
 * The changes drawn to the frame are applied only to the current [Buffer]. After the closure
 * returns, the current buffer is compared to the previous buffer and only the changes are applied
 * to the terminal. This avoids drawing redundant cells.
 */
class Frame internal constructor(
    /** The area of the viewport */
    private val viewportArea: Rect,
    /** The buffer that is used to draw the current frame */
    val buffer: Buffer,
    /** The frame count indicating the sequence number of this frame */
    private val frameCount: Int
) {
    /**
     * Where should the cursor be after drawing this frame?
     *
     * If null, the cursor is hidden and its position is controlled by the backend.
     * If set, the cursor is shown and placed at the specified position after the call
     * to [Terminal.draw].
     */
    internal var cursorPosition: Position? = null

    /**
     * The area of the current frame.
     *
     * This is guaranteed not to change during rendering, so may be called multiple times.
     *
     * If your app listens for a resize event from the backend, it should ignore the values from
     * the event for any calculations that are used to render the current frame and use this value
     * instead as this is the area of the buffer that is used to render the current frame.
     */
    fun area(): Rect = viewportArea

    /**
     * Render a [Widget] to the current buffer using [Widget.render].
     *
     * Usually the area argument is the size of the current frame or a sub-area of the current
     * frame (which can be obtained using Layout to split the total area).
     *
     * # Example
     *
     * ```kotlin
     * val block = Block.new()
     * val area = Rect.new(0, 0, 5, 5)
     * frame.renderWidget(block, area)
     * ```
     */
    fun renderWidget(widget: Widget, area: Rect) {
        widget.render(area, buffer)
    }

    /**
     * Render a [StatefulWidget] to the current buffer using [StatefulWidget.render].
     *
     * Usually the area argument is the size of the current frame or a sub-area of the current
     * frame (which can be obtained using Layout to split the total area).
     *
     * The state argument should be an instance of the state associated to the given [StatefulWidget].
     *
     * # Example
     *
     * ```kotlin
     * val state = ListState.default().withSelected(1)
     * val list = List.new(listOf(ListItem.new("Item 1"), ListItem.new("Item 2")))
     * val area = Rect.new(0, 0, 5, 5)
     * frame.renderStatefulWidget(list, area, state)
     * ```
     */
    fun <S> renderStatefulWidget(widget: StatefulWidget<S>, area: Rect, state: S) {
        widget.render(area, buffer, state)
    }

    /**
     * After drawing this frame, make the cursor visible and put it at the specified position.
     * If this method is not called, the cursor will be hidden.
     *
     * Note that this will interfere with calls to [Terminal.hideCursor],
     * [Terminal.showCursor], and [Terminal.setCursorPosition]. Pick one of the APIs and
     * stick with it.
     */
    fun setCursorPosition(position: Position) {
        cursorPosition = position
    }

    /**
     * After drawing this frame, make the cursor visible and put it at the specified (x, y)
     * coordinates. If this method is not called, the cursor will be hidden.
     */
    fun setCursorPosition(x: Int, y: Int) {
        setCursorPosition(Position(x, y))
    }

    /**
     * Returns the current frame count.
     *
     * This method provides access to the frame count, which is a sequence number indicating
     * how many frames have been rendered up to (but not including) this one. It can be used
     * for purposes such as animation, performance tracking, or debugging.
     *
     * Each time a frame has been rendered, this count is incremented,
     * providing a consistent way to reference the order and number of frames processed by the
     * terminal. When count reaches its maximum value (Int.MAX_VALUE), it wraps around to zero.
     *
     * This count is particularly useful when dealing with dynamic content or animations where the
     * state of the display changes over time. By tracking the frame count, developers can
     * synchronize updates or changes to the content with the rendering process.
     */
    fun count(): Int = frameCount
}

/**
 * Represents the state of the terminal after all changes performed in the last
 * [Terminal.draw] call have been applied. Therefore, it is only valid until the next call to
 * [Terminal.draw].
 */
data class CompletedFrame(
    /** The buffer that was used to draw the last frame */
    val buffer: Buffer,
    /** The size of the last frame */
    val area: Rect,
    /** The frame count indicating the sequence number of this frame */
    val count: Int
)
