// port-lint: source ratatui-core/src/buffer/cell_width.rs
package ratatui.buffer

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
    if (isEmpty()) return 0u
    if (length == 1) {
        val ch = this[0]
        // Rust fast-paths for single-byte ASCII (str::len() == 1). Kotlin `length` counts chars,
        // so explicitly restrict this to ASCII.
        if (ch.code <= 0x7F) {
            return 1u
        }
    }
    return width().toUShort()
}
