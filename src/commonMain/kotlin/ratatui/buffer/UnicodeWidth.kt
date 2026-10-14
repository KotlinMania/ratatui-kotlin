// port-lint: source ratatui-core/src/buffer/cell_width.rs (UnicodeWidthStr::width delegate)
package ratatui.buffer

import io.github.kotlinmania.unicodewidth.unicodeWidth

/**
 * Returns the display width of this string in terminal cells.
 *
 * Delegates to `unicode-width-kotlin` (`io.github.kotlinmania:unicode-width-kotlin:0.1.1`),
 * which is a faithful port of Rust's `unicode_width::UnicodeWidthStr::width()`.
 *
 * This replaces the previous hand-rolled implementation that used ad-hoc width
 * tables and grapheme-cluster heuristics. The published library handles ZWJ
 * sequences, combining marks, variation selectors, and wide characters using
 * the same Unicode width tables as the upstream Rust crate.
 */
internal fun String.width(): Int = unicodeWidth()
