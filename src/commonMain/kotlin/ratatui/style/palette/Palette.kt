// port-lint: source ratatui-core/src/style/palette.rs
package ratatui.style.palette

/**
 * A module for defining color palettes.
 */

// pub mod material;
// pub mod tailwind;

@Suppress("unused")
internal object PaletteModule {
    // Mirrors module presence from Rust.
    val tailwindPalette = ratatui.style.palette.tailwind.Palette::class
}
