// port-lint: source ratatui-core/src/buffer.rs
package ratatui.buffer

import ratatui.buffer.Buffer
import ratatui.buffer.BufferDiff
import ratatui.buffer.Cell
import ratatui.buffer.CellDiffOption
import ratatui.buffer.CellWidth

/**
 * A module for the [Buffer] and [Cell] types.
 */

@Suppress("unused")
private val exports = listOf(
    Buffer::class,
    Cell::class,
    CellDiffOption::class,
    CellWidth::class,
    BufferDiff::class,
)
