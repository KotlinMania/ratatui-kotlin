package ratatui.symbols.border

import ratatui.symbols.block.Block
import ratatui.symbols.line.Line
import ratatui.symbols.line.NORMAL as LINE_NORMAL
import ratatui.symbols.line.ROUNDED as LINE_ROUNDED
import ratatui.symbols.line.DOUBLE as LINE_DOUBLE
import ratatui.symbols.line.THICK as LINE_THICK
import ratatui.symbols.line.LIGHT_DOUBLE_DASHED as LINE_LIGHT_DOUBLE_DASHED
import ratatui.symbols.line.HEAVY_DOUBLE_DASHED as LINE_HEAVY_DOUBLE_DASHED
import ratatui.symbols.line.LIGHT_TRIPLE_DASHED as LINE_LIGHT_TRIPLE_DASHED
import ratatui.symbols.line.HEAVY_TRIPLE_DASHED as LINE_HEAVY_TRIPLE_DASHED
import ratatui.symbols.line.LIGHT_QUADRUPLE_DASHED as LINE_LIGHT_QUADRUPLE_DASHED
import ratatui.symbols.line.HEAVY_QUADRUPLE_DASHED as LINE_HEAVY_QUADRUPLE_DASHED

/**
 * A set of border symbols for drawing borders around areas.
 */
data class Set(
    val topLeft: String,
    val topRight: String,
    val bottomLeft: String,
    val bottomRight: String,
    val verticalLeft: String,
    val verticalRight: String,
    val horizontalTop: String,
    val horizontalBottom: String
) {
    companion object {
        /**
         * Default border set with plain single-line borders.
         */
        fun default(): Set = PLAIN

        /**
         * Creates a border set from a line set.
         */
        fun fromLineSet(lineSet: ratatui.symbols.line.Set): Set = Set(
            topLeft = lineSet.topLeft,
            topRight = lineSet.topRight,
            bottomLeft = lineSet.bottomLeft,
            bottomRight = lineSet.bottomRight,
            verticalLeft = lineSet.vertical,
            verticalRight = lineSet.vertical,
            horizontalTop = lineSet.horizontal,
            horizontalBottom = lineSet.horizontal
        )
    }
}

// Quadrant characters
object Quadrant {
    const val TOP_LEFT = "▘"
    const val TOP_RIGHT = "▝"
    const val BOTTOM_LEFT = "▖"
    const val BOTTOM_RIGHT = "▗"
    const val TOP_HALF = "▀"
    const val BOTTOM_HALF = "▄"
    const val LEFT_HALF = "▌"
    const val RIGHT_HALF = "▐"
    const val TOP_LEFT_BOTTOM_LEFT_BOTTOM_RIGHT = "▙"
    const val TOP_LEFT_TOP_RIGHT_BOTTOM_LEFT = "▛"
    const val TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT = "▜"
    const val TOP_RIGHT_BOTTOM_LEFT_BOTTOM_RIGHT = "▟"
    const val TOP_LEFT_BOTTOM_RIGHT = "▚"
    const val TOP_RIGHT_BOTTOM_LEFT = "▞"
    const val BLOCK = "█"
}

// One-eighth characters
object OneEighth {
    const val TOP = "▔"
    const val BOTTOM = "▁"
    const val LEFT = "▏"
    const val RIGHT = "▕"
}

/**
 * Border Set with a single line width
 *
 * ```
 * ┌─────┐
 * │xxxxx│
 * │xxxxx│
 * └─────┘
 * ```
 */
val PLAIN = Set(
    topLeft = LINE_NORMAL.topLeft,
    topRight = LINE_NORMAL.topRight,
    bottomLeft = LINE_NORMAL.bottomLeft,
    bottomRight = LINE_NORMAL.bottomRight,
    verticalLeft = LINE_NORMAL.vertical,
    verticalRight = LINE_NORMAL.vertical,
    horizontalTop = LINE_NORMAL.horizontal,
    horizontalBottom = LINE_NORMAL.horizontal
)

/**
 * Border Set with a single line width and rounded corners
 *
 * ```
 * ╭─────╮
 * │xxxxx│
 * │xxxxx│
 * ╰─────╯
 * ```
 */
val ROUNDED = Set(
    topLeft = LINE_ROUNDED.topLeft,
    topRight = LINE_ROUNDED.topRight,
    bottomLeft = LINE_ROUNDED.bottomLeft,
    bottomRight = LINE_ROUNDED.bottomRight,
    verticalLeft = LINE_ROUNDED.vertical,
    verticalRight = LINE_ROUNDED.vertical,
    horizontalTop = LINE_ROUNDED.horizontal,
    horizontalBottom = LINE_ROUNDED.horizontal
)

/**
 * Border Set with a double line width
 *
 * ```
 * ╔═════╗
 * ║xxxxx║
 * ║xxxxx║
 * ╚═════╝
 * ```
 */
val DOUBLE = Set(
    topLeft = LINE_DOUBLE.topLeft,
    topRight = LINE_DOUBLE.topRight,
    bottomLeft = LINE_DOUBLE.bottomLeft,
    bottomRight = LINE_DOUBLE.bottomRight,
    verticalLeft = LINE_DOUBLE.vertical,
    verticalRight = LINE_DOUBLE.vertical,
    horizontalTop = LINE_DOUBLE.horizontal,
    horizontalBottom = LINE_DOUBLE.horizontal
)

/**
 * Border Set with a thick line width
 *
 * ```
 * ┏━━━━━┓
 * ┃xxxxx┃
 * ┃xxxxx┃
 * ┗━━━━━┛
 * ```
 */
val THICK = Set(
    topLeft = LINE_THICK.topLeft,
    topRight = LINE_THICK.topRight,
    bottomLeft = LINE_THICK.bottomLeft,
    bottomRight = LINE_THICK.bottomRight,
    verticalLeft = LINE_THICK.vertical,
    verticalRight = LINE_THICK.vertical,
    horizontalTop = LINE_THICK.horizontal,
    horizontalBottom = LINE_THICK.horizontal
)

/** Border Set with light double-dashed border lines */
val LIGHT_DOUBLE_DASHED = Set.fromLineSet(LINE_LIGHT_DOUBLE_DASHED)

/** Border Set with thick double-dashed border lines */
val HEAVY_DOUBLE_DASHED = Set.fromLineSet(LINE_HEAVY_DOUBLE_DASHED)

/** Border Set with light triple-dashed border lines */
val LIGHT_TRIPLE_DASHED = Set.fromLineSet(LINE_LIGHT_TRIPLE_DASHED)

/** Border Set with thick triple-dashed border lines */
val HEAVY_TRIPLE_DASHED = Set.fromLineSet(LINE_HEAVY_TRIPLE_DASHED)

/** Border Set with light quadruple-dashed border lines */
val LIGHT_QUADRUPLE_DASHED = Set.fromLineSet(LINE_LIGHT_QUADRUPLE_DASHED)

/** Border Set with thick quadruple-dashed border lines */
val HEAVY_QUADRUPLE_DASHED = Set.fromLineSet(LINE_HEAVY_QUADRUPLE_DASHED)

/**
 * Quadrant used for setting a border outside a block by one half cell "pixel".
 *
 * ```
 * ▛▀▀▀▀▀▜
 * ▌xxxxx▐
 * ▌xxxxx▐
 * ▙▄▄▄▄▄▟
 * ```
 */
val QUADRANT_OUTSIDE = Set(
    topLeft = Quadrant.TOP_LEFT_TOP_RIGHT_BOTTOM_LEFT,
    topRight = Quadrant.TOP_LEFT_TOP_RIGHT_BOTTOM_RIGHT,
    bottomLeft = Quadrant.TOP_LEFT_BOTTOM_LEFT_BOTTOM_RIGHT,
    bottomRight = Quadrant.TOP_RIGHT_BOTTOM_LEFT_BOTTOM_RIGHT,
    verticalLeft = Quadrant.LEFT_HALF,
    verticalRight = Quadrant.RIGHT_HALF,
    horizontalTop = Quadrant.TOP_HALF,
    horizontalBottom = Quadrant.BOTTOM_HALF
)

/**
 * Quadrant used for setting a border inside a block by one half cell "pixel".
 *
 * ```
 * ▗▄▄▄▄▄▖
 * ▐xxxxx▌
 * ▐xxxxx▌
 * ▝▀▀▀▀▀▘
 * ```
 */
val QUADRANT_INSIDE = Set(
    topRight = Quadrant.BOTTOM_LEFT,
    topLeft = Quadrant.BOTTOM_RIGHT,
    bottomRight = Quadrant.TOP_LEFT,
    bottomLeft = Quadrant.TOP_RIGHT,
    verticalLeft = Quadrant.RIGHT_HALF,
    verticalRight = Quadrant.LEFT_HALF,
    horizontalTop = Quadrant.BOTTOM_HALF,
    horizontalBottom = Quadrant.TOP_HALF
)

/**
 * Wide border set based on McGugan box technique
 *
 * ```
 * ▁▁▁▁▁▁▁
 * ▏xxxxx▕
 * ▏xxxxx▕
 * ▔▔▔▔▔▔▔
 * ```
 */
val ONE_EIGHTH_WIDE = Set(
    topRight = OneEighth.BOTTOM,
    topLeft = OneEighth.BOTTOM,
    bottomRight = OneEighth.TOP,
    bottomLeft = OneEighth.TOP,
    verticalLeft = OneEighth.LEFT,
    verticalRight = OneEighth.RIGHT,
    horizontalTop = OneEighth.BOTTOM,
    horizontalBottom = OneEighth.TOP
)

/**
 * Tall border set based on McGugan box technique
 *
 * ```
 * ▕▔▔▏
 * ▕xx▏
 * ▕xx▏
 * ▕▁▁▏
 * ```
 */
val ONE_EIGHTH_TALL = Set(
    topRight = OneEighth.LEFT,
    topLeft = OneEighth.RIGHT,
    bottomRight = OneEighth.LEFT,
    bottomLeft = OneEighth.RIGHT,
    verticalLeft = OneEighth.RIGHT,
    verticalRight = OneEighth.LEFT,
    horizontalTop = OneEighth.TOP,
    horizontalBottom = OneEighth.BOTTOM
)

/**
 * Wide proportional (visually equal width and height) border using quadrants.
 *
 * ```
 * ▄▄▄▄
 * █xx█
 * █xx█
 * ▀▀▀▀
 * ```
 */
val PROPORTIONAL_WIDE = Set(
    topRight = Quadrant.BOTTOM_HALF,
    topLeft = Quadrant.BOTTOM_HALF,
    bottomRight = Quadrant.TOP_HALF,
    bottomLeft = Quadrant.TOP_HALF,
    verticalLeft = Quadrant.BLOCK,
    verticalRight = Quadrant.BLOCK,
    horizontalTop = Quadrant.BOTTOM_HALF,
    horizontalBottom = Quadrant.TOP_HALF
)

/**
 * Tall proportional (visually equal width and height) border using quadrants.
 *
 * ```
 * ▕█▀▀█
 * ▕█xx█
 * ▕█xx█
 * ▕█▄▄█
 * ```
 */
val PROPORTIONAL_TALL = Set(
    topRight = Quadrant.BLOCK,
    topLeft = Quadrant.BLOCK,
    bottomRight = Quadrant.BLOCK,
    bottomLeft = Quadrant.BLOCK,
    verticalLeft = Quadrant.BLOCK,
    verticalRight = Quadrant.BLOCK,
    horizontalTop = Quadrant.TOP_HALF,
    horizontalBottom = Quadrant.BOTTOM_HALF
)

/**
 * Solid border set using full blocks for all sides.
 *
 * ```
 * ████
 * █xx█
 * █xx█
 * ████
 * ```
 */
val FULL = Set(
    topLeft = Block.FULL,
    topRight = Block.FULL,
    bottomLeft = Block.FULL,
    bottomRight = Block.FULL,
    verticalLeft = Block.FULL,
    verticalRight = Block.FULL,
    horizontalTop = Block.FULL,
    horizontalBottom = Block.FULL
)

/**
 * Empty border set using spaces for all sides.
 *
 * This is useful for ensuring that the border style is applied to a border
 * on a block with a title without actually drawing a border.
 */
val EMPTY = Set(
    topLeft = " ",
    topRight = " ",
    bottomLeft = " ",
    bottomRight = " ",
    verticalLeft = " ",
    verticalRight = " ",
    horizontalTop = " ",
    horizontalBottom = " "
)
