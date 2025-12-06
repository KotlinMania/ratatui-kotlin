package ratatui.widgets.table

import ratatui.style.Style
import ratatui.style.Styled

/**
 * A single row of data to be displayed in a [Table] widget.
 *
 * A `Row` is a collection of [Cell]s.
 *
 * By default, a row has a height of 1 but you can change this using [Row.height].
 *
 * You can set the style of the entire row using [Row.style]. This [Style] will be combined
 * with the [Style] of each individual [Cell] by adding the [Style] of the [Cell] to the
 * [Style] of the [Row].
 *
 * # Examples
 *
 * You can create `Row`s from simple strings:
 *
 * ```kotlin
 * Row.new(listOf("Cell1", "Cell2", "Cell3"))
 * ```
 *
 * If you need a bit more control over individual cells, you can explicitly create [Cell]s:
 *
 * ```kotlin
 * Row.new(listOf(
 *     Cell.new("Cell1"),
 *     Cell.new("Cell2").style(Style.new().red().italic())
 * ))
 * ```
 */
data class Row(
    /** The cells in this row */
    internal val cells: List<Cell> = emptyList(),
    /** The height of this row */
    internal val height: Int = 1,
    /** The top margin of this row */
    internal val topMargin: Int = 0,
    /** The bottom margin of this row */
    internal val bottomMargin: Int = 0,
    /** The style of this row */
    internal val rowStyle: Style = Style.default()
) : Styled<Row> {

    /**
     * Set the cells of the [Row].
     */
    fun cells(cells: List<Cell>): Row = copy(cells = cells)

    /**
     * Set the cells of the [Row] from strings.
     */
    fun cells(vararg cellStrings: String): Row = copy(cells = cellStrings.map { Cell.new(it) })

    /**
     * Set the fixed height of the [Row].
     *
     * Any [Cell] whose content has more lines than this height will see its content truncated.
     * By default, the height is `1`.
     */
    fun height(height: Int): Row = copy(height = height)

    /**
     * Set the top margin. By default, the top margin is `0`.
     *
     * The top margin is the number of blank lines to be displayed before the row.
     */
    fun topMargin(margin: Int): Row = copy(topMargin = margin)

    /**
     * Set the bottom margin. By default, the bottom margin is `0`.
     *
     * The bottom margin is the number of blank lines to be displayed after the row.
     */
    fun bottomMargin(margin: Int): Row = copy(bottomMargin = margin)

    /**
     * Set the [Style] of the entire row.
     *
     * This [Style] can be overridden by the [Style] of a any individual [Cell] or by their
     * [Text] content.
     */
    fun style(style: Style): Row = copy(rowStyle = style)

    /**
     * Returns the total height of the row including margins.
     */
    internal fun heightWithMargin(): Int = height + topMargin + bottomMargin

    // Styled implementation
    override fun getStyle(): Style = rowStyle

    override fun setStyle(style: Style): Row = style(style)

    companion object {
        /**
         * Creates a new [Row] from a list of [Cell]s.
         */
        fun new(cells: List<Cell>): Row = Row(cells = cells)

        /**
         * Creates a new [Row] from strings.
         */
        fun new(vararg cellStrings: String): Row = Row(cells = cellStrings.map { Cell.new(it) })

        /**
         * Creates a new [Row] from strings.
         */
        fun fromStrings(cellStrings: List<String>): Row = Row(cells = cellStrings.map { Cell.new(it) })

        /**
         * Creates a default empty row.
         */
        fun default(): Row = Row()
    }
}
