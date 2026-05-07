// port-lint: source ratatui-core/src/style/palette/tailwind.rs
package ratatui.style.palette.tailwind

import ratatui.style.Color

/**
 * Represents the Tailwind CSS [default color palette](https://tailwindcss.com/docs/customizing-colors#default-color-palette).
 *
 * There are 22 palettes. Each palette has 11 colors, with variants from 50 to 950. `BLACK` and
 * `WHITE` are also included for completeness and to avoid being affected by any terminal theme that
 * might be in use.
 *
 * Transliteration target: `ratatui-core/src/style/palette/tailwind.rs`.
 *
 * # Example
 *
 * ```kotlin
 * import ratatui.style.Color
 * import ratatui.style.palette.tailwind.BLUE
 * import ratatui.style.palette.tailwind.RED
 *
 * check(RED.c500 == Color.Rgb(239.toUByte(), 68.toUByte(), 68.toUByte()))
 * check(BLUE.c500 == Color.Rgb(59.toUByte(), 130.toUByte(), 246.toUByte()))
 * ```
 */
data class Palette(
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
    val c950: Color
)

val BLACK: Color = Color.fromU32(0x000000u)

val WHITE: Color = Color.fromU32(0xffffffu)

val SLATE: Palette = Palette(
    c50 = Color.fromU32(0xf8fafcu),
    c100 = Color.fromU32(0xf1f5f9u),
    c200 = Color.fromU32(0xe2e8f0u),
    c300 = Color.fromU32(0xcbd5e1u),
    c400 = Color.fromU32(0x94a3b8u),
    c500 = Color.fromU32(0x64748bu),
    c600 = Color.fromU32(0x475569u),
    c700 = Color.fromU32(0x334155u),
    c800 = Color.fromU32(0x1e293bu),
    c900 = Color.fromU32(0x0f172au),
    c950 = Color.fromU32(0x020617u)
)

val GRAY: Palette = Palette(
    c50 = Color.fromU32(0xf9fafbu),
    c100 = Color.fromU32(0xf3f4f6u),
    c200 = Color.fromU32(0xe5e7ebu),
    c300 = Color.fromU32(0xd1d5dbu),
    c400 = Color.fromU32(0x9ca3afu),
    c500 = Color.fromU32(0x6b7280u),
    c600 = Color.fromU32(0x4b5563u),
    c700 = Color.fromU32(0x374151u),
    c800 = Color.fromU32(0x1f2937u),
    c900 = Color.fromU32(0x111827u),
    c950 = Color.fromU32(0x030712u)
)

val ZINC: Palette = Palette(
    c50 = Color.fromU32(0xfafafau),
    c100 = Color.fromU32(0xf4f4f5u),
    c200 = Color.fromU32(0xe4e4e7u),
    c300 = Color.fromU32(0xd4d4d8u),
    c400 = Color.fromU32(0xa1a1aau),
    c500 = Color.fromU32(0x71717au),
    c600 = Color.fromU32(0x52525bu),
    c700 = Color.fromU32(0x3f3f46u),
    c800 = Color.fromU32(0x27272au),
    c900 = Color.fromU32(0x18181bu),
    c950 = Color.fromU32(0x09090bu)
)

val NEUTRAL: Palette = Palette(
    c50 = Color.fromU32(0xfafafau),
    c100 = Color.fromU32(0xf5f5f5u),
    c200 = Color.fromU32(0xe5e5e5u),
    c300 = Color.fromU32(0xd4d4d4u),
    c400 = Color.fromU32(0xa3a3a3u),
    c500 = Color.fromU32(0x737373u),
    c600 = Color.fromU32(0x525252u),
    c700 = Color.fromU32(0x404040u),
    c800 = Color.fromU32(0x262626u),
    c900 = Color.fromU32(0x171717u),
    c950 = Color.fromU32(0x0a0a0au)
)

val STONE: Palette = Palette(
    c50 = Color.fromU32(0xfafaf9u),
    c100 = Color.fromU32(0xf5f5f4u),
    c200 = Color.fromU32(0xe7e5e4u),
    c300 = Color.fromU32(0xd6d3d1u),
    c400 = Color.fromU32(0xa8a29eu),
    c500 = Color.fromU32(0x78716cu),
    c600 = Color.fromU32(0x57534eu),
    c700 = Color.fromU32(0x44403cu),
    c800 = Color.fromU32(0x292524u),
    c900 = Color.fromU32(0x1c1917u),
    c950 = Color.fromU32(0x0c0a09u)
)

val RED: Palette = Palette(
    c50 = Color.fromU32(0xfef2f2u),
    c100 = Color.fromU32(0xfee2e2u),
    c200 = Color.fromU32(0xfecacau),
    c300 = Color.fromU32(0xfca5a5u),
    c400 = Color.fromU32(0xf87171u),
    c500 = Color.fromU32(0xef4444u),
    c600 = Color.fromU32(0xdc2626u),
    c700 = Color.fromU32(0xb91c1cu),
    c800 = Color.fromU32(0x991b1bu),
    c900 = Color.fromU32(0x7f1d1du),
    c950 = Color.fromU32(0x450a0au)
)

val ORANGE: Palette = Palette(
    c50 = Color.fromU32(0xfff7edu),
    c100 = Color.fromU32(0xffedd5u),
    c200 = Color.fromU32(0xfed7aau),
    c300 = Color.fromU32(0xfdba74u),
    c400 = Color.fromU32(0xfb923cu),
    c500 = Color.fromU32(0xf97316u),
    c600 = Color.fromU32(0xea580cu),
    c700 = Color.fromU32(0xc2410cu),
    c800 = Color.fromU32(0x9a3412u),
    c900 = Color.fromU32(0x7c2d12u),
    c950 = Color.fromU32(0x431407u)
)

val AMBER: Palette = Palette(
    c50 = Color.fromU32(0xfffbebu),
    c100 = Color.fromU32(0xfef3c7u),
    c200 = Color.fromU32(0xfde68au),
    c300 = Color.fromU32(0xfcd34du),
    c400 = Color.fromU32(0xfbbf24u),
    c500 = Color.fromU32(0xf59e0bu),
    c600 = Color.fromU32(0xd97706u),
    c700 = Color.fromU32(0xb45309u),
    c800 = Color.fromU32(0x92400eu),
    c900 = Color.fromU32(0x78350fu),
    c950 = Color.fromU32(0x451a03u)
)

val YELLOW: Palette = Palette(
    c50 = Color.fromU32(0xfefce8u),
    c100 = Color.fromU32(0xfef9c3u),
    c200 = Color.fromU32(0xfef08au),
    c300 = Color.fromU32(0xfde047u),
    c400 = Color.fromU32(0xfacc15u),
    c500 = Color.fromU32(0xeab308u),
    c600 = Color.fromU32(0xca8a04u),
    c700 = Color.fromU32(0xa16207u),
    c800 = Color.fromU32(0x854d0eu),
    c900 = Color.fromU32(0x713f12u),
    c950 = Color.fromU32(0x422006u)
)

val LIME: Palette = Palette(
    c50 = Color.fromU32(0xf7fee7u),
    c100 = Color.fromU32(0xecfccbu),
    c200 = Color.fromU32(0xd9f99du),
    c300 = Color.fromU32(0xbef264u),
    c400 = Color.fromU32(0xa3e635u),
    c500 = Color.fromU32(0x84cc16u),
    c600 = Color.fromU32(0x65a30du),
    c700 = Color.fromU32(0x4d7c0fu),
    c800 = Color.fromU32(0x3f6212u),
    c900 = Color.fromU32(0x365314u),
    c950 = Color.fromU32(0x1a2e05u)
)

val GREEN: Palette = Palette(
    c50 = Color.fromU32(0xf0fdf4u),
    c100 = Color.fromU32(0xdcfce7u),
    c200 = Color.fromU32(0xbbf7d0u),
    c300 = Color.fromU32(0x86efacu),
    c400 = Color.fromU32(0x4ade80u),
    c500 = Color.fromU32(0x22c55eu),
    c600 = Color.fromU32(0x16a34au),
    c700 = Color.fromU32(0x15803du),
    c800 = Color.fromU32(0x166534u),
    c900 = Color.fromU32(0x14532du),
    c950 = Color.fromU32(0x052e16u)
)

val EMERALD: Palette = Palette(
    c50 = Color.fromU32(0xecfdf5u),
    c100 = Color.fromU32(0xd1fae5u),
    c200 = Color.fromU32(0xa7f3d0u),
    c300 = Color.fromU32(0x6ee7b7u),
    c400 = Color.fromU32(0x34d399u),
    c500 = Color.fromU32(0x10b981u),
    c600 = Color.fromU32(0x059669u),
    c700 = Color.fromU32(0x047857u),
    c800 = Color.fromU32(0x065f46u),
    c900 = Color.fromU32(0x064e3bu),
    c950 = Color.fromU32(0x022c22u)
)

val TEAL: Palette = Palette(
    c50 = Color.fromU32(0xf0fdfau),
    c100 = Color.fromU32(0xccfbf1u),
    c200 = Color.fromU32(0x99f6e4u),
    c300 = Color.fromU32(0x5eead4u),
    c400 = Color.fromU32(0x2dd4bfu),
    c500 = Color.fromU32(0x14b8a6u),
    c600 = Color.fromU32(0x0d9488u),
    c700 = Color.fromU32(0x0f766eu),
    c800 = Color.fromU32(0x115e59u),
    c900 = Color.fromU32(0x134e4au),
    c950 = Color.fromU32(0x042f2eu)
)

val CYAN: Palette = Palette(
    c50 = Color.fromU32(0xecfeffu),
    c100 = Color.fromU32(0xcffafeu),
    c200 = Color.fromU32(0xa5f3fcu),
    c300 = Color.fromU32(0x67e8f9u),
    c400 = Color.fromU32(0x22d3eeu),
    c500 = Color.fromU32(0x06b6d4u),
    c600 = Color.fromU32(0x0891b2u),
    c700 = Color.fromU32(0x0e7490u),
    c800 = Color.fromU32(0x155e75u),
    c900 = Color.fromU32(0x164e63u),
    c950 = Color.fromU32(0x083344u)
)

val SKY: Palette = Palette(
    c50 = Color.fromU32(0xf0f9ffu),
    c100 = Color.fromU32(0xe0f2feu),
    c200 = Color.fromU32(0xbae6fdu),
    c300 = Color.fromU32(0x7dd3fcu),
    c400 = Color.fromU32(0x38bdf8u),
    c500 = Color.fromU32(0x0ea5e9u),
    c600 = Color.fromU32(0x0284c7u),
    c700 = Color.fromU32(0x0369a1u),
    c800 = Color.fromU32(0x075985u),
    c900 = Color.fromU32(0x0c4a6eu),
    c950 = Color.fromU32(0x082f49u)
)

val BLUE: Palette = Palette(
    c50 = Color.fromU32(0xeff6ffu),
    c100 = Color.fromU32(0xdbeafeu),
    c200 = Color.fromU32(0xbfdbfeu),
    c300 = Color.fromU32(0x93c5fdu),
    c400 = Color.fromU32(0x60a5fau),
    c500 = Color.fromU32(0x3b82f6u),
    c600 = Color.fromU32(0x2563ebu),
    c700 = Color.fromU32(0x1d4ed8u),
    c800 = Color.fromU32(0x1e40afu),
    c900 = Color.fromU32(0x1e3a8au),
    c950 = Color.fromU32(0x172554u)
)

val INDIGO: Palette = Palette(
    c50 = Color.fromU32(0xeef2ffu),
    c100 = Color.fromU32(0xe0e7ffu),
    c200 = Color.fromU32(0xc7d2feu),
    c300 = Color.fromU32(0xa5b4fcu),
    c400 = Color.fromU32(0x818cf8u),
    c500 = Color.fromU32(0x6366f1u),
    c600 = Color.fromU32(0x4f46e5u),
    c700 = Color.fromU32(0x4338cau),
    c800 = Color.fromU32(0x3730a3u),
    c900 = Color.fromU32(0x312e81u),
    c950 = Color.fromU32(0x1e1b4bu)
)

val VIOLET: Palette = Palette(
    c50 = Color.fromU32(0xf5f3ffu),
    c100 = Color.fromU32(0xede9feu),
    c200 = Color.fromU32(0xddd6feu),
    c300 = Color.fromU32(0xc4b5fdu),
    c400 = Color.fromU32(0xa78bfau),
    c500 = Color.fromU32(0x8b5cf6u),
    c600 = Color.fromU32(0x7c3aedu),
    c700 = Color.fromU32(0x6d28d9u),
    c800 = Color.fromU32(0x5b21b6u),
    c900 = Color.fromU32(0x4c1d95u),
    c950 = Color.fromU32(0x2e1065u)
)

val PURPLE: Palette = Palette(
    c50 = Color.fromU32(0xfaf5ffu),
    c100 = Color.fromU32(0xf3e8ffu),
    c200 = Color.fromU32(0xe9d5ffu),
    c300 = Color.fromU32(0xd8b4feu),
    c400 = Color.fromU32(0xc084fcu),
    c500 = Color.fromU32(0xa855f7u),
    c600 = Color.fromU32(0x9333eau),
    c700 = Color.fromU32(0x7e22ceu),
    c800 = Color.fromU32(0x6b21a8u),
    c900 = Color.fromU32(0x581c87u),
    c950 = Color.fromU32(0x3b0764u)
)

val FUCHSIA: Palette = Palette(
    c50 = Color.fromU32(0xfdf4ffu),
    c100 = Color.fromU32(0xfae8ffu),
    c200 = Color.fromU32(0xf5d0feu),
    c300 = Color.fromU32(0xf0abfcu),
    c400 = Color.fromU32(0xe879f9u),
    c500 = Color.fromU32(0xd946efu),
    c600 = Color.fromU32(0xc026d3u),
    c700 = Color.fromU32(0xa21cafu),
    c800 = Color.fromU32(0x86198fu),
    c900 = Color.fromU32(0x701a75u),
    c950 = Color.fromU32(0x4a044eu)
)

val PINK: Palette = Palette(
    c50 = Color.fromU32(0xfdf2f8u),
    c100 = Color.fromU32(0xfce7f3u),
    c200 = Color.fromU32(0xfbcfe8u),
    c300 = Color.fromU32(0xf9a8d4u),
    c400 = Color.fromU32(0xf472b6u),
    c500 = Color.fromU32(0xec4899u),
    c600 = Color.fromU32(0xdb2777u),
    c700 = Color.fromU32(0xbe185du),
    c800 = Color.fromU32(0x9d174du),
    c900 = Color.fromU32(0x831843u),
    c950 = Color.fromU32(0x500724u)
)

val ROSE: Palette = Palette(
    c50 = Color.fromU32(0xfff1f2u),
    c100 = Color.fromU32(0xffe4e6u),
    c200 = Color.fromU32(0xfecdd3u),
    c300 = Color.fromU32(0xfda4afu),
    c400 = Color.fromU32(0xfb7185u),
    c500 = Color.fromU32(0xf43f5eu),
    c600 = Color.fromU32(0xe11d48u),
    c700 = Color.fromU32(0xbe123cu),
    c800 = Color.fromU32(0x9f1239u),
    c900 = Color.fromU32(0x881337u),
    c950 = Color.fromU32(0x4c0519u)
)
