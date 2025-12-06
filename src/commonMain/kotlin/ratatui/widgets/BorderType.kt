package ratatui.widgets

import ratatui.symbols.border.Set as BorderSet
import ratatui.symbols.border.PLAIN
import ratatui.symbols.border.ROUNDED
import ratatui.symbols.border.DOUBLE
import ratatui.symbols.border.THICK
import ratatui.symbols.border.LIGHT_DOUBLE_DASHED
import ratatui.symbols.border.HEAVY_DOUBLE_DASHED
import ratatui.symbols.border.LIGHT_TRIPLE_DASHED
import ratatui.symbols.border.HEAVY_TRIPLE_DASHED
import ratatui.symbols.border.LIGHT_QUADRUPLE_DASHED
import ratatui.symbols.border.HEAVY_QUADRUPLE_DASHED
import ratatui.symbols.border.QUADRANT_INSIDE
import ratatui.symbols.border.QUADRANT_OUTSIDE

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

    companion object {
        /**
         * Convert a BorderType into the corresponding [BorderSet] of border symbols.
         */
        fun borderSymbols(borderType: BorderType): BorderSet = borderType.toBorderSet()
    }
}
