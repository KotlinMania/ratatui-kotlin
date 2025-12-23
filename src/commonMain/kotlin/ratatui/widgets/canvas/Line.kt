package ratatui.widgets.canvas

import ratatui.style.Color
import kotlin.math.abs

/**
 * A line from `(x1, y1)` to `(x2, y2)` with the given color.
 *
 * @property x1 x of the starting point
 * @property y1 y of the starting point
 * @property x2 x of the ending point
 * @property y2 y of the ending point
 * @property color Color of the line
 */
data class Line(
    val x1: Double = 0.0,
    val y1: Double = 0.0,
    val x2: Double = 0.0,
    val y2: Double = 0.0,
    val color: Color = Color.Reset
) : Shape {

    override fun draw(painter: Painter) {
        val (xBounds, yBounds) = painter.bounds()
        val clipped = clipLine(xBounds, yBounds, x1, y1, x2, y2) ?: return

        val (worldX1, worldY1, worldX2, worldY2) = clipped
        val p1 = painter.getPoint(worldX1, worldY1) ?: return
        val p2 = painter.getPoint(worldX2, worldY2) ?: return

        val (px1, py1) = p1
        val (px2, py2) = p2

        val (dx, xRange) = if (px2 >= px1) {
            Pair(px2 - px1, px1..px2)
        } else {
            Pair(px1 - px2, px2..px1)
        }
        val (dy, yRange) = if (py2 >= py1) {
            Pair(py2 - py1, py1..py2)
        } else {
            Pair(py1 - py2, py2..py1)
        }

        when {
            dx == 0 -> {
                for (y in yRange) {
                    painter.paint(px1, y, color)
                }
            }
            dy == 0 -> {
                for (x in xRange) {
                    painter.paint(x, py1, color)
                }
            }
            dy < dx -> {
                if (px1 > px2) {
                    drawLineLow(painter, px2, py2, px1, py1, color)
                } else {
                    drawLineLow(painter, px1, py1, px2, py2, color)
                }
            }
            else -> {
                if (py1 > py2) {
                    drawLineHigh(painter, px2, py2, px1, py1, color)
                } else {
                    drawLineHigh(painter, px1, py1, px2, py2, color)
                }
            }
        }
    }

    companion object {
        /**
         * Create a new line from `(x1, y1)` to `(x2, y2)` with the given color.
         */
        fun new(x1: Double, y1: Double, x2: Double, y2: Double, color: Color): Line =
            Line(x1, y1, x2, y2, color)
    }
}

/**
 * Result of line clipping.
 */
private data class ClippedLine(val x1: Double, val y1: Double, val x2: Double, val y2: Double)

/**
 * Cohen-Sutherland line clipping algorithm.
 * Clips a line to fit within the given bounds.
 */
private fun clipLine(
    xBounds: DoubleArray,
    yBounds: DoubleArray,
    x1: Double,
    y1: Double,
    x2: Double,
    y2: Double
): ClippedLine? {
    val xMin = xBounds[0]
    val xMax = xBounds[1]
    val yMin = yBounds[0]
    val yMax = yBounds[1]

    var px1 = x1
    var py1 = y1
    var px2 = x2
    var py2 = y2

    var code1 = computeOutCode(px1, py1, xMin, xMax, yMin, yMax)
    var code2 = computeOutCode(px2, py2, xMin, xMax, yMin, yMax)

    while (true) {
        when {
            // Both points inside window
            (code1 or code2) == 0 -> return ClippedLine(px1, py1, px2, py2)
            // Both points share an outside zone (line is completely outside)
            (code1 and code2) != 0 -> return null
            else -> {
                // Line needs clipping
                val codeOut = if (code1 != 0) code1 else code2
                var x = 0.0
                var y = 0.0

                when {
                    (codeOut and TOP) != 0 -> {
                        x = px1 + (px2 - px1) * (yMax - py1) / (py2 - py1)
                        y = yMax
                    }
                    (codeOut and BOTTOM) != 0 -> {
                        x = px1 + (px2 - px1) * (yMin - py1) / (py2 - py1)
                        y = yMin
                    }
                    (codeOut and RIGHT) != 0 -> {
                        y = py1 + (py2 - py1) * (xMax - px1) / (px2 - px1)
                        x = xMax
                    }
                    (codeOut and LEFT) != 0 -> {
                        y = py1 + (py2 - py1) * (xMin - px1) / (px2 - px1)
                        x = xMin
                    }
                }

                if (codeOut == code1) {
                    px1 = x
                    py1 = y
                    code1 = computeOutCode(px1, py1, xMin, xMax, yMin, yMax)
                } else {
                    px2 = x
                    py2 = y
                    code2 = computeOutCode(px2, py2, xMin, xMax, yMin, yMax)
                }
            }
        }
    }
}

// Cohen-Sutherland region codes
private const val INSIDE = 0
private const val LEFT = 1
private const val RIGHT = 2
private const val BOTTOM = 4
private const val TOP = 8

private fun computeOutCode(x: Double, y: Double, xMin: Double, xMax: Double, yMin: Double, yMax: Double): Int {
    var code = INSIDE
    if (x < xMin) code = code or LEFT
    else if (x > xMax) code = code or RIGHT
    if (y < yMin) code = code or BOTTOM
    else if (y > yMax) code = code or TOP
    return code
}

/**
 * Bresenham's line algorithm for lines with slope < 1.
 */
private fun drawLineLow(painter: Painter, x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
    val dx = x2 - x1
    val dy = abs(y2 - y1)
    var d = 2 * dy - dx
    var y = y1
    for (x in x1..x2) {
        painter.paint(x, y, color)
        if (d > 0) {
            y = if (y1 > y2) {
                maxOf(0, y - 1)
            } else {
                y + 1
            }
            d -= 2 * dx
        }
        d += 2 * dy
    }
}

/**
 * Bresenham's line algorithm for lines with slope >= 1.
 */
private fun drawLineHigh(painter: Painter, x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
    val dx = abs(x2 - x1)
    val dy = y2 - y1
    var d = 2 * dx - dy
    var x = x1
    for (y in y1..y2) {
        painter.paint(x, y, color)
        if (d > 0) {
            x = if (x1 > x2) {
                maxOf(0, x - 1)
            } else {
                x + 1
            }
            d -= 2 * dy
        }
        d += 2 * dx
    }
}
