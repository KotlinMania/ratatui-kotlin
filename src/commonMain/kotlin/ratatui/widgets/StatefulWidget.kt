// port-lint: source ratatui-core/src/widgets/stateful_widget.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect

/**
 * A `StatefulWidget` is a widget that can take advantage of some local state to remember things
 * between two draw calls.
 *
 * For a comprehensive guide to widgets, including trait explanations, implementation patterns,
 * and available widgets, see the `widgets` module documentation in the upstream Rust project.
 *
 * Most widgets can be drawn directly based on the input parameters. However, some features may
 * require some kind of associated state to be implemented.
 *
 * For example, the `List` widget can highlight the item currently selected. This can be translated
 * into an offset, which is the number of elements to skip in order to have the selected item within
 * the viewport currently allocated to this widget. The widget can therefore only provide the
 * following behavior: whenever the selected item is out of the viewport scroll to a predefined
 * position (making the selected item the last viewable item or the one in the middle for example).
 * Nonetheless, if the widget has access to the last computed offset then it can implement a
 * natural scrolling experience where the last offset is reused until the selected item is out of
 * the viewport.
 *
 * ## Examples
 *
 * ```kotlin
 * import ratatui.backend.TestBackend
 * import ratatui.widgets.List
 * import ratatui.widgets.ListItem
 * import ratatui.widgets.ListState
 * import ratatui.terminal.Terminal
 *
 * // Let's say we have some events to display.
 * class Events(
 *     // `items` is the state managed by your application.
 *     var items: List<String>,
 *     // `state` is the state that can be modified by the UI.
 *     var state: ListState = ListState.default(),
 * ) {
 *     fun setItems(items: List<String>) {
 *         this.items = items
 *         // We reset the state as the associated items have changed.
 *         this.state = ListState.default()
 *     }
 * }
 *
 * val backend = TestBackend.new(5u, 5u)
 * val terminal = Terminal.new(backend)
 *
 * val events = Events(listOf("Item 1", "Item 2"))
 * terminal.draw { f ->
 *     val items = events.items.map { ListItem.new(it) }
 *     val list = List.new(items)
 *     f.renderStatefulWidget(list, f.size(), events.state)
 * }
 * ```
 *
 * Transliteration of `ratatui_core::widgets::StatefulWidget`.
 */
interface StatefulWidget<State> {
    /**
     * Draws the current state of the widget in the given buffer.
     *
     * The widget may mutate [state] to retain information between draws (e.g. scroll offsets).
     */
    fun render(area: Rect, buf: Buffer, state: State)
}
