package ratatui.widgets.chart

import ratatui.buffer.Buffer
import ratatui.layout.Alignment
import ratatui.layout.Constraint
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.line.Line as LineSymbols
import ratatui.text.Line
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome
import ratatui.widgets.canvas.Canvas
import ratatui.widgets.canvas.Line as CanvasLine
import ratatui.widgets.canvas.Points
import kotlin.math.max

/**
 * A container that holds all the infos about where to display each elements of the chart (axis,
 * labels, legend, ...).
 */
internal data class ChartLayout(
    /** Location of the title of the x axis */
    val titleX: Position? = null,
    /** Location of the title of the y axis */
    val titleY: Position? = null,
    /** Location of the first label of the x axis */
    val labelX: Int? = null,
    /** Location of the first label of the y axis */
    val labelY: Int? = null,
    /** Y coordinate of the horizontal axis */
    val axisX: Int? = null,
    /** X coordinate of the vertical axis */
    val axisY: Int? = null,
    /** Area of the legend */
    val legendArea: Rect? = null,
    /** Area of the graph */
    val graphArea: Rect = Rect.ZERO
)

/**
 * A widget to plot one or more [Dataset] in a cartesian coordinate system
 *
 * To use this widget, start by creating one or more [Dataset]. With it, you can set the
 * [data points][Dataset.data], the [name][Dataset.name] or the
 * [chart type][Dataset.graphType]. See [Dataset] for a complete documentation of what is
 * possible.
 *
 * Then, you'll usually want to configure the [Axis]. Axis [titles][Axis.title],
 * [bounds][Axis.bounds] and [labels][Axis.labels] can be configured on both axis. See [Axis]
 * for a complete documentation of what is possible.
 *
 * Finally, you can pass all of that to the `Chart` via [Chart.new], [Chart.xAxis] and
 * [Chart.yAxis].
 *
 * Additionally, `Chart` allows configuring the legend [position][Chart.legendPosition] and
 * [hiding constraints][Chart.hiddenLegendConstraints].
 *
 * # Examples
 *
 * ```kotlin
 * // Create the datasets to fill the chart with
 * val datasets = listOf(
 *     // Scatter chart
 *     Dataset.default()
 *         .name("data1")
 *         .marker(Marker.Dot)
 *         .graphType(GraphType.Scatter)
 *         .style(Style.default().cyan())
 *         .data(listOf(0.0 to 5.0, 1.0 to 6.0, 1.5 to 6.434)),
 *     // Line chart
 *     Dataset.default()
 *         .name("data2")
 *         .marker(Marker.Braille)
 *         .graphType(GraphType.Line)
 *         .style(Style.default().magenta())
 *         .data(listOf(4.0 to 5.0, 5.0 to 8.0, 7.66 to 13.5)),
 * )
 *
 * // Create the X axis and define its properties
 * val xAxis = Axis.default()
 *     .title("X Axis".red())
 *     .style(Style.default().white())
 *     .bounds(0.0 to 10.0)
 *     .labels("0.0", "5.0", "10.0")
 *
 * // Create the Y axis and define its properties
 * val yAxis = Axis.default()
 *     .title("Y Axis".red())
 *     .style(Style.default().white())
 *     .bounds(0.0 to 10.0)
 *     .labels("0.0", "5.0", "10.0")
 *
 * // Create the chart and link all the parts together
 * val chart = Chart.new(datasets)
 *     .block(Block.new().title("Chart"))
 *     .xAxis(xAxis)
 *     .yAxis(yAxis)
 * ```
 */
data class Chart(
    /** A block to display around the widget eventually */
    private val block: Block? = null,
    /** The horizontal axis */
    private val xAxisConfig: Axis = Axis.default(),
    /** The vertical axis */
    private val yAxisConfig: Axis = Axis.default(),
    /** A reference to the datasets */
    private val datasetsList: List<Dataset> = emptyList(),
    /** The widget base style */
    private val chartStyle: Style = Style.default(),
    /** Constraints used to determine whether the legend should be shown or not */
    private val hiddenLegendConstraintsConfig: Pair<Constraint, Constraint> =
        Pair(Constraint.Ratio(1u, 4u), Constraint.Ratio(1u, 4u)),
    /** The position determine where the legend is shown or hide regardless of constraints */
    private val legendPositionConfig: LegendPosition? = LegendPosition.default()
) : Widget, Styled<Chart> {

    /**
     * Wraps the chart with the given [Block]
     *
     * @param block The block to wrap the chart in
     * @return A new Chart with the block set
     */
    fun block(block: Block): Chart = copy(block = block)

    /**
     * Sets the style of the entire chart
     *
     * Styles of [Axis] and [Dataset] will have priority over this style.
     *
     * @param style The style to apply
     * @return A new Chart with the style set
     */
    fun style(style: Style): Chart = copy(chartStyle = style)

    /**
     * Sets the X [Axis]
     *
     * The default is an empty [Axis], i.e. only a line.
     *
     * @param axis The X axis
     * @return A new Chart with the X axis set
     */
    fun xAxis(axis: Axis): Chart = copy(xAxisConfig = axis)

    /**
     * Sets the Y [Axis]
     *
     * The default is an empty [Axis], i.e. only a line.
     *
     * @param axis The Y axis
     * @return A new Chart with the Y axis set
     */
    fun yAxis(axis: Axis): Chart = copy(yAxisConfig = axis)

    /**
     * Sets the constraints used to determine whether the legend should be shown or not.
     *
     * The tuple's first constraint is used for the width and the second for the height. If the
     * legend takes more space than what is allowed by any constraint, the legend is hidden.
     * [Constraint.Min] is an exception and will always show the legend.
     *
     * If this is not set, the default behavior is to hide the legend if it is greater than 25% of
     * the chart, either horizontally or vertically.
     *
     * @param constraints The width and height constraints
     * @return A new Chart with the hidden legend constraints set
     */
    fun hiddenLegendConstraints(constraints: Pair<Constraint, Constraint>): Chart =
        copy(hiddenLegendConstraintsConfig = constraints)

    /**
     * Sets the position of a legend or hide it
     *
     * The default is [LegendPosition.TopRight].
     *
     * If `null` is given, hide the legend even if [hiddenLegendConstraints] determines it
     * should be shown.
     *
     * @param position The legend position, or null to hide it
     * @return A new Chart with the legend position set
     */
    fun legendPosition(position: LegendPosition?): Chart = copy(legendPositionConfig = position)

    /**
     * Compute the internal layout of the chart given the area.
     */
    private fun layout(area: Rect): ChartLayout? {
        if (area.height == 0 || area.width == 0) {
            return null
        }

        var x = area.left()
        var y = area.bottom() - 1

        var labelX: Int? = null
        if (xAxisConfig.labelsList.isNotEmpty() && y > area.top()) {
            labelX = y
            y -= 1
        }

        val labelY: Int? = if (yAxisConfig.labelsList.isNotEmpty()) x else null
        x += maxWidthOfLabelsLeftOfYAxis(area, yAxisConfig.labelsList.isNotEmpty())

        var axisX: Int? = null
        if (xAxisConfig.labelsList.isNotEmpty() && y > area.top()) {
            axisX = y
            y -= 1
        }

        var axisY: Int? = null
        if (yAxisConfig.labelsList.isNotEmpty() && x + 1 < area.right()) {
            axisY = x
            x += 1
        }

        val graphWidth = (area.right() - x).coerceAtLeast(0)
        val graphHeight = (y - area.top() + 1).coerceAtLeast(0)
        val graphArea = Rect.new(x, area.top(), graphWidth, graphHeight)

        var titleX: Position? = null
        xAxisConfig.title?.let { title ->
            val w = title.width()
            if (w < graphArea.width && graphArea.height > 2) {
                titleX = Position(x + graphArea.width - w, y)
            }
        }

        var titleY: Position? = null
        yAxisConfig.title?.let { title ->
            val w = title.width()
            if (w + 1 < graphArea.width && graphArea.height > 2) {
                titleY = Position(x, area.top())
            }
        }

        var legendArea: Rect? = null
        legendPositionConfig?.let { legendPosition ->
            val legends = datasetsList.mapNotNull { it.datasetName?.width() }
            if (legends.isNotEmpty()) {
                val innerWidth = legends.maxOrNull() ?: 0
                val legendWidth = innerWidth + 2
                val legendHeight = legends.size + 2

                // Simplified constraint check - just check if it fits
                val maxLegendWidth = when (val c = hiddenLegendConstraintsConfig.first) {
                    is Constraint.Min -> Int.MAX_VALUE
                    is Constraint.Ratio -> (graphArea.width.toUInt() * c.numerator / c.denominator).toInt()
                    is Constraint.Percentage -> graphArea.width * c.value / 100
                    is Constraint.Length -> c.value
                    is Constraint.Max -> c.value
                    is Constraint.Fill -> graphArea.width
                }

                val maxLegendHeight = when (val c = hiddenLegendConstraintsConfig.second) {
                    is Constraint.Min -> Int.MAX_VALUE
                    is Constraint.Ratio -> (graphArea.height.toUInt() * c.numerator / c.denominator).toInt()
                    is Constraint.Percentage -> graphArea.height * c.value / 100
                    is Constraint.Length -> c.value
                    is Constraint.Max -> c.value
                    is Constraint.Fill -> graphArea.height
                }

                if (innerWidth > 0 && legendWidth <= maxLegendWidth && legendHeight <= maxLegendHeight) {
                    legendArea = legendPosition.layout(
                        graphArea,
                        legendWidth,
                        legendHeight,
                        titleX?.let { xAxisConfig.title?.width() } ?: 0,
                        titleY?.let { yAxisConfig.title?.width() } ?: 0
                    )
                }
            }
        }

        return ChartLayout(
            titleX = titleX,
            titleY = titleY,
            labelX = labelX,
            labelY = labelY,
            axisX = axisX,
            axisY = axisY,
            legendArea = legendArea,
            graphArea = graphArea
        )
    }

    private fun maxWidthOfLabelsLeftOfYAxis(area: Rect, hasYAxis: Boolean): Int {
        var maxWidth = yAxisConfig.labelsList.maxOfOrNull { it.width() } ?: 0

        xAxisConfig.labelsList.firstOrNull()?.let { firstXLabel ->
            val firstLabelWidth = firstXLabel.width()
            val widthLeftOfYAxis = when (xAxisConfig.labelsAlign) {
                Alignment.Left -> {
                    val yAxisOffset = if (hasYAxis) 1 else 0
                    (firstLabelWidth - yAxisOffset).coerceAtLeast(0)
                }
                Alignment.Center -> firstLabelWidth / 2
                Alignment.Right -> 0
            }
            maxWidth = max(maxWidth, widthLeftOfYAxis)
        }

        return maxWidth.coerceAtMost(area.width / 3)
    }

    private fun renderXLabels(buf: Buffer, layout: ChartLayout, chartArea: Rect, graphArea: Rect) {
        val y = layout.labelX ?: return
        val labels = xAxisConfig.labelsList
        val labelsLen = labels.size
        if (labelsLen < 2) return

        val widthBetweenTicks = graphArea.width / labelsLen

        val labelAlignment = when (xAxisConfig.labelsAlign) {
            Alignment.Left -> Alignment.Right
            Alignment.Center -> Alignment.Center
            Alignment.Right -> Alignment.Left
        }

        // First label
        val firstLabel = labels.first()
        val firstLabelArea = firstXLabelArea(y, firstLabel.width(), widthBetweenTicks, chartArea, graphArea)
        renderLabel(buf, firstLabel, firstLabelArea, labelAlignment)

        // Middle labels
        for ((i, label) in labels.subList(1, labels.size - 1).withIndex()) {
            val x = graphArea.left() + (i + 1) * widthBetweenTicks + 1
            val labelArea = Rect.new(x, y, (widthBetweenTicks - 1).coerceAtLeast(0), 1)
            renderLabel(buf, label, labelArea, Alignment.Center)
        }

        // Last label
        val lastLabelX = graphArea.right() - widthBetweenTicks
        val lastLabelArea = Rect.new(lastLabelX, y, widthBetweenTicks, 1)
        renderLabel(buf, labels.last(), lastLabelArea, Alignment.Right)
    }

    private fun firstXLabelArea(
        y: Int,
        labelWidth: Int,
        maxWidthAfterYAxis: Int,
        chartArea: Rect,
        graphArea: Rect
    ): Rect {
        val (minX, maxX) = when (xAxisConfig.labelsAlign) {
            Alignment.Left -> Pair(chartArea.left(), graphArea.left())
            Alignment.Center -> Pair(
                chartArea.left(),
                graphArea.left() + minOf(maxWidthAfterYAxis, labelWidth)
            )
            Alignment.Right -> Pair(
                (graphArea.left() - 1).coerceAtLeast(0),
                graphArea.left() + maxWidthAfterYAxis
            )
        }
        return Rect.new(minX, y, maxX - minX, 1)
    }

    private fun renderLabel(buf: Buffer, label: Line, labelArea: Rect, alignment: Alignment) {
        val alignedLabel = when (alignment) {
            Alignment.Left -> label.leftAligned()
            Alignment.Center -> label.centered()
            Alignment.Right -> label.rightAligned()
        }
        alignedLabel.render(labelArea, buf)
    }

    private fun renderYLabels(buf: Buffer, layout: ChartLayout, chartArea: Rect, graphArea: Rect) {
        val x = layout.labelY ?: return
        val labels = yAxisConfig.labelsList
        val labelsLen = labels.size

        for ((i, label) in labels.withIndex()) {
            val dy = i * (graphArea.height - 1) / (labelsLen - 1)
            if (dy < graphArea.bottom()) {
                val labelArea = Rect.new(
                    x,
                    (graphArea.bottom() - 1 - dy).coerceAtLeast(0),
                    (graphArea.left() - chartArea.left() - 1).coerceAtLeast(0),
                    1
                )
                renderLabel(buf, label, labelArea, yAxisConfig.labelsAlign)
            }
        }
    }

    override fun render(area: Rect, buf: Buffer) {
        buf.setStyle(area, chartStyle)

        block?.render(area, buf)
        val chartArea = block.innerIfSome(area)
        val layout = layout(chartArea) ?: return
        val graphArea = layout.graphArea

        renderXLabels(buf, layout, chartArea, graphArea)
        renderYLabels(buf, layout, chartArea, graphArea)

        // Render X axis line
        layout.axisX?.let { y ->
            for (x in graphArea.left() until graphArea.right()) {
                buf[x, y].setSymbol(LineSymbols.HORIZONTAL).setStyle(xAxisConfig.axisStyle)
            }
        }

        // Render Y axis line
        layout.axisY?.let { x ->
            for (y in graphArea.top() until graphArea.bottom()) {
                buf[x, y].setSymbol(LineSymbols.VERTICAL).setStyle(yAxisConfig.axisStyle)
            }
        }

        // Render axis intersection
        layout.axisX?.let { axisXY ->
            layout.axisY?.let { axisYX ->
                buf[axisYX, axisXY].setSymbol(LineSymbols.BOTTOM_LEFT).setStyle(xAxisConfig.axisStyle)
            }
        }

        // Render datasets using Canvas
        Canvas<(ratatui.widgets.canvas.Context) -> Unit>()
            .backgroundColor(chartStyle.bg ?: Color.Reset)
            .xBounds(xAxisConfig.axisBounds)
            .yBounds(yAxisConfig.axisBounds)
            .paint { ctx ->
                for (dataset in datasetsList) {
                    ctx.marker(dataset.datasetMarker)
                    val color = dataset.datasetStyle.fg ?: Color.Reset

                    // Draw points
                    ctx.draw(Points(
                        coords = dataset.dataPoints,
                        color = color
                    ))

                    // Draw lines or bars based on graph type
                    when (dataset.datasetGraphType) {
                        GraphType.Line -> {
                            dataset.dataPoints.windowed(2).forEach { window ->
                                val (p1, p2) = window
                                ctx.draw(CanvasLine(
                                    x1 = p1.first,
                                    y1 = p1.second,
                                    x2 = p2.first,
                                    y2 = p2.second,
                                    color = color
                                ))
                            }
                        }
                        GraphType.Bar -> {
                            for ((x, y) in dataset.dataPoints) {
                                ctx.draw(CanvasLine(
                                    x1 = x,
                                    y1 = 0.0,
                                    x2 = x,
                                    y2 = y,
                                    color = color
                                ))
                            }
                        }
                        GraphType.Scatter -> { /* Points already drawn */ }
                    }
                }
            }
            .render(graphArea, buf)

        // Render X axis title
        layout.titleX?.let { pos ->
            xAxisConfig.title?.let { title ->
                val width = (graphArea.right() - pos.x).coerceAtMost(title.width())
                buf.setLine(pos.x, pos.y, title, width)
            }
        }

        // Render Y axis title
        layout.titleY?.let { pos ->
            yAxisConfig.title?.let { title ->
                val width = (graphArea.right() - pos.x).coerceAtMost(title.width())
                buf.setLine(pos.x, pos.y, title, width)
            }
        }

        // Render legend
        layout.legendArea?.let { legendArea ->
            Block.bordered().render(legendArea, buf)

            datasetsList
                .filter { it.datasetName != null }
                .forEachIndexed { i, dataset ->
                    val name = dataset.datasetName!!.patchStyle(dataset.datasetStyle)
                    name.render(
                        Rect.new(
                            legendArea.x + 1,
                            legendArea.y + 1 + i,
                            legendArea.width - 2,
                            1
                        ),
                        buf
                    )
                }
        }
    }

    // Styled implementation
    override fun getStyle(): Style = chartStyle

    override fun setStyle(style: Style): Chart = style(style)

    companion object {
        /**
         * Creates a chart with the given [datasets][Dataset]
         *
         * A chart can render multiple datasets.
         *
         * @param datasets The list of datasets
         * @return A new Chart
         */
        fun new(datasets: List<Dataset>): Chart = Chart(datasetsList = datasets)

        /**
         * Creates a chart with the given [datasets][Dataset]
         *
         * @param datasets The datasets
         * @return A new Chart
         */
        fun new(vararg datasets: Dataset): Chart = Chart(datasetsList = datasets.toList())

        /**
         * Creates an empty default chart.
         */
        fun default(): Chart = Chart()
    }
}
