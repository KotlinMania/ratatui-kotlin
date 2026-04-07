// port-lint: source ratatui-core/src/widgets/stateful_widget.rs
package ratatui.widgets

import ratatui.buffer.Buffer
import ratatui.layout.Rect

/**
 * A `StatefulWidget` is a widget that can take advantage of some local state to remember things
 * between two draw calls.
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

