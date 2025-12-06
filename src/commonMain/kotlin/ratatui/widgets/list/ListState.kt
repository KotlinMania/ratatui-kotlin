package ratatui.widgets.list

/**
 * State of the [List] widget.
 *
 * This state can be used to scroll through items and select one. When the list is rendered as a
 * stateful widget, the selected item will be highlighted and the list will be shifted to ensure
 * that the selected item is visible. This will modify the [ListState] object passed to the
 * render method.
 *
 * The state consists of two fields:
 * - [offset]: the index of the first item to be displayed
 * - [selected]: the index of the selected item, which can be null if no item is selected
 *
 * # Example
 *
 * ```kotlin
 * val items = listOf("Item 1")
 * val list = List.new(items)
 *
 * // This should be stored outside of the function in your application state.
 * val state = ListState()
 *
 * state.offset = 1 // display the second item and onwards
 * state.select(3)  // select the fourth item (0-indexed)
 *
 * list.render(area, buffer, state)
 * ```
 */
data class ListState(
    /** Index of the first item to be displayed */
    var offset: Int = 0,
    /** Index of the selected item, or null if no item is selected */
    var selected: Int? = null
) {
    /**
     * Sets the index of the first item to be displayed.
     *
     * @param offset The new offset value
     * @return This state for chaining
     */
    fun withOffset(offset: Int): ListState {
        this.offset = offset
        return this
    }

    /**
     * Sets the index of the selected item.
     *
     * @param selected The index to select, or null for no selection
     * @return This state for chaining
     */
    fun withSelected(selected: Int?): ListState {
        this.selected = selected
        return this
    }

    /**
     * Sets the index of the selected item.
     *
     * Set to null if no item is selected. This will also reset the offset to 0.
     *
     * @param index The index to select, or null for no selection
     */
    fun select(index: Int?) {
        selected = index
        if (index == null) {
            offset = 0
        }
    }

    /**
     * Selects the next item or the first one if no item is selected.
     *
     * Note: until the list is rendered, the number of items is not known, so the index is set to
     * 0 and will be corrected when the list is rendered.
     */
    fun selectNext() {
        val next = selected?.let { it + 1 } ?: 0
        select(next)
    }

    /**
     * Selects the previous item or the last one if no item is selected.
     *
     * Note: until the list is rendered, the number of items is not known, so the index is set to
     * Int.MAX_VALUE and will be corrected when the list is rendered.
     */
    fun selectPrevious() {
        val previous = selected?.let { (it - 1).coerceAtLeast(0) } ?: Int.MAX_VALUE
        select(previous)
    }

    /**
     * Selects the first item.
     *
     * Note: until the list is rendered, the number of items is not known, so the index is set to
     * 0 and will be corrected when the list is rendered.
     */
    fun selectFirst() {
        select(0)
    }

    /**
     * Selects the last item.
     *
     * Note: until the list is rendered, the number of items is not known, so the index is set to
     * Int.MAX_VALUE and will be corrected when the list is rendered.
     */
    fun selectLast() {
        select(Int.MAX_VALUE)
    }

    /**
     * Scrolls down by a specified amount in the list.
     *
     * This method updates the selected index by moving it down by the given amount.
     * If the amount causes the index to go out of bounds (i.e., if the index is greater than
     * the length of the list), the last item in the list will be selected.
     *
     * @param amount The number of items to scroll down
     */
    fun scrollDownBy(amount: Int) {
        val current = selected ?: 0
        select(current + amount)
    }

    /**
     * Scrolls up by a specified amount in the list.
     *
     * This method updates the selected index by moving it up by the given amount.
     * If the amount causes the index to go out of bounds (i.e., less than zero),
     * the first item in the list will be selected.
     *
     * @param amount The number of items to scroll up
     */
    fun scrollUpBy(amount: Int) {
        val current = selected ?: 0
        select((current - amount).coerceAtLeast(0))
    }

    companion object {
        /** Creates a default ListState with no selection and offset at 0 */
        fun default(): ListState = ListState()
    }
}
