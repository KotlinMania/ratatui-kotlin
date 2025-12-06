package ratatui.widgets

/**
 * Bitflags that can be composed to set the visible borders essentially on the block widget.
 *
 * This is implemented as a value class wrapping a UByte for efficient storage while providing
 * type safety and bitwise operations.
 *
 * # Example
 *
 * ```kotlin
 * // Show top and bottom borders
 * val borders = Borders.TOP or Borders.BOTTOM
 *
 * // Check if a border is set
 * if (borders.contains(Borders.TOP)) {
 *     // ...
 * }
 * ```
 */
/**
 * Borders is a simple wrapper around a UByte that provides bitwise operations.
 * Using a data class for multiplatform compatibility (value classes with @JvmInline are JVM-specific).
 */
data class Borders(val bits: UByte) {
    /** Returns true if no borders are set */
    fun isEmpty(): Boolean = bits == 0.toUByte()

    /** Returns true if all borders are set */
    fun isAll(): Boolean = bits == ALL.bits

    /** Returns true if the given border flag is set */
    fun contains(other: Borders): Boolean = (bits and other.bits) == other.bits

    /** Returns true if any of the given border flags are set */
    fun intersects(other: Borders): Boolean = (bits and other.bits) != 0.toUByte()

    /** Combines this border set with another using bitwise OR */
    infix fun or(other: Borders): Borders = Borders((bits or other.bits).toUByte())

    /** Combines this border set with another using bitwise OR */
    fun union(other: Borders): Borders = this or other

    /** Returns the intersection of this border set with another */
    infix fun and(other: Borders): Borders = Borders((bits and other.bits).toUByte())

    /** Returns this border set with the given borders removed */
    fun minus(other: Borders): Borders = Borders((bits and other.bits.inv()).toUByte())

    override fun toString(): String {
        if (isEmpty()) return "NONE"
        if (isAll()) return "ALL"

        val names = mutableListOf<String>()
        if (contains(TOP)) names.add("TOP")
        if (contains(RIGHT)) names.add("RIGHT")
        if (contains(BOTTOM)) names.add("BOTTOM")
        if (contains(LEFT)) names.add("LEFT")
        return names.joinToString(" | ")
    }

    companion object {
        /** Show no border (default) */
        val NONE = Borders(0b0000u)

        /** Show the top border */
        val TOP = Borders(0b0001u)

        /** Show the right border */
        val RIGHT = Borders(0b0010u)

        /** Show the bottom border */
        val BOTTOM = Borders(0b0100u)

        /** Show the left border */
        val LEFT = Borders(0b1000u)

        /** Show all borders */
        val ALL = Borders((TOP.bits or RIGHT.bits or BOTTOM.bits or LEFT.bits).toUByte())

        /** Alias for [ALL] */
        fun all(): Borders = ALL

        /** Alias for [NONE] */
        fun empty(): Borders = NONE

        /** Creates Borders from raw bits, returning null if invalid */
        fun fromBits(bits: UByte): Borders? {
            return if (bits <= ALL.bits) Borders(bits) else null
        }
    }
}
