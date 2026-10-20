// port-lint: source ratatui-core/src/widgets/widget.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style

/**
 * A `Widget` is a type that can be drawn on a [Buffer] in a given [Rect].
 *
 * For a comprehensive guide to widgets, including interface explanations, implementation patterns,
 * and available widgets, see the [ratatui.widgets] package documentation.
 *
 * Prior to Ratatui 0.26.0, widgets generally were created for each frame as they were consumed
 * during rendering. This meant that they were not meant to be stored but used as *commands* to
 * draw common figures in the UI.
 *
 * Starting with Ratatui 0.26.0, all the internal widgets implement `Widget` so that an existing
 * widget instance can be rendered repeatedly. This allows you to keep a widget in a field and
 * render it later. Widget libraries should consider also doing this to allow for more flexibility
 * in how widgets are used.
 *
 * In Ratatui 0.26.0, we also added an unstable [WidgetRef] interface and implemented this on all
 * the internal widgets. In addition to the above benefit of rendering an existing widget, this
 * also allows you to render heterogeneous widgets through a common interface. This is useful when
 * you want to store a collection of widgets with different types. You can then iterate over the
 * collection and render each widget.
 *
 * In general where you expect a widget to work on its data without modifying it, implement
 * `Widget` on a class whose [render] does not mutate the receiver. If you need to store state
 * between draw calls, implement [StatefulWidget] if you want the widget itself to be immutable,
 * or implement `Widget` on a class whose `render` mutates the receiver if you want the widget to
 * be mutable. The mutable widget pattern is used infrequently in apps, but can be quite useful.
 *
 * A blanket extension that lets a [WidgetRef] be rendered as a `Widget` is provided. `Widget` is
 * also available for [String] receivers as a top-level extension function (see [render]).
 *
 * # Examples
 *
 * ```kotlin
 * val backend = TestBackend.new(5, 5)
 * val terminal = Terminal.new(backend)
 *
 * terminal.draw { frame ->
 *     frame.renderWidget(Clear, frame.area())
 * }
 * ```
 *
 * It is common to render widgets inside other widgets:
 *
 * ```kotlin
 * class MyWidget : Widget {
 *     override fun render(area: Rect, buf: Buffer) {
 *         Line.raw("Hello").render(area, buf)
 *     }
 * }
 * ```
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
 * The string is drawn onto [buf] within [area]. Long strings are truncated to fit the area
 * width, so callers can pass arbitrary lengths without trimming up front.
 */
fun String.render(area: Rect, buf: Buffer) {
    buf.setStringn(area.x, area.y, this, area.width, Style.new())
}

/**
 * Renders an optional widget.
 *
 * When the receiver is non-null its [Widget.render] is invoked; otherwise this is a no-op.
 */
fun Widget?.render(area: Rect, buf: Buffer) {
    if (this != null) {
        this.render(area, buf)
    }
}
