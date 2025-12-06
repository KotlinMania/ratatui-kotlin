package ratatui.symbols.bar

/**
 * Bar characters for drawing vertical bar charts.
 * These are bottom-aligned blocks of varying heights.
 */
object Bar {
    const val FULL = "█"
    const val SEVEN_EIGHTHS = "▇"
    const val THREE_QUARTERS = "▆"
    const val FIVE_EIGHTHS = "▅"
    const val HALF = "▄"
    const val THREE_EIGHTHS = "▃"
    const val ONE_QUARTER = "▂"
    const val ONE_EIGHTH = "▁"
}

/**
 * A set of bar symbols for drawing charts.
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
         * Default bar set with nine levels of granularity.
         */
        fun default(): Set = NINE_LEVELS
    }
}

/**
 * Bar set with three levels: full, half, and empty.
 */
val THREE_LEVELS = Set(
    full = Bar.FULL,
    sevenEighths = Bar.FULL,
    threeQuarters = Bar.HALF,
    fiveEighths = Bar.HALF,
    half = Bar.HALF,
    threeEighths = Bar.HALF,
    oneQuarter = Bar.HALF,
    oneEighth = " ",
    empty = " "
)

/**
 * Bar set with nine levels of granularity.
 */
val NINE_LEVELS = Set(
    full = Bar.FULL,
    sevenEighths = Bar.SEVEN_EIGHTHS,
    threeQuarters = Bar.THREE_QUARTERS,
    fiveEighths = Bar.FIVE_EIGHTHS,
    half = Bar.HALF,
    threeEighths = Bar.THREE_EIGHTHS,
    oneQuarter = Bar.ONE_QUARTER,
    oneEighth = Bar.ONE_EIGHTH,
    empty = " "
)
