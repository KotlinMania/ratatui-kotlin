// port-lint: source ratatui-core/src/widgets/stateful_widget.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect

/**
 * A `StatefulWidget` is a widget that can take advantage of some local state to remember things
 * between two draw calls.
 *
 * For a comprehensive guide to widgets, including trait explanations, implementation patterns,
 * and available widgets, see the `widgets` module documentation.
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
 * import ratatui.terminal.Terminal
 * import ratatui.widgets.list.List as RatatuiList
 * import ratatui.widgets.list.ListItem
 * import ratatui.widgets.list.ListState
 *
 * // Let's say we have some events to display.
 * class Events(
 *     // `items` is the state managed by your application.
 *     var items: kotlin.collections.List<String>,
 *     // `state` is the state that can be modified by the UI. It stores the index of the selected
 *     // item as well as the offset computed during the previous draw call (used to implement
 *     // natural scrolling).
 *     var state: ListState = ListState.default(),
 * ) {
 *     fun setItems(items: List<String>) {
 *         this.items = items
 *         // We reset the state as the associated items have changed. This effectively resets
 *         // the selection as well as the stored offset.
 *         this.state = ListState.default()
 *     }
 *
 *     // Select the next item. This will not be reflected until the widget is drawn in the
 *     // `Terminal.draw` callback using `Frame.renderStatefulWidget`.
 *     fun next() {
 *         val i = when (val selected = state.selected) {
 *             null -> 0
 *             else -> if (selected >= items.size - 1) 0 else selected + 1
 *         }
 *         state.select(i)
 *     }
 *
 *     // Select the previous item. This will not be reflected until the widget is drawn in the
 *     // `Terminal.draw` callback using `Frame.renderStatefulWidget`.
 *     fun previous() {
 *         val i = when (val selected = state.selected) {
 *             null -> 0
 *             else -> if (selected == 0) items.size - 1 else selected - 1
 *         }
 *         state.select(i)
 *     }
 *
 *     // Unselect the currently selected item if any. The implementation of ListState makes
 *     // sure that the stored offset is also reset.
 *     fun unselect() {
 *         state.select(null)
 *     }
 * }
 *
 * val backend = TestBackend.new(5, 5)
 * val terminal = Terminal.new(backend)
 *
 * val events = Events(listOf("Item 1", "Item 2"))
 * while (true) {
 *     terminal.draw { f ->
 *         // The items managed by the application are transformed to something that is understood
 *         // by ratatui.
 *         val items = events.items.map { ListItem.new(it) }
 *         // The `List` widget is then built with those items.
 *         val list = RatatuiList.new(items)
 *         // Finally the widget is rendered using the associated state. `events.state` is
 *         // effectively the only thing that we will "remember" from this draw call.
 *         f.renderStatefulWidget(list, f.size(), events.state)
 *     }
 *
 *     // In response to some input events or an external http request or whatever:
 *     events.next()
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
