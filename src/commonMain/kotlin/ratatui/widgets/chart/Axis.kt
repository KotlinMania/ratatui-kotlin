package ratatui.widgets.chart

import ratatui.layout.Alignment
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Line

/**
 * An X or Y axis for the [Chart] widget
 *
 * An axis can have a [title] which will be displayed at the end of the axis. For an
 * X axis this is the right, for a Y axis, this is the top.
 *
 * You can also set the bounds and labels on this axis using respectively [Axis.bounds] and
 * [Axis.labels].
 *
 * See [Chart.xAxis] and [Chart.yAxis] to set an axis on a chart.
 *
 * # Example
 *
 * ```kotlin
 * val axis = Axis.default()
 *     .title("X Axis")
 *     .style(Style.default().gray())
 *     .bounds(0.0 to 50.0)
 *     .labels(listOf("0".bold(), "25".into(), "50".bold()))
 * ```
 */
data class Axis(
    /** Title displayed next to axis end */
    internal val title: Line? = null,
    /** Bounds for the axis (all data points outside these limits will not be represented) */
    internal val axisBounds: DoubleArray = doubleArrayOf(0.0, 0.0),
    /** A list of labels to put to the left or below the axis */
    internal val labelsList: List<Line> = emptyList(),
    /** The style used to draw the axis itself */
    internal val axisStyle: Style = Style.default(),
    /** The alignment of the labels of the Axis */
    internal val labelsAlign: Alignment = Alignment.Left
) : Styled<Axis> {

    /**
     * Sets the axis title
     *
     * It will be displayed at the end of the axis. For an X axis this is the right, for a Y axis,
     * this is the top.
     *
     * @param title The title text
     * @return A new Axis with the title set
     */
    fun title(title: String): Axis = copy(title = Line.from(title))

    /**
     * Sets the axis title
     *
     * It will be displayed at the end of the axis. For an X axis this is the right, for a Y axis,
     * this is the top.
     *
     * @param title The title as a Line
     * @return A new Axis with the title set
     */
    fun title(title: Line): Axis = copy(title = title)

    /**
     * Sets the bounds of this axis
     *
     * In other words, sets the min and max value on this axis.
     *
     * @param bounds The bounds as a DoubleArray [min, max]
     * @return A new Axis with the bounds set
     */
    fun bounds(bounds: DoubleArray): Axis = copy(axisBounds = bounds)

    /**
     * Sets the bounds of this axis
     *
     * In other words, sets the min and max value on this axis.
     *
     * @param bounds The bounds as a Pair (min, max)
     * @return A new Axis with the bounds set
     */
    fun bounds(bounds: Pair<Double, Double>): Axis = copy(axisBounds = doubleArrayOf(bounds.first, bounds.second))

    /**
     * Sets the bounds of this axis
     *
     * In other words, sets the min and max value on this axis.
     *
     * @param min The minimum bound
     * @param max The maximum bound
     * @return A new Axis with the bounds set
     */
    fun bounds(min: Double, max: Double): Axis = copy(axisBounds = doubleArrayOf(min, max))

    /**
     * Sets the axis labels
     *
     * - For the X axis, the labels are displayed left to right.
     * - For the Y axis, the labels are displayed bottom to top.
     *
     * @param labels The list of labels
     * @return A new Axis with the labels set
     */
    fun labels(labels: List<Line>): Axis = copy(labelsList = labels)

    /**
     * Sets the axis labels from strings
     *
     * @param labels The list of label strings
     * @return A new Axis with the labels set
     */
    fun labelsFromStrings(labels: List<String>): Axis = copy(labelsList = labels.map { Line.from(it) })

    /**
     * Sets the axis labels from vararg strings
     *
     * @param labels The label strings
     * @return A new Axis with the labels set
     */
    fun labels(vararg labels: String): Axis = copy(labelsList = labels.map { Line.from(it) })

    /**
     * Sets the axis style
     *
     * @param style The style to apply
     * @return A new Axis with the style set
     */
    fun style(style: Style): Axis = copy(axisStyle = style)

    /**
     * Sets the labels alignment of the axis
     *
     * The alignment behaves differently based on the axis:
     * - Y axis: The labels are aligned within the area on the left of the axis
     * - X axis: The first X-axis label is aligned relative to the Y-axis
     *
     * On the X axis, this parameter only affects the first label.
     *
     * @param alignment The alignment for labels
     * @return A new Axis with the labels alignment set
     */
    fun labelsAlignment(alignment: Alignment): Axis = copy(labelsAlign = alignment)

    // Styled implementation
    override fun getStyle(): Style = axisStyle

    override fun setStyle(style: Style): Axis = style(style)

    // equals/hashCode need to handle the DoubleArray properly
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Axis) return false

        if (title != other.title) return false
        if (!axisBounds.contentEquals(other.axisBounds)) return false
        if (labelsList != other.labelsList) return false
        if (axisStyle != other.axisStyle) return false
        if (labelsAlign != other.labelsAlign) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title?.hashCode() ?: 0
        result = 31 * result + axisBounds.contentHashCode()
        result = 31 * result + labelsList.hashCode()
        result = 31 * result + axisStyle.hashCode()
        result = 31 * result + labelsAlign.hashCode()
        return result
    }

    companion object {
        /**
         * Creates a default Axis.
         */
        fun default(): Axis = Axis()

        /**
         * Creates a new Axis (alias for [default]).
         */
        fun new(): Axis = Axis()
    }
}
