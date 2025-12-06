package ratatui.widgets.block

/**
 * Defines the padding for a [Block].
 *
 * See the [Block.padding] method to configure its padding.
 *
 * This concept is similar to [CSS padding](https://developer.mozilla.org/en-US/docs/Web/CSS/padding).
 *
 * **NOTE**: Terminal cells are often taller than they are wide, so to make horizontal and vertical
 * padding seem equal, doubling the horizontal padding is usually pretty good.
 *
 * # Example
 *
 * ```kotlin
 * Padding.uniform(1)
 * Padding.horizontal(2)
 * Padding.left(3)
 * Padding.proportional(4)
 * Padding.symmetric(5, 6)
 * ```
 */
data class Padding(
    /** Left padding */
    val left: Int,
    /** Right padding */
    val right: Int,
    /** Top padding */
    val top: Int,
    /** Bottom padding */
    val bottom: Int
) {
    companion object {
        /** Padding with all fields set to 0 */
        val ZERO = Padding(0, 0, 0, 0)

        /**
         * Creates a new Padding by specifying every field individually.
         *
         * Note: the order of the fields does not match the order of the CSS properties.
         */
        fun new(left: Int, right: Int, top: Int, bottom: Int): Padding =
            Padding(left, right, top, bottom)

        /**
         * Creates a Padding with all fields set to 0.
         */
        @Deprecated("use Padding.ZERO instead", ReplaceWith("Padding.ZERO"))
        fun zero(): Padding = ZERO

        /**
         * Creates a Padding with the same value for left and right.
         */
        fun horizontal(value: Int): Padding = Padding(
            left = value,
            right = value,
            top = 0,
            bottom = 0
        )

        /**
         * Creates a Padding with the same value for top and bottom.
         */
        fun vertical(value: Int): Padding = Padding(
            left = 0,
            right = 0,
            top = value,
            bottom = value
        )

        /**
         * Creates a Padding with the same value for all fields.
         */
        fun uniform(value: Int): Padding = Padding(
            left = value,
            right = value,
            top = value,
            bottom = value
        )

        /**
         * Creates a Padding that is visually proportional to the terminal.
         *
         * This represents a padding of 2x the value for left and right and 1x the value for
         * top and bottom.
         */
        fun proportional(value: Int): Padding = Padding(
            left = 2 * value,
            right = 2 * value,
            top = value,
            bottom = value
        )

        /**
         * Creates a Padding that is symmetric.
         *
         * The x value is used for left and right and the y value is used for top and bottom.
         */
        fun symmetric(x: Int, y: Int): Padding = Padding(
            left = x,
            right = x,
            top = y,
            bottom = y
        )

        /**
         * Creates a Padding that only sets the left padding.
         */
        fun left(value: Int): Padding = Padding(
            left = value,
            right = 0,
            top = 0,
            bottom = 0
        )

        /**
         * Creates a Padding that only sets the right padding.
         */
        fun right(value: Int): Padding = Padding(
            left = 0,
            right = value,
            top = 0,
            bottom = 0
        )

        /**
         * Creates a Padding that only sets the top padding.
         */
        fun top(value: Int): Padding = Padding(
            left = 0,
            right = 0,
            top = value,
            bottom = 0
        )

        /**
         * Creates a Padding that only sets the bottom padding.
         */
        fun bottom(value: Int): Padding = Padding(
            left = 0,
            right = 0,
            top = 0,
            bottom = value
        )
    }
}
