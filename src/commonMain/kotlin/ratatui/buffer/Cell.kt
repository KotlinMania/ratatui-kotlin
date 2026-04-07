// port-lint: source ratatui-core/src/buffer/cell.rs
package ratatui.buffer

import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.symbols.merge.MergeStrategy

/**
 * A non-zero unsigned short value.
 *
 * This mirrors Rust's `core::num::NonZeroU16`.
 */
data class NonZeroUShort private constructor(
    private val value: UShort
) {
    fun get(): UShort = value

    companion object {
        fun new(value: UShort): NonZeroUShort? = if (value == 0.toUShort()) null else NonZeroUShort(value)
    }
}

/**
 * Cell diffing options.
 *
 * Transliteration of `ratatui_core::buffer::CellDiffOption`.
 */
sealed class CellDiffOption {
    /** No special option. */
    data object None : CellDiffOption()

    /** Skip this cell when diffing. */
    data object Skip : CellDiffOption()

    /**
     * Force a width regardless of the symbol text width.
     *
     * Escape sequences can have a computed width that doesn't match what is written to the screen.
     */
    data class ForcedWidth(val width: NonZeroUShort) : CellDiffOption()
}

/**
 * A buffer cell.
 *
 * Transliteration of `ratatui_core::buffer::Cell`.
 */
class Cell private constructor(
    private var symbol: String?,
    var fg: Color,
    var bg: Color,
    var modifier: Modifier,
    var diffOption: CellDiffOption,
    @Deprecated("use setDiffOption(CellDiffOption.Skip) instead")
    var skip: Boolean
) {
    companion object {
        @Suppress("DEPRECATION")
        val EMPTY: Cell = Cell(
            symbol = null,
            fg = Color.Reset,
            bg = Color.Reset,
            modifier = Modifier.empty(),
            diffOption = CellDiffOption.None,
            skip = false
        )

        fun new(symbol: String): Cell = Cell(
            symbol = symbol,
            fg = Color.Reset,
            bg = Color.Reset,
            modifier = Modifier.empty(),
            diffOption = CellDiffOption.None,
            skip = false
        )
    }

    fun clone(): Cell = Cell(
        symbol = symbol,
        fg = fg,
        bg = bg,
        modifier = modifier,
        diffOption = diffOption,
        skip = skip
    )

    /**
     * Gets the symbol of the cell.
     *
     * If the cell has no symbol, returns a single space character.
     */
    fun symbol(): String = symbol ?: " "

    /**
     * Merges the symbol of the cell with the one already on the cell, using the provided strategy.
     */
    fun mergeSymbol(symbol: String, strategy: MergeStrategy): Cell {
        val merged = this.symbol?.let { existing -> strategy.merge(existing, symbol) } ?: symbol
        this.symbol = merged
        return this
    }

    /** Sets the symbol of the cell. */
    fun setSymbol(symbol: String): Cell {
        this.symbol = symbol
        return this
    }

    /** Appends a symbol to the cell. This is particularly useful for zero-width characters. */
    internal fun appendSymbol(symbol: String): Cell {
        val existing = this.symbol ?: ""
        this.symbol = existing + symbol
        return this
    }

    /** Sets the symbol of the cell to a single character. */
    fun setChar(ch: Char): Cell {
        this.symbol = ch.toString()
        return this
    }

    /** Sets the foreground color of the cell. */
    fun setFg(color: Color): Cell {
        fg = color
        return this
    }

    /** Sets the background color of the cell. */
    fun setBg(color: Color): Cell {
        bg = color
        return this
    }

    /**
     * Sets the style of the cell.
     *
     * Transliteration of Rust: `set_style<S: Into<Style>>`.
     */
    fun setStyle(style: Style): Cell {
        style.fg?.let { fg = it }
        style.bg?.let { bg = it }
        modifier = modifier.insert(style.addModifier)
        modifier = modifier.remove(style.subModifier)
        return this
    }

    /** Returns the style of the cell. */
    fun style(): Style {
        return Style(
            fg = fg,
            bg = bg,
            addModifier = modifier,
            subModifier = Modifier.empty()
        )
    }

    @Deprecated("use setDiffOption(CellDiffOption.Skip) instead")
    @Suppress("DEPRECATION")
    fun setSkip(skip: Boolean): Cell {
        this.skip = skip
        return this
    }

    fun setDiffOption(diffOption: CellDiffOption): Cell {
        this.diffOption = diffOption
        return this
    }

    @Suppress("DEPRECATION")
    fun reset() {
        val empty = EMPTY
        symbol = empty.symbol
        fg = empty.fg
        bg = empty.bg
        modifier = empty.modifier
        diffOption = empty.diffOption
        skip = empty.skip
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cell) return false

        val symbolsEq = symbol() == other.symbol()
        if (!symbolsEq) return false
        if (fg != other.fg) return false
        if (bg != other.bg) return false
        if (modifier != other.modifier) return false
        if (diffOption != other.diffOption) return false

        @Suppress("DEPRECATION")
        if (skip != other.skip) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol().hashCode()
        result = 31 * result + fg.hashCode()
        result = 31 * result + bg.hashCode()
        result = 31 * result + modifier.hashCode()
        result = 31 * result + diffOption.hashCode()
        @Suppress("DEPRECATION")
        result = 31 * result + skip.hashCode()
        return result
    }
}
