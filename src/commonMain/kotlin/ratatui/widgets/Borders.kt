// port-lint: source ratatui-widgets/src/borders.rs
package ratatui.widgets

import ratatui.symbols.border.DOUBLE
import ratatui.symbols.border.HEAVY_DOUBLE_DASHED
import ratatui.symbols.border.HEAVY_QUADRUPLE_DASHED
import ratatui.symbols.border.HEAVY_TRIPLE_DASHED
import ratatui.symbols.border.LIGHT_DOUBLE_DASHED
import ratatui.symbols.border.LIGHT_QUADRUPLE_DASHED
import ratatui.symbols.border.LIGHT_TRIPLE_DASHED
import ratatui.symbols.border.PLAIN
import ratatui.symbols.border.QUADRANT_INSIDE
import ratatui.symbols.border.QUADRANT_OUTSIDE
import ratatui.symbols.border.ROUNDED
import ratatui.symbols.border.Set as BorderSet
import ratatui.symbols.border.THICK

/**
 * Border related types ([Borders], [BorderType]) and a helper to create borders ([border]).
 *
 * Transliteration of Rust `ratatui-widgets/src/borders.rs`.
 */
data class Borders(val bits: UByte) {
    /** Returns true if no borders are set. */
    fun isEmpty(): Boolean = bits == 0.toUByte()

    /** Returns true if all borders are set. */
    fun isAll(): Boolean = bits == ALL.bits

    /** Returns true if the given border flag is set. */
    fun contains(other: Borders): Boolean = (bits and other.bits) == other.bits

    /** Returns true if any of the given border flags are set. */
    fun intersects(other: Borders): Boolean = (bits and other.bits) != 0.toUByte()

    /** Combines this border set with another using bitwise OR. */
    infix fun or(other: Borders): Borders = Borders(bits or other.bits)

    /** Combines this border set with another using bitwise OR. */
    fun union(other: Borders): Borders = this or other

    /** Returns the intersection of this border set with another. */
    infix fun and(other: Borders): Borders = Borders(bits and other.bits)

    /** Returns this border set with the given borders removed. */
    fun minus(other: Borders): Borders = Borders(bits and other.bits.inv())

    /**
     * Display the Borders bitflags as a list of names.
     *
     * `Borders.NONE` is displayed as `NONE` and `Borders.ALL` is displayed as `ALL`. If multiple
     * flags are set, they are otherwise displayed separated by a pipe character.
     */
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
        /** Show no border (default). */
        val NONE: Borders = empty()

        /** Show the top border. */
        val TOP: Borders = Borders(0b0001u)

        /** Show the right border. */
        val RIGHT: Borders = Borders(0b0010u)

        /** Show the bottom border. */
        val BOTTOM: Borders = Borders(0b0100u)

        /** Show the left border. */
        val LEFT: Borders = Borders(0b1000u)

        /** Show all borders. */
        val ALL: Borders = Borders(TOP.bits or RIGHT.bits or BOTTOM.bits or LEFT.bits)

        /** Alias for [ALL]. */
        fun all(): Borders = ALL

        /** Alias for [NONE]. */
        fun empty(): Borders = Borders(0b0000u)

        /** Creates Borders from raw bits, returning null if invalid. */
        fun fromBits(bits: UByte): Borders? = if (bits <= ALL.bits) Borders(bits) else null
    }
}

/**
 * The type of border of a [Block].
 *
 * See the [Block.borders] method to configure its borders.
 */
enum class BorderType {
    /**
     * A plain, simple border.
     *
     * This is the default.
     *
     * ```
     * ┌───────┐
     * │       │
     * └───────┘
     * ```
     */
    Plain,

    /**
     * A plain border with rounded corners.
     *
     * ```
     * ╭───────╮
     * │       │
     * ╰───────╯
     * ```
     */
    Rounded,

    /**
     * A doubled border.
     *
     * Note this uses one character that draws two lines.
     *
     * ```
     * ╔═══════╗
     * ║       ║
     * ╚═══════╝
     * ```
     */
    Double,

    /**
     * A thick border.
     *
     * ```
     * ┏━━━━━━━┓
     * ┃       ┃
     * ┗━━━━━━━┛
     * ```
     */
    Thick,

    /**
     * A light double-dashed border.
     *
     * ```
     * ┌╌╌╌╌╌╌╌┐
     * ╎       ╎
     * └╌╌╌╌╌╌╌┘
     * ```
     */
    LightDoubleDashed,

    /**
     * A heavy double-dashed border.
     *
     * ```
     * ┏╍╍╍╍╍╍╍┓
     * ╏       ╏
     * ┗╍╍╍╍╍╍╍┛
     * ```
     */
    HeavyDoubleDashed,

    /**
     * A light triple-dashed border.
     *
     * ```
     * ┌┄┄┄┄┄┄┄┐
     * ┆       ┆
     * └┄┄┄┄┄┄┄┘
     * ```
     */
    LightTripleDashed,

    /**
     * A heavy triple-dashed border.
     *
     * ```
     * ┏┅┅┅┅┅┅┅┓
     * ┇       ┇
     * ┗┅┅┅┅┅┅┅┛
     * ```
     */
    HeavyTripleDashed,

    /**
     * A light quadruple-dashed border.
     *
     * ```
     * ┌┈┈┈┈┈┈┈┐
     * ┊       ┊
     * └┈┈┈┈┈┈┈┘
     * ```
     */
    LightQuadrupleDashed,

    /**
     * A heavy quadruple-dashed border.
     *
     * ```
     * ┏┉┉┉┉┉┉┉┓
     * ┋       ┋
     * ┗┉┉┉┉┉┉┉┛
     * ```
     */
    HeavyQuadrupleDashed,

    /**
     * A border with a single line on the inside of a half block.
     *
     * ```
     * ▗▄▄▄▄▄▄▄▖
     * ▐       ▌
     * ▐       ▌
     * ▝▀▀▀▀▀▀▀▘
     * ```
     */
    QuadrantInside,

    /**
     * A border with a single line on the outside of a half block.
     *
     * ```
     * ▛▀▀▀▀▀▀▀▜
     * ▌       ▐
     * ▌       ▐
     * ▙▄▄▄▄▄▄▄▟
     * ```
     */
    QuadrantOutside;

    /**
     * Convert this BorderType into the corresponding [BorderSet] of border symbols.
     */
    fun toBorderSet(): BorderSet = when (this) {
        Plain -> PLAIN
        Rounded -> ROUNDED
        Double -> DOUBLE
        Thick -> THICK
        LightDoubleDashed -> LIGHT_DOUBLE_DASHED
        HeavyDoubleDashed -> HEAVY_DOUBLE_DASHED
        LightTripleDashed -> LIGHT_TRIPLE_DASHED
        HeavyTripleDashed -> HEAVY_TRIPLE_DASHED
        LightQuadrupleDashed -> LIGHT_QUADRUPLE_DASHED
        HeavyQuadrupleDashed -> HEAVY_QUADRUPLE_DASHED
        QuadrantInside -> QUADRANT_INSIDE
        QuadrantOutside -> QUADRANT_OUTSIDE
    }

    /**
     * Convert this BorderType into the corresponding [BorderSet] of border symbols.
     */
    fun borderSymbols(): BorderSet = toBorderSet()

    companion object {
        /**
         * Convert a BorderType into the corresponding [BorderSet] of border symbols.
         */
        fun borderSymbols(borderType: BorderType): BorderSet = borderType.toBorderSet()
    }
}

/**
 * Construct a combination of [Borders] from any number of individual border flags.
 *
 * Mirrors the upstream `border!()` macro, using function call syntax.
 *
 * ## Examples
 *
 * ```
 * border() == Borders.NONE
 * border(Borders.TOP) == Borders.TOP
 * border(Borders.TOP, Borders.BOTTOM) == (Borders.TOP or Borders.BOTTOM)
 * ```
 */
fun border(vararg borders: Borders): Borders {
    var result = Borders.NONE
    for (b in borders) {
        result = result.union(b)
    }
    return result
}
