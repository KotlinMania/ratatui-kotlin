package ratatui.symbols.line

/**
 * Line drawing characters for borders and separators.
 */
object Line {
    // Vertical lines
    const val VERTICAL = "│"
    const val DOUBLE_VERTICAL = "║"
    const val THICK_VERTICAL = "┃"
    const val LIGHT_DOUBLE_DASH_VERTICAL = "╎"
    const val HEAVY_DOUBLE_DASH_VERTICAL = "╏"
    const val LIGHT_TRIPLE_DASH_VERTICAL = "┆"
    const val HEAVY_TRIPLE_DASH_VERTICAL = "┇"
    const val LIGHT_QUADRUPLE_DASH_VERTICAL = "┊"
    const val HEAVY_QUADRUPLE_DASH_VERTICAL = "┋"

    // Horizontal lines
    const val HORIZONTAL = "─"
    const val DOUBLE_HORIZONTAL = "═"
    const val THICK_HORIZONTAL = "━"
    const val LIGHT_DOUBLE_DASH_HORIZONTAL = "╌"
    const val HEAVY_DOUBLE_DASH_HORIZONTAL = "╍"
    const val LIGHT_TRIPLE_DASH_HORIZONTAL = "┄"
    const val HEAVY_TRIPLE_DASH_HORIZONTAL = "┅"
    const val LIGHT_QUADRUPLE_DASH_HORIZONTAL = "┈"
    const val HEAVY_QUADRUPLE_DASH_HORIZONTAL = "┉"

    // Corners - top right
    const val TOP_RIGHT = "┐"
    const val ROUNDED_TOP_RIGHT = "╮"
    const val DOUBLE_TOP_RIGHT = "╗"
    const val THICK_TOP_RIGHT = "┓"

    // Corners - top left
    const val TOP_LEFT = "┌"
    const val ROUNDED_TOP_LEFT = "╭"
    const val DOUBLE_TOP_LEFT = "╔"
    const val THICK_TOP_LEFT = "┏"

    // Corners - bottom right
    const val BOTTOM_RIGHT = "┘"
    const val ROUNDED_BOTTOM_RIGHT = "╯"
    const val DOUBLE_BOTTOM_RIGHT = "╝"
    const val THICK_BOTTOM_RIGHT = "┛"

    // Corners - bottom left
    const val BOTTOM_LEFT = "└"
    const val ROUNDED_BOTTOM_LEFT = "╰"
    const val DOUBLE_BOTTOM_LEFT = "╚"
    const val THICK_BOTTOM_LEFT = "┗"

    // T-junctions
    const val VERTICAL_LEFT = "┤"
    const val DOUBLE_VERTICAL_LEFT = "╣"
    const val THICK_VERTICAL_LEFT = "┫"

    const val VERTICAL_RIGHT = "├"
    const val DOUBLE_VERTICAL_RIGHT = "╠"
    const val THICK_VERTICAL_RIGHT = "┣"

    const val HORIZONTAL_DOWN = "┬"
    const val DOUBLE_HORIZONTAL_DOWN = "╦"
    const val THICK_HORIZONTAL_DOWN = "┳"

    const val HORIZONTAL_UP = "┴"
    const val DOUBLE_HORIZONTAL_UP = "╩"
    const val THICK_HORIZONTAL_UP = "┻"

    // Cross
    const val CROSS = "┼"
    const val DOUBLE_CROSS = "╬"
    const val THICK_CROSS = "╋"
}

/**
 * A set of line symbols for drawing borders.
 */
data class Set(
    val vertical: String,
    val horizontal: String,
    val topRight: String,
    val topLeft: String,
    val bottomRight: String,
    val bottomLeft: String,
    val verticalLeft: String,
    val verticalRight: String,
    val horizontalDown: String,
    val horizontalUp: String,
    val cross: String
) {
    companion object {
        /**
         * Default line set with normal single-line characters.
         */
        fun default(): Set = NORMAL
    }
}

/** Normal single-line border set */
val NORMAL = Set(
    vertical = Line.VERTICAL,
    horizontal = Line.HORIZONTAL,
    topRight = Line.TOP_RIGHT,
    topLeft = Line.TOP_LEFT,
    bottomRight = Line.BOTTOM_RIGHT,
    bottomLeft = Line.BOTTOM_LEFT,
    verticalLeft = Line.VERTICAL_LEFT,
    verticalRight = Line.VERTICAL_RIGHT,
    horizontalDown = Line.HORIZONTAL_DOWN,
    horizontalUp = Line.HORIZONTAL_UP,
    cross = Line.CROSS
)

/** Rounded corners border set */
val ROUNDED = Set(
    vertical = Line.VERTICAL,
    horizontal = Line.HORIZONTAL,
    topRight = Line.ROUNDED_TOP_RIGHT,
    topLeft = Line.ROUNDED_TOP_LEFT,
    bottomRight = Line.ROUNDED_BOTTOM_RIGHT,
    bottomLeft = Line.ROUNDED_BOTTOM_LEFT,
    verticalLeft = Line.VERTICAL_LEFT,
    verticalRight = Line.VERTICAL_RIGHT,
    horizontalDown = Line.HORIZONTAL_DOWN,
    horizontalUp = Line.HORIZONTAL_UP,
    cross = Line.CROSS
)

/** Double-line border set */
val DOUBLE = Set(
    vertical = Line.DOUBLE_VERTICAL,
    horizontal = Line.DOUBLE_HORIZONTAL,
    topRight = Line.DOUBLE_TOP_RIGHT,
    topLeft = Line.DOUBLE_TOP_LEFT,
    bottomRight = Line.DOUBLE_BOTTOM_RIGHT,
    bottomLeft = Line.DOUBLE_BOTTOM_LEFT,
    verticalLeft = Line.DOUBLE_VERTICAL_LEFT,
    verticalRight = Line.DOUBLE_VERTICAL_RIGHT,
    horizontalDown = Line.DOUBLE_HORIZONTAL_DOWN,
    horizontalUp = Line.DOUBLE_HORIZONTAL_UP,
    cross = Line.DOUBLE_CROSS
)

/** Thick border set */
val THICK = Set(
    vertical = Line.THICK_VERTICAL,
    horizontal = Line.THICK_HORIZONTAL,
    topRight = Line.THICK_TOP_RIGHT,
    topLeft = Line.THICK_TOP_LEFT,
    bottomRight = Line.THICK_BOTTOM_RIGHT,
    bottomLeft = Line.THICK_BOTTOM_LEFT,
    verticalLeft = Line.THICK_VERTICAL_LEFT,
    verticalRight = Line.THICK_VERTICAL_RIGHT,
    horizontalDown = Line.THICK_HORIZONTAL_DOWN,
    horizontalUp = Line.THICK_HORIZONTAL_UP,
    cross = Line.THICK_CROSS
)

/** Light double-dashed border set */
val LIGHT_DOUBLE_DASHED = NORMAL.copy(
    vertical = Line.LIGHT_DOUBLE_DASH_VERTICAL,
    horizontal = Line.LIGHT_DOUBLE_DASH_HORIZONTAL
)

/** Heavy double-dashed border set */
val HEAVY_DOUBLE_DASHED = THICK.copy(
    vertical = Line.HEAVY_DOUBLE_DASH_VERTICAL,
    horizontal = Line.HEAVY_DOUBLE_DASH_HORIZONTAL
)

/** Light triple-dashed border set */
val LIGHT_TRIPLE_DASHED = NORMAL.copy(
    vertical = Line.LIGHT_TRIPLE_DASH_VERTICAL,
    horizontal = Line.LIGHT_TRIPLE_DASH_HORIZONTAL
)

/** Heavy triple-dashed border set */
val HEAVY_TRIPLE_DASHED = THICK.copy(
    vertical = Line.HEAVY_TRIPLE_DASH_VERTICAL,
    horizontal = Line.HEAVY_TRIPLE_DASH_HORIZONTAL
)

/** Light quadruple-dashed border set */
val LIGHT_QUADRUPLE_DASHED = NORMAL.copy(
    vertical = Line.LIGHT_QUADRUPLE_DASH_VERTICAL,
    horizontal = Line.LIGHT_QUADRUPLE_DASH_HORIZONTAL
)

/** Heavy quadruple-dashed border set */
val HEAVY_QUADRUPLE_DASHED = THICK.copy(
    vertical = Line.HEAVY_QUADRUPLE_DASH_VERTICAL,
    horizontal = Line.HEAVY_QUADRUPLE_DASH_HORIZONTAL
)
