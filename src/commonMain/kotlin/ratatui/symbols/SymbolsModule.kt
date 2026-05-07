// port-lint: source ratatui-core/src/symbols.rs
@file:Suppress("unused")
/**
 * Symbols and markers for drawing various widgets.
 */
package ratatui.symbols

// Rust:
//   pub use marker::{DOT, Marker};
private val marker = Marker::class
private val dot = DOT

// Rust:
//   pub mod bar; pub mod block; pub mod border; pub mod braille; pub mod half_block;
//   pub mod line; pub mod marker; pub mod merge; pub mod pixel; pub mod scrollbar; pub mod shade;
private val bar = ratatui.symbols.bar.Bar::class
private val block = ratatui.symbols.block.Block::class
private val border = ratatui.symbols.border.Set::class
private val braille = Braille::class
private val halfBlock = HalfBlock::class
private val line = ratatui.symbols.line.Set::class
private val merge = ratatui.symbols.merge.MergeStrategy::class
private val pixel = Pixel::class
private val scrollbar = ratatui.symbols.scrollbar.Set::class
private val shade = ratatui.symbols.shade.Shade::class
