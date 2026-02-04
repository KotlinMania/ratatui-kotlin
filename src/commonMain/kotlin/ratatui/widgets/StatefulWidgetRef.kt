// port-lint: source ratatui/src/widgets/stateful_widget_ref.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect

/**
 * A StatefulWidgetRef is an interface that allows rendering a stateful widget by reference.
 *
 * This is the stateful equivalent of [WidgetRef]. It is useful when you need to store a reference
 * to a stateful widget and render it later. It also allows you to render boxed stateful widgets.
 *
 * This interface was introduced in Ratatui 0.26.0. It is currently marked as unstable in the
 * original Rust implementation as the API is still being evaluated. We maintain the same semantics
 * in this Kotlin port.
 *
 * See the documentation for [WidgetRef] for more information on widget references. See the
 * documentation for [StatefulWidget] for more information on stateful widgets.
 *
 * For comprehensive information about widget implementation patterns, rendering, and usage,
 * see the [widgets] module documentation.
 *
 * # Examples
 *
 * ```kotlin
 * class PersonalGreeting : StatefulWidgetRef<String> {
 *     override fun renderRef(area: Rect, buf: Buffer, state: String) {
 *         Line.raw("Hello $state").render(area, buf)
 *     }
 * }
 *
 * // Also implement StatefulWidget for backwards compatibility if needed
 * class PersonalGreetingWidget : StatefulWidget<String> {
 *     override fun render(area: Rect, buf: Buffer, state: String) {
 *         PersonalGreeting().renderRef(area, buf, state)
 *     }
 * }
 *
 * fun render(area: Rect, buf: Buffer) {
 *     val widget = PersonalGreeting()
 *     var state = "world"
 *     widget.renderRef(area, buf, state)
 * }
 * ```
 */
interface StatefulWidgetRef<State> {
    /**
     * Draws the current state of the widget in the given buffer. That is the only method required
     * to implement a custom stateful widget.
     *
     * @param area The rectangular area to render the widget into
     * @param buf The buffer to render the widget to
     * @param state The mutable state of the widget
     */
    fun renderRef(area: Rect, buf: Buffer, state: State)
}

/**
 * Extension to allow rendering a StatefulWidget as a StatefulWidgetRef.
 *
 * This provides backwards compatibility and allows StatefulWidget implementations to be used
 * where StatefulWidgetRef is expected.
 */
fun <State> StatefulWidget<State>.asStatefulWidgetRef(): StatefulWidgetRef<State> =
    object : StatefulWidgetRef<State> {
        override fun renderRef(area: Rect, buf: Buffer, state: State) {
            this@asStatefulWidgetRef.render(area, buf, state)
        }
    }
