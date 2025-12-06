package ratatui.widgets.gauge

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.line.Line as LineSymbols
import ratatui.text.Line
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * A compact widget to display a progress bar over a single thin line.
 *
 * This can be useful to indicate the progression of a task, like a download.
 *
 * A `LineGauge` renders a line filled with symbols defined by [LineGauge.filledSymbol] and
 * [LineGauge.unfilledSymbol] according to the value given to [LineGauge.ratio].
 * Unlike [Gauge], only the width can be defined by the rendering [Rect]. The height is always 1.
 *
 * The associated label is always left-aligned. If not set with [LineGauge.label], the label is
 * the percentage of the bar filled.
 *
 * To style the gauge line use [LineGauge.filledStyle] and [LineGauge.unfilledStyle] which
 * let you pick a color for foreground (i.e. line) and background of the filled and unfilled part
 * of gauge respectively.
 *
 * # Example
 *
 * ```kotlin
 * LineGauge.default()
 *     .block(Block.bordered().title("Progress"))
 *     .filledStyle(Style.new().white().onBlack().bold())
 *     .filledSymbol(LineSymbols.THICK_HORIZONTAL)
 *     .ratio(0.4)
 * ```
 *
 * # See also
 *
 * - [Gauge] for bigger, higher precision and more configurable progress bar
 */
data class LineGauge(
    private val block: Block? = null,
    private val ratio: Double = 0.0,
    private val label: Line? = null,
    private val widgetStyle: Style = Style.default(),
    private val filledSymbol: String = LineSymbols.HORIZONTAL,
    private val unfilledSymbol: String = LineSymbols.HORIZONTAL,
    private val filledStyle: Style = Style.default(),
    private val unfilledStyle: Style = Style.default()
) : Widget, Styled<LineGauge> {

    /**
     * Surrounds the `LineGauge` with a [Block].
     */
    fun block(block: Block): LineGauge = copy(block = block)

    /**
     * Sets the bar progression from a ratio (float).
     *
     * `ratio` is the ratio between filled bar over empty bar (i.e. `3/4` completion is `0.75`).
     * This is more easily seen as a floating point percentage (e.g. 42% = `0.42`).
     *
     * @throws IllegalArgumentException if ratio is not between 0 and 1 inclusively.
     */
    fun ratio(ratio: Double): LineGauge {
        require(ratio in 0.0..1.0) {
            "Ratio should be between 0 and 1 inclusively."
        }
        return copy(ratio = ratio)
    }

    /**
     * Sets the symbol for the filled part of the gauge.
     */
    fun filledSymbol(symbol: String): LineGauge = copy(filledSymbol = symbol)

    /**
     * Sets the symbol for the unfilled part of the gauge.
     */
    fun unfilledSymbol(symbol: String): LineGauge = copy(unfilledSymbol = symbol)

    /**
     * Sets the label to display.
     *
     * With `LineGauge`, labels are only on the left, see [Gauge] for a centered label.
     * If the label is not defined, it is the percentage filled.
     */
    fun label(label: String): LineGauge = copy(label = Line.from(label))

    /**
     * Sets the label to display.
     */
    fun label(label: Line): LineGauge = copy(label = label)

    /**
     * Sets the widget style.
     *
     * This will style everything except the bar itself, so basically the block (if any) and
     * background.
     */
    fun style(style: Style): LineGauge = copy(widgetStyle = style)

    /**
     * Sets the style of filled part of the bar.
     */
    fun filledStyle(style: Style): LineGauge = copy(filledStyle = style)

    /**
     * Sets the style of the unfilled part of the bar.
     */
    fun unfilledStyle(style: Style): LineGauge = copy(unfilledStyle = style)

    // Widget implementation
    override fun render(area: Rect, buf: Buffer) {
        buf.setStyle(area, widgetStyle)
        block?.render(area, buf)
        val gaugeArea = block.innerIfSome(area)
        if (gaugeArea.isEmpty()) {
            return
        }

        val percentValue = (ratio * 100.0).roundToInt()
        val defaultLabel = Line.from("${percentValue.toString().padStart(3)}%")
        val actualLabel = label ?: defaultLabel
        val (col, row) = buf.setLine(gaugeArea.left(), gaugeArea.top(), actualLabel, gaugeArea.width)
        val start = col + 1
        if (start >= gaugeArea.right()) {
            return
        }

        val end = start + floor((gaugeArea.right() - start).toDouble() * ratio).toInt()
        for (x in start until end) {
            buf[x, row]
                .setSymbol(filledSymbol)
                .setStyle(filledStyle)
        }
        for (x in end until gaugeArea.right()) {
            buf[x, row]
                .setSymbol(unfilledSymbol)
                .setStyle(unfilledStyle)
        }
    }

    // Styled implementation
    override fun getStyle(): Style = widgetStyle

    override fun setStyle(style: Style): LineGauge = style(style)

    companion object {
        /**
         * Creates a default LineGauge widget.
         */
        fun default(): LineGauge = LineGauge()

        /**
         * Creates a new LineGauge widget.
         */
        fun new(): LineGauge = LineGauge()
    }
}
