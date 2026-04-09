// port-lint: source ratatui-core/src/style/palette.rs
@file:Suppress("unused")
/**
 * A module for defining color palettes.
 *
 * This file mirrors `ratatui-core`'s `style/palette.rs` module entrypoint.
 *
 * Upstream declares submodules:
 * - `material`
 * - `tailwind`
 */
package ratatui.style.palette

// Rust:
//   pub mod material;
//   pub mod tailwind;
private val material = ratatui.style.palette.material.AccentedPalette::class
private val tailwind = ratatui.style.palette.tailwind.Palette::class
