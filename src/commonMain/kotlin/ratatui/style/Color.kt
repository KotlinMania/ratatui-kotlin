// port-lint: source ratatui-core/src/style/color.rs
package ratatui.style

/**
 * ANSI Color
 *
 * All colors from the [ANSI color table](https://en.wikipedia.org/wiki/ANSI_escape_code#Colors)
 * are supported (though some names are not exactly the same).
 *
 * | Color Name     | Color                   | Foreground | Background |
 * |----------------|-------------------------|------------|------------|
 * | `black`        | [Color.Black]           | 30         | 40         |
 * | `red`          | [Color.Red]             | 31         | 41         |
 * | `green`        | [Color.Green]           | 32         | 42         |
 * | `yellow`       | [Color.Yellow]          | 33         | 43         |
 * | `blue`         | [Color.Blue]            | 34         | 44         |
 * | `magenta`      | [Color.Magenta]         | 35         | 45         |
 * | `cyan`         | [Color.Cyan]            | 36         | 46         |
 * | `gray`*        | [Color.Gray]            | 37         | 47         |
 * | `darkgray`*    | [Color.DarkGray]        | 90         | 100        |
 * | `lightred`     | [Color.LightRed]        | 91         | 101        |
 * | `lightgreen`   | [Color.LightGreen]      | 92         | 102        |
 * | `lightyellow`  | [Color.LightYellow]     | 93         | 103        |
 * | `lightblue`    | [Color.LightBlue]       | 94         | 104        |
 * | `lightmagenta` | [Color.LightMagenta]    | 95         | 105        |
 * | `lightcyan`    | [Color.LightCyan]       | 96         | 106        |
 * | `white`*       | [Color.White]           | 97         | 107        |
 *
 * - `gray` is sometimes called `white` - this is not supported as we use `white` for bright white
 * - `gray` is sometimes called `silver` - this is supported
 * - `darkgray` is sometimes called `light black` or `bright black` (both are supported)
 * - `white` is sometimes called `light white` or `bright white` (both are supported)
 * - we support `bright` and `light` prefixes for all colors
 * - we support `-` and `_` and ` ` as separators for all colors
 * - we support both `gray` and `grey` spellings
 *
 * `Color` can be used anywhere that accepts a [Style] because
 * [Style.from][Style.Companion.from] accepts a foreground color.
 *
 * Example:
 * ```kotlin
 * assertEquals(Color.fromStr("red"), Color.Red)
 * assertEquals(Color.fromStr("lightred"), Color.LightRed)
 * assertEquals(Color.fromStr("light red"), Color.LightRed)
 * assertEquals(Color.fromStr("light-red"), Color.LightRed)
 * assertEquals(Color.fromStr("light_red"), Color.LightRed)
 * assertEquals(Color.fromStr("bright red"), Color.LightRed)
 * assertEquals(Color.fromStr("silver"), Color.Gray)
 * assertEquals(Color.fromStr("dark-grey"), Color.DarkGray)
 * assertEquals(Color.fromStr("white"), Color.White)
 * ```
 */
sealed class Color {
    /** Resets the foreground or background color. */
    data object Reset : Color()

    /** ANSI Color: Black. Foreground: 30, Background: 40. */
    data object Black : Color()

    /** ANSI Color: Red. Foreground: 31, Background: 41. */
    data object Red : Color()

    /** ANSI Color: Green. Foreground: 32, Background: 42. */
    data object Green : Color()

    /** ANSI Color: Yellow. Foreground: 33, Background: 43. */
    data object Yellow : Color()

    /** ANSI Color: Blue. Foreground: 34, Background: 44. */
    data object Blue : Color()

    /** ANSI Color: Magenta. Foreground: 35, Background: 45. */
    data object Magenta : Color()

    /** ANSI Color: Cyan. Foreground: 36, Background: 46. */
    data object Cyan : Color()

    /**
     * ANSI Color: White. Foreground: 37, Background: 47
     *
     * Note that this is sometimes called `silver` or `white` but we use `white` for bright white
     */
    data object Gray : Color()

    /**
     * ANSI Color: Bright Black. Foreground: 90, Background: 100
     *
     * Note that this is sometimes called `light black` or `bright black` but we use `dark gray`
     */
    data object DarkGray : Color()

    /** ANSI Color: Bright Red. Foreground: 91, Background: 101. */
    data object LightRed : Color()

    /** ANSI Color: Bright Green. Foreground: 92, Background: 102. */
    data object LightGreen : Color()

    /** ANSI Color: Bright Yellow. Foreground: 93, Background: 103. */
    data object LightYellow : Color()

    /** ANSI Color: Bright Blue. Foreground: 94, Background: 104. */
    data object LightBlue : Color()

    /** ANSI Color: Bright Magenta. Foreground: 95, Background: 105. */
    data object LightMagenta : Color()

    /** ANSI Color: Bright Cyan. Foreground: 96, Background: 106. */
    data object LightCyan : Color()

    /**
     * ANSI Color: Bright White. Foreground: 97, Background: 107.
     *
     * Sometimes called `bright white` or `light white` in some terminals.
     */
    data object White : Color()

    /**
     * An RGB color.
     *
     * Note that only terminals that support 24-bit true color will display this correctly.
     * Notably versions of Windows Terminal prior to Windows 10 and macOS Terminal.app do not
     * support this.
     *
     * See also: https://en.wikipedia.org/wiki/ANSI_escape_code#24-bit
     */
    data class Rgb(val r: UByte, val g: UByte, val b: UByte) : Color() {
        override fun toString(): String {
            val rHex = r.toString(16).padStart(2, '0').uppercase()
            val gHex = g.toString(16).padStart(2, '0').uppercase()
            val bHex = b.toString(16).padStart(2, '0').uppercase()
            return "#$rHex$gHex$bHex"
        }
    }

    /**
     * An 8-bit 256 color.
     *
     * See also: https://en.wikipedia.org/wiki/ANSI_escape_code#8-bit
     */
    data class Indexed(val index: UByte) : Color() {
        override fun toString(): String = index.toString()
    }

    companion object {
        /** Default color (Reset). */
        fun default(): Color = Reset

        /**
         * Converts a tuple of 3 `u8` values to a [Color.Rgb] instance.
         */
        fun from(rgb: Triple<UByte, UByte, UByte>): Color = Rgb(rgb.first, rgb.second, rgb.third)

        /**
         * Converts an array of 3 or 4 `u8` values to a [Color.Rgb] instance.
         *
         * - If `bytes.size == 3`, returns `Rgb(r, g, b)`.
         * - If `bytes.size == 4`, returns `Rgb(r, g, b)` (ignoring alpha).
         */
        fun from(bytes: UByteArray): Color {
            if (bytes.size != 3 && bytes.size != 4) {
                throw IllegalArgumentException("Expected 3 or 4 bytes, got ${bytes.size}")
            }
            return Rgb(bytes[0], bytes[1], bytes[2])
        }

        /**
         * Converts a `UInt` to a `Color`.
         *
         * The UInt should be in the format 0x00RRGGBB.
         */
        fun fromU32(u: UInt): Color {
            val r = ((u shr 16) and 0xFFu).toUByte()
            val g = ((u shr 8) and 0xFFu).toUByte()
            val b = (u and 0xFFu).toUByte()
            return Rgb(r, g, b)
        }

        /**
         * Converts a string representation to a [Color] instance.
         *
         * Supports named colors, RGB hex values, and indexed colors. If the string cannot be
         * parsed, a [ParseColorError] is returned.
         */
        fun fromStr(s: String): Color {
            // There is a mix of different color names and formats in the wild.
            // This is an attempt to support as many as possible.
            val normalized = s.lowercase()
                .replace(" ", "")
                .replace("-", "")
                .replace("_", "")
                .replace("bright", "light")
                .replace("grey", "gray")
                .replace("silver", "gray")
                .replace("lightblack", "darkgray")
                .replace("lightwhite", "white")
                .replace("lightgray", "white")

            return when (normalized) {
                "reset" -> Reset
                "black" -> Black
                "red" -> Red
                "green" -> Green
                "yellow" -> Yellow
                "blue" -> Blue
                "magenta" -> Magenta
                "cyan" -> Cyan
                "gray" -> Gray
                "darkgray" -> DarkGray
                "lightred" -> LightRed
                "lightgreen" -> LightGreen
                "lightyellow" -> LightYellow
                "lightblue" -> LightBlue
                "lightmagenta" -> LightMagenta
                "lightcyan" -> LightCyan
                "white" -> White
                else -> {
                    // Try parsing as indexed color (0-255)
                    s.toUByteOrNull()?.let { return Indexed(it) }

                    // Try parsing as hex color
                    parseHexColor(s)?.let { (r, g, b) -> return Rgb(r, g, b) }

                    throw ParseColorError()
                }
            }
        }
    }

    internal fun stylizeDebug(kind: ColorDebugKind): ColorDebug = ColorDebug(kind = kind, color = this)

    override fun toString(): String =
        // Most variants are `data object`s which already use their name as `toString()`.
        // Keep an explicit match here to mirror Rust's `fmt::Display` arms.
        when (this) {
            is Reset -> "Reset"
            is Black -> "Black"
            is Red -> "Red"
            is Green -> "Green"
            is Yellow -> "Yellow"
            is Blue -> "Blue"
            is Magenta -> "Magenta"
            is Cyan -> "Cyan"
            is Gray -> "Gray"
            is DarkGray -> "DarkGray"
            is LightRed -> "LightRed"
            is LightGreen -> "LightGreen"
            is LightYellow -> "LightYellow"
            is LightBlue -> "LightBlue"
            is LightMagenta -> "LightMagenta"
            is LightCyan -> "LightCyan"
            is White -> "White"
            is Rgb -> this.toString()
            is Indexed -> this.toString()
        }
}

/** Error type indicating a failure to parse a color string. */
class ParseColorError : Exception("Failed to parse Colors")

private fun parseHexColor(input: String): Triple<UByte, UByte, UByte>? {
    if (!input.startsWith('#') || input.length != 7) {
        return null
    }
    val r = input.substring(1, 3).toUByteOrNull(16) ?: return null
    val g = input.substring(3, 5).toUByteOrNull(16) ?: return null
    val b = input.substring(5, 7).toUByteOrNull(16) ?: return null
    return Triple(r, g, b)
}
