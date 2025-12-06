package ratatui.buffer

import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.symbols.merge.MergeStrategy

/**
 * A buffer cell
 *
 * Each cell in the buffer contains a symbol (grapheme cluster), foreground color,
 * background color, and text modifiers.
 */
data class Cell(
    /**
     * The string to be drawn in the cell.
     *
     * This accepts unicode grapheme clusters which might take up more than one cell.
     * If null, the cell is considered empty and will display as a single space.
     */
    private var symbol: String? = null,

    /** The foreground color of the cell. */
    var fg: Color = Color.Reset,

    /** The background color of the cell. */
    var bg: Color = Color.Reset,

    /** The underline color of the cell. */
    var underlineColor: Color = Color.Reset,

    /** The modifier of the cell. */
    var modifier: Modifier = Modifier.empty(),

    /** Whether the cell should be skipped when copying (diffing) the buffer to the screen. */
    var skip: Boolean = false
) {

    companion object {
        /** An empty Cell */
        val EMPTY: Cell = Cell()

        /**
         * Creates a new Cell with the given symbol.
         */
        fun new(symbol: String): Cell = Cell(symbol = symbol)
    }

    /**
     * Gets the symbol of the cell.
     *
     * If the cell has no symbol, returns a single space character.
     */
    fun symbol(): String = symbol ?: " "

    /**
     * Merges the symbol of the cell with the one already on the cell, using the provided
     * [MergeStrategy].
     *
     * Merges Box Drawing Unicode block characters to create a single character representing
     * their combination, useful for border collapsing. Currently limited to box drawing
     * characters, with potential future support for others.
     *
     * Merging may not be perfect due to Unicode limitations; some symbol combinations might not
     * produce a valid character. [MergeStrategy] defines how to handle such cases, e.g.,
     * `Exact` for valid merges only, or `Fuzzy` for close matches.
     *
     * If the cell has no symbol set, it will set the symbol to the provided one rather than
     * merging.
     *
     * @param newSymbol The symbol to merge with the existing one
     * @param strategy The merge strategy to use
     * @return This cell for chaining
     */
    fun mergeSymbol(newSymbol: String, strategy: MergeStrategy): Cell {
        val mergedSymbol = symbol?.let { strategy.merge(it, newSymbol) } ?: newSymbol
        symbol = mergedSymbol
        return this
    }

    /**
     * Sets the symbol of the cell.
     *
     * @param newSymbol The new symbol
     * @return This cell for chaining
     */
    fun setSymbol(newSymbol: String): Cell {
        symbol = newSymbol
        return this
    }

    /**
     * Appends a symbol to the cell.
     *
     * This is particularly useful for adding zero-width characters to the cell.
     *
     * @param appendSymbol The symbol to append
     * @return This cell for chaining
     */
    internal fun appendSymbol(appendSymbol: String): Cell {
        symbol = (symbol ?: "") + appendSymbol
        return this
    }

    /**
     * Sets the symbol of the cell to a single character.
     *
     * @param ch The character to set
     * @return This cell for chaining
     */
    fun setChar(ch: Char): Cell {
        symbol = ch.toString()
        return this
    }

    /**
     * Sets the foreground color of the cell.
     *
     * @param color The foreground color
     * @return This cell for chaining
     */
    fun setFg(color: Color): Cell {
        fg = color
        return this
    }

    /**
     * Sets the background color of the cell.
     *
     * @param color The background color
     * @return This cell for chaining
     */
    fun setBg(color: Color): Cell {
        bg = color
        return this
    }

    /**
     * Sets the style of the cell.
     *
     * @param style The style to apply
     * @return This cell for chaining
     */
    fun setStyle(style: Style): Cell {
        style.fg?.let { fg = it }
        style.bg?.let { bg = it }
        style.underlineColor?.let { underlineColor = it }
        modifier = modifier.insert(style.addModifier)
        modifier = modifier.remove(style.subModifier)
        return this
    }

    /**
     * Returns the style of the cell.
     */
    fun style(): Style = Style(
        fg = fg,
        bg = bg,
        underlineColor = underlineColor,
        addModifier = modifier,
        subModifier = Modifier.empty()
    )

    /**
     * Sets the cell to be skipped when copying (diffing) the buffer to the screen.
     *
     * This is helpful when it is necessary to prevent the buffer from overwriting a cell that is
     * covered by an image from some terminal graphics protocol (Sixel / iTerm / Kitty ...).
     *
     * @param skip Whether to skip this cell
     * @return This cell for chaining
     */
    fun setSkip(skip: Boolean): Cell {
        this.skip = skip
        return this
    }

    /**
     * Resets the cell to the empty state.
     */
    fun reset() {
        symbol = null
        fg = Color.Reset
        bg = Color.Reset
        underlineColor = Color.Reset
        modifier = Modifier.empty()
        skip = false
    }

    /**
     * Compares two Cells for equality.
     *
     * Note that cells with no symbol (i.e., Cell.EMPTY) are considered equal to cells with a
     * single space symbol. This is to ensure that empty cells are treated uniformly,
     * regardless of how they were created.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cell) return false

        // Treat null symbol and " " as equal
        if (symbol() != other.symbol()) return false
        if (fg != other.fg) return false
        if (bg != other.bg) return false
        if (underlineColor != other.underlineColor) return false
        if (modifier != other.modifier) return false
        if (skip != other.skip) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol().hashCode()
        result = 31 * result + fg.hashCode()
        result = 31 * result + bg.hashCode()
        result = 31 * result + underlineColor.hashCode()
        result = 31 * result + modifier.hashCode()
        result = 31 * result + skip.hashCode()
        return result
    }
}

/**
 * Creates a Cell from a single character.
 */
fun Char.toCell(): Cell = Cell().setChar(this)
