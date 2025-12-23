package ratatui.widgets.sparkline

/**
 * Defines the direction in which sparkline will be rendered.
 *
 * See [Sparkline.direction].
 */
enum class RenderDirection {
    /** The first value is on the left, going to the right */
    LeftToRight,
    /** The first value is on the right, going to the left */
    RightToLeft;

    /**
     * Returns the string representation of this direction.
     */
    override fun toString(): String = when (this) {
        LeftToRight -> "LeftToRight"
        RightToLeft -> "RightToLeft"
    }

    companion object {
        /**
         * Parses a string into a [RenderDirection].
         *
         * Accepts various formats: "LeftToRight", "left-to-right", "left_to_right", "ltr"
         *
         * @param value The string to parse
         * @return The parsed direction, or null if parsing fails
         */
        fun fromString(value: String): RenderDirection? {
            return when (value.lowercase().replace("-", "").replace("_", "")) {
                "lefttoright", "ltr" -> LeftToRight
                "righttoleft", "rtl" -> RightToLeft
                else -> null
            }
        }

        /**
         * Parses a string into a [RenderDirection], throwing if parsing fails.
         *
         * @param value The string to parse
         * @return The parsed direction
         * @throws IllegalArgumentException if parsing fails
         */
        fun parse(value: String): RenderDirection {
            return fromString(value)
                ?: throw IllegalArgumentException("Invalid RenderDirection: $value")
        }
    }
}
