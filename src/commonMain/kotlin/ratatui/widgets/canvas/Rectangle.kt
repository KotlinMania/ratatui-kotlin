package ratatui.widgets.canvas

import ratatui.style.Color

/**
 * A rectangle to draw on a [Canvas].
 *
 * Sizes used here are **not** in terminal cell. This is much more similar to the
 * mathematic coordinate system.
 *
 * @property x The x position of the rectangle. The rectangle is positioned from its bottom left corner.
 * @property y The y position of the rectangle. The rectangle is positioned from its bottom left corner.
 * @property width The width of the rectangle.
 * @property height The height of the rectangle.
 * @property color The color of the rectangle.
 */
data class Rectangle(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val width: Double = 0.0,
    val height: Double = 0.0,
    val color: Color = Color.Reset
) : Shape {

    override fun draw(painter: Painter) {
        val lines = listOf(
            Line(
                x1 = x,
                y1 = y,
                x2 = x,
                y2 = y + height,
                color = color
            ),
            Line(
                x1 = x,
                y1 = y + height,
                x2 = x + width,
                y2 = y + height,
                color = color
            ),
            Line(
                x1 = x + width,
                y1 = y,
                x2 = x + width,
                y2 = y + height,
                color = color
            ),
            Line(
                x1 = x,
                y1 = y,
                x2 = x + width,
                y2 = y,
                color = color
            )
        )
        for (line in lines) {
            line.draw(painter)
        }
    }

    companion object {
        /**
         * Create a new rectangle with the given position, size, and color.
         */
        fun new(x: Double, y: Double, width: Double, height: Double, color: Color): Rectangle =
            Rectangle(x, y, width, height, color)
    }
}
