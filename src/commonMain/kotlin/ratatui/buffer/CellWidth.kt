package ratatui.buffer

import ratatui.text.unicodeWidth

/**
 * Returns the display width in terminal cells.
 *
 * Transliteration of `ratatui_core::buffer::CellWidth`.
 */
interface CellWidth {
    fun cellWidth(): UShort
}

/**
 * CellWidth for a string.
 *
 * Note: Control characters should be filtered out before reaching this point. Single-byte control
 * characters that slip through will be reported as width 1 (matching the Rust note).
 */
fun String.cellWidth(): UShort {
    if (length == 1) {
        // Rust uses debug_assert to validate this isn't ASCII control; keep behavior without throwing.
        return 1u
    }
    return unicodeWidth(this).toUShort()
}

/**
 * CellWidth for a Cell.
 *
 * Returns ForcedWidth when set, otherwise falls back to the width of the cell's symbol.
 */
fun Cell.cellWidth(): UShort {
    return when (val option = diffOption) {
        CellDiffOption.Skip -> symbol().cellWidth()
        CellDiffOption.None -> symbol().cellWidth()
        is CellDiffOption.ForcedWidth -> option.width.get()
    }
}

