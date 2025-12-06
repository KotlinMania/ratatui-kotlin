package ratatui.widgets.tabs

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.line.Line as LineSymbols
import ratatui.text.Line
import ratatui.text.Span
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome

/**
 * A widget that displays a horizontal set of Tabs with a single tab selected.
 *
 * Each tab title is stored as a [Line] which can be individually styled. The selected tab is set
 * using [Tabs.select] and styled using [Tabs.highlightStyle]. The divider can be customized
 * with [Tabs.divider]. Padding can be set with [Tabs.padding] or [Tabs.paddingLeft] and
 * [Tabs.paddingRight].
 *
 * The divider defaults to |, and padding defaults to a singular space on each side.
 *
 * # Example
 *
 * ```kotlin
 * Tabs.new(listOf("Tab1", "Tab2", "Tab3", "Tab4"))
 *     .block(Block.bordered().title("Tabs"))
 *     .style(Style.new().white())
 *     .highlightStyle(Style.new().yellow())
 *     .select(2)
 *     .divider(".")
 *     .padding("->", "<-")
 * ```
 */
data class Tabs(
    /** A block to wrap this widget in if necessary */
    private val block: Block? = null,
    /** One title for each tab */
    private val titles: List<Line> = emptyList(),
    /** The index of the selected tab */
    private val selected: Int? = null,
    /** The style used to draw the text */
    private val tabsStyle: Style = Style.default(),
    /** Style to apply to the selected item */
    private val highlightStyle: Style = DEFAULT_HIGHLIGHT_STYLE,
    /** Tab divider */
    private val divider: Span = Span.raw(LineSymbols.VERTICAL),
    /** Tab left padding */
    private val paddingLeft: Line = Line.from(" "),
    /** Tab right padding */
    private val paddingRight: Line = Line.from(" ")
) : Widget, Styled<Tabs> {

    /**
     * Sets the titles of the tabs.
     */
    fun titles(titles: List<Line>): Tabs {
        val newSelected = if (titles.isEmpty()) {
            null
        } else {
            selected?.coerceAtMost(titles.size - 1) ?: 0
        }
        return copy(titles = titles, selected = newSelected)
    }

    /**
     * Sets the titles of the tabs from strings.
     */
    fun titles(vararg titles: String): Tabs = titles(titles.map { Line.from(it) })

    /**
     * Surrounds the Tabs with a [Block].
     */
    fun block(block: Block): Tabs = copy(block = block)

    /**
     * Sets the selected tab.
     *
     * The first tab has index 0 (this is also the default index).
     */
    fun select(selected: Int?): Tabs = copy(selected = selected)

    /**
     * Sets the selected tab by index.
     */
    fun select(selected: Int): Tabs = copy(selected = selected)

    /**
     * Sets the style of the tabs.
     */
    fun style(style: Style): Tabs = copy(tabsStyle = style)

    /**
     * Sets the style for the highlighted tab.
     */
    fun highlightStyle(style: Style): Tabs = copy(highlightStyle = style)

    /**
     * Sets the string to use as tab divider.
     *
     * By default, the divider is a pipe (`|`).
     */
    fun divider(divider: String): Tabs = copy(divider = Span.raw(divider))

    /**
     * Sets the span to use as tab divider.
     */
    fun divider(divider: Span): Tabs = copy(divider = divider)

    /**
     * Sets the padding between tabs.
     *
     * Both default to space.
     */
    fun padding(left: String, right: String): Tabs =
        copy(paddingLeft = Line.from(left), paddingRight = Line.from(right))

    /**
     * Sets the padding between tabs using Lines.
     */
    fun padding(left: Line, right: Line): Tabs =
        copy(paddingLeft = left, paddingRight = right)

    /**
     * Sets the left side padding between tabs.
     *
     * Defaults to a space.
     */
    fun paddingLeft(padding: String): Tabs = copy(paddingLeft = Line.from(padding))

    /**
     * Sets the left side padding between tabs using a Line.
     */
    fun paddingLeft(padding: Line): Tabs = copy(paddingLeft = padding)

    /**
     * Sets the right side padding between tabs.
     *
     * Defaults to a space.
     */
    fun paddingRight(padding: String): Tabs = copy(paddingRight = Line.from(padding))

    /**
     * Sets the right side padding between tabs using a Line.
     */
    fun paddingRight(padding: Line): Tabs = copy(paddingRight = padding)

    /**
     * Returns the width of the rendered tabs.
     *
     * The width includes the titles, dividers, and padding. It does not include any borders
     * added by the optional block.
     */
    fun width(): Int {
        val titlesWidth = titles.sumOf { it.width() }
        val titleCount = titles.size
        val dividerCount = (titleCount - 1).coerceAtLeast(0)
        val dividerWidth = dividerCount * divider.width()
        val leftPaddingWidth = titleCount * paddingLeft.width()
        val rightPaddingWidth = titleCount * paddingRight.width()
        return titlesWidth + dividerWidth + leftPaddingWidth + rightPaddingWidth
    }

    // Widget implementation
    override fun render(area: Rect, buf: Buffer) {
        buf.setStyle(area, tabsStyle)
        block?.render(area, buf)
        val inner = block.innerIfSome(area)
        renderTabs(inner, buf)
    }

    private fun renderTabs(tabsArea: Rect, buf: Buffer) {
        if (tabsArea.isEmpty()) {
            return
        }

        var x = tabsArea.left()
        val titlesLength = titles.size

        for ((i, title) in titles.withIndex()) {
            val lastTitle = titlesLength - 1 == i
            var remainingWidth = tabsArea.right() - x

            if (remainingWidth <= 0) {
                break
            }

            // Left Padding
            val (newX1, _) = buf.setLine(x, tabsArea.top(), paddingLeft, remainingWidth)
            x = newX1
            remainingWidth = tabsArea.right() - x
            if (remainingWidth <= 0) {
                break
            }

            // Title
            val titleStartX = x
            val (newX2, _) = buf.setLine(x, tabsArea.top(), title, remainingWidth)
            if (selected == i) {
                buf.setStyle(
                    Rect(
                        x = titleStartX,
                        y = tabsArea.top(),
                        width = newX2 - titleStartX,
                        height = 1
                    ),
                    highlightStyle
                )
            }
            x = newX2
            remainingWidth = tabsArea.right() - x
            if (remainingWidth <= 0) {
                break
            }

            // Right Padding
            val (newX3, _) = buf.setLine(x, tabsArea.top(), paddingRight, remainingWidth)
            x = newX3
            remainingWidth = tabsArea.right() - x
            if (remainingWidth <= 0 || lastTitle) {
                break
            }

            // Divider
            val (newX4, _) = buf.setSpan(x, tabsArea.top(), divider, remainingWidth)
            x = newX4
        }
    }

    // Styled implementation
    override fun getStyle(): Style = tabsStyle

    override fun setStyle(style: Style): Tabs = style(style)

    companion object {
        private val DEFAULT_HIGHLIGHT_STYLE = Style.default().addModifier(Modifier.REVERSED)

        /**
         * Creates new Tabs from their titles.
         *
         * The first tab is selected by default.
         */
        fun new(titles: List<Line>): Tabs {
            val selected = if (titles.isEmpty()) null else 0
            return Tabs(titles = titles, selected = selected)
        }

        /**
         * Creates new Tabs from string titles.
         */
        fun new(vararg titles: String): Tabs = new(titles.map { Line.from(it) })

        /**
         * Creates new Tabs from string titles list.
         */
        fun fromStrings(titles: List<String>): Tabs = new(titles.map { Line.from(it) })

        /**
         * Creates an empty default Tabs widget.
         */
        fun default(): Tabs = Tabs()
    }
}
