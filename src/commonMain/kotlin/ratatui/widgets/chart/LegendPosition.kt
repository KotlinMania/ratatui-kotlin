package ratatui.widgets.chart

import ratatui.layout.Rect

/**
 * Allow users to specify the position of a legend in a [Chart]
 *
 * See [Chart.legendPosition]
 */
enum class LegendPosition {
    /** Legend is centered on top */
    Top,
    /** Legend is in the top-right corner. This is the **default**. */
    TopRight,
    /** Legend is in the top-left corner */
    TopLeft,
    /** Legend is centered on the left */
    Left,
    /** Legend is centered on the right */
    Right,
    /** Legend is centered on the bottom */
    Bottom,
    /** Legend is in the bottom-right corner */
    BottomRight,
    /** Legend is in the bottom-left corner */
    BottomLeft;

    internal fun layout(
        area: Rect,
        legendWidth: Int,
        legendHeight: Int,
        xTitleWidth: Int,
        yTitleWidth: Int
    ): Rect? {
        var heightMargin = area.height - legendHeight
        if (xTitleWidth != 0) {
            heightMargin -= 1
        }
        if (yTitleWidth != 0) {
            heightMargin -= 1
        }
        if (heightMargin < 0) {
            return null
        }

        val (x, y) = when (this) {
            TopRight -> {
                if (legendWidth + yTitleWidth > area.width) {
                    Pair(area.right() - legendWidth, area.top() + 1)
                } else {
                    Pair(area.right() - legendWidth, area.top())
                }
            }
            TopLeft -> {
                if (yTitleWidth != 0) {
                    Pair(area.left(), area.top() + 1)
                } else {
                    Pair(area.left(), area.top())
                }
            }
            Top -> {
                val xPos = (area.width - legendWidth) / 2
                if (area.left() + yTitleWidth > xPos) {
                    Pair(area.left() + xPos, area.top() + 1)
                } else {
                    Pair(area.left() + xPos, area.top())
                }
            }
            Left -> {
                var yPos = (area.height - legendHeight) / 2
                if (yTitleWidth != 0) {
                    yPos += 1
                }
                if (xTitleWidth != 0) {
                    yPos = (yPos - 1).coerceAtLeast(0)
                }
                Pair(area.left(), area.top() + yPos)
            }
            Right -> {
                var yPos = (area.height - legendHeight) / 2
                if (yTitleWidth != 0) {
                    yPos += 1
                }
                if (xTitleWidth != 0) {
                    yPos = (yPos - 1).coerceAtLeast(0)
                }
                Pair(area.right() - legendWidth, area.top() + yPos)
            }
            BottomLeft -> {
                if (xTitleWidth + legendWidth > area.width) {
                    Pair(area.left(), area.bottom() - legendHeight - 1)
                } else {
                    Pair(area.left(), area.bottom() - legendHeight)
                }
            }
            BottomRight -> {
                if (xTitleWidth != 0) {
                    Pair(area.right() - legendWidth, area.bottom() - legendHeight - 1)
                } else {
                    Pair(area.right() - legendWidth, area.bottom() - legendHeight)
                }
            }
            Bottom -> {
                val xPos = area.left() + (area.width - legendWidth) / 2
                if (xPos + legendWidth > area.right() - xTitleWidth) {
                    Pair(xPos, area.bottom() - legendHeight - 1)
                } else {
                    Pair(xPos, area.bottom() - legendHeight)
                }
            }
        }

        return Rect.new(x, y, legendWidth, legendHeight)
    }

    companion object {
        fun default(): LegendPosition = TopRight
    }
}
