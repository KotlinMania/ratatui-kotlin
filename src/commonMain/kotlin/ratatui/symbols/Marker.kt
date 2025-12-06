package ratatui.symbols

/**
 * Marker dot symbol.
 */
const val DOT = "•"

/**
 * Marker to use when plotting data points.
 */
enum class Marker {
    /**
     * One point per cell in shape of dot (`•`)
     */
    Dot,

    /**
     * One point per cell in shape of a block (`█`)
     */
    Block,

    /**
     * One point per cell in the shape of a bar (`▄`)
     */
    Bar,

    /**
     * Use the Unicode Braille Patterns block to represent data points.
     *
     * This is a 2x4 grid of dots, where each dot can be either on or off.
     *
     * Note: Support for this marker is limited to terminals and fonts that support Unicode
     * Braille Patterns. If your terminal does not support this, you will see unicode replacement
     * characters (`�`) instead of Braille dots (`⠓`, `⣇`, `⣿`).
     */
    Braille,

    /**
     * Use the unicode block and half block characters (`█`, `▄`, and `▀`) to represent points in
     * a grid that is double the resolution of the terminal. Because each terminal cell is
     * generally about twice as tall as it is wide, this allows for a square grid of pixels.
     */
    HalfBlock,

    /**
     * Use quadrant characters to represent data points.
     *
     * Quadrant characters display densely packed and regularly spaced pseudo-pixels with a 2x2
     * resolution per character, without visible bands between cells.
     */
    Quadrant,

    /**
     * Use sextant characters from the Unicode Symbols for Legacy Computing
     * Supplement to represent data points.
     *
     * Sextant characters display densely packed and regularly spaced pseudo-pixels with a 2x3
     * resolution per character, without visible bands between cells.
     *
     * Note: the Symbols for Legacy Computing Supplement block is a relatively recent addition to
     * unicode that is less broadly supported than Braille dots.
     */
    Sextant,

    /**
     * Use octant characters from the Unicode Symbols for Legacy Computing
     * Supplement to represent data points.
     *
     * Octant characters have the same 2x4 resolution as Braille characters but display densely
     * packed and regularly spaced pseudo-pixels, without visible bands between cells.
     *
     * Note: the Symbols for Legacy Computing Supplement block is a relatively recent addition to
     * unicode that is less broadly supported than Braille dots.
     */
    Octant;

    companion object {
        /**
         * Default marker is Dot.
         */
        fun default(): Marker = Dot
    }
}
