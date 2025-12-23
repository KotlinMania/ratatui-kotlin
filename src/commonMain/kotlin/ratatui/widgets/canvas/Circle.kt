package ratatui.widgets.canvas

import ratatui.style.Color
import kotlin.math.cos
import kotlin.math.sin

/**
 * A circle with a given center and radius and with a given color.
 *
 * @property x x coordinate of the circle's center
 * @property y y coordinate of the circle's center
 * @property radius Radius of the circle
 * @property color Color of the circle
 */
data class Circle(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val radius: Double = 0.0,
    val color: Color = Color.Reset
) : Shape {

    override fun draw(painter: Painter) {
        for (angle in 0 until 360) {
            val radians = angle.toDouble() * kotlin.math.PI / 180.0
            val circleX = x + radius * cos(radians)
            val circleY = y + radius * sin(radians)
            val point = painter.getPoint(circleX, circleY)
            if (point != null) {
                painter.paint(point.first, point.second, color)
            }
        }
    }

    companion object {
        /**
         * Create a new circle with the given center, radius, and color.
         */
        fun new(x: Double, y: Double, radius: Double, color: Color): Circle =
            Circle(x, y, radius, color)
    }
}
