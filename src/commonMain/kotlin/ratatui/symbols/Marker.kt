// port-lint: source ratatui-core/src/symbols/marker.rs
package ratatui.symbols

/**
 * Marker dot symbol.
 */
const val DOT: String = "•"

/**
 * Marker to use when plotting data points.
 *
 * Transliteration of `ratatuiCore::symbols::marker::Marker`.
 */
sealed class Marker {
    /**
     * One point per cell in shape of dot (`•`)
     */
    data object Dot : Marker()

    /**
     * One point per cell in shape of a block (`█`)
     */
    data object Block : Marker()

    /**
     * One point per cell in the shape of a bar (`▄`)
     */
    data object Bar : Marker()

    /**
     * Use the [Unicode Braille Patterns](https://en.wikipedia.org/wiki/BraillePatterns) block to
     * represent data points.
     *
     * This is a 2x4 grid of dots, where each dot can be either on or off.
     *
     * Note: Support for this marker is limited to terminals and fonts that support Unicode
     * Braille Patterns. If your terminal does not support this, you will see unicode replacement
     * characters (`�`) instead of Braille dots (`⠓`, `⣇`, `⣿`).
     */
    data object Braille : Marker()

    /**
     * Use the unicode block and half block characters (`█`, `▄`, and `▀`) to represent points in
     * a grid that is double the resolution of the terminal. Because each terminal cell is
     * generally about twice as tall as it is wide, this allows for a square grid of pixels.
     */
    data object HalfBlock : Marker()

    /**
     * Use quadrant characters to represent data points.
     *
     * Quadrant characters display densely packed and regularly spaced pseudo-pixels with a 2x2
     * resolution per character, without visible bands between cells.
     */
    data object Quadrant : Marker()

    /**
     * Use sextant characters from the [Unicode Symbols for Legacy Computing
     * Supplement](https://en.wikipedia.org/wiki/SymbolsForLegacyComputingSupplement) to
     * represent data points.
     *
     * Sextant characters display densely packed and regularly spaced pseudo-pixels with a 2x3
     * resolution per character, without visible bands between cells.
     *
     * Note: the Symbols for Legacy Computing Supplement block is a relatively recent addition to
     * unicode that is less broadly supported than Braille dots. If your terminal does not support
     * this, you will see unicode replacement characters (`�`) instead of sextants (`🬌`, `🬲`, `🬑`).
     */
    data object Sextant : Marker()

    /**
     * Use octant characters from the [Unicode Symbols for Legacy Computing
     * Supplement](https://en.wikipedia.org/wiki/SymbolsForLegacyComputingSupplement) to
     * represent data points.
     *
     * Octant characters have the same 2x4 resolution as Braille characters but display densely
     * packed and regularly spaced pseudo-pixels, without visible bands between cells.
     *
     * Note: the Symbols for Legacy Computing Supplement block is a relatively recent addition to
     * unicode that is less broadly supported than Braille dots. If your terminal does not support
     * this, you will see unicode replacement characters (`�`) instead of octants (`𜴇`, `𜷀`, `𜴷`).
     */
    data object Octant : Marker()

    /**
     * Custom marker where the supplied char is applied once per cell.
     */
    data class Custom(val char: Char) : Marker() {
        override fun toString(): String = "Custom"
    }

    override fun toString(): String {
        return when (this) {
            Bar -> "Bar"
            Block -> "Block"
            Braille -> "Braille"
            Dot -> "Dot"
            HalfBlock -> "HalfBlock"
            Octant -> "Octant"
            Quadrant -> "Quadrant"
            Sextant -> "Sextant"
            is Custom -> "Custom"
        }
    }

    companion object {
        /**
         * Default marker is Dot.
         */
        fun default(): Marker = Dot

        /**
         * Parse a marker from its string representation.
         *
         * Mirrors the upstream `EnumString`-derived parsing used by `"Dot".parse::<Marker>()`.
         */
        fun fromStr(value: String): Marker? {
            return when (value) {
                "Dot" -> Dot
                "Block" -> Block
                "Bar" -> Bar
                "Braille" -> Braille
                "HalfBlock" -> HalfBlock
                "Quadrant" -> Quadrant
                "Sextant" -> Sextant
                "Octant" -> Octant
                "Custom" -> Custom('+') // No payload in string form; choose a stable default.
                else -> null
            }
        }
    }
}
