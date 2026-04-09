// port-lint: source ratatui-core/src/buffer.rs
package ratatui.buffer

/**
 * A module for the [Buffer] and [Cell] types.
 *
 * This mirrors Rust's `ratatui-core/src/buffer.rs`, which declares submodules and publicly exports
 * the buffer primitives:
 * - [Buffer]
 * - [Cell]
 * - [CellDiffOption]
 * - [CellWidth]
 * - [BufferDiff]
 */

@Suppress("unused")
private val exports = listOf(
    Buffer::class,
    Cell::class,
    CellDiffOption::class,
    CellWidth::class,
    BufferDiff::class,
)
