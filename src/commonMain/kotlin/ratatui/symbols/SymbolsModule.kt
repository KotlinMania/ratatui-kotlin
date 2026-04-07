// port-lint: source ratatui-core/src/symbols.rs
package ratatui.symbols

/**
 * Symbols and markers for drawing various widgets.
 *
 * Transliteration of `ratatui-core/src/symbols.rs`.
 *
 * Rust source (for reference):
 * - `pub use marker::{DOT, Marker};`
 * - `pub mod bar;`
 * - `pub mod block;`
 * - `pub mod border;`
 * - `pub mod braille;`
 * - `pub mod half_block;`
 * - `pub mod line;`
 * - `pub mod marker;`
 * - `pub mod merge;`
 * - `pub mod pixel;`
 * - `pub mod scrollbar;`
 * - `pub mod shade;`
 *
 * In Kotlin, the marker exports live in [ratatui.symbols], and the symbol sets live in
 * subpackages (for example, [ratatui.symbols.bar] and [ratatui.symbols.border]).
 */
@Suppress("unused")
internal object SymbolsModule {
    // Mirrors `pub use marker::{DOT, Marker};`.
    val dot = DOT
    val marker = Marker::class
}
