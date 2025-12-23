package ratatui.widgets.canvas

import ratatui.style.Color

/**
 * A group of points with a given color.
 *
 * @property coords List of points to draw as (x, y) pairs
 * @property color Color of the points
 */
data class Points(
    val coords: List<Pair<Double, Double>> = emptyList(),
    val color: Color = Color.Reset
) : Shape {

    /**
     * Create a new Points shape with the given coordinates and color.
     */
    constructor(coords: Array<Pair<Double, Double>>, color: Color) : this(coords.toList(), color)

    override fun draw(painter: Painter) {
        for ((x, y) in coords) {
            val point = painter.getPoint(x, y)
            if (point != null) {
                painter.paint(point.first, point.second, color)
            }
        }
    }

    companion object {
        /**
         * Create a new Points shape with the given coordinates and color.
         */
        fun new(coords: List<Pair<Double, Double>>, color: Color): Points = Points(coords, color)
    }
}
