package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect

/**
 * A widget to clear/reset a certain area to allow overdrawing (e.g. for popups).
 *
 * This widget **cannot be used to clear the terminal on the first render** as ratatui assumes
 * the render area is empty. Use `Terminal.clear` instead.
 *
 * # Examples
 *
 * ```kotlin
 * fun drawOnClear(frame: Frame, area: Rect) {
 *     val block = Block.bordered().title("Block")
 *     Clear.render(area, buffer) // <- this will clear/reset the area first
 *     block.render(area, buffer) // now render the block widget
 * }
 * ```
 *
 * # Popup Example
 *
 * For a more complete example of how to utilize `Clear` to realize popups, see
 * the popup example.
 */
object Clear : Widget {
    override fun render(area: Rect, buf: Buffer) {
        for (x in area.left() until area.right()) {
            for (y in area.top() until area.bottom()) {
                buf[x, y].reset()
            }
        }
    }
}
