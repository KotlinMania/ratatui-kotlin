package ratatui.widgets.block

import ratatui.buffer.Buffer
import ratatui.layout.Alignment
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.border.Set as BorderSet
import ratatui.symbols.merge.MergeStrategy
import ratatui.text.Line
import ratatui.widgets.Borders
import ratatui.widgets.BorderType
import ratatui.widgets.Widget

/**
 * Defines the position of the title.
 *
 * The title can be positioned on top or at the bottom of the block.
 *
 * # Example
 *
 * ```kotlin
 * Block.bordered()
 *     .titlePosition(TitlePosition.Top)
 *     .title("Top Title")
 * Block.bordered()
 *     .titlePosition(TitlePosition.Bottom)
 *     .title("Bottom Title")
 * ```
 */
enum class TitlePosition {
    /** Position the title at the top of the block. */
    Top,
    /** Position the title at the bottom of the block. */
    Bottom
}

/**
 * A widget that renders borders, titles, and padding around other widgets.
 *
 * A `Block` is a foundational widget that creates visual containers by drawing borders around an
 * area. It serves as a wrapper or frame for other widgets, providing structure and visual
 * separation in terminal UIs. Most built-in widgets in Ratatui use a pattern where they accept an
 * optional `Block` parameter that wraps the widget's content.
 *
 * When a widget renders with a block, the widget's style is applied first, then the block's style,
 * and finally the widget's content is rendered within the inner area calculated by the block. This
 * layered approach allows for flexible styling where the block can provide background colors,
 * borders, and padding while the inner widget handles its own content styling.
 *
 * Multiple blocks can be nested within each other. The [Block.inner] method calculates the area
 * available for content after accounting for borders, titles, and padding, making it easy to nest
 * blocks or position widgets within a block's boundaries.
 *
 * # Constructor Methods
 *
 * - [Block.new] - Creates a block with no borders or padding
 * - [Block.bordered] - Creates a block with all borders enabled
 *
 * # Border Configuration
 *
 * - [Block.borders] - Specifies which borders to display
 * - [Block.borderStyle] - Sets the style of the borders
 * - [Block.borderType] - Sets border symbols (single, double, thick, rounded, etc.)
 * - [Block.borderSet] - Sets custom border symbols as a [BorderSet]
 * - [Block.mergeBorders] - Controls how borders merge with adjacent blocks
 *
 * # Title Configuration
 *
 * - [Block.title] - Adds a title to the block
 * - [Block.titleTop] - Adds a title to the top of the block
 * - [Block.titleBottom] - Adds a title to the bottom of the block
 * - [Block.titleAlignment] - Sets default alignment for all titles
 * - [Block.titleStyle] - Sets the style for all titles
 * - [Block.titlePosition] - Sets default position for titles
 *
 * # Styling and Layout
 *
 * - [Block.style] - Sets the base style of the block
 * - [Block.padding] - Adds internal padding within the borders
 * - [Block.inner] - Calculates the inner area available for content
 *
 * # Examples
 *
 * Create a simple bordered block:
 *
 * ```kotlin
 * val block = Block.bordered().title("My Block")
 * ```
 *
 * Create a block with custom border styling:
 *
 * ```kotlin
 * val block = Block.bordered()
 *     .title("Styled Block")
 *     .borderType(BorderType.Rounded)
 *     .borderStyle(Style.new().cyan())
 *     .style(Style.new().onBlack())
 * ```
 *
 * Add multiple titles with different alignments:
 *
 * ```kotlin
 * val block = Block.bordered()
 *     .titleTop(Line.from("Left").leftAligned())
 *     .titleTop(Line.from("Center").centered())
 *     .titleTop(Line.from("Right").rightAligned())
 *     .titleBottom("Status: OK")
 * ```
 */
data class Block(
    /** List of titles with their optional positions */
    private val titles: MutableList<Pair<TitlePosition?, Line>> = mutableListOf(),
    /** The style to be patched to all titles of the block */
    private val titlesStyle: Style = Style.default(),
    /** The default alignment of the titles that don't have one */
    private val titlesAlignment: Alignment = Alignment.Left,
    /** The default position of the titles that don't have one */
    private val titlesPosition: TitlePosition = TitlePosition.Top,
    /** Visible borders */
    private val borderFlags: Borders = Borders.NONE,
    /** Border style */
    private val borderStyle: Style = Style.default(),
    /** The symbols used to render the border */
    private val borderSet: BorderSet = BorderType.Plain.toBorderSet(),
    /** Widget style */
    private val blockStyle: Style = Style.default(),
    /** Block padding */
    private val blockPadding: Padding = Padding.ZERO,
    /** Border merging strategy */
    private val mergeBordersStrategy: MergeStrategy = MergeStrategy.Replace
) : Widget, Styled<Block> {

    /**
     * Adds a title to the block using the default position.
     *
     * The position of the title is determined by the `titlesPosition` field of the block, which
     * defaults to `Top`. This can be changed using the [titlePosition] method. For
     * explicit positioning, use [titleTop] or [titleBottom].
     *
     * You can call this function multiple times to add multiple titles.
     *
     * @param title The title text or Line
     * @return A new Block with the title added
     */
    fun title(title: String): Block {
        val newTitles = titles.toMutableList()
        newTitles.add(null to Line.from(title))
        return copy(titles = newTitles)
    }

    /**
     * Adds a title to the block using the default position.
     */
    fun title(title: Line): Block {
        val newTitles = titles.toMutableList()
        newTitles.add(null to title)
        return copy(titles = newTitles)
    }

    /**
     * Adds a title to the top of the block.
     *
     * @param title The title text or Line
     * @return A new Block with the title added at the top
     */
    fun titleTop(title: String): Block {
        val newTitles = titles.toMutableList()
        newTitles.add(TitlePosition.Top to Line.from(title))
        return copy(titles = newTitles)
    }

    /**
     * Adds a title to the top of the block.
     */
    fun titleTop(title: Line): Block {
        val newTitles = titles.toMutableList()
        newTitles.add(TitlePosition.Top to title)
        return copy(titles = newTitles)
    }

    /**
     * Adds a title to the bottom of the block.
     *
     * @param title The title text or Line
     * @return A new Block with the title added at the bottom
     */
    fun titleBottom(title: String): Block {
        val newTitles = titles.toMutableList()
        newTitles.add(TitlePosition.Bottom to Line.from(title))
        return copy(titles = newTitles)
    }

    /**
     * Adds a title to the bottom of the block.
     */
    fun titleBottom(title: Line): Block {
        val newTitles = titles.toMutableList()
        newTitles.add(TitlePosition.Bottom to title)
        return copy(titles = newTitles)
    }

    /**
     * Applies the style to all titles.
     *
     * This style will be applied to all titles of the block. If a title has a style set, it will
     * be applied after this style.
     *
     * @param style The style to apply to titles
     * @return A new Block with the title style set
     */
    fun titleStyle(style: Style): Block = copy(titlesStyle = style)

    /**
     * Sets the default [Alignment] for all block titles.
     *
     * Titles that explicitly set an [Alignment] will ignore this.
     *
     * @param alignment The default alignment for titles
     * @return A new Block with the title alignment set
     */
    fun titleAlignment(alignment: Alignment): Block = copy(titlesAlignment = alignment)

    /**
     * Sets the default [TitlePosition] for all block titles.
     *
     * @param position The default position for titles
     * @return A new Block with the title position set
     */
    fun titlePosition(position: TitlePosition): Block = copy(titlesPosition = position)

    /**
     * Defines the style of the borders.
     *
     * This style is applied only to the areas covered by borders, and is applied to the block
     * after any [style] is applied.
     *
     * @param style The style for the borders
     * @return A new Block with the border style set
     */
    fun borderStyle(style: Style): Block = copy(borderStyle = style)

    /**
     * Defines the style of the entire block.
     *
     * This is the most generic [Style] a block can receive, it will be merged with any other
     * more specific styles. Elements can be styled further with [titleStyle] and
     * [borderStyle], which will be applied on top of this style.
     *
     * @param style The base style for the block
     * @return A new Block with the style set
     */
    fun style(style: Style): Block = copy(blockStyle = style)

    /**
     * Defines which borders to display.
     *
     * [Borders] can also be styled with [borderStyle] and [borderType].
     *
     * @param borders The borders to display
     * @return A new Block with the borders set
     */
    fun borders(borders: Borders): Block = copy(borderFlags = borders)

    /**
     * Sets the symbols used to display the border (e.g. single line, double line, thick or
     * rounded borders).
     *
     * Setting this overwrites any custom [borderSet] that was set.
     *
     * @param borderType The type of border symbols to use
     * @return A new Block with the border type set
     */
    fun borderType(borderType: BorderType): Block = copy(borderSet = borderType.toBorderSet())

    /**
     * Sets the symbols used to display the border as a [BorderSet].
     *
     * Setting this overwrites any [borderType] that was set.
     *
     * @param borderSet The custom border symbols to use
     * @return A new Block with the border set
     */
    fun borderSet(borderSet: BorderSet): Block = copy(borderSet = borderSet)

    /**
     * Defines the padding inside a `Block`.
     *
     * @param padding The padding to apply inside the block
     * @return A new Block with the padding set
     */
    fun padding(padding: Padding): Block = copy(blockPadding = padding)

    /**
     * Sets the block's [MergeStrategy] for overlapping characters.
     *
     * Changing the strategy to [MergeStrategy.Exact] or [MergeStrategy.Fuzzy] collapses border
     * characters that intersect with any previously rendered borders.
     *
     * @param strategy The merge strategy to use
     * @return A new Block with the merge strategy set
     */
    fun mergeBorders(strategy: MergeStrategy): Block = copy(mergeBordersStrategy = strategy)

    /**
     * Computes the inner area of a block after subtracting space for borders, titles, and padding.
     *
     * @param area The outer area of the block
     * @return The inner area available for content
     */
    fun inner(area: Rect): Rect {
        var inner = area

        if (borderFlags.intersects(Borders.LEFT)) {
            inner = inner.copy(
                x = (inner.x + 1).coerceAtMost(inner.right()),
                width = (inner.width - 1).coerceAtLeast(0)
            )
        }
        if (borderFlags.intersects(Borders.TOP) || hasTitleAtPosition(TitlePosition.Top)) {
            inner = inner.copy(
                y = (inner.y + 1).coerceAtMost(inner.bottom()),
                height = (inner.height - 1).coerceAtLeast(0)
            )
        }
        if (borderFlags.intersects(Borders.RIGHT)) {
            inner = inner.copy(width = (inner.width - 1).coerceAtLeast(0))
        }
        if (borderFlags.intersects(Borders.BOTTOM) || hasTitleAtPosition(TitlePosition.Bottom)) {
            inner = inner.copy(height = (inner.height - 1).coerceAtLeast(0))
        }

        inner = inner.copy(
            x = inner.x + blockPadding.left,
            y = inner.y + blockPadding.top,
            width = (inner.width - blockPadding.left - blockPadding.right).coerceAtLeast(0),
            height = (inner.height - blockPadding.top - blockPadding.bottom).coerceAtLeast(0)
        )

        return inner
    }

    /**
     * Checks if there is a title at the given position.
     */
    private fun hasTitleAtPosition(position: TitlePosition): Boolean {
        return titles.any { (pos, _) -> (pos ?: titlesPosition) == position }
    }

    // Widget implementation
    override fun render(area: Rect, buf: Buffer) {
        val clippedArea = area.intersection(buf.area)
        if (clippedArea.isEmpty()) {
            return
        }
        buf.setStyle(clippedArea, blockStyle)
        renderBorders(clippedArea, buf)
        renderTitles(clippedArea, buf)
    }

    private fun renderBorders(area: Rect, buf: Buffer) {
        renderSides(area, buf)
        renderCorners(area, buf)
    }

    private fun renderSides(area: Rect, buf: Buffer) {
        val left = area.left()
        val top = area.top()
        // area.right() and area.bottom() are outside the rect, subtract 1 to get the last row/col
        val right = area.right() - 1
        val bottom = area.bottom() - 1

        // The first and last element of each line are not drawn when there is an adjacent line as
        // this would cause the corner to initially be merged with a side character and then a
        // corner character to be drawn on top of it.
        val isReplace = mergeBordersStrategy != MergeStrategy.Replace
        val leftInset = left + if (isReplace && borderFlags.contains(Borders.LEFT)) 1 else 0
        val topInset = top + if (isReplace && borderFlags.contains(Borders.TOP)) 1 else 0
        val rightInset = right - if (isReplace && borderFlags.contains(Borders.RIGHT)) 1 else 0
        val bottomInset = bottom - if (isReplace && borderFlags.contains(Borders.BOTTOM)) 1 else 0

        // Left border
        if (borderFlags.contains(Borders.LEFT)) {
            for (y in topInset..bottomInset) {
                buf[left, y]
                    .mergeSymbol(borderSet.verticalLeft, mergeBordersStrategy)
                    .setStyle(borderStyle)
            }
        }

        // Top border
        if (borderFlags.contains(Borders.TOP)) {
            for (x in leftInset..rightInset) {
                buf[x, top]
                    .mergeSymbol(borderSet.horizontalTop, mergeBordersStrategy)
                    .setStyle(borderStyle)
            }
        }

        // Right border
        if (borderFlags.contains(Borders.RIGHT)) {
            for (y in topInset..bottomInset) {
                buf[right, y]
                    .mergeSymbol(borderSet.verticalRight, mergeBordersStrategy)
                    .setStyle(borderStyle)
            }
        }

        // Bottom border
        if (borderFlags.contains(Borders.BOTTOM)) {
            for (x in leftInset..rightInset) {
                buf[x, bottom]
                    .mergeSymbol(borderSet.horizontalBottom, mergeBordersStrategy)
                    .setStyle(borderStyle)
            }
        }
    }

    private fun renderCorners(area: Rect, buf: Buffer) {
        data class Corner(val border: Borders, val x: Int, val y: Int, val symbol: String)

        val corners = listOf(
            Corner(Borders.RIGHT or Borders.BOTTOM, area.right() - 1, area.bottom() - 1, borderSet.bottomRight),
            Corner(Borders.RIGHT or Borders.TOP, area.right() - 1, area.top(), borderSet.topRight),
            Corner(Borders.LEFT or Borders.BOTTOM, area.left(), area.bottom() - 1, borderSet.bottomLeft),
            Corner(Borders.LEFT or Borders.TOP, area.left(), area.top(), borderSet.topLeft)
        )

        for (corner in corners) {
            if (borderFlags.contains(corner.border)) {
                buf[corner.x, corner.y]
                    .mergeSymbol(corner.symbol, mergeBordersStrategy)
                    .setStyle(borderStyle)
            }
        }
    }

    private fun renderTitles(area: Rect, buf: Buffer) {
        renderTitlePosition(TitlePosition.Top, area, buf)
        renderTitlePosition(TitlePosition.Bottom, area, buf)
    }

    private fun renderTitlePosition(position: TitlePosition, area: Rect, buf: Buffer) {
        // NOTE: the order in which these functions are called defines the overlapping behavior
        renderLeftTitles(position, area, buf)
        renderCenterTitles(position, area, buf)
        renderRightTitles(position, area, buf)
    }

    /**
     * Render titles aligned to the right of the block
     */
    private fun renderRightTitles(position: TitlePosition, area: Rect, buf: Buffer) {
        val rightTitles = filteredTitles(position, Alignment.Right).toList()
        var titlesArea = titlesArea(area, position)

        // render titles in reverse order to align them to the right
        for (title in rightTitles.asReversed()) {
            if (titlesArea.isEmpty()) {
                break
            }
            val titleWidth = title.width()
            val titleArea = Rect(
                x = (titlesArea.right() - titleWidth).coerceAtLeast(titlesArea.left()),
                y = titlesArea.y,
                width = titleWidth.coerceAtMost(titlesArea.width),
                height = titlesArea.height
            )
            buf.setStyle(titleArea, titlesStyle)
            title.render(titleArea, buf)

            // bump the width of the titles area to the left
            titlesArea = titlesArea.copy(
                width = (titlesArea.width - titleWidth - 1).coerceAtLeast(0)
            )
        }
    }

    /**
     * Render titles in the center of the block
     */
    private fun renderCenterTitles(position: TitlePosition, area: Rect, buf: Buffer) {
        val centerArea = titlesArea(area, position)
        val centerTitles = filteredTitles(position, Alignment.Center).toList()

        // titles are rendered with a space after each title except the last one
        val totalWidth = centerTitles
            .sumOf { it.width() + 1 }
            .minus(1)
            .coerceAtLeast(0)

        if (totalWidth <= centerArea.width) {
            renderCenteredTitlesWithoutTruncation(centerTitles, totalWidth, centerArea, buf)
        } else {
            renderCenteredTitlesWithTruncation(centerTitles, totalWidth, centerArea, buf)
        }
    }

    private fun renderCenteredTitlesWithoutTruncation(
        titles: List<Line>,
        totalWidth: Int,
        area: Rect,
        buf: Buffer
    ) {
        // titles fit in the area, center them
        val x = area.left() + (area.width - totalWidth) / 2
        var currentArea = area.copy(x = x)

        for (title in titles) {
            val width = title.width()
            val titleArea = currentArea.copy(width = width)
            buf.setStyle(titleArea, titlesStyle)
            title.render(titleArea, buf)
            // Move the rendering cursor to the right, leaving 1 column space.
            currentArea = currentArea.copy(
                x = currentArea.x + width + 1,
                width = (currentArea.width - width - 1).coerceAtLeast(0)
            )
        }
    }

    private fun renderCenteredTitlesWithTruncation(
        titles: List<Line>,
        totalWidth: Int,
        area: Rect,
        buf: Buffer
    ) {
        var currentArea = area
        // titles do not fit in the area, truncate the left side using an offset
        var offset = (totalWidth - currentArea.width) / 2

        for (title in titles) {
            if (currentArea.isEmpty()) {
                break
            }
            val titleWidth = title.width()
            val width = currentArea.width.coerceAtMost(titleWidth) - offset
            if (width <= 0) {
                offset -= titleWidth + 1
                continue
            }
            val titleArea = currentArea.copy(width = width)
            buf.setStyle(titleArea, titlesStyle)

            if (offset > 0) {
                // truncate the left side of the title to fit the area
                title.rightAligned().render(titleArea, buf)
                offset = (offset - width - 1).coerceAtLeast(0)
            } else {
                // truncate the right side of the title to fit the area if needed
                title.leftAligned().render(titleArea, buf)
            }
            // Leave 1 column of spacing between titles
            currentArea = currentArea.copy(
                x = currentArea.x + width + 1,
                width = (currentArea.width - width - 1).coerceAtLeast(0)
            )
        }
    }

    /**
     * Render titles aligned to the left of the block
     */
    private fun renderLeftTitles(position: TitlePosition, area: Rect, buf: Buffer) {
        val leftTitles = filteredTitles(position, Alignment.Left)
        var titlesArea = titlesArea(area, position)

        for (title in leftTitles) {
            if (titlesArea.isEmpty()) {
                break
            }
            val titleWidth = title.width()
            val titleArea = titlesArea.copy(width = titleWidth.coerceAtMost(titlesArea.width))
            buf.setStyle(titleArea, titlesStyle)
            title.render(titleArea, buf)

            // bump the titles area to the right and reduce its width
            titlesArea = titlesArea.copy(
                x = titlesArea.x + titleWidth + 1,
                width = (titlesArea.width - titleWidth - 1).coerceAtLeast(0)
            )
        }
    }

    /**
     * An iterator over the titles that match the position and alignment
     */
    private fun filteredTitles(position: TitlePosition, alignment: Alignment): Sequence<Line> {
        return titles.asSequence()
            .filter { (pos, _) -> (pos ?: titlesPosition) == position }
            .filter { (_, line) -> (line.alignment ?: titlesAlignment) == alignment }
            .map { (_, line) -> line }
    }

    /**
     * An area that is one line tall and spans the width of the block excluding the borders and
     * is positioned at the top or bottom of the block.
     */
    private fun titlesArea(area: Rect, position: TitlePosition): Rect {
        val leftBorder = if (borderFlags.contains(Borders.LEFT)) 1 else 0
        val rightBorder = if (borderFlags.contains(Borders.RIGHT)) 1 else 0
        return Rect(
            x = area.left() + leftBorder,
            y = when (position) {
                TitlePosition.Top -> area.top()
                TitlePosition.Bottom -> area.bottom() - 1
            },
            width = (area.width - leftBorder - rightBorder).coerceAtLeast(0),
            height = 1
        )
    }

    /**
     * Calculate the left, and right space the [Block] will take up.
     *
     * The result takes the [Block]'s, [Borders], and [Padding] into account.
     */
    internal fun horizontalSpace(): Pair<Int, Int> {
        val left = blockPadding.left + if (borderFlags.contains(Borders.LEFT)) 1 else 0
        val right = blockPadding.right + if (borderFlags.contains(Borders.RIGHT)) 1 else 0
        return left to right
    }

    /**
     * Calculate the top, and bottom space that the [Block] will take up.
     *
     * Takes the [Padding], [TitlePosition], and the [Borders] that are selected into
     * account when calculating the result.
     */
    internal fun verticalSpace(): Pair<Int, Int> {
        val hasTop = borderFlags.contains(Borders.TOP) || hasTitleAtPosition(TitlePosition.Top)
        val top = blockPadding.top + if (hasTop) 1 else 0
        val hasBottom = borderFlags.contains(Borders.BOTTOM) || hasTitleAtPosition(TitlePosition.Bottom)
        val bottom = blockPadding.bottom + if (hasBottom) 1 else 0
        return top to bottom
    }

    // Styled implementation
    override fun getStyle(): Style = blockStyle

    override fun setStyle(style: Style): Block = style(style)

    companion object {
        /**
         * Creates a new block with no [Borders] or [Padding].
         */
        fun new(): Block = Block()

        /**
         * Create a new block with [all borders][Borders.ALL] shown.
         *
         * ```kotlin
         * assertEquals(Block.bordered(), Block.new().borders(Borders.ALL))
         * ```
         */
        fun bordered(): Block = Block(borderFlags = Borders.ALL)

        /**
         * Creates a default block (alias for [new]).
         */
        fun default(): Block = new()
    }
}

/**
 * An extension trait for [Block] that provides some convenience methods.
 *
 * This is implemented for `Block?` to simplify the common case of having a
 * widget with an optional block.
 */
fun Block?.innerIfSome(area: Rect): Rect = this?.inner(area) ?: area
