// port-lint: source ratatui-core/src/symbols.rs
package ratatui.symbols

import ratatui.symbols.bar.Bar
import ratatui.symbols.block.Block
import ratatui.symbols.border.Set as BorderSet
import ratatui.symbols.line.Line
import ratatui.symbols.merge.MergeStrategy
import ratatui.symbols.scrollbar.Set as ScrollbarSet
import ratatui.symbols.shade.Shade

/**
 * Symbols and markers for drawing various widgets.
 *
 * This mirrors Rust's `ratatui-core/src/symbols.rs`, which is primarily a module entry-point that
 * exposes the various symbol submodules (bar, block, border, etc).
 */

@Suppress("unused")
private val exports = listOf(
    DOT,
    Marker.Dot,
    Bar.FULL,
    Block.FULL,
    BorderSet::class,
    Line.VERTICAL,
    MergeStrategy.Replace,
    ScrollbarSet::class,
    Shade.LIGHT,
)
