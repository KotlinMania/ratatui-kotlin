// port-lint: source ratatui-core/src/buffer.rs
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
