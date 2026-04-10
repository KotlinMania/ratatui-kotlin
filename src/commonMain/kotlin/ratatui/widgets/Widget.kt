// port-lint: source ratatui-core/src/widgets/widget.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style

/**
 * A `Widget` is a type that can be drawn on a [Buffer] in a given [Rect].
 *
 * For a comprehensive guide to widgets, including trait explanations, implementation patterns,
 * and available widgets, see the `widgets` module documentation.
 *
 * Prior to Ratatui 0.26.0, widgets generally were created for each frame as they were consumed
 * during rendering. This meant that they were not meant to be stored but used as *commands* to
 * draw common figures in the UI.
 *
 * Starting with Ratatui 0.26.0, all the internal widgets implement Widget for a reference to
 * themselves. This allows you to store a reference to a widget and render it later. Widget crates
 * should consider also doing this to allow for more flexibility in how widgets are used.
 *
 * In Ratatui 0.26.0, we also added an unstable `WidgetRef` trait and implemented this on all the
 * internal widgets. In addition to the above benefit of rendering references to widgets, this also
 * allows you to render boxed widgets. This is useful when you want to store a collection of
 * widgets with different types. You can then iterate over the collection and render each widget.
 * See <https://github.com/ratatui/ratatui/issues/1287> for more information.
 *
 * In general where you expect a widget to immutably work on its data, we recommended to implement
 * `Widget` for a reference to the widget (`impl Widget for &MyWidget`). If you need to store state
 * between draw calls, implement `StatefulWidget` if you want the Widget to be immutable, or
 * implement `Widget` for a mutable reference to the widget (`impl Widget for &mut MyWidget`) if
 * you want the widget to be mutable. The mutable widget pattern is used infrequently in apps, but
 * can be quite useful.
 *
 * A blanket implementation of `Widget` for `&W` where `W` implements `WidgetRef` is provided.
 * Widget is also implemented for `&str` and `String` types.
 *
 * # Examples
 *
 * ```kotlin
 * import ratatui.backend.TestBackend
 * import ratatui.terminal.Terminal
 * import ratatui.widgets.Clear
 *
 * val backend = TestBackend.new(5, 5)
 * val terminal = Terminal(backend)
 *
 * terminal.draw { frame ->
 *     frame.renderWidget(Clear, frame.area())
 * }
 * ```
 *
 * It's common to render widgets inside other widgets:
 *
 * ```kotlin
 * import ratatui.buffer.Buffer
 * import ratatui.layout.Rect
 * import ratatui.text.Line
 * import ratatui.widgets.Widget
 *
 * class MyWidget : Widget {
 *     override fun render(area: Rect, buf: Buffer) {
 *         Line.from("Hello").render(area, buf)
 *     }
 * }
 * ```
 *
 * Transliteration of `ratatui_core::widgets::Widget`.
 */
interface Widget {
    /**
     * Draws the current state of the widget in the given buffer. That is the only method required
     * to implement a custom widget.
     */
    fun render(area: Rect, buf: Buffer)
}

/**
 * Renders a string as a widget.
 *
 * This implementation allows a string to act as a widget, meaning it can be drawn onto a [Buffer]
 * in a specified [Rect]. This avoids the need for ownership transfer (as in Rust) and provides
 * a convenient way to draw text directly to the screen.
 *
 * Mirrors Rust `impl Widget for &str` and `impl Widget for String`.
 */
fun String.render(area: Rect, buf: Buffer) {
    buf.setStringn(area.x, area.y, this, area.width, Style.new())
}

/**
 * Renders an optional string as a widget.
 *
 * Mirrors Rust `impl<W: Widget> Widget for Option<W>` applied to `Option<&str>` / `Option<String>`.
 */
fun String?.render(area: Rect, buf: Buffer) {
    if (this != null) {
        this.render(area, buf)
    }
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
