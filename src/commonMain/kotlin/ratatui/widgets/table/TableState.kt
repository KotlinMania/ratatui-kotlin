package ratatui.widgets.table

/**
 * State of a [Table] widget.
 *
 * This state can be used to scroll through the rows and select one of them. When the table is
 * rendered as a stateful widget, the selected row, column and cell will be highlighted and the
 * table will be shifted to ensure that the selected row is visible. This will modify the
 * [TableState] object passed to the render method.
 *
 * The state consists of:
 * - [offset]: the index of the first row to be displayed
 * - [selected]: the index of the selected row, which can be `null` if no row is selected
 * - [selectedColumn]: the index of the selected column, which can be `null` if no column is selected
 *
 * # Example
 *
 * ```kotlin
 * val rows = listOf(Row.new("Cell1", "Cell2"))
 * val widths = listOf(Constraint.Length(5), Constraint.Length(5))
 * val table = Table.new(rows, widths)
 *
 * // Note: TableState should be stored outside of your render method
 * val state = TableState()
 *
 * state.offset = 1 // display the second row and onwards
 * state.select(3)  // select the fourth row (0-indexed)
 * state.selectColumn(2) // select the third column (0-indexed)
 *
 * table.render(area, buffer, state)
 * ```
 */
data class TableState(
    /** Index of the first row to be displayed */
    var offset: Int = 0,
    /** Index of the selected row, or null if no row is selected */
    var selected: Int? = null,
    /** Index of the selected column, or null if no column is selected */
    var selectedColumn: Int? = null
) {
    /**
     * Sets the index of the first row to be displayed.
     */
    fun withOffset(offset: Int): TableState {
        this.offset = offset
        return this
    }

    /**
     * Sets the index of the selected row.
     */
    fun withSelected(selected: Int?): TableState {
        this.selected = selected
        return this
    }

    /**
     * Sets the index of the selected column.
     */
    fun withSelectedColumn(selected: Int?): TableState {
        this.selectedColumn = selected
        return this
    }

    /**
     * Sets the indexes of the selected cell.
     */
    fun withSelectedCell(selected: Pair<Int, Int>?): TableState {
        if (selected != null) {
            this.selected = selected.first
            this.selectedColumn = selected.second
        } else {
            this.selected = null
            this.selectedColumn = null
        }
        return this
    }

    /**
     * Returns the indexes of the selected cell.
     */
    fun selectedCell(): Pair<Int, Int>? {
        val row = selected
        val col = selectedColumn
        return if (row != null && col != null) {
            row to col
        } else {
            null
        }
    }

    /**
     * Sets the index of the selected row.
     * Set to `null` if no row is selected. This will also reset the offset to `0`.
     */
    fun select(index: Int?) {
        selected = index
        if (index == null) {
            offset = 0
        }
    }

    /**
     * Sets the index of the selected column.
     */
    fun selectColumn(index: Int?) {
        selectedColumn = index
    }

    /**
     * Sets the indexes of the selected cell.
     * Set to `null` if no cell is selected. This will also reset the row offset to `0`.
     */
    fun selectCell(indexes: Pair<Int, Int>?) {
        if (indexes != null) {
            selected = indexes.first
            selectedColumn = indexes.second
        } else {
            offset = 0
            selected = null
            selectedColumn = null
        }
    }

    /**
     * Selects the next row or the first one if no row is selected.
     */
    fun selectNext() {
        val next = selected?.let { it + 1 } ?: 0
        select(next)
    }

    /**
     * Selects the next column or the first one if no column is selected.
     */
    fun selectNextColumn() {
        val next = selectedColumn?.let { it + 1 } ?: 0
        selectColumn(next)
    }

    /**
     * Selects the previous row or the last one if no row is selected.
     */
    fun selectPrevious() {
        val previous = selected?.let { (it - 1).coerceAtLeast(0) } ?: Int.MAX_VALUE
        select(previous)
    }

    /**
     * Selects the previous column or the last one if no column is selected.
     */
    fun selectPreviousColumn() {
        val previous = selectedColumn?.let { (it - 1).coerceAtLeast(0) } ?: Int.MAX_VALUE
        selectColumn(previous)
    }

    /**
     * Selects the first row.
     */
    fun selectFirst() {
        select(0)
    }

    /**
     * Selects the first column.
     */
    fun selectFirstColumn() {
        selectColumn(0)
    }

    /**
     * Selects the last row.
     */
    fun selectLast() {
        select(Int.MAX_VALUE)
    }

    /**
     * Selects the last column.
     */
    fun selectLastColumn() {
        selectColumn(Int.MAX_VALUE)
    }

    /**
     * Scrolls down by a specified amount in the table.
     */
    fun scrollDownBy(amount: Int) {
        val current = selected ?: 0
        select(current + amount)
    }

    /**
     * Scrolls up by a specified amount in the table.
     */
    fun scrollUpBy(amount: Int) {
        val current = selected ?: 0
        select((current - amount).coerceAtLeast(0))
    }

    /**
     * Scrolls right by a specified amount in the table.
     */
    fun scrollRightBy(amount: Int) {
        val current = selectedColumn ?: 0
        selectColumn(current + amount)
    }

    /**
     * Scrolls left by a specified amount in the table.
     */
    fun scrollLeftBy(amount: Int) {
        val current = selectedColumn ?: 0
        selectColumn((current - amount).coerceAtLeast(0))
    }

    companion object {
        /** Creates a default TableState with no selection and offset at 0 */
        fun default(): TableState = TableState()
    }
}
