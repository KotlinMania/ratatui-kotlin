// port-lint: source ratatui-core/src/symbols.rs
/**
 * Symbols and markers for drawing various widgets.
 *
 * This file mirrors `ratatui-core`'s `symbols.rs` module entrypoint and exists to keep the port
 * aligned with the Rust module structure without relying on Kotlin `typealias` re-exports.
 *
 * In the Rust code, this module re-exports [Marker] and [DOT] from `symbols::marker` and declares
 * submodules for the various symbol sets (bars, borders, braille, scrolling, shading, etc.).
 */
package ratatui.symbols
