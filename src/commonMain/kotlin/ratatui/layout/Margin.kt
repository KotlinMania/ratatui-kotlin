/**
 * Represents spacing around rectangular areas.
 *
 * [Margin] defines the horizontal and vertical spacing that should be applied around a rectangular
 * area. It's commonly used with [Layout] to add space between the
 * layout's boundaries and its contents, or with [Rect.inner] and
 * [Rect.outer] to create padded areas.
 *
 * The margin values represent the number of character cells to add on each side. For horizontal
 * margin, the space is applied to both the left and right sides. For vertical margin, the space
 * is applied to both the top and bottom sides.
 *
 * ## Construction
 *
 * - [new] - Create a new margin with horizontal and vertical spacing
 * - Default constructor - Create with zero margin
 *
 * ## Examples
 *
 * ```kotlin
 * // Create a margin of 2 cells horizontally and 1 cell vertically
 * val margin = Margin.new(2, 1)
 *
 * // Apply directly to a rectangle
 * val area = Rect.new(0, 0, 80, 24)
 * val innerArea = area.inner(margin)
 *
 * // Or use with a layout (which only accepts uniform margins)
 * val layout = Layout.vertical(listOf(Constraint.Fill(1))).margin(2)
 * ```
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
package ratatui.layout


/**
 * Represents spacing around rectangular areas.
 *
 * @property horizontal The horizontal spacing in cells (applied to left and right)
 * @property vertical The vertical spacing in cells (applied to top and bottom)
 */
data class Margin(
    val horizontal: Int,
    val vertical: Int
) {

    companion object {
        /** A zero margin */
        val ZERO: Margin = Margin(0, 0)

        /** Create a new margin */
        fun new(horizontal: Int, vertical: Int): Margin = Margin(horizontal, vertical)
    }

    override fun toString(): String = "${horizontal}x${vertical}"
}
