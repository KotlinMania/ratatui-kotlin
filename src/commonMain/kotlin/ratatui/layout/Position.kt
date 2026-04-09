// port-lint: source ratatui-core/src/layout/position.rs
/**
 * Position in the terminal coordinate system.
 *
 * The position is relative to the top left corner of the terminal window, with the top left corner
 * being (0, 0). The x axis is horizontal increasing to the right, and the y axis is vertical
 * increasing downwards.
 *
 * [Position] is used throughout the layout system to represent specific points in the terminal.
 * It can be created from coordinates, tuples, or extracted from rectangular areas.
 *
 * ## Construction
 *
 * - [new] - Create a new position from x and y coordinates
 * - Default constructor - Create at origin (0, 0)
 *
 * ## Conversion
 *
 * - [from] with `Pair<Int, Int>` - Create from `Pair(x, y)`
 * - [from] with [Rect] - Create from [Rect] (uses top-left corner)
 * - [toPair] - Convert to `Pair(x, y)`
 *
 * ## Movement
 *
 * - [offset] - Move by an [Offset]
 * - [plus] and [minus] - Shift by offsets with clamping
 * - [plusAssign] and [minusAssign] - In-place shifting
 *
 * ## Examples
 *
 * ```kotlin
 * import ratatui.layout.Offset
 * import ratatui.layout.Position
 * import ratatui.layout.Rect
 *
 * // the following are all equivalent
 * var position = Position(x = 1, y = 2)
 * position = Position.new(1, 2)
 * position = Position.from(Pair(1, 2))
 * position = Position.from(Rect.new(1, 2, 3, 4))
 *
 * // movement by offsets
 * val moved = Position.new(5, 5) + Offset.new(2, -3)
 * check(moved == Position.new(7, 2))
 * ```
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
package ratatui.layout

/**
 * A position in the terminal coordinate system.
 *
 * @property x The x coordinate of the position.
 *   The x coordinate is relative to the left edge of the terminal window, with the left edge being 0.
 * @property y The y coordinate of the position.
 *   The y coordinate is relative to the top edge of the terminal window, with the top edge being 0.
 */
data class Position(
    var x: Int,
    var y: Int
) : Comparable<Position> {

    companion object {
        private const val U16_MAX: Int = 0xFFFF

        /** Position at the origin, the top left edge at 0,0 */
        val ORIGIN: Position = Position(0, 0)

        /** Position at the minimum x and y values */
        val MIN: Position = ORIGIN

        /** Position at the maximum x and y values */
        val MAX: Position = Position(U16_MAX, U16_MAX)

        /** Create a new position */
        fun new(x: Int, y: Int): Position = Position(x, y)

        /** Create a position from a pair of coordinates */
        fun from(pair: Pair<Int, Int>): Position = Position(pair.first, pair.second)

        /** Create a position from a Rect (uses top-left corner) */
        fun from(rect: Rect): Position = rect.asPosition()
    }

    /**
     * Moves the position by the given offset.
     *
     * Positive offsets move right and down, negative offsets move left and up. Values that would
     * move the position outside the `u16` range are clamped to the nearest edge.
     */
    fun offset(offset: Offset): Position = this + offset

    /** Convert to a pair of coordinates */
    fun toPair(): Pair<Int, Int> = Pair(x, y)

    override fun compareTo(other: Position): Int {
        val xCompare = x.compareTo(other.x)
        return if (xCompare != 0) xCompare else y.compareTo(other.y)
    }

    override fun toString(): String = "($x, $y)"

    /**
     * Moves the position by the given offset.
     *
     * Values that would move the position outside the `u16` range are clamped to the nearest edge.
     */
    operator fun plus(offset: Offset): Position {
        val max = U16_MAX.toLong()
        val newX = (x.toLong() + offset.x.toLong()).coerceIn(0, max).toInt()
        val newY = (y.toLong() + offset.y.toLong()).coerceIn(0, max).toInt()
        return Position(newX, newY)
    }

    /**
     * Moves the position by the inverse of the given offset.
     *
     * Values that would move the position outside the `u16` range are clamped to the nearest edge.
     */
    operator fun minus(offset: Offset): Position {
        val max = U16_MAX.toLong()
        val newX = (x.toLong() - offset.x.toLong()).coerceIn(0, max).toInt()
        val newY = (y.toLong() - offset.y.toLong()).coerceIn(0, max).toInt()
        return Position(newX, newY)
    }

    /**
     * Moves the position in place by the given offset.
     *
     * Values that would move the position outside the Int range are clamped to the nearest edge.
     */
    operator fun plusAssign(offset: Offset) {
        val moved = this + offset
        x = moved.x
        y = moved.y
    }

    /**
     * Moves the position in place by the inverse of the given offset.
     *
     * Values that would move the position outside the Int range are clamped to the nearest edge.
     */
    operator fun minusAssign(offset: Offset) {
        val moved = this - offset
        x = moved.x
        y = moved.y
    }
}

/**
 * Moves the position by the given offset.
 *
 * Mirrors Rust `impl Add<Position> for Offset`.
 */
operator fun Offset.plus(position: Position): Position = position + this
