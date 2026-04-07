// port-lint: source ratatui-core/src/buffer/cell_width.rs
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
        val ch = this[0]
        // Rust fast-paths for single-byte ASCII (str::len() == 1). Kotlin `length` counts chars,
        // so explicitly restrict this to ASCII.
        if (ch.code <= 0x7F) {
            return 1u
        }
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
