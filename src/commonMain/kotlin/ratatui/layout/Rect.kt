/**
 * A rectangular area in the terminal.
 *
 * A [Rect] represents a rectangular region in the terminal coordinate system, defined by its
 * top-left corner position and dimensions. This is the fundamental building block for all layout
 * operations and widget rendering in Ratatui.
 *
 * Rectangles are used throughout the layout system to define areas where widgets can be rendered.
 * They are typically created by [Layout] operations that divide terminal space, but can also be
 * manually constructed for specific positioning needs.
 *
 * The coordinate system uses the top-left corner as the origin (0, 0), with x increasing to the
 * right and y increasing downward. All measurements are in character cells.
 *
 * ## Construction and Conversion
 *
 * - [new] - Create a new rectangle from coordinates and dimensions
 * - [asPosition] - Convert to a position at the top-left corner
 * - [asSize] - Convert to a size representing the dimensions
 * - [from] with `Pair<Position, Size>` - Create from position and size tuple
 *
 * ## Geometry and Properties
 *
 * - [area] - Calculate the total area in character cells
 * - [isEmpty] - Check if the rectangle has zero area
 * - [left], [right], [top], [bottom] - Get edge coordinates
 *
 * ## Spatial Operations
 *
 * - [inner], [outer] - Apply margins to shrink or expand
 * - [offset] - Move the rectangle by a relative amount
 * - [resize] - Change the rectangle size while keeping the bottom/right in range
 * - [union] - Combine with another rectangle to create a bounding box
 * - [intersection] - Find the overlapping area with another rectangle
 * - [clamp] - Constrain the rectangle to fit within another
 *
 * ## Examples
 *
 * ```kotlin
 * val rect = Rect.new(1, 2, 3, 4)
 * assertEquals(rect, Rect(x = 1, y = 2, width = 3, height = 4))
 * ```
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
package ratatui.layout

/**
 * A rectangular area in the terminal.
 *
 * @property x The x coordinate of the top left corner of the Rect.
 * @property y The y coordinate of the top left corner of the Rect.
 * @property width The width of the Rect.
 * @property height The height of the Rect.
 */
data class Rect(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {

    companion object {
        /** A zero sized Rect at position 0,0 */
        val ZERO: Rect = Rect(0, 0, 0, 0)

        /** The minimum possible Rect */
        val MIN: Rect = ZERO

        /** The maximum possible Rect */
        val MAX: Rect = new(0, 0, Int.MAX_VALUE, Int.MAX_VALUE)

        /**
         * Creates a new Rect, with width and height limited to keep both bounds within Int.
         *
         * If the width or height would cause the right or bottom coordinate to be larger than the
         * maximum value of Int, the width or height will be clamped to keep the right or bottom
         * coordinate within Int.
         */
        fun new(x: Int, y: Int, width: Int, height: Int): Rect {
            val clampedWidth = (x.toLong() + width).coerceAtMost(Int.MAX_VALUE.toLong()) - x
            val clampedHeight = (y.toLong() + height).coerceAtMost(Int.MAX_VALUE.toLong()) - y
            return Rect(x, y, clampedWidth.toInt(), clampedHeight.toInt())
        }

        /** Create a Rect from a Position and Size */
        fun from(pair: Pair<Position, Size>): Rect = Rect(
            x = pair.first.x,
            y = pair.first.y,
            width = pair.second.width,
            height = pair.second.height
        )

        /** Create a Rect from a Size at origin (0, 0) */
        fun from(size: Size): Rect = Rect(
            x = 0,
            y = 0,
            width = size.width,
            height = size.height
        )

        /** Create a Rect that is empty (for Buffer.empty) - used by TestBackend */
        fun empty(rect: Rect): Rect = rect
    }

    /** The area of the Rect */
    fun area(): Long = width.toLong() * height.toLong()

    /** Returns true if the Rect has no area */
    fun isEmpty(): Boolean = width == 0 || height == 0

    /** Returns the left coordinate of the Rect */
    fun left(): Int = x

    /**
     * Returns the right coordinate of the Rect. This is the first coordinate outside of the Rect.
     *
     * If the right coordinate is larger than the maximum value of Int, it will be clamped to
     * Int.MAX_VALUE.
     */
    fun right(): Int = (x.toLong() + width).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    /** Returns the top coordinate of the Rect */
    fun top(): Int = y

    /**
     * Returns the bottom coordinate of the Rect. This is the first coordinate outside of the Rect.
     *
     * If the bottom coordinate is larger than the maximum value of Int, it will be clamped to
     * Int.MAX_VALUE.
     */
    fun bottom(): Int = (y.toLong() + height).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

    /**
     * Returns a new Rect inside the current one, with the given margin on each side.
     *
     * If the margin is larger than the Rect, the returned Rect will have no area.
     */
    fun inner(margin: Margin): Rect {
        val doubledMarginHorizontal = margin.horizontal * 2
        val doubledMarginVertical = margin.vertical * 2

        return if (width < doubledMarginHorizontal || height < doubledMarginVertical) {
            ZERO
        } else {
            Rect(
                x = x + margin.horizontal,
                y = y + margin.vertical,
                width = width - doubledMarginHorizontal,
                height = height - doubledMarginVertical
            )
        }
    }

    /**
     * Returns a new Rect outside the current one, with the given margin applied on each side.
     *
     * If the margin causes the Rect's bounds to be outside the range of an Int, the Rect will
     * be truncated to keep the bounds within Int.
     */
    fun outer(margin: Margin): Rect {
        val newX = (x - margin.horizontal).coerceAtLeast(0)
        val newY = (y - margin.vertical).coerceAtLeast(0)
        val newWidth = ((right().toLong() + margin.horizontal)
            .coerceAtMost(Int.MAX_VALUE.toLong()) - newX).toInt()
        val newHeight = ((bottom().toLong() + margin.vertical)
            .coerceAtMost(Int.MAX_VALUE.toLong()) - newY).toInt()
        return Rect(newX, newY, newWidth, newHeight)
    }

    /**
     * Moves the Rect without modifying its size.
     *
     * See [Offset] for details.
     */
    fun offset(offset: Offset): Rect {
        val newX = (x + offset.x).coerceIn(0, Int.MAX_VALUE - width)
        val newY = (y + offset.y).coerceIn(0, Int.MAX_VALUE - height)
        return copy(x = newX, y = newY)
    }

    /**
     * Resizes the Rect, clamping to keep the right and bottom within Int.MAX_VALUE.
     *
     * The position is preserved.
     */
    fun resize(size: Size): Rect {
        val newWidth = ((x.toLong() + size.width)
            .coerceAtMost(Int.MAX_VALUE.toLong()) - x).toInt()
        val newHeight = ((y.toLong() + size.height)
            .coerceAtMost(Int.MAX_VALUE.toLong()) - y).toInt()
        return copy(width = newWidth, height = newHeight)
    }

    /** Returns a new Rect that contains both the current one and the given one */
    fun union(other: Rect): Rect {
        val x1 = minOf(x, other.x)
        val y1 = minOf(y, other.y)
        val x2 = maxOf(right(), other.right())
        val y2 = maxOf(bottom(), other.bottom())
        return Rect(
            x = x1,
            y = y1,
            width = x2 - x1,
            height = y2 - y1
        )
    }

    /**
     * Returns a new Rect that is the intersection of the current one and the given one.
     *
     * If the two Rects do not intersect, the returned Rect will have no area.
     */
    fun intersection(other: Rect): Rect {
        val x1 = maxOf(x, other.x)
        val y1 = maxOf(y, other.y)
        val x2 = minOf(right(), other.right())
        val y2 = minOf(bottom(), other.bottom())
        return Rect(
            x = x1,
            y = y1,
            width = (x2 - x1).coerceAtLeast(0),
            height = (y2 - y1).coerceAtLeast(0)
        )
    }

    /** Returns true if the two Rects intersect */
    fun intersects(other: Rect): Boolean {
        return x < other.right() &&
                right() > other.x &&
                y < other.bottom() &&
                bottom() > other.y
    }

    /**
     * Returns true if the given position is inside the Rect.
     *
     * The position is considered inside the Rect if it is on the Rect's border.
     */
    fun contains(position: Position): Boolean {
        return position.x >= x &&
                position.x < right() &&
                position.y >= y &&
                position.y < bottom()
    }

    /**
     * Clamp this Rect to fit inside the other Rect.
     *
     * If the width or height of this Rect is larger than the other Rect, it will be clamped to
     * the other Rect's width or height.
     */
    fun clamp(other: Rect): Rect {
        val newWidth = minOf(width, other.width)
        val newHeight = minOf(height, other.height)
        val newX = x.coerceIn(
            other.x,
            (other.right() - newWidth).coerceAtLeast(other.x)
        )
        val newY = y.coerceIn(
            other.y,
            (other.bottom() - newHeight).coerceAtLeast(other.y)
        )
        return new(newX, newY, newWidth, newHeight)
    }

    /** Returns a [Position] with the same coordinates as this Rect */
    fun asPosition(): Position = Position(x, y)

    /** Converts the Rect into a [Size] */
    fun asSize(): Size = Size(width, height)

    /** Convert to a pair of position and size */
    fun toPair(): Pair<Position, Size> = Pair(asPosition(), asSize())

    override fun toString(): String = "${width}x${height}+${x}+${y}"

    /**
     * Returns an iterator over the columns of the Rect.
     *
     * Each column is a Rect with width 1 and the same height and y-coordinate as this Rect.
     */
    fun columns(): Iterator<Rect> = iterator {
        for (col in x until right()) {
            yield(Rect(col, y, 1, height))
        }
    }

    /**
     * Returns an iterator over the rows of the Rect.
     *
     * Each row is a Rect with height 1 and the same width and x-coordinate as this Rect.
     */
    fun rows(): Iterator<Rect> = iterator {
        for (row in y until bottom()) {
            yield(Rect(x, row, width, 1))
        }
    }

    /**
     * Returns a new Rect with the x coordinate indented by the given width.
     *
     * The width is reduced by the indent amount. If the indent is larger than the width,
     * the width becomes 0.
     */
    fun indentX(indent: Int): Rect {
        val newX = (x.toLong() + indent).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val newWidth = if (indent >= width) 0 else width - indent
        return copy(x = newX, width = newWidth)
    }
}

// Operator extensions for Rect + Offset
operator fun Rect.plus(offset: Offset): Rect = this.offset(offset)
operator fun Rect.minus(offset: Offset): Rect = this.offset(Offset(-offset.x, -offset.y))
