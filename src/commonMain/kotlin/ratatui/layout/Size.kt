/**
 * A simple size struct for representing dimensions in the terminal.
 *
 * The width and height are stored as [Int] values and represent the number of columns and rows
 * respectively. This is used throughout the layout system to represent dimensions of rectangular
 * areas and other layout elements.
 *
 * Size can be created from tuples, extracted from rectangular areas, or constructed directly.
 * It's commonly used in conjunction with [Position] to define rectangular areas.
 *
 * ## Construction
 *
 * - [new] - Create a new size from width and height
 * - Default constructor - Create with zero dimensions
 *
 * ## Conversion
 *
 * - [from] with `Pair<Int, Int>` - Create from tuple
 * - [from] with [Rect] - Create from Rect (uses width and height)
 * - [toPair] - Convert to `Pair<Int, Int>` tuple
 *
 * ## Computation
 *
 * - [area] - Compute the total number of cells covered by the size
 *
 * ## Examples
 *
 * ```kotlin
 * val size = Size.new(80, 24)
 * assertEquals(size.area(), 1920)
 * val size = Size.from(Pair(80, 24))
 * val size = Size.from(Rect.new(0, 0, 80, 24))
 * assertEquals(size.area(), 1920)
 * ```
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
package ratatui.layout

/**
 * A size representing dimensions in the terminal.
 *
 * @property width The width in columns
 * @property height The height in rows
 */
data class Size(
    val width: Int,
    val height: Int
) {

    companion object {
        /** A zero sized Size */
        val ZERO: Size = Size(0, 0)

        /** The minimum possible Size */
        val MIN: Size = ZERO

        /** The maximum possible Size */
        val MAX: Size = Size(Int.MAX_VALUE, Int.MAX_VALUE)

        /** Create a new Size */
        fun new(width: Int, height: Int): Size = Size(width, height)

        /** Create a Size from a pair of dimensions */
        fun from(pair: Pair<Int, Int>): Size = Size(pair.first, pair.second)

        /** Create a Size from a Rect (uses width and height) */
        fun from(rect: Rect): Size = rect.asSize()
    }

    /**
     * Compute the total area of the size as a [Long].
     *
     * The multiplication uses [Long] to avoid overflow when the width and height are large.
     */
    fun area(): Long = width.toLong() * height.toLong()

    /** Convert to a pair of dimensions */
    fun toPair(): Pair<Int, Int> = Pair(width, height)

    override fun toString(): String = "${width}x${height}"
}
