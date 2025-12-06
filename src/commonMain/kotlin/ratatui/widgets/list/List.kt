package ratatui.widgets.list

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Line
import ratatui.widgets.StatefulWidget
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome

/**
 * Defines the direction in which the list will be rendered.
 *
 * If there are too few items to fill the screen, the list will stick to the starting edge.
 */
enum class ListDirection {
    /** The first value is on the top, going to the bottom */
    TopToBottom,
    /** The first value is on the bottom, going to the top */
    BottomToTop
}

/**
 * A widget to display several items among which one can be selected (optional).
 *
 * A list is a collection of [ListItem]s.
 *
 * This is different from a Table because it does not handle columns, headers or footers and
 * the item's height is automatically determined. A List can also be put in reverse order (i.e.
 * *bottom to top*) whereas a Table cannot.
 *
 * List items can be aligned using [ratatui.text.Text.alignment], for more details see [ListItem].
 *
 * [List] is also a [StatefulWidget], which means you can use it with [ListState] to allow
 * the user to scroll through items and select one of them.
 *
 * # Fluent setters
 *
 * - [highlightStyle] sets the style of the selected item.
 * - [highlightSymbol] sets the symbol to be displayed in front of the selected item.
 * - [repeatHighlightSymbol] sets whether to repeat the symbol and style over selected
 *   multi-line items
 * - [direction] sets the list direction
 *
 * # Examples
 *
 * ```kotlin
 * val items = listOf("Item 1", "Item 2", "Item 3")
 * val list = List.new(items)
 *     .block(Block.bordered().title("List"))
 *     .style(Style.new().white())
 *     .highlightStyle(Style.new().italic())
 *     .highlightSymbol(">>")
 *     .repeatHighlightSymbol(true)
 *     .direction(ListDirection.BottomToTop)
 *
 * list.render(area, buffer)
 * ```
 *
 * # Stateful example
 *
 * ```kotlin
 * // This should be stored outside of the function in your application state.
 * val state = ListState()
 * val items = listOf("Item 1", "Item 2", "Item 3")
 * val list = List.new(items)
 *     .block(Block.bordered().title("List"))
 *     .highlightStyle(Style.new().reversed())
 *     .highlightSymbol(">>")
 *     .repeatHighlightSymbol(true)
 *
 * list.render(area, buffer, state)
 * ```
 */
data class List(
    /** An optional block to wrap the widget in */
    private val block: Block? = null,
    /** The items in the list */
    private val items: kotlin.collections.List<ListItem> = emptyList(),
    /** Style used as a base style for the widget */
    private val listStyle: Style = Style.default(),
    /** List display direction */
    private val direction: ListDirection = ListDirection.TopToBottom,
    /** Style used to render selected item */
    private val highlightStyle: Style = Style.default(),
    /** Symbol in front of the selected item (shifts all items to the right) */
    private val highlightSymbol: Line? = null,
    /** Whether to repeat the highlight symbol for each line of the selected item */
    private val repeatHighlightSymbol: Boolean = false,
    /** Decides when to allocate spacing for the selection symbol */
    private val highlightSpacing: HighlightSpacing = HighlightSpacing.WhenSelected,
    /** How many items to try to keep visible before and after the selected item */
    private val scrollPadding: Int = 0
) : Widget, StatefulWidget<ListState>, Styled<List> {

    /**
     * Set the items.
     *
     * @param items The items to display
     * @return A new List with the items set
     */
    fun items(items: kotlin.collections.List<ListItem>): List = copy(items = items)

    /**
     * Set the items from strings.
     *
     * @param items The string items to display
     * @return A new List with the items set
     */
    fun items(vararg items: String): List = copy(items = items.map { ListItem.new(it) })

    /**
     * Wraps the list with a custom [Block] widget.
     *
     * @param block The block to wrap the list in
     * @return A new List with the block set
     */
    fun block(block: Block): List = copy(block = block)

    /**
     * Sets the base style of the widget.
     *
     * All text rendered by the widget will use this style, unless overridden by [Block.style],
     * [ListItem.style], or the styles of the ListItem's content.
     *
     * @param style The base style for the list
     * @return A new List with the style set
     */
    fun style(style: Style): List = copy(listStyle = style)

    /**
     * Set the symbol to be displayed in front of the selected item.
     *
     * By default there is no highlight symbol.
     *
     * @param symbol The highlight symbol string
     * @return A new List with the highlight symbol set
     */
    fun highlightSymbol(symbol: String): List = copy(highlightSymbol = Line.from(symbol))

    /**
     * Set the symbol to be displayed in front of the selected item.
     *
     * @param symbol The highlight symbol as a Line
     * @return A new List with the highlight symbol set
     */
    fun highlightSymbol(symbol: Line): List = copy(highlightSymbol = symbol)

    /**
     * Set the style of the selected item.
     *
     * This style will be applied to the entire item, including the highlight symbol if it is
     * displayed, and will override any style set on the item or on the individual cells.
     *
     * @param style The highlight style
     * @return A new List with the highlight style set
     */
    fun highlightStyle(style: Style): List = copy(highlightStyle = style)

    /**
     * Set whether to repeat the highlight symbol and style over selected multi-line items.
     *
     * This is false by default.
     *
     * @param repeat Whether to repeat the highlight symbol
     * @return A new List with the repeat setting
     */
    fun repeatHighlightSymbol(repeat: Boolean): List = copy(repeatHighlightSymbol = repeat)

    /**
     * Set when to show the highlight spacing.
     *
     * The highlight spacing is the spacing that is allocated for the selection symbol (if enabled)
     * and is used to shift the list when an item is selected.
     *
     * @param spacing The highlight spacing mode
     * @return A new List with the highlight spacing set
     */
    fun highlightSpacing(spacing: HighlightSpacing): List = copy(highlightSpacing = spacing)

    /**
     * Defines the list direction (up or down).
     *
     * Defines if the List is displayed *top to bottom* (default) or *bottom to top*.
     * If there are too few items to fill the screen, the list will stick to the starting edge.
     *
     * @param direction The list direction
     * @return A new List with the direction set
     */
    fun direction(direction: ListDirection): List = copy(direction = direction)

    /**
     * Sets the number of items around the currently selected item that should be kept visible.
     *
     * A padding value of 1 will keep 1 item above and 1 item below visible if possible.
     *
     * @param padding The scroll padding
     * @return A new List with the scroll padding set
     */
    fun scrollPadding(padding: Int): List = copy(scrollPadding = padding)

    /**
     * Returns the number of [ListItem]s in the list.
     */
    fun len(): Int = items.size

    /**
     * Returns true if the list contains no elements.
     */
    fun isEmpty(): Boolean = items.isEmpty()

    // Widget implementation (renders without state)
    override fun render(area: Rect, buf: Buffer) {
        val state = ListState()
        render(area, buf, state)
    }

    // StatefulWidget implementation
    override fun render(area: Rect, buf: Buffer, state: ListState) {
        buf.setStyle(area, listStyle)
        block?.render(area, buf)
        val listArea = block.innerIfSome(area)

        if (listArea.isEmpty()) {
            return
        }

        if (items.isEmpty()) {
            state.select(null)
            return
        }

        // If the selected index is out of bounds, set it to the last item
        state.selected?.let { selected ->
            if (selected >= items.size) {
                state.select(items.size - 1)
            }
        }

        val listHeight = listArea.height

        val (firstVisibleIndex, lastVisibleIndex) = getItemsBounds(
            state.selected,
            state.offset,
            listHeight
        )

        // Important: this changes the state's offset to be the beginning of the now viewable items
        state.offset = firstVisibleIndex

        // Get our set highlighted symbol (if one was set)
        val defaultHighlightSymbol = Line.default()
        val effectiveHighlightSymbol = highlightSymbol ?: defaultHighlightSymbol
        val highlightSymbolWidth = effectiveHighlightSymbol.width()
        val emptySymbol = Line.from(" ".repeat(highlightSymbolWidth))

        var currentHeight = 0
        val selectionSpacing = highlightSpacing.shouldAdd(state.selected != null)

        val itemsToRender = items.asSequence()
            .withIndex()
            .drop(state.offset)
            .take(lastVisibleIndex - firstVisibleIndex)

        for ((i, item) in itemsToRender) {
            val (x, y) = if (direction == ListDirection.BottomToTop) {
                currentHeight += item.height()
                listArea.left() to (listArea.bottom() - currentHeight)
            } else {
                val pos = listArea.left() to (listArea.top() + currentHeight)
                currentHeight += item.height()
                pos
            }

            val rowArea = Rect(x, y, listArea.width, item.height())

            val itemStyle = listStyle.patch(item.itemStyle)
            buf.setStyle(rowArea, itemStyle)

            val isSelected = state.selected == i

            val itemArea = if (selectionSpacing) {
                Rect(
                    x = rowArea.x + highlightSymbolWidth,
                    y = rowArea.y,
                    width = (rowArea.width - highlightSymbolWidth).coerceAtLeast(0),
                    height = rowArea.height
                )
            } else {
                rowArea
            }
            item.content.render(itemArea, buf)

            if (isSelected) {
                buf.setStyle(rowArea, highlightStyle)
            }

            if (selectionSpacing) {
                for (j in 0 until item.content.height()) {
                    // if the item is selected, we need to display the highlight symbol:
                    // - either for the first line of the item only,
                    // - or for each line of the item if the appropriate option is set
                    val line = if (isSelected && (j == 0 || repeatHighlightSymbol)) {
                        effectiveHighlightSymbol
                    } else {
                        emptySymbol
                    }
                    val highlightArea = Rect(x, y + j, highlightSymbolWidth, 1)
                    line.render(highlightArea, buf)
                }
            }
        }
    }

    /**
     * Given an offset, calculate which items can fit in a given area.
     */
    private fun getItemsBounds(
        selected: Int?,
        offset: Int,
        maxHeight: Int
    ): Pair<Int, Int> {
        val safeOffset = offset.coerceAtMost((items.size - 1).coerceAtLeast(0))

        // Note: visible here implies visible in the given area
        var firstVisibleIndex = safeOffset
        var lastVisibleIndex = safeOffset

        // Current height of all items in the list to render, beginning at the offset
        var heightFromOffset = 0

        // Calculate the last visible index and total height of the items
        // that will fit in the available space
        for (item in items.drop(safeOffset)) {
            if (heightFromOffset + item.height() > maxHeight) {
                break
            }
            heightFromOffset += item.height()
            lastVisibleIndex++
        }

        // Get the selected index and apply scroll_padding to it, but still honor the offset if
        // nothing is selected.
        val indexToDisplay = applyScrollPaddingToSelectedIndex(
            selected,
            maxHeight,
            firstVisibleIndex,
            lastVisibleIndex
        ) ?: safeOffset

        // If we have an item selected that is out of the viewable area,
        // we still need to show this item
        while (indexToDisplay >= lastVisibleIndex && lastVisibleIndex < items.size) {
            heightFromOffset += items[lastVisibleIndex].height()
            lastVisibleIndex++

            // Now we need to hide previous items since we didn't have space
            // for the selected/offset item
            while (heightFromOffset > maxHeight && firstVisibleIndex < lastVisibleIndex) {
                heightFromOffset -= items[firstVisibleIndex].height()
                firstVisibleIndex++
            }
        }

        // If the selected item index is not in the viewable area, let's try to show the item
        while (indexToDisplay < firstVisibleIndex && firstVisibleIndex > 0) {
            firstVisibleIndex--
            heightFromOffset += items[firstVisibleIndex].height()

            // Don't show an item if it is beyond our viewable height
            while (heightFromOffset > maxHeight && lastVisibleIndex > firstVisibleIndex) {
                lastVisibleIndex--
                heightFromOffset -= items[lastVisibleIndex].height()
            }
        }

        return firstVisibleIndex to lastVisibleIndex
    }

    /**
     * Applies scroll padding to the selected index, reducing the padding value to keep the
     * selected item on screen even with items of inconsistent sizes.
     */
    private fun applyScrollPaddingToSelectedIndex(
        selected: Int?,
        maxHeight: Int,
        firstVisibleIndex: Int,
        lastVisibleIndex: Int
    ): Int? {
        if (selected == null) return null

        val lastValidIndex = (items.size - 1).coerceAtLeast(0)
        val safeSelected = selected.coerceAtMost(lastValidIndex)

        // The below loop handles situations where the list item sizes may not be consistent,
        // where the offset would have excluded some items that we want to include, or could
        // cause the offset value to be set to an inconsistent value each time we render.
        // The padding value will be reduced in case any of these issues would occur
        var effectivePadding = scrollPadding
        while (effectivePadding > 0) {
            var heightAroundSelected = 0
            val rangeStart = (safeSelected - effectivePadding).coerceAtLeast(0)
            val rangeEnd = (safeSelected + effectivePadding).coerceAtMost(lastValidIndex)
            for (index in rangeStart..rangeEnd) {
                heightAroundSelected += items[index].height()
            }
            if (heightAroundSelected <= maxHeight) {
                break
            }
            effectivePadding--
        }

        val adjustedIndex = when {
            (safeSelected + effectivePadding).coerceAtMost(lastValidIndex) >= lastVisibleIndex ->
                safeSelected + effectivePadding
            (safeSelected - effectivePadding).coerceAtLeast(0) < firstVisibleIndex ->
                (safeSelected - effectivePadding).coerceAtLeast(0)
            else -> safeSelected
        }

        return adjustedIndex.coerceAtMost(lastValidIndex)
    }

    // Styled implementation
    override fun getStyle(): Style = listStyle

    override fun setStyle(style: Style): List = style(style)

    companion object {
        /**
         * Creates a new list from ListItems.
         *
         * @param items The items to display
         * @return A new List widget
         */
        fun new(items: kotlin.collections.List<ListItem>): List = List(items = items)

        /**
         * Creates a new list from strings.
         *
         * @param items The string items to display
         * @return A new List widget
         */
        fun new(vararg items: String): List = List(items = items.map { ListItem.new(it) })

        /**
         * Creates an empty default list.
         */
        fun default(): List = List()
    }
}
