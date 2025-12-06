package ratatui.widgets.gauge

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.block.Block as BlockSymbols
import ratatui.text.Span
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome
import kotlin.math.roundToInt

/**
 * A widget to display a progress bar.
 *
 * A `Gauge` renders a bar filled according to the value given to [Gauge.percent] or
 * [Gauge.ratio]. The bar width and height are defined by the [Rect] it is rendered in.
 *
 * The associated label is always centered horizontally and vertically. If not set with
 * [Gauge.label], the label is the percentage of the bar filled.
 *
 * You might want to have a higher precision bar using [Gauge.useUnicode].
 *
 * This can be useful to indicate the progression of a task, like a download.
 *
 * # Example
 *
 * ```kotlin
 * Gauge.default()
 *     .block(Block.bordered().title("Progress"))
 *     .gaugeStyle(Style.new().white().onBlack())
 *     .percent(20)
 * ```
 *
 * # See also
 *
 * - [LineGauge] for a thin progress bar
 */
data class Gauge(
    private val block: Block? = null,
    private val ratio: Double = 0.0,
    private val label: Span? = null,
    private val useUnicode: Boolean = false,
    private val gaugeStyle: Style = Style.default(),
    private val widgetStyle: Style = Style.default()
) : Widget, Styled<Gauge> {

    /**
     * Surrounds the `Gauge` with a [Block].
     *
     * The gauge is rendered in the inner portion of the block once space for borders and padding
     * is reserved. Styles set on the block do **not** affect the bar itself.
     */
    fun block(block: Block): Gauge = copy(block = block)

    /**
     * Sets the bar progression from a percentage.
     *
     * @throws IllegalArgumentException if percent is not between 0 and 100 inclusively.
     *
     * # See also
     *
     * See [Gauge.ratio] to set from a float.
     */
    fun percent(percent: Int): Gauge {
        require(percent in 0..100) {
            "Percentage should be between 0 and 100 inclusively."
        }
        return copy(ratio = percent.toDouble() / 100.0)
    }

    /**
     * Sets the bar progression from a ratio (float).
     *
     * `ratio` is the ratio between filled bar over empty bar (i.e. `3/4` completion is `0.75`).
     * This is more easily seen as a floating point percentage (e.g. 42% = `0.42`).
     *
     * @throws IllegalArgumentException if ratio is not between 0 and 1 inclusively.
     *
     * # See also
     *
     * See [Gauge.percent] to set from a percentage.
     */
    fun ratio(ratio: Double): Gauge {
        require(ratio in 0.0..1.0) {
            "Ratio should be between 0 and 1 inclusively."
        }
        return copy(ratio = ratio)
    }

    /**
     * Sets the label to display in the center of the bar.
     *
     * For a left-aligned label, see [LineGauge].
     * If the label is not defined, it is the percentage filled.
     */
    fun label(label: String): Gauge = copy(label = Span.raw(label))

    /**
     * Sets the label to display in the center of the bar.
     */
    fun label(label: Span): Gauge = copy(label = label)

    /**
     * Sets the widget style.
     *
     * This will style the block (if any non-styled) and background of the widget (everything
     * except the bar itself). [Block] style set with [Gauge.block] takes precedence.
     */
    fun style(style: Style): Gauge = copy(widgetStyle = style)

    /**
     * Sets the style of the bar.
     */
    fun gaugeStyle(style: Style): Gauge = copy(gaugeStyle = style)

    /**
     * Sets whether to use unicode characters to display the progress bar.
     *
     * This enables the use of unicode block characters.
     * This is useful to display a higher precision bar (8 extra fractional parts per cell).
     */
    fun useUnicode(unicode: Boolean): Gauge = copy(useUnicode = unicode)

    // Widget implementation
    override fun render(area: Rect, buf: Buffer) {
        buf.setStyle(area, widgetStyle)
        block?.render(area, buf)
        val inner = block.innerIfSome(area)
        renderGauge(inner, buf)
    }

    private fun renderGauge(gaugeArea: Rect, buf: Buffer) {
        if (gaugeArea.isEmpty()) {
            return
        }

        buf.setStyle(gaugeArea, gaugeStyle)

        // compute label value and its position
        // label is put at the center of the gauge_area
        val defaultLabel = Span.raw("${(ratio * 100.0).roundToInt()}%")
        val actualLabel = label ?: defaultLabel
        val clampedLabelWidth = gaugeArea.width.coerceAtMost(actualLabel.width())
        val labelCol = gaugeArea.left() + (gaugeArea.width - clampedLabelWidth) / 2
        val labelRow = gaugeArea.top() + gaugeArea.height / 2

        // the gauge will be filled proportionally to the ratio
        val filledWidth = gaugeArea.width.toDouble() * ratio
        val end = if (useUnicode) {
            gaugeArea.left() + kotlin.math.floor(filledWidth).toInt()
        } else {
            gaugeArea.left() + filledWidth.roundToInt()
        }

        for (y in gaugeArea.top() until gaugeArea.bottom()) {
            // render the filled area (left to end)
            for (x in gaugeArea.left() until end) {
                // Use full block for the filled part of the gauge and spaces for the part that is
                // covered by the label. Note that the background and foreground colors are swapped
                // for the label part, otherwise the gauge will be inverted
                if (x < labelCol || x >= labelCol + clampedLabelWidth || y != labelRow) {
                    buf[x, y]
                        .setSymbol(BlockSymbols.FULL)
                        .setFg(gaugeStyle.fg ?: Color.Reset)
                        .setBg(gaugeStyle.bg ?: Color.Reset)
                } else {
                    buf[x, y]
                        .setSymbol(" ")
                        .setFg(gaugeStyle.bg ?: Color.Reset)
                        .setBg(gaugeStyle.fg ?: Color.Reset)
                }
            }
            if (useUnicode && ratio < 1.0 && end < gaugeArea.right()) {
                buf[end, y].setSymbol(getUnicodeBlock(filledWidth % 1.0))
            }
        }
        // render the label
        buf.setSpan(labelCol, labelRow, actualLabel, clampedLabelWidth)
    }

    // Styled implementation
    override fun getStyle(): Style = widgetStyle

    override fun setStyle(style: Style): Gauge = style(style)

    companion object {
        /**
         * Creates a default Gauge widget.
         */
        fun default(): Gauge = Gauge()

        /**
         * Creates a new Gauge widget.
         */
        fun new(): Gauge = Gauge()
    }
}

/**
 * Returns the unicode block character for the given fractional width.
 */
private fun getUnicodeBlock(frac: Double): String {
    return when ((frac * 8.0).roundToInt()) {
        1 -> BlockSymbols.ONE_EIGHTH
        2 -> BlockSymbols.ONE_QUARTER
        3 -> BlockSymbols.THREE_EIGHTHS
        4 -> BlockSymbols.HALF
        5 -> BlockSymbols.FIVE_EIGHTHS
        6 -> BlockSymbols.THREE_QUARTERS
        7 -> BlockSymbols.SEVEN_EIGHTHS
        8 -> BlockSymbols.FULL
        else -> " "
    }
}
