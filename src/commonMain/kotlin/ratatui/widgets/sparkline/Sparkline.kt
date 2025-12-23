package ratatui.widgets.sparkline

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.bar.Set as BarSet
import ratatui.symbols.bar.NINE_LEVELS
import ratatui.symbols.shade.Shade
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome
import kotlin.math.min

/**
 * Widget to render a sparkline over one or more lines.
 *
 * Each bar in a [Sparkline] represents a value from the provided dataset. The height of the bar
 * is determined by the value in the dataset.
 *
 * You can create a [Sparkline] using [Sparkline.default].
 *
 * The data is set using [Sparkline.data]. The data can be a list of [ULong], [ULong?], or a
 * [SparklineBar]. For the [ULong?] and [SparklineBar] cases, a data point with a value of `null`
 * is interpreted as the _absence_ of a value.
 *
 * [Sparkline] can be styled using [Sparkline.style]. The style may be set for the
 * entire widget or for individual bars by setting individual [SparklineBar.barStyle].
 *
 * The bars are rendered using a set of symbols. The default set is [NINE_LEVELS].
 * You can change the set using [Sparkline.barSet].
 *
 * If the data provided is a list of [ULong] or [ULong?], the bars will be styled with the
 * style of the sparkline. If the data is a list of [SparklineBar], the bars will be
 * styled with the style of the sparkline combined with the style provided in the [SparklineBar]
 * if it is set, otherwise the sparkline style will be used.
 *
 * Absent values will be rendered with the style set by [Sparkline.absentValueStyle] and
 * the symbol set by [Sparkline.absentValueSymbol].
 *
 * # Setter methods
 *
 * - [Sparkline.block] wraps the sparkline in a [Block]
 * - [Sparkline.data] defines the dataset, you'll almost always want to use it
 * - [Sparkline.max] sets the maximum value of bars
 * - [Sparkline.direction] sets the render direction
 *
 * # Examples
 *
 * ```kotlin
 * Sparkline.default()
 *     .block(Block.bordered().title("Sparkline"))
 *     .data(listOf(0u, 2u, 3u, 4u, 1u, 4u, 10u))
 *     .max(5u)
 *     .direction(RenderDirection.RightToLeft)
 *     .style(Style.default().red().onWhite())
 *     .absentValueStyle(Style.default().fg(Color.Red))
 *     .absentValueSymbol(Shade.FULL)
 * ```
 */
data class Sparkline(
    /** A block to wrap the widget in */
    private val block: Block? = null,
    /** Widget style */
    private val sparklineStyle: Style = Style.default(),
    /** Style of absent values */
    private val absentStyle: Style = Style.default(),
    /** The symbol to use for absent values */
    private val absentSymbol: String = Shade.EMPTY,
    /** A list of the data to display */
    private val dataList: List<SparklineBar> = emptyList(),
    /** The maximum value to take to compute the maximum bar height */
    private val maxValue: ULong? = null,
    /** A set of bar symbols used to represent the given data */
    private val barSetSymbols: BarSet = NINE_LEVELS,
    /** The direction to render the sparkline */
    private val renderDirection: RenderDirection = RenderDirection.LeftToRight
) : Widget, Styled<Sparkline> {

    /**
     * Wraps the sparkline with the given [block].
     *
     * @param block The block to wrap the sparkline in
     * @return A new Sparkline with the block set
     */
    fun block(block: Block): Sparkline = copy(block = block)

    /**
     * Sets the style of the entire widget.
     *
     * The foreground corresponds to the bars while the background is everything else.
     *
     * @param style The style to apply
     * @return A new Sparkline with the style set
     */
    fun style(style: Style): Sparkline = copy(sparklineStyle = style)

    /**
     * Sets the style to use for absent values.
     *
     * Absent values are values in the dataset that are `null`.
     *
     * The foreground corresponds to the bars while the background is everything else.
     *
     * @param style The style for absent values
     * @return A new Sparkline with the absent value style set
     */
    fun absentValueStyle(style: Style): Sparkline = copy(absentStyle = style)

    /**
     * Sets the symbol to use for absent values.
     *
     * Absent values are values in the dataset that are `null`.
     *
     * The default is [Shade.EMPTY].
     *
     * @param symbol The symbol for absent values
     * @return A new Sparkline with the absent value symbol set
     */
    fun absentValueSymbol(symbol: String): Sparkline = copy(absentSymbol = symbol)

    /**
     * Sets the dataset for the sparkline.
     *
     * Each item in the dataset is a bar in the sparkline. The height of the bar is determined by
     * the value in the dataset.
     *
     * @param data The list of bars
     * @return A new Sparkline with the data set
     */
    fun data(data: List<SparklineBar>): Sparkline = copy(dataList = data)

    /**
     * Sets the dataset for the sparkline from a list of ULong values.
     *
     * @param data The list of values
     * @return A new Sparkline with the data set
     */
    fun dataFromValues(data: List<ULong>): Sparkline =
        copy(dataList = data.map { SparklineBar.from(it) })

    /**
     * Sets the dataset for the sparkline from an array of ULong values.
     *
     * @param data The array of values
     * @return A new Sparkline with the data set
     */
    fun data(vararg data: ULong): Sparkline =
        copy(dataList = data.map { SparklineBar.from(it) })

    /**
     * Sets the dataset for the sparkline from a list of Int values.
     *
     * @param data The list of values
     * @return A new Sparkline with the data set
     */
    fun dataFromInts(data: List<Int>): Sparkline =
        copy(dataList = data.map { SparklineBar.from(it) })

    /**
     * Sets the dataset for the sparkline from a list of nullable ULong values.
     *
     * A value of `null` is interpreted as the _absence_ of a value, as distinct from a value of `0`.
     * Absent values will be rendered with [absentValueStyle] and [absentValueSymbol].
     *
     * @param data The list of nullable values
     * @return A new Sparkline with the data set
     */
    fun dataFromNullable(data: List<ULong?>): Sparkline =
        copy(dataList = data.map { SparklineBar(value = it) })

    /**
     * Sets the dataset for the sparkline from a list of nullable Int values.
     *
     * A value of `null` is interpreted as the _absence_ of a value, as distinct from a value of `0`.
     * Absent values will be rendered with [absentValueStyle] and [absentValueSymbol].
     *
     * @param data The list of nullable values
     * @return A new Sparkline with the data set
     */
    fun dataFromNullableInts(data: List<Int?>): Sparkline =
        copy(dataList = data.map { SparklineBar(value = it?.toULong()) })

    /**
     * Sets the dataset for the sparkline from an array of Int values.
     *
     * @param data The array of values
     * @return A new Sparkline with the data set
     */
    fun dataInts(vararg data: Int): Sparkline =
        copy(dataList = data.map { SparklineBar.from(it) })

    /**
     * Sets the maximum value of bars.
     *
     * Every bar will be scaled accordingly. If no max is given, this will be the max in the
     * dataset.
     *
     * @param max The maximum value
     * @return A new Sparkline with the max set
     */
    fun max(max: ULong): Sparkline = copy(maxValue = max)

    /**
     * Sets the characters used to display the bars.
     *
     * Can be [ratatui.symbols.bar.THREE_LEVELS], [ratatui.symbols.bar.NINE_LEVELS] (default)
     * or a custom [BarSet].
     *
     * @param barSet The bar character set
     * @return A new Sparkline with the bar set
     */
    fun barSet(barSet: BarSet): Sparkline = copy(barSetSymbols = barSet)

    /**
     * Sets the direction of the sparkline.
     *
     * [RenderDirection.LeftToRight] by default.
     *
     * @param direction The render direction
     * @return A new Sparkline with the direction set
     */
    fun direction(direction: RenderDirection): Sparkline = copy(renderDirection = direction)

    override fun render(area: Rect, buf: Buffer) {
        block?.render(area, buf)
        val inner = block.innerIfSome(area)
        renderSparkline(inner, buf)
    }

    private fun renderSparkline(sparkArea: Rect, buf: Buffer) {
        if (sparkArea.isEmpty()) {
            return
        }

        // Determine the maximum height across all bars
        val maxHeight = maxValue
            ?: dataList.mapNotNull { it.value }.maxOrNull()
            ?: 1uL

        // Determine the maximum index to render
        val maxIndex = min(sparkArea.width, dataList.size)

        // Render each item in the data
        for ((i, item) in dataList.take(maxIndex).withIndex()) {
            val x = when (renderDirection) {
                RenderDirection.LeftToRight -> sparkArea.left() + i
                RenderDirection.RightToLeft -> sparkArea.right() - i - 1
            }

            // Determine the height, symbol and style to use for the item
            //
            // If the item is not absent:
            // - the height is the value of the item scaled to the height of the spark area
            // - the symbol is determined by the scaled height
            // - the style is the style of the item, if one is set
            //
            // Otherwise:
            // - the height is the total height of the spark area
            // - the symbol is the absent value symbol
            // - the style is the absent value style
            val (initialHeight, symbol, itemStyle) = when {
                item.value != null -> {
                    val height = if (maxHeight == 0uL) {
                        0uL
                    } else {
                        item.value * sparkArea.height.toULong() * 8uL / maxHeight
                    }
                    Triple(height, null, item.barStyle)
                }
                else -> Triple(
                    sparkArea.height.toULong() * 8uL,
                    absentSymbol,
                    absentStyle
                )
            }

            var height = initialHeight

            // Render the item from top to bottom
            //
            // If the symbol is set it will be used for the entire height of the bar, otherwise the
            // symbol will be determined by the _remaining_ height.
            //
            // If the style is set it will be used for the entire height of the bar, otherwise the
            // sparkline style will be used.
            for (j in (0 until sparkArea.height).reversed()) {
                val barSymbol = symbol ?: symbolForHeight(height)
                if (height > 8uL) {
                    height -= 8uL
                } else {
                    height = 0uL
                }
                buf[x, sparkArea.top() + j]
                    .setSymbol(barSymbol)
                    .setStyle(sparklineStyle.patch(itemStyle ?: Style.default()))
            }
        }
    }

    private fun symbolForHeight(height: ULong): String = when (height.toInt()) {
        0 -> barSetSymbols.empty
        1 -> barSetSymbols.oneEighth
        2 -> barSetSymbols.oneQuarter
        3 -> barSetSymbols.threeEighths
        4 -> barSetSymbols.half
        5 -> barSetSymbols.fiveEighths
        6 -> barSetSymbols.threeQuarters
        7 -> barSetSymbols.sevenEighths
        else -> barSetSymbols.full
    }

    // Styled implementation
    override fun getStyle(): Style = sparklineStyle

    override fun setStyle(style: Style): Sparkline = style(style)

    companion object {
        /**
         * Creates a new default Sparkline.
         */
        fun default(): Sparkline = Sparkline()

        /**
         * Creates a new Sparkline (alias for [default]).
         */
        fun new(): Sparkline = Sparkline()
    }
}
