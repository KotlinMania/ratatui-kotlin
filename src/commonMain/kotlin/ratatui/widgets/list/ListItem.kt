package ratatui.widgets.list

import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.Text

/**
 * A single item in a [List].
 *
 * The item's height is defined by the number of lines it contains. This can be queried using
 * [height]. Similarly, [width] will return the maximum width of all lines.
 *
 * You can set the style of an item with [style]. This [Style] will be combined with the [Style]
 * of the inner [Text]. The [Style] of the [Text] will be added to the [Style] of the [ListItem].
 *
 * You can also align a ListItem by aligning its underlying [Text] and [Line]s. For that,
 * see [Text.alignment] and [Line.alignment]. On a multiline Text, one Line can override
 * the alignment by setting it explicitly.
 *
 * # Examples
 *
 * You can create ListItems from simple strings:
 *
 * ```kotlin
 * val item = ListItem.new("Item 1")
 * ```
 *
 * From Text:
 *
 * ```kotlin
 * val item = ListItem.new(Text.styled("Item 1", Style.new().red()))
 * ```
 *
 * A styled ListItem:
 *
 * ```kotlin
 * val item = ListItem.new("Item 1").style(Style.new().red().italic())
 * ```
 *
 * A right-aligned ListItem:
 *
 * ```kotlin
 * ListItem.new(Text.from("foo").rightAligned())
 * ```
 */
data class ListItem(
    /** The content of the list item */
    val content: Text,
    /** The style of the list item */
    val itemStyle: Style = Style.default()
) : Styled<ListItem> {

    /**
     * Sets the item style.
     *
     * This [Style] can be overridden by the [Style] of the [Text] content.
     *
     * @param style The style to apply to the item
     * @return A new ListItem with the style applied
     */
    fun style(style: Style): ListItem = copy(itemStyle = style)

    /**
     * Returns the item height (number of lines).
     */
    fun height(): Int = content.height()

    /**
     * Returns the max width of all the lines.
     */
    fun width(): Int = content.width()

    // Styled implementation
    override fun getStyle(): Style = itemStyle

    override fun setStyle(style: Style): ListItem = style(style)

    companion object {
        /**
         * Creates a new ListItem from a string.
         */
        fun new(content: String): ListItem = ListItem(content = Text.from(content))

        /**
         * Creates a new ListItem from Text.
         */
        fun new(content: Text): ListItem = ListItem(content = content)

        /**
         * Creates a new ListItem from a Line.
         */
        fun new(content: Line): ListItem = ListItem(content = Text.from(content))

        /**
         * Creates a new ListItem from a Span.
         */
        fun new(content: Span): ListItem = ListItem(content = Text.from(content))

        /**
         * Creates a new ListItem from a list of Lines.
         */
        fun new(lines: kotlin.collections.List<Line>): ListItem = ListItem(content = Text.from(lines))
    }
}

/**
 * Extension function to convert a String to a ListItem.
 */
fun String.toListItem(): ListItem = ListItem.new(this)
