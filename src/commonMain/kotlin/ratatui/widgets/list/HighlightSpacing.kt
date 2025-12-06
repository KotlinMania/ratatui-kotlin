package ratatui.widgets.list

/**
 * Defines when to allocate spacing for the highlight symbol in lists and tables.
 *
 * This setting controls whether space is reserved for the selection indicator symbol,
 * which affects the layout and visual consistency of the widget.
 */
enum class HighlightSpacing {
    /**
     * Always allocate spacing for the highlight symbol.
     *
     * This ensures the widget maintains consistent width regardless of selection state,
     * preventing layout shifts when items are selected or deselected.
     */
    Always,

    /**
     * Only allocate spacing when an item is selected.
     *
     * This is the default behavior for backwards compatibility. The widget will shift
     * when an item is selected to make room for the highlight symbol.
     */
    WhenSelected,

    /**
     * Never allocate spacing for the highlight symbol.
     *
     * The highlight symbol will not be displayed, even when an item is selected.
     */
    Never;

    /**
     * Determines if spacing should be added based on the current selection state.
     *
     * @param hasSelection Whether any item is currently selected
     * @return True if spacing should be allocated
     */
    fun shouldAdd(hasSelection: Boolean): Boolean = when (this) {
        Always -> true
        WhenSelected -> hasSelection
        Never -> false
    }
}
