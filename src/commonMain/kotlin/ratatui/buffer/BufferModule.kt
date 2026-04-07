// port-lint: source ratatui-core/src/buffer.rs
package ratatui.buffer

/**
 * A module for the [Buffer] and [Cell] types.
 */
@Suppress("unused")
internal object BufferModule {
    // mod assert;
    // mod buffer;
    // mod cell;
    // mod cell_width;
    // mod diff;
    //
    // pub use buffer::Buffer;
    // pub use cell::{Cell, CellDiffOption};
    // pub use cell_width::CellWidth;
    // pub use diff::BufferDiff;

    // Mirrors `pub use` exports from Rust.
    val buffer = Buffer::class
    val cell = Cell::class
    val cellDiffOption = CellDiffOption::class
    val cellWidth = CellWidth::class
    val bufferDiff = BufferDiff::class
}
