// port-lint: source ratatui/src/widgets/widget_ref.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style

/**
 * A WidgetRef is an interface that allows rendering a widget by reference.
 *
 * This interface is useful when you want to store a reference to a widget and render it later.
 * It also allows you to render boxed widgets.
 *
 * Boxed widgets allow you to store widgets with types that are not known at compile time. This is
 * useful when you want to store a collection of widgets with different types. You can then iterate
 * over the collection and render each widget.
 *
 * This interface was introduced in Ratatui 0.26.0 and is currently marked as unstable in the
 * original Rust implementation. We maintain the same semantics in this Kotlin port.
 *
 * For comprehensive information about widget implementation patterns, rendering, and usage,
 * see the [widgets] module documentation.
 *
 * # Examples
 *
 * ```kotlin
 * class Greeting : WidgetRef {
 *     override fun renderRef(area: Rect, buf: Buffer) {
 *         Line.raw("Hello").render(area, buf)
 *     }
 * }
 *
 * class Farewell : WidgetRef {
 *     override fun renderRef(area: Rect, buf: Buffer) {
 *         Line.raw("Goodbye").rightAligned().render(area, buf)
 *     }
 * }
 *
 * fun render(area: Rect, buf: Buffer) {
 *     val greeting = Greeting()
 *     val farewell = Farewell()
 *
 *     // these calls do not consume the widgets, so they can be used again later
 *     greeting.renderRef(area, buf)
 *     farewell.renderRef(area, buf)
 *
 *     // a collection of widgets with different types
 *     val widgets: List<WidgetRef> = listOf(greeting, farewell)
 *     for (widget in widgets) {
 *         widget.renderRef(area, buf)
 *     }
 * }
 * ```
 */
interface WidgetRef {
    /**
     * Draws the current state of the widget in the given buffer. That is the only method required
     * to implement a custom widget.
     *
     * @param area The rectangular area to render the widget into
     * @param buf The buffer to render the widget to
     */
    fun renderRef(area: Rect, buf: Buffer)
}

/**
 * Extension to allow rendering a Widget as a WidgetRef.
 *
 * This provides backwards compatibility and allows Widget implementations to be used
 * where WidgetRef is expected.
 */
fun Widget.asWidgetRef(): WidgetRef = object : WidgetRef {
    override fun renderRef(area: Rect, buf: Buffer) {
        this@asWidgetRef.render(area, buf)
    }
}

/**
 * Provides the ability to render a String by reference.
 *
 * This extension allows for a String to be rendered onto the [Buffer], using the default
 * style settings. It ensures that a String can be rendered efficiently by reference,
 * without the need to create a separate widget instance.
 */
fun String.renderAsWidget(area: Rect, buf: Buffer) {
    buf.setStringn(area.x, area.y, this, area.width.toInt(), Style.default())
}

/**
 * WidgetRef extension for String that allows using a String as a WidgetRef.
 */
fun String.asWidgetRef(): WidgetRef = object : WidgetRef {
    override fun renderRef(area: Rect, buf: Buffer) {
        this@asWidgetRef.renderAsWidget(area, buf)
    }
}

/**
 * A WidgetRef implementation for nullable widgets.
 *
 * This is a convenience implementation that makes it easy to attach optional child widgets
 * to parent widgets. It allows you to render an optional widget by reference.
 *
 * The internal widgets use this pattern to render the optional Block widgets that are included
 * on most widgets.
 *
 * # Examples
 *
 * ```kotlin
 * class Parent(private val child: Child?) : WidgetRef {
 *     override fun renderRef(area: Rect, buf: Buffer) {
 *         child?.renderRef(area, buf)
 *     }
 * }
 *
 * class Child : WidgetRef {
 *     override fun renderRef(area: Rect, buf: Buffer) {
 *         Line.raw("Hello from child").render(area, buf)
 *     }
 * }
 * ```
 */
fun <W : WidgetRef> W?.renderRef(area: Rect, buf: Buffer) {
    this?.renderRef(area, buf)
}
