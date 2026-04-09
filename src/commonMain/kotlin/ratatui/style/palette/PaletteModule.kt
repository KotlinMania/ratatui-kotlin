// port-lint: source ratatui-core/src/style/palette.rs
package ratatui.style.palette

import ratatui.style.palette.tailwind.Palette as TailwindPalette

/**
 * A module for defining color palettes.
 *
 * This mirrors Rust's `ratatui-core/src/style/palette.rs`, which declares palette submodules such
 * as `material` and `tailwind`.
 */

@Suppress("unused")
private val exports = listOf(
    TailwindPalette::class,
)
