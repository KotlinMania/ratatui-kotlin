package ratatui.widgets.table

import ratatui.buffer.Buffer
import ratatui.layout.Constraint
import ratatui.layout.Direction
import ratatui.layout.Flex
import ratatui.layout.Layout
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Text
import ratatui.widgets.StatefulWidget
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome
import ratatui.widgets.list.HighlightSpacing

/**
 * A widget to display data in formatted columns.
 *
 * A `Table` is a collection of [Row]s, each composed of [Cell]s.
 *
 * You can construct a [Table] using either [Table.new] or [Table.default] and then chain
 * builder style methods to set the desired properties.
 *
 * Make sure to call the [Table.widths] method, otherwise the columns will all have a width of 0
 * and thus not be visible.
 *
 * [Table] implements [Widget] and so it can be drawn using `Frame.render`.
 *
 * [Table] is also a [StatefulWidget], which means you can use it with [TableState] to allow
 * the user to scroll through the rows and select one of them. When rendering a [Table] with a
 * [TableState], the selected row, column and cell will be highlighted.
 *
 * # Example
 *
 * ```kotlin
 * val rows = listOf(Row.new("Cell1", "Cell2", "Cell3"))
 * val widths = listOf(
 *     Constraint.Length(5),
 *     Constraint.Length(5),
 *     Constraint.Length(10)
 * )
 * val table = Table.new(rows, widths)
 *     .columnSpacing(1)
 *     .style(Style.new().blue())
 *     .header(
 *         Row.new("Col1", "Col2", "Col3")
 *             .style(Style.new().bold())
 *             .bottomMargin(1)
 *     )
 *     .footer(Row.new("Updated on Dec 28"))
 *     .block(Block.new().title("Table"))
 *     .rowHighlightStyle(Style.new().reversed())
 *     .highlightSymbol(">>")
 * ```
 *
 * # Stateful example
 *
 * ```kotlin
 * // Note: TableState should be stored in your application state
 * val state = TableState()
 * val rows = listOf(
 *     Row.new("Row11", "Row12", "Row13"),
 *     Row.new("Row21", "Row22", "Row23"),
 *     Row.new("Row31", "Row32", "Row33")
 * )
 * val widths = listOf(
 *     Constraint.Length(5),
 *     Constraint.Length(5),
 *     Constraint.Length(10)
 * )
 * val table = Table.new(rows, widths)
 *     .block(Block.new().title("Table"))
 *     .rowHighlightStyle(Style.new().reversed())
 *     .highlightSymbol(">>")
 *
 * table.render(area, buffer, state)
 * ```
 */
data class Table(
    /** Data to display in each row */
    private val rows: List<Row> = emptyList(),
    /** Optional header */
    private val header: Row? = null,
    /** Optional footer */
    private val footer: Row? = null,
    /** Width constraints for each column */
    private val widths: List<Constraint> = emptyList(),
    /** Space between each column */
    private val columnSpacing: Int = 1,
    /** A block to wrap the widget in */
    private val block: Block? = null,
    /** Base style for the widget */
    private val tableStyle: Style = Style.default(),
    /** Style used to render the selected row */
    private val rowHighlightStyle: Style = Style.default(),
    /** Style used to render the selected column */
    private val columnHighlightStyle: Style = Style.default(),
    /** Style used to render the selected cell */
    private val cellHighlightStyle: Style = Style.default(),
    /** Symbol in front of the selected row */
    private val highlightSymbol: Text = Text.default(),
    /** Decides when to allocate spacing for the row selection */
    private val highlightSpacing: HighlightSpacing = HighlightSpacing.WhenSelected,
    /** Controls how to distribute extra space among the columns */
    private val flex: Flex = Flex.Start
) : Widget, StatefulWidget<TableState>, Styled<Table> {

    /**
     * Set the rows of the table.
     */
    fun rows(rows: List<Row>): Table = copy(rows = rows)

    /**
     * Sets the header row.
     */
    fun header(header: Row): Table = copy(header = header)

    /**
     * Sets the footer row.
     */
    fun footer(footer: Row): Table = copy(footer = footer)

    /**
     * Set the widths of the columns.
     */
    fun widths(widths: List<Constraint>): Table {
        ensurePercentagesLessThan100(widths)
        return copy(widths = widths)
    }

    /**
     * Set the spacing between columns.
     */
    fun columnSpacing(spacing: Int): Table = copy(columnSpacing = spacing)

    /**
     * Wraps the table with a custom [Block] widget.
     */
    fun block(block: Block): Table = copy(block = block)

    /**
     * Sets the base style of the widget.
     */
    fun style(style: Style): Table = copy(tableStyle = style)

    /**
     * Set the style of the selected row.
     */
    fun rowHighlightStyle(style: Style): Table = copy(rowHighlightStyle = style)

    /**
     * Set the style of the selected column.
     */
    fun columnHighlightStyle(style: Style): Table = copy(columnHighlightStyle = style)

    /**
     * Set the style of the selected cell.
     */
    fun cellHighlightStyle(style: Style): Table = copy(cellHighlightStyle = style)

    /**
     * Set the symbol to be displayed in front of the selected row.
     */
    fun highlightSymbol(symbol: String): Table = copy(highlightSymbol = Text.from(symbol))

    /**
     * Set the symbol to be displayed in front of the selected row.
     */
    fun highlightSymbol(symbol: Text): Table = copy(highlightSymbol = symbol)

    /**
     * Set when to show the highlight spacing.
     */
    fun highlightSpacing(spacing: HighlightSpacing): Table = copy(highlightSpacing = spacing)

    /**
     * Set how extra space is distributed amongst columns.
     */
    fun flex(flex: Flex): Table = copy(flex = flex)

    // Widget implementation (renders without state)
    override fun render(area: Rect, buf: Buffer) {
        val state = TableState()
        render(area, buf, state)
    }

    // StatefulWidget implementation
    override fun render(area: Rect, buf: Buffer, state: TableState) {
        buf.setStyle(area, tableStyle)
        block?.render(area, buf)
        val tableArea = block.innerIfSome(area)

        if (tableArea.isEmpty()) {
            return
        }

        // Clamp selected row
        state.selected?.let { selected ->
            if (selected >= rows.size) {
                state.select(if (rows.isEmpty()) null else rows.size - 1)
            }
        }

        if (rows.isEmpty()) {
            state.select(null)
        }

        // Clamp selected column
        val columnCount = columnCount()
        state.selectedColumn?.let { selected ->
            if (selected >= columnCount) {
                state.selectColumn(if (columnCount == 0) null else columnCount - 1)
            }
        }
        if (columnCount == 0) {
            state.selectColumn(null)
        }

        val selectionWidth = selectionWidth(state)
        val columnWidths = getColumnWidths(tableArea.width, selectionWidth, columnCount)
        val (headerArea, rowsArea, footerArea) = layout(tableArea)

        renderHeader(headerArea, buf, columnWidths)
        renderRows(rowsArea, buf, state, selectionWidth, columnWidths)
        renderFooter(footerArea, buf, columnWidths)
    }

    /**
     * Splits the table area into a header, rows area and a footer.
     */
    private fun layout(area: Rect): Triple<Rect, Rect, Rect> {
        val headerTopMargin = header?.topMargin ?: 0
        val headerHeight = header?.height ?: 0
        val headerBottomMargin = header?.bottomMargin ?: 0
        val footerTopMargin = footer?.topMargin ?: 0
        val footerHeight = footer?.height ?: 0
        val footerBottomMargin = footer?.bottomMargin ?: 0

        val layout = Layout.vertical(listOf(
            Constraint.Length(headerTopMargin),
            Constraint.Length(headerHeight),
            Constraint.Length(headerBottomMargin),
            Constraint.Min(0),
            Constraint.Length(footerTopMargin),
            Constraint.Length(footerHeight),
            Constraint.Length(footerBottomMargin)
        )).split(area)

        return Triple(layout[1], layout[3], layout[5])
    }

    private fun renderHeader(area: Rect, buf: Buffer, columnWidths: List<Pair<Int, Int>>) {
        val h = header ?: return
        buf.setStyle(area, h.rowStyle)
        for ((index, cell) in h.cells.withIndex()) {
            if (index < columnWidths.size) {
                val (x, width) = columnWidths[index]
                cell.render(Rect(area.x + x, area.y, width, area.height), buf)
            }
        }
    }

    private fun renderFooter(area: Rect, buf: Buffer, columnWidths: List<Pair<Int, Int>>) {
        val f = footer ?: return
        buf.setStyle(area, f.rowStyle)
        for ((index, cell) in f.cells.withIndex()) {
            if (index < columnWidths.size) {
                val (x, width) = columnWidths[index]
                cell.render(Rect(area.x + x, area.y, width, area.height), buf)
            }
        }
    }

    private fun renderRows(
        area: Rect,
        buf: Buffer,
        state: TableState,
        selectionWidth: Int,
        columnWidths: List<Pair<Int, Int>>
    ) {
        if (rows.isEmpty()) {
            return
        }

        val (startIndex, endIndex) = visibleRows(state, area)
        state.offset = startIndex

        var yOffset = 0
        var selectedRowArea: Rect? = null

        for ((i, row) in rows.withIndex().drop(startIndex).take(endIndex - startIndex)) {
            val y = area.y + yOffset + row.topMargin
            val height = (y + row.height).coerceAtMost(area.bottom()) - y
            if (height <= 0) continue

            val rowArea = Rect(area.x, y, area.width, height)
            buf.setStyle(rowArea, row.rowStyle)

            val isSelected = state.selected == i
            if (selectionWidth > 0 && isSelected) {
                val selectionArea = Rect(rowArea.x, rowArea.y, selectionWidth, rowArea.height)
                buf.setStyle(selectionArea, row.rowStyle)
                highlightSymbol.render(selectionArea, buf)
            }

            for ((cellIndex, cell) in row.cells.withIndex()) {
                if (cellIndex < columnWidths.size) {
                    val (x, width) = columnWidths[cellIndex]
                    cell.render(Rect(rowArea.x + x, rowArea.y, width, rowArea.height), buf)
                }
            }

            if (isSelected) {
                selectedRowArea = rowArea
            }
            yOffset += row.heightWithMargin()
        }

        val selectedColumnArea = state.selectedColumn?.let { col ->
            columnWidths.getOrNull(col)?.let { (x, width) ->
                Rect(x + area.x, area.y, width, area.height)
            }
        }

        when {
            selectedRowArea != null && selectedColumnArea != null -> {
                buf.setStyle(selectedRowArea, rowHighlightStyle)
                buf.setStyle(selectedColumnArea, columnHighlightStyle)
                val cellArea = selectedRowArea.intersection(selectedColumnArea)
                buf.setStyle(cellArea, cellHighlightStyle)
            }
            selectedRowArea != null -> {
                buf.setStyle(selectedRowArea, rowHighlightStyle)
            }
            selectedColumnArea != null -> {
                buf.setStyle(selectedColumnArea, columnHighlightStyle)
            }
        }
    }

    /**
     * Return the indexes of the visible rows.
     */
    private fun visibleRows(state: TableState, area: Rect): Pair<Int, Int> {
        val lastRow = (rows.size - 1).coerceAtLeast(0)
        var start = state.offset.coerceAtMost(lastRow)

        state.selected?.let { selected ->
            start = start.coerceAtMost(selected)
        }

        var end = start
        var height = 0

        for (item in rows.drop(start)) {
            if (height + item.height > area.height) {
                break
            }
            height += item.heightWithMargin()
            end++
        }

        state.selected?.let { selected ->
            val clampedSelected = selected.coerceAtMost(lastRow)

            // Scroll down until the selected row is visible
            while (clampedSelected >= end && end < rows.size) {
                height += rows[end].heightWithMargin()
                end++
                while (height > area.height && start < end) {
                    height -= rows[start].heightWithMargin()
                    start++
                }
            }
        }

        // Include a partial row if there is space
        if (height < area.height && end < rows.size) {
            end++
        }

        return start to end
    }

    /**
     * Get all offsets and widths of all user specified columns.
     */
    private fun getColumnWidths(maxWidth: Int, selectionWidth: Int, colCount: Int): List<Pair<Int, Int>> {
        val effectiveWidths = if (widths.isEmpty()) {
            // Divide the space between each column equally
            val colWidth = maxWidth / colCount.coerceAtLeast(1)
            (0 until colCount).map { Constraint.Length(colWidth) }
        } else {
            widths
        }

        // Calculate the columns area (excluding selection)
        val selectionConstraint = Constraint.Length(selectionWidth)
        val columnsConstraint = Constraint.Fill(0)
        val selectionLayout = Layout.horizontal(listOf(selectionConstraint, columnsConstraint))
            .split(Rect(0, 0, maxWidth, 1))
        val columnsArea = selectionLayout[1]

        // Split columns area according to widths
        val rects = Layout.horizontal(effectiveWidths)
            .flex(flex)
            .spacing(columnSpacing)
            .split(columnsArea)

        return rects.map { it.x to it.width }
    }

    private fun columnCount(): Int {
        return (rows.asSequence() + listOfNotNull(header, footer).asSequence())
            .map { it.cells.size }
            .maxOrNull() ?: 0
    }

    /**
     * Returns the width of the selection column.
     */
    private fun selectionWidth(state: TableState): Int {
        val hasSelection = state.selected != null
        return if (highlightSpacing.shouldAdd(hasSelection)) {
            highlightSymbol.width()
        } else {
            0
        }
    }

    // Styled implementation
    override fun getStyle(): Style = tableStyle

    override fun setStyle(style: Style): Table = style(style)

    companion object {
        /**
         * Creates a new [Table] widget with the given rows and widths.
         */
        fun new(rows: List<Row>, widths: List<Constraint>): Table {
            ensurePercentagesLessThan100(widths)
            return Table(rows = rows, widths = widths)
        }

        /**
         * Creates an empty default table.
         */
        fun default(): Table = Table()

        private fun ensurePercentagesLessThan100(widths: List<Constraint>) {
            for (w in widths) {
                if (w is Constraint.Percentage) {
                    require(w.value <= 100) {
                        "Percentages should be between 0 and 100 inclusively."
                    }
                }
            }
        }
    }
}
