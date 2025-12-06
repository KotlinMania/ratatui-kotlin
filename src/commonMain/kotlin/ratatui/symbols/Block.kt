package ratatui.symbols.block

/**
 * Block characters for drawing horizontal bar charts.
 * These are left-aligned blocks of varying widths.
 */
object Block {
    const val FULL = "█"
    const val SEVEN_EIGHTHS = "▉"
    const val THREE_QUARTERS = "▊"
    const val FIVE_EIGHTHS = "▋"
    const val HALF = "▌"
    const val THREE_EIGHTHS = "▍"
    const val ONE_QUARTER = "▎"
    const val ONE_EIGHTH = "▏"
}

/**
 * A set of block symbols for drawing charts.
 */
data class Set(
    val full: String,
    val sevenEighths: String,
    val threeQuarters: String,
    val fiveEighths: String,
    val half: String,
    val threeEighths: String,
    val oneQuarter: String,
    val oneEighth: String,
    val empty: String
) {
    companion object {
        /**
         * Default block set with nine levels of granularity.
         */
        fun default(): Set = NINE_LEVELS
    }
}

/**
 * Block set with three levels: full, half, and empty.
 */
val THREE_LEVELS = Set(
    full = Block.FULL,
    sevenEighths = Block.FULL,
    threeQuarters = Block.HALF,
    fiveEighths = Block.HALF,
    half = Block.HALF,
    threeEighths = Block.HALF,
    oneQuarter = Block.HALF,
    oneEighth = " ",
    empty = " "
)

/**
 * Block set with nine levels of granularity.
 */
val NINE_LEVELS = Set(
    full = Block.FULL,
    sevenEighths = Block.SEVEN_EIGHTHS,
    threeQuarters = Block.THREE_QUARTERS,
    fiveEighths = Block.FIVE_EIGHTHS,
    half = Block.HALF,
    threeEighths = Block.THREE_EIGHTHS,
    oneQuarter = Block.ONE_QUARTER,
    oneEighth = Block.ONE_EIGHTH,
    empty = " "
)
