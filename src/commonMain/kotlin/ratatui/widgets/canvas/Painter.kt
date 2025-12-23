package ratatui.widgets.canvas

import ratatui.style.Color
import kotlin.math.round

/**
 * Painter is an abstraction over the [Context] that allows to draw shapes on the grid.
 *
 * It is used by the [Shape] interface to draw shapes on the grid. It can be useful to think of this
 * as similar to the Buffer struct that is used to draw widgets on the terminal.
 */
class Painter internal constructor(
    private val context: Context,
    private val resolution: Pair<Double, Double>
) {
    /**
     * Convert the `(x, y)` coordinates to location of a point on the grid.
     *
     * `(x, y)` coordinates are expressed in the coordinate system of the canvas. The origin is in
     * the lower left corner of the canvas (unlike most other coordinates in Ratatui where the
     * origin is the upper left corner). The `x` and `y` bounds of the canvas define the specific
     * area of some coordinate system that will be drawn on the canvas. The resolution of the grid
     * is used to convert the `(x, y)` coordinates to the location of a point on the grid.
     *
     * The grid coordinates are expressed in the coordinate system of the grid. The origin is in
     * the top left corner of the grid. The x and y bounds of the grid are always `[0, width - 1]`
     * and `[0, height - 1]` respectively. The resolution of the grid is used to convert the
     * `(x, y)` coordinates to the location of a point on the grid.
     *
     * Points are rounded to the nearest grid cell (with points exactly in the center of a cell
     * rounding up).
     *
     * @return The grid coordinates as (x, y) pair, or null if the point is outside the canvas bounds
     */
    fun getPoint(x: Double, y: Double): Pair<Int, Int>? {
        val left = context.xBounds[0]
        val right = context.xBounds[1]
        val bottom = context.yBounds[0]
        val top = context.yBounds[1]

        if (x < left || x > right || y < bottom || y > top) {
            return null
        }

        val width = right - left
        val height = top - bottom

        if (width <= 0.0 || height <= 0.0) {
            return null
        }

        val gridX = round((x - left) * (resolution.first - 1.0) / width).toInt()
        val gridY = round((top - y) * (resolution.second - 1.0) / height).toInt()

        return Pair(gridX, gridY)
    }

    /**
     * Paint a point of the grid.
     */
    fun paint(x: Int, y: Int, color: Color) {
        context.grid.paint(x, y, color)
    }

    /**
     * Canvas context bounds by axis.
     *
     * @return Pair of (xBounds, yBounds) arrays
     */
    fun bounds(): Pair<DoubleArray, DoubleArray> {
        return Pair(context.xBounds, context.yBounds)
    }

    companion object {
        internal fun from(context: Context): Painter {
            val resolution = context.grid.resolution()
            return Painter(context, resolution)
        }
    }
}
