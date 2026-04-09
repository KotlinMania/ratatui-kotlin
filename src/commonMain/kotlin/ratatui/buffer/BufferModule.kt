// port-lint: source ratatui-core/src/buffer.rs
@file:Suppress("unused")
/**
 * A module for the [Buffer] and [Cell] types.
 *
 * This mirrors `ratatui-core`'s `buffer.rs` module entrypoint and exists to keep the port aligned
 * with the Rust module structure without relying on Kotlin `typealias` re-exports.
 *
 * In the upstream Rust module, the entrypoint re-exports these types:
 * - [Buffer]
 * - [Cell] and [CellDiffOption]
 * - [CellWidth]
 * - [BufferDiff]
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
private val cell = Cell::class
private val cellDiffOption = CellDiffOption::class
private val cellWidth = CellWidth::class
private val diff = BufferDiff::class
