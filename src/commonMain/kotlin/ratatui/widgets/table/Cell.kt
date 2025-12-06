package ratatui.widgets.table

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.Text

/**
 * A [Cell] contains the [Text] to be displayed in a [Row] of a [Table].
 *
 * You can apply a [Style] to the [Cell] using [Cell.style]. This will set the style for the
 * entire area of the cell. Any [Style] set on the [Text] content will be combined with the
 * [Style] of the [Cell] by adding the [Style] of the [Text] content to the [Style] of
 * the [Cell]. Styles set on the text content will only affect the content.
 *
 * You can use [Text.alignment] when creating a cell to align its content.
 *
 * # Examples
 *
 * You can create a `Cell` from anything that can be converted to a [Text].
 *
 * ```kotlin
 * Cell.new("simple string")
 * Cell.new(Span.raw("span"))
 * Cell.new(Line.from(listOf(
 *     Span.raw("a vec of "),
 *     Span.raw("spans").bold()
 * )))
 * Cell.new(Text.from("a text"))
 * ```
 *
 * A styled Cell:
 *
 * ```kotlin
 * Cell.new("Cell 1").style(Style.new().red().italic())
 * ```
 */
data class Cell(
    /** The content of the cell */
    val content: Text = Text.default(),
    /** The style of the cell */
    private val cellStyle: Style = Style.default()
) : Styled<Cell> {

    /**
     * Set the content of the [Cell].
     */
    fun content(content: Text): Cell = copy(content = content)

    /**
     * Set the content of the [Cell] from a string.
     */
    fun content(content: String): Cell = copy(content = Text.from(content))

    /**
     * Set the [Style] of this cell.
     *
     * This [Style] will override the [Style] of the [Row] and can be overridden by the [Style]
     * of the [Text] content.
     */
    fun style(style: Style): Cell = copy(cellStyle = style)

    /**
     * Render the cell to the buffer.
     */
    internal fun render(area: Rect, buf: Buffer) {
        buf.setStyle(area, cellStyle)
        content.render(area, buf)
    }

    // Styled implementation
    override fun getStyle(): Style = cellStyle

    override fun setStyle(style: Style): Cell = style(style)

    companion object {
        /**
         * Creates a new [Cell] from a string.
         */
        fun new(content: String): Cell = Cell(content = Text.from(content))

        /**
         * Creates a new [Cell] from [Text].
         */
        fun new(content: Text): Cell = Cell(content = content)

        /**
         * Creates a new [Cell] from a [Line].
         */
        fun new(content: Line): Cell = Cell(content = Text.from(content))

        /**
         * Creates a new [Cell] from a [Span].
         */
        fun new(content: Span): Cell = Cell(content = Text.from(content))

        /**
         * Creates a default empty cell.
         */
        fun default(): Cell = Cell()
    }
}

/**
 * Extension function to convert a String to a Cell.
 */
fun String.toCell(): Cell = Cell.new(this)
