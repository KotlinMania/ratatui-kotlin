package ratatui.widgets.sparkline

import ratatui.style.Style

/**
 * A bar in a [Sparkline].
 *
 * The height of the bar is determined by the value and a value of `null` is interpreted as the
 * _absence_ of a value, as distinct from a value of `0`.
 */
data class SparklineBar(
    /** The value of the bar. If `null`, the bar is absent. */
    val value: ULong?,
    /** The style of the bar. If `null`, the bar will use the style of the sparkline. */
    val barStyle: Style? = null
) {
    /**
     * Sets the style of the bar.
     *
     * If not set, the default style of the sparkline will be used.
     *
     * As well as the style of the sparkline, each [SparklineBar] may optionally set its own
     * style. If set, the style of the bar will be the style of the sparkline combined with
     * the style of the bar.
     *
     * @param style The style to apply to the bar
     * @return A new SparklineBar with the style set
     */
    fun style(style: Style?): SparklineBar = copy(barStyle = style)

    companion object {
        /**
         * Creates a SparklineBar from a ULong value.
         */
        fun from(value: ULong): SparklineBar = SparklineBar(value = value)

        /**
         * Creates a SparklineBar from an optional ULong value.
         */
        fun from(value: ULong?): SparklineBar = SparklineBar(value = value)

        /**
         * Creates a SparklineBar from an Int value.
         */
        fun from(value: Int): SparklineBar = SparklineBar(value = value.toULong())

        /**
         * Creates a SparklineBar from an optional Int value.
         */
        fun fromOptional(value: Int?): SparklineBar = SparklineBar(value = value?.toULong())

        /**
         * Creates an absent (empty) bar.
         */
        fun absent(): SparklineBar = SparklineBar(value = null)
    }
}
