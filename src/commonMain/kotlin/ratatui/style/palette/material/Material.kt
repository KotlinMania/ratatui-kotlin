// port-lint: source ratatui-core/src/style/palette/material.rs
package ratatui.style.palette.material

import ratatui.style.Color

/**
 * Material design color palettes.
 *
 * Represents the colors from the 2014 [Material design color palettes][palettes] by Google.
 *
 * [palettes]: https://m2.material.io/design/color/the-color-system.html#tools-for-picking-colors
 *
 * There are 16 palettes with accent colors, and 3 palettes without accent colors. Each palette
 * has 10 colors, with variants from 50 to 900. The accent palettes also have 4 accent colors with
 * variants from 100 to 700. Black and White are also included for completeness and to avoid being
 * affected by any terminal theme that might be in use.
 *
 * This module exists to provide a convenient way to use the colors from the [`matdesign-color`
 * crate] in your application.
 *
 * Example:
 * ```kotlin
 * import ratatui.style.Color
 * import ratatui.style.palette.material.BLUE
 * import ratatui.style.palette.material.RED
 *
 * check(RED.c500 == Color.Rgb(244u, 67u, 54u))
 * check(BLUE.c500 == Color.Rgb(33u, 150u, 243u))
 * ```
 *
 * [`matdesign-color` crate]: https://crates.io/crates/matdesign-color
 */

/**
 * A palette of colors for use in Material design with accent colors.
 *
 * This is a collection of colors that are used in Material design. They consist of a set of
 * colors from 50 to 900, and a set of accent colors from 100 to 700.
 */
data class AccentedPalette(
    val c50: Color,
    val c100: Color,
    val c200: Color,
    val c300: Color,
    val c400: Color,
    val c500: Color,
    val c600: Color,
    val c700: Color,
    val c800: Color,
    val c900: Color,
    val a100: Color,
    val a200: Color,
    val a400: Color,
    val a700: Color
) {
    companion object {
        /**
         * Create a new [AccentedPalette] from the given variants.
         *
         * The variants should be in the format `[0x00RRGGBB, ...]`.
         */
        fun fromVariants(variants: UIntArray): AccentedPalette {
            require(variants.size == 14) { "Expected 14 variants, got ${variants.size}" }
            return AccentedPalette(
                c50 = Color.fromU32(variants[0]),
                c100 = Color.fromU32(variants[1]),
                c200 = Color.fromU32(variants[2]),
                c300 = Color.fromU32(variants[3]),
                c400 = Color.fromU32(variants[4]),
                c500 = Color.fromU32(variants[5]),
                c600 = Color.fromU32(variants[6]),
                c700 = Color.fromU32(variants[7]),
                c800 = Color.fromU32(variants[8]),
                c900 = Color.fromU32(variants[9]),
                a100 = Color.fromU32(variants[10]),
                a200 = Color.fromU32(variants[11]),
                a400 = Color.fromU32(variants[12]),
                a700 = Color.fromU32(variants[13])
            )
        }
    }
}

/**
 * A palette of colors for use in Material design without accent colors.
 *
 * This is a collection of colors that are used in Material design. They consist of a set of
 * colors from 50 to 900.
 */
data class NonAccentedPalette(
    val c50: Color,
    val c100: Color,
    val c200: Color,
    val c300: Color,
    val c400: Color,
    val c500: Color,
    val c600: Color,
    val c700: Color,
    val c800: Color,
    val c900: Color
) {
    companion object {
        /**
         * Create a new [NonAccentedPalette] from the given variants.
         *
         * The variants should be in the format `[0x00RRGGBB, ...]`.
         */
        fun fromVariants(variants: UIntArray): NonAccentedPalette {
            require(variants.size == 10) { "Expected 10 variants, got ${variants.size}" }
            return NonAccentedPalette(
                c50 = Color.fromU32(variants[0]),
                c100 = Color.fromU32(variants[1]),
                c200 = Color.fromU32(variants[2]),
                c300 = Color.fromU32(variants[3]),
                c400 = Color.fromU32(variants[4]),
                c500 = Color.fromU32(variants[5]),
                c600 = Color.fromU32(variants[6]),
                c700 = Color.fromU32(variants[7]),
                c800 = Color.fromU32(variants[8]),
                c900 = Color.fromU32(variants[9])
            )
        }
    }
}

// Accented palettes

val RED: AccentedPalette = AccentedPalette.fromVariants(Variants.RED)
val PINK: AccentedPalette = AccentedPalette.fromVariants(Variants.PINK)
val PURPLE: AccentedPalette = AccentedPalette.fromVariants(Variants.PURPLE)
val DEEP_PURPLE: AccentedPalette = AccentedPalette.fromVariants(Variants.DEEP_PURPLE)
val INDIGO: AccentedPalette = AccentedPalette.fromVariants(Variants.INDIGO)
val BLUE: AccentedPalette = AccentedPalette.fromVariants(Variants.BLUE)
val LIGHT_BLUE: AccentedPalette = AccentedPalette.fromVariants(Variants.LIGHT_BLUE)
val CYAN: AccentedPalette = AccentedPalette.fromVariants(Variants.CYAN)
val TEAL: AccentedPalette = AccentedPalette.fromVariants(Variants.TEAL)
val GREEN: AccentedPalette = AccentedPalette.fromVariants(Variants.GREEN)
val LIGHT_GREEN: AccentedPalette = AccentedPalette.fromVariants(Variants.LIGHT_GREEN)
val LIME: AccentedPalette = AccentedPalette.fromVariants(Variants.LIME)
val YELLOW: AccentedPalette = AccentedPalette.fromVariants(Variants.YELLOW)
val AMBER: AccentedPalette = AccentedPalette.fromVariants(Variants.AMBER)
val ORANGE: AccentedPalette = AccentedPalette.fromVariants(Variants.ORANGE)
val DEEP_ORANGE: AccentedPalette = AccentedPalette.fromVariants(Variants.DEEP_ORANGE)

// Unaccented palettes

val BROWN: NonAccentedPalette = NonAccentedPalette.fromVariants(Variants.BROWN)
val GRAY: NonAccentedPalette = NonAccentedPalette.fromVariants(Variants.GRAY)
val BLUE_GRAY: NonAccentedPalette = NonAccentedPalette.fromVariants(Variants.BLUE_GRAY)

// Black and white included for completeness

val BLACK: Color = Color.fromU32(0x000000u)
val WHITE: Color = Color.fromU32(0xFFFFFFu)

private object Variants {
    val RED: UIntArray = uintArrayOf(
        0xFFEBEEu, 0xFFCDD2u, 0xEF9A9Au, 0xE57373u, 0xEF5350u, 0xF44336u, 0xE53935u, 0xD32F2Fu, 0xC62828u,
        0xB71C1Cu, 0xFF8A80u, 0xFF5252u, 0xFF1744u, 0xD50000u
    )
    val PINK: UIntArray = uintArrayOf(
        0xFCE4ECu, 0xF8BBD0u, 0xF48FB1u, 0xF06292u, 0xEC407Au, 0xE91E63u, 0xD81B60u, 0xC2185Bu, 0xAD1457u,
        0x880E4Fu, 0xFF80ABu, 0xFF4081u, 0xF50057u, 0xC51162u
    )
    val PURPLE: UIntArray = uintArrayOf(
        0xF3E5F5u, 0xE1BEE7u, 0xCE93D8u, 0xBA68C8u, 0xAB47BCu, 0x9C27B0u, 0x8E24AAu, 0x7B1FA2u, 0x6A1B9Au,
        0x4A148Cu, 0xEA80FCu, 0xE040FBu, 0xD500F9u, 0xAA00FFu
    )
    val DEEP_PURPLE: UIntArray = uintArrayOf(
        0xEDE7F6u, 0xD1C4E9u, 0xB39DDBu, 0x9575CDu, 0x7E57C2u, 0x673AB7u, 0x5E35B1u, 0x512DA8u, 0x4527A0u,
        0x311B92u, 0xB388FFu, 0x7C4DFFu, 0x651FFFu, 0x6200EAu
    )
    val INDIGO: UIntArray = uintArrayOf(
        0xE8EAF6u, 0xC5CAE9u, 0x9FA8DAu, 0x7986CBu, 0x5C6BC0u, 0x3F51B5u, 0x3949ABu, 0x303F9Fu, 0x283593u,
        0x1A237Eu, 0x8C9EFFu, 0x536DFEu, 0x3D5AFEu, 0x304FFEu
    )
    val BLUE: UIntArray = uintArrayOf(
        0xE3F2FDu, 0xBBDEFBu, 0x90CAF9u, 0x64B5F6u, 0x42A5F5u, 0x2196F3u, 0x1E88E5u, 0x1976D2u, 0x1565C0u,
        0x0D47A1u, 0x82B1FFu, 0x448AFFu, 0x2979FFu, 0x2962FFu
    )
    val LIGHT_BLUE: UIntArray = uintArrayOf(
        0xE1F5FEu, 0xB3E5FCu, 0x81D4FAu, 0x4FC3F7u, 0x29B6F6u, 0x03A9F4u, 0x039BE5u, 0x0288D1u, 0x0277BDu,
        0x01579Bu, 0x80D8FFu, 0x40C4FFu, 0x00B0FFu, 0x0091EAu
    )
    val CYAN: UIntArray = uintArrayOf(
        0xE0F7FAu, 0xB2EBF2u, 0x80DEEAu, 0x4DD0E1u, 0x26C6DAu, 0x00BCD4u, 0x00ACC1u, 0x0097A7u, 0x00838Fu,
        0x006064u, 0x84FFFFu, 0x18FFFFu, 0x00E5FFu, 0x00B8D4u
    )
    val TEAL: UIntArray = uintArrayOf(
        0xE0F2F1u, 0xB2DFDBu, 0x80CBC4u, 0x4DB6ACu, 0x26A69Au, 0x009688u, 0x00897Bu, 0x00796Bu, 0x00695Cu,
        0x004D40u, 0xA7FFEBu, 0x64FFDAu, 0x1DE9B6u, 0x00BFA5u
    )
    val GREEN: UIntArray = uintArrayOf(
        0xE8F5E9u, 0xC8E6C9u, 0xA5D6A7u, 0x81C784u, 0x66BB6Au, 0x4CAF50u, 0x43A047u, 0x388E3Cu, 0x2E7D32u,
        0x1B5E20u, 0xB9F6CAu, 0x69F0AEu, 0x00E676u, 0x00C853u
    )
    val LIGHT_GREEN: UIntArray = uintArrayOf(
        0xF1F8E9u, 0xDCEDC8u, 0xC5E1A5u, 0xAED581u, 0x9CCC65u, 0x8BC34Au, 0x7CB342u, 0x689F38u, 0x558B2Fu,
        0x33691Eu, 0xCCFF90u, 0xB2FF59u, 0x76FF03u, 0x64DD17u
    )
    val LIME: UIntArray = uintArrayOf(
        0xF9FBE7u, 0xF0F4C3u, 0xE6EE9Cu, 0xDCE775u, 0xD4E157u, 0xCDDC39u, 0xC0CA33u, 0xAFB42Bu, 0x9E9D24u,
        0x827717u, 0xF4FF81u, 0xEEFF41u, 0xC6FF00u, 0xAEEA00u
    )
    val YELLOW: UIntArray = uintArrayOf(
        0xFFFDE7u, 0xFFF9C4u, 0xFFF59Du, 0xFFF176u, 0xFFEE58u, 0xFFEB3Bu, 0xFDD835u, 0xFBC02Du, 0xF9A825u,
        0xF57F17u, 0xFFFF8Du, 0xFFFF00u, 0xFFEA00u, 0xFFD600u
    )
    val AMBER: UIntArray = uintArrayOf(
        0xFFF8E1u, 0xFFECB3u, 0xFFE082u, 0xFFD54Fu, 0xFFCA28u, 0xFFC107u, 0xFFB300u, 0xFFA000u, 0xFF8F00u,
        0xFF6F00u, 0xFFE57Fu, 0xFFD740u, 0xFFC400u, 0xFFAB00u
    )
    val ORANGE: UIntArray = uintArrayOf(
        0xFFF3E0u, 0xFFE0B2u, 0xFFCC80u, 0xFFB74Du, 0xFFA726u, 0xFF9800u, 0xFB8C00u, 0xF57C00u, 0xEF6C00u,
        0xE65100u, 0xFFD180u, 0xFFAB40u, 0xFF9100u, 0xFF6D00u
    )
    val DEEP_ORANGE: UIntArray = uintArrayOf(
        0xFBE9E7u, 0xFFCCBCu, 0xFFAB91u, 0xFF8A65u, 0xFF7043u, 0xFF5722u, 0xF4511Eu, 0xE64A19u, 0xD84315u,
        0xBF360Cu, 0xFF9E80u, 0xFF6E40u, 0xFF3D00u, 0xDD2C00u
    )
    val BROWN: UIntArray = uintArrayOf(
        0xEFEBE9u, 0xD7CCC8u, 0xBCAAA4u, 0xA1887Fu, 0x8D6E63u, 0x795548u, 0x6D4C41u, 0x5D4037u, 0x4E342Eu,
        0x3E2723u
    )
    val GRAY: UIntArray = uintArrayOf(
        0xFAFAFAu, 0xF5F5F5u, 0xEEEEEEu, 0xE0E0E0u, 0xBDBDBDu, 0x9E9E9Eu, 0x757575u, 0x616161u, 0x424242u,
        0x212121u
    )
    val BLUE_GRAY: UIntArray = uintArrayOf(
        0xECEFF1u, 0xCFD8DCu, 0xB0BEC5u, 0x90A4AEu, 0x78909Cu, 0x607D8Bu, 0x546E7Au, 0x455A64u, 0x37474Fu,
        0x263238u
    )
}
