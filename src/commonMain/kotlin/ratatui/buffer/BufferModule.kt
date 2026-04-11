// port-lint: source ratatui-core/src/buffer.rs
@file:Suppress("unused")
/**
 * A module for the [Buffer] and [Cell] types.
 */
package ratatui.buffer

// Rust:
//   mod assert;
//   mod buffer;
//   mod cell;
//   mod cell_width;
//   mod diff;
private val assert = ::assertBufferEq
private val buffer = Buffer::class
private val cell = arrayOf(Cell::class, CellDiffOption::class)
private val cellWidth = CellWidth::class
private val diff = BufferDiff::class
