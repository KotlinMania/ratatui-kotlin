package ratatui.widgets.chart

import ratatui.style.Style
import ratatui.style.Styled
import ratatui.symbols.Marker
import ratatui.text.Line

/**
 * A group of data points
 *
 * This is the main element composing a [Chart].
 *
 * A dataset can be [named][Dataset.name]. Only named datasets will be rendered in the legend.
 *
 * After that, you can pass it data with [Dataset.data]. Data is a list of `Pair<Double, Double>`,
 * the first element being X and the second Y. It's also worth noting that, unlike
 * the [Rect][ratatui.layout.Rect], here the Y axis is bottom to top, as in math.
 *
 * You can also customize the rendering by using [Dataset.marker] and [Dataset.graphType].
 *
 * # Example
 *
 * This example draws a red line between two points.
 *
 * ```kotlin
 * val dataset = Dataset.default()
 *     .name("dataset 1")
 *     .data(listOf(1.0 to 1.0, 5.0 to 5.0))
 *     .marker(Marker.Braille)
 *     .graphType(GraphType.Line)
 *     .red()
 * ```
 */
data class Dataset(
    /** Name of the dataset (used in the legend if shown) */
    internal val datasetName: Line? = null,
    /** A reference to the actual data */
    internal val dataPoints: List<Pair<Double, Double>> = emptyList(),
    /** Symbol used for each points of this dataset */
    internal val datasetMarker: Marker = Marker.Dot,
    /** Determines graph type used for drawing points */
    internal val datasetGraphType: GraphType = GraphType.Scatter,
    /** Style used to plot this dataset */
    internal val datasetStyle: Style = Style.default()
) : Styled<Dataset> {

    /**
     * Sets the name of the dataset
     *
     * The dataset's name is used when displaying the chart legend. Datasets don't require a name
     * and can be created without specifying one. Once assigned, a name can't be removed, only
     * changed.
     *
     * The name can be styled (see [Line] for that), but the dataset's style will always have
     * precedence.
     *
     * @param name The name as a string
     * @return A new Dataset with the name set
     */
    fun name(name: String): Dataset = copy(datasetName = Line.from(name))

    /**
     * Sets the name of the dataset
     *
     * @param name The name as a Line
     * @return A new Dataset with the name set
     */
    fun name(name: Line): Dataset = copy(datasetName = name)

    /**
     * Sets the data points of this dataset
     *
     * Points will then either be rendered as scattered points or with lines between them
     * depending on [Dataset.graphType].
     *
     * Data consist in a list of `Pair<Double, Double>`, the first element being X and the
     * second Y. It's also worth noting that, unlike the [Rect][ratatui.layout.Rect], here the
     * Y axis is bottom to top, as in math.
     *
     * @param data The list of data points as pairs
     * @return A new Dataset with the data set
     */
    fun data(data: List<Pair<Double, Double>>): Dataset = copy(dataPoints = data)

    /**
     * Sets the data points of this dataset from vararg pairs
     *
     * @param data The data points
     * @return A new Dataset with the data set
     */
    fun data(vararg data: Pair<Double, Double>): Dataset = copy(dataPoints = data.toList())

    /**
     * Sets the kind of character to use to display this dataset
     *
     * You can use dots (`•`), blocks (`█`), bars (`▄`), braille (`⠓`, `⣇`, `⣿`) or half-blocks
     * (`█`, `▄`, and `▀`). See [Marker] for more details.
     *
     * Note [Marker.Braille] requires a font that supports Unicode Braille Patterns.
     *
     * @param marker The marker to use
     * @return A new Dataset with the marker set
     */
    fun marker(marker: Marker): Dataset = copy(datasetMarker = marker)

    /**
     * Sets how the dataset should be drawn
     *
     * [Chart] can draw [scatter][GraphType.Scatter], [line][GraphType.Line] or
     * [bar][GraphType.Bar] charts. A scatter chart draws only the points in the dataset, a line
     * chart draws a line between each point, and a bar chart draws a line from the x axis to the
     * point. See [GraphType] for more details.
     *
     * @param graphType The graph type to use
     * @return A new Dataset with the graph type set
     */
    fun graphType(graphType: GraphType): Dataset = copy(datasetGraphType = graphType)

    /**
     * Sets the style of this dataset
     *
     * The given style will be used to draw the legend and the data points. Currently the legend
     * will use the entire style whereas the data points will only use the foreground.
     *
     * @param style The style to apply
     * @return A new Dataset with the style set
     */
    fun style(style: Style): Dataset = copy(datasetStyle = style)

    // Styled implementation
    override fun getStyle(): Style = datasetStyle

    override fun setStyle(style: Style): Dataset = style(style)

    companion object {
        /**
         * Creates a default Dataset.
         */
        fun default(): Dataset = Dataset()

        /**
         * Creates a new Dataset (alias for [default]).
         */
        fun new(): Dataset = Dataset()
    }
}
