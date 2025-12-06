package ratatui.widgets.barchart

import ratatui.buffer.Buffer
import ratatui.layout.Alignment
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.text.Line
import ratatui.widgets.Widget

/**
 * A group of bars to be shown by the [BarChart].
 *
 * # Examples
 *
 * ```kotlin
 * val group = BarGroup.new(listOf(Bar.withLabel("Red", 20), Bar.withLabel("Blue", 15)))
 * ```
 */
data class BarGroup(
    /** Label of the group. It will be printed centered under this group of bars */
    internal val label: Line? = null,
    /** List of bars to be shown */
    internal val bars: List<Bar> = emptyList()
) {

    /**
     * Set the group label.
     */
    fun label(label: String): BarGroup = copy(label = Line.from(label))

    /**
     * Set the group label.
     */
    fun label(label: Line): BarGroup = copy(label = label)

    /**
     * Set the bars of the group to be shown.
     */
    fun bars(bars: List<Bar>): BarGroup = copy(bars = bars)

    /**
     * Set the bars of the group to be shown.
     */
    fun bars(vararg bars: Bar): BarGroup = copy(bars = bars.toList())

    /**
     * The maximum bar value of this group.
     */
    internal fun max(): Long? = bars.maxOfOrNull { it.value }

    internal fun renderLabel(buf: Buffer, area: Rect, defaultLabelStyle: Style) {
        val labelLine = label ?: return
        // align the label. Necessary to do it this way as we don't want to set the style
        // of the whole area, just the label area
        val width = labelLine.width().coerceAtMost(area.width)
        val labelArea = when (labelLine.alignment) {
            Alignment.Center -> Rect(
                x = area.x + (area.width - width) / 2,
                y = area.y,
                width = width,
                height = area.height
            )
            Alignment.Right -> Rect(
                x = area.x + area.width - width,
                y = area.y,
                width = width,
                height = area.height
            )
            else -> Rect(
                x = area.x,
                y = area.y,
                width = width,
                height = area.height
            )
        }
        buf.setStyle(labelArea, defaultLabelStyle)
        labelLine.render(labelArea, buf)
    }

    companion object {
        /**
         * Creates a new BarGroup with the given bars.
         */
        fun new(bars: List<Bar>): BarGroup = BarGroup(bars = bars)

        /**
         * Creates a new BarGroup with the given bars.
         */
        fun new(vararg bars: Bar): BarGroup = BarGroup(bars = bars.toList())

        /**
         * Creates a new BarGroup with the given bars and label.
         */
        fun withLabel(label: String, bars: List<Bar>): BarGroup = BarGroup(
            label = Line.from(label),
            bars = bars
        )

        /**
         * Creates a new BarGroup with the given bars and label.
         */
        fun withLabel(label: Line, bars: List<Bar>): BarGroup = BarGroup(
            label = label,
            bars = bars
        )

        /**
         * Creates a BarGroup from a list of (label, value) pairs.
         */
        fun from(data: List<Pair<String, Long>>): BarGroup = BarGroup(
            bars = data.map { (label, value) -> Bar.withLabel(label, value) }
        )

        /**
         * Creates a BarGroup from vararg (label, value) pairs.
         */
        fun from(vararg data: Pair<String, Long>): BarGroup = from(data.toList())

        /**
         * Creates a default empty BarGroup.
         */
        fun default(): BarGroup = BarGroup()
    }
}
