// port-lint: source ratatui-core/src/widgets/widget.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style

/**
 * A `Widget` is a type that can be drawn on a [Buffer] in a given [Rect].
 *
 * Transliteration of `ratatui_core::widgets::Widget`.
 */
interface Widget {
    /**
     * Draws the current state of the widget in the given buffer.
     */
    fun render(area: Rect, buf: Buffer)
}

/**
 * Renders a string as a widget.
 *
 * Mirrors Rust `impl Widget for &str` / `impl Widget for String`.
 */
fun String.render(area: Rect, buf: Buffer) {
    buf.setStringn(area.x, area.y, this, area.width, Style.new())
}

/**
 * Renders an optional widget.
 *
 * Mirrors Rust `impl<W: Widget> Widget for Option<W>`.
 */
fun <W : Widget> W?.render(area: Rect, buf: Buffer) {
    if (this != null) {
        this.render(area, buf)
    }
}
