package ratatui.widgets.barchart

import ratatui.buffer.Buffer
import ratatui.layout.Direction
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.bar.NINE_LEVELS
import ratatui.symbols.bar.Set as BarSet
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome

/**
 * A chart showing values as bars.
 *
 * Here is a possible `BarChart` output:
 * ```
 * ┌─────────────────────────────────┐
 * │                             ████│
 * │                        ▅▅▅▅ ████│
 * │            ▇▇▇▇        ████ ████│
 * │     ▄▄▄▄   ████ ████   ████ ████│
 * │▆10▆ █20█   █50█ █40█   █60█ █90█│
 * │ B1   B2     B1   B2     B1   B2 │
 * │ Group1      Group2      Group3  │
 * └─────────────────────────────────┘
 * ```
 *
 * A `BarChart` is composed of a set of [Bar] which can be set via [BarChart.data].
 * Bars can be styled globally ([BarChart.barStyle]) or individually ([Bar.style]).
 *
 * The `BarChart` widget can also show groups of bars via [BarGroup].
 * A [BarGroup] is a set of [Bar], multiple can be added to a `BarChart` using
 * [BarChart.data] multiple times.
 *
 * The chart can have a [Direction] (by default the bars are Vertical).
 * This is set using [BarChart.direction].
 *
 * # Examples
 *
 * ```kotlin
 * BarChart.default()
 *     .block(Block.bordered().title("BarChart"))
 *     .barWidth(3)
 *     .barGap(1)
 *     .groupGap(3)
 *     .barStyle(Style.new().yellow().onRed())
 *     .valueStyle(Style.new().red().bold())
 *     .labelStyle(Style.new().white())
 *     .data(BarGroup.from("A0" to 0L, "A1" to 2L, "A2" to 4L, "A3" to 3L))
 *     .data(BarGroup.new(
 *         Bar.withLabel("B0", 10),
 *         Bar.withLabel("B2", 20)
 *     ))
 *     .max(4)
 * ```
 */
data class BarChart(
    /** Block to wrap the widget in */
    private val block: Block? = null,
    /** The width of each bar */
    private val barWidth: Int = 1,
    /** The gap between each bar */
    private val barGap: Int = 1,
    /** The gap between each group */
    private val groupGap: Int = 0,
    /** Set of symbols used to display the data */
    private val barSet: BarSet = NINE_LEVELS,
    /** Style of the bars */
    private val barStyle: Style = Style.default(),
    /** Style of the values printed at the bottom of each bar */
    private val valueStyle: Style = Style.default(),
    /** Style of the labels printed under each bar */
    private val labelStyle: Style = Style.default(),
    /** Style for the widget */
    private val chartStyle: Style = Style.default(),
    /** Vector of groups containing bars */
    private val data: MutableList<BarGroup> = mutableListOf(),
    /** Value necessary for a bar to reach the maximum height */
    private val max: Long? = null,
    /** Direction of the bars */
    private val direction: Direction = Direction.Vertical
) : Widget, Styled<BarChart> {

    /**
     * Add group of bars to the BarChart.
     */
    fun data(group: BarGroup): BarChart {
        if (group.bars.isNotEmpty()) {
            val newData = data.toMutableList()
            newData.add(group)
            return copy(data = newData)
        }
        return this
    }

    /**
     * Surround the BarChart with a [Block].
     */
    fun block(block: Block): BarChart = copy(block = block)

    /**
     * Set the value necessary for a [Bar] to reach the maximum height.
     *
     * If not set, the maximum value in the data is taken as reference.
     */
    fun max(max: Long): BarChart = copy(max = max)

    /**
     * Set the default style of the bar.
     *
     * It is also possible to set individually the style of each [Bar].
     * In this case the default style will be patched by the individual style.
     */
    fun barStyle(style: Style): BarChart = copy(barStyle = style)

    /**
     * Set the width of the displayed bars.
     *
     * For Horizontal bars this becomes the height of the bar.
     * If not set, this defaults to 1.
     */
    fun barWidth(width: Int): BarChart = copy(barWidth = width)

    /**
     * Set the gap between each bar.
     *
     * If not set, this defaults to 1.
     */
    fun barGap(gap: Int): BarChart = copy(barGap = gap)

    /**
     * The [BarSet] to use for displaying the bars.
     *
     * If not set, the default is [NINE_LEVELS].
     */
    fun barSet(barSet: BarSet): BarChart = copy(barSet = barSet)

    /**
     * Set the default value style of the bar.
     *
     * It is also possible to set individually the value style of each [Bar].
     */
    fun valueStyle(style: Style): BarChart = copy(valueStyle = style)

    /**
     * Set the default label style of the groups and bars.
     *
     * It is also possible to set individually the label style of each [Bar] or [BarGroup].
     */
    fun labelStyle(style: Style): BarChart = copy(labelStyle = style)

    /**
     * Set the gap between [BarGroup]s.
     */
    fun groupGap(gap: Int): BarChart = copy(groupGap = gap)

    /**
     * Set the style of the entire chart.
     *
     * The style will be applied to everything that isn't styled (borders, bars, labels, ...).
     */
    fun style(style: Style): BarChart = copy(chartStyle = style)

    /**
     * Set the direction of the bars.
     *
     * Vertical bars are the default.
     */
    fun direction(direction: Direction): BarChart = copy(direction = direction)

    // Widget implementation
    override fun render(area: Rect, buf: Buffer) {
        buf.setStyle(area, chartStyle)

        block?.render(area, buf)
        val inner = block.innerIfSome(area)

        if (inner.isEmpty() || data.isEmpty() || barWidth == 0) {
            return
        }

        when (direction) {
            Direction.Horizontal -> renderHorizontal(buf, inner)
            Direction.Vertical -> renderVertical(buf, inner)
        }
    }

    /**
     * Returns the visible bars length in ticks. A cell contains 8 ticks.
     */
    private fun groupTicks(availableSpace: Int, barMaxLength: Int): List<List<Long>> {
        val maxValue = maximumDataValue()
        var space = availableSpace
        val result = mutableListOf<List<Long>>()

        for (group in data) {
            if (space == 0) break

            val nBars = group.bars.size
            val groupWidth = nBars * barWidth + (nBars - 1).coerceAtLeast(0) * barGap

            val barsToRender = if (space > groupWidth) {
                space = (space - groupWidth - groupGap - barGap).coerceAtLeast(0)
                nBars
            } else {
                val maxBars = (space + barGap) / (barWidth + barGap)
                if (maxBars > 0) {
                    space = 0
                    maxBars
                } else {
                    continue
                }
            }

            val ticks = group.bars.take(barsToRender).map { bar ->
                bar.value * barMaxLength * 8 / maxValue
            }
            result.add(ticks)
        }

        return result
    }

    /**
     * Get label information.
     */
    private fun labelInfo(availableHeight: Int): LabelInfo {
        if (availableHeight == 0) {
            return LabelInfo(groupLabelVisible = false, barLabelVisible = false, height = 0)
        }

        val barLabelVisible = data.any { group -> group.bars.any { it.label != null } }

        if (availableHeight == 1 && barLabelVisible) {
            return LabelInfo(groupLabelVisible = false, barLabelVisible = true, height = 1)
        }

        val groupLabelVisible = data.any { it.label != null }
        val height = (if (groupLabelVisible) 1 else 0) + (if (barLabelVisible) 1 else 0)
        return LabelInfo(groupLabelVisible = groupLabelVisible, barLabelVisible = barLabelVisible, height = height)
    }

    private fun renderHorizontal(buf: Buffer, area: Rect) {
        // get the longest label
        val labelSize = data
            .flatMap { it.bars }
            .mapNotNull { it.label }
            .maxOfOrNull { it.width() } ?: 0

        val labelX = area.x
        val margin = if (labelSize != 0) 1 else 0
        val barsArea = Rect(
            x = area.x + labelSize + margin,
            y = area.y,
            width = (area.width - labelSize - margin).coerceAtLeast(0),
            height = area.height
        )

        val groupTicks = groupTicks(barsArea.height, barsArea.width)

        // print all visible bars, label and values
        var barY = barsArea.top()
        for ((ticksVec, group) in groupTicks.zip(data)) {
            for ((ticks, bar) in ticksVec.zip(group.bars)) {
                val barLength = (ticks / 8).toInt()
                val patchedBarStyle = barStyle.patch(bar.barStyle)

                for (y in 0 until barWidth) {
                    val currentBarY = barY + y
                    for (x in 0 until barsArea.width) {
                        val symbol = if (x < barLength) barSet.full else barSet.empty
                        buf[barsArea.left() + x, currentBarY]
                            .setSymbol(symbol)
                            .setStyle(patchedBarStyle)
                    }
                }

                val barValueArea = Rect(
                    x = barsArea.x,
                    y = barY + (barWidth / 2),
                    width = barsArea.width,
                    height = 1
                )

                // label
                bar.label?.let { label ->
                    buf.setLine(labelX, barValueArea.top(), label, labelSize)
                }

                bar.renderValueWithDifferentStyles(
                    buf,
                    barValueArea,
                    barLength,
                    valueStyle,
                    barStyle
                )

                barY += barGap + barWidth
            }

            // if group_gap is zero, then there is no place to print the group label
            // check also if the group label is still inside the visible area
            val labelY = barY - barGap
            if (groupGap > 0 && labelY < barsArea.bottom()) {
                val labelRect = Rect(
                    x = barsArea.x,
                    y = labelY,
                    width = barsArea.width,
                    height = 1
                )
                group.renderLabel(buf, labelRect, labelStyle)
                barY += groupGap
            }
        }
    }

    private fun renderVertical(buf: Buffer, area: Rect) {
        val labelInfoData = labelInfo(area.height - 1)

        val barsArea = Rect(
            x = area.x,
            y = area.y,
            width = area.width,
            height = (area.height - labelInfoData.height).coerceAtLeast(0)
        )

        val groupTicks = groupTicks(barsArea.width, barsArea.height)
        renderVerticalBars(barsArea, buf, groupTicks)
        renderLabelsAndValues(area, buf, labelInfoData, groupTicks)
    }

    private fun renderVerticalBars(area: Rect, buf: Buffer, groupTicks: List<List<Long>>) {
        // print all visible bars (without labels and values)
        var barX = area.left()
        for ((ticksVec, group) in groupTicks.zip(data)) {
            for ((ticksValue, bar) in ticksVec.zip(group.bars)) {
                var ticks = ticksValue
                for (j in (0 until area.height).reversed()) {
                    val symbol = when (ticks) {
                        0L -> barSet.empty
                        1L -> barSet.oneEighth
                        2L -> barSet.oneQuarter
                        3L -> barSet.threeEighths
                        4L -> barSet.half
                        5L -> barSet.fiveEighths
                        6L -> barSet.threeQuarters
                        7L -> barSet.sevenEighths
                        else -> barSet.full
                    }

                    val patchedBarStyle = barStyle.patch(bar.barStyle)

                    for (x in 0 until barWidth) {
                        buf[barX + x, area.top() + j]
                            .setSymbol(symbol)
                            .setStyle(patchedBarStyle)
                    }

                    ticks = (ticks - 8).coerceAtLeast(0)
                }
                barX += barGap + barWidth
            }
            barX += groupGap
        }
    }

    /**
     * Get the maximum data value. The returned value is always greater or equal to 1.
     */
    private fun maximumDataValue(): Long {
        return (max ?: data.mapNotNull { it.max() }.maxOrNull() ?: 0L).coerceAtLeast(1L)
    }

    private fun renderLabelsAndValues(
        area: Rect,
        buf: Buffer,
        labelInfo: LabelInfo,
        groupTicks: List<List<Long>>
    ) {
        // print labels and values in one go
        var barX = area.left()
        val barY = area.bottom() - labelInfo.height - 1
        for ((group, ticksVec) in data.zip(groupTicks)) {
            if (group.bars.isEmpty()) {
                continue
            }
            // print group labels under the bars or the previous labels
            if (labelInfo.groupLabelVisible) {
                val labelMaxWidth = ticksVec.size * (barWidth + barGap) - barGap
                val groupArea = Rect(
                    x = barX,
                    y = area.bottom() - 1,
                    width = labelMaxWidth,
                    height = 1
                )
                group.renderLabel(buf, groupArea, labelStyle)
            }

            // print the bar values and numbers
            for ((bar, ticks) in group.bars.zip(ticksVec)) {
                if (labelInfo.barLabelVisible) {
                    bar.renderLabel(buf, barWidth, barX, barY + 1, labelStyle)
                }

                bar.renderValue(buf, barWidth, barX, barY, valueStyle, ticks)

                barX += barGap + barWidth
            }
            barX += groupGap
        }
    }

    // Styled implementation
    override fun getStyle(): Style = chartStyle

    override fun setStyle(style: Style): BarChart = style(style)

    companion object {
        /**
         * Creates a new vertical BarChart widget with the given bars.
         */
        fun new(bars: List<Bar>): BarChart = BarChart(
            data = mutableListOf(BarGroup.new(bars)),
            direction = Direction.Vertical
        )

        /**
         * Creates a new vertical BarChart widget with the given bars.
         */
        fun new(vararg bars: Bar): BarChart = new(bars.toList())

        /**
         * Creates a new BarChart widget with a vertical direction.
         */
        fun vertical(bars: List<Bar>): BarChart = new(bars)

        /**
         * Creates a new BarChart widget with a horizontal direction.
         */
        fun horizontal(bars: List<Bar>): BarChart = BarChart(
            data = mutableListOf(BarGroup.new(bars)),
            direction = Direction.Horizontal
        )

        /**
         * Creates a new BarChart widget with a group of bars.
         */
        fun grouped(groups: List<BarGroup>): BarChart = BarChart(
            data = groups.toMutableList()
        )

        /**
         * Creates a default BarChart.
         */
        fun default(): BarChart = BarChart()
    }
}

/**
 * Internal data class for label layout information.
 */
private data class LabelInfo(
    val groupLabelVisible: Boolean,
    val barLabelVisible: Boolean,
    val height: Int
)
