package ratatui.widgets.chart

/**
 * Used to determine which style of graphing to use
 */
enum class GraphType {
    /**
     * Draw each point. This is the default.
     */
    Scatter,

    /**
     * Draw a line between each following point.
     *
     * The order of the lines will be the same as the order of the points in the dataset, which
     * allows this widget to draw lines both left-to-right and right-to-left
     */
    Line,

    /**
     * Draw a bar chart. This will draw a bar for each point in the dataset.
     */
    Bar;

    /**
     * Returns the string representation of this graph type.
     */
    override fun toString(): String = when (this) {
        Scatter -> "Scatter"
        Line -> "Line"
        Bar -> "Bar"
    }

    companion object {
        /**
         * Parses a string into a [GraphType].
         *
         * Accepts various formats: "Scatter", "scatter", "SCATTER"
         *
         * @param value The string to parse
         * @return The parsed graph type, or null if parsing fails
         */
        fun fromString(value: String): GraphType? {
            return when (value.lowercase()) {
                "scatter" -> Scatter
                "line" -> Line
                "bar" -> Bar
                else -> null
            }
        }

        /**
         * Parses a string into a [GraphType], throwing if parsing fails.
         *
         * @param value The string to parse
         * @return The parsed graph type
         * @throws IllegalArgumentException if parsing fails
         */
        fun parse(value: String): GraphType {
            return fromString(value)
                ?: throw IllegalArgumentException("Invalid GraphType: $value")
        }
    }
}
