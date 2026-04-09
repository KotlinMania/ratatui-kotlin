// port-lint: source ratatui-core/src/symbols.rs
@file:Suppress("unused")

package ratatui.symbols

/**
 * Symbols and markers for drawing various widgets.
 *
 * Transliteration of `ratatui-core/src/symbols.rs`.
 *
 * The upstream Rust module is an entry point that:
 * - re-exports [DOT] and [Marker]
 * - exposes the symbol submodules:
 *   - `bar`
 *   - `block`
 *   - `border`
 *   - `braille`
 *   - `half_block`
 *   - `line`
 *   - `marker`
 *   - `merge`
 *   - `pixel`
 *   - `scrollbar`
 *   - `shade`
 */

private val _dotExport: String = DOT
private val _markerExport: Marker = Marker.default()

private val _barModule: Any = ratatui.symbols.bar.Bar
private val _blockModule: Any = ratatui.symbols.block.Block
private val _borderModule: Any = ratatui.symbols.border.PLAIN
private val _brailleModule: Any = Braille
private val _halfBlockModule: Any = HalfBlock
private val _lineModule: Any = ratatui.symbols.line.Line
private val _mergeModule: Any = ratatui.symbols.merge.MergeStrategy
private val _pixelModule: Any = Pixel
private val _scrollbarModule: Any = ratatui.symbols.scrollbar.VERTICAL
private val _shadeModule: Any = ratatui.symbols.shade.Shade
