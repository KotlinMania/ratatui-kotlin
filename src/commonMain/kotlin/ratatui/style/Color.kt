// port-lint: source ratatui-core/src/style/color.rs
package ratatui.style

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.int
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

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
@Serializable(with = ColorSerializer::class)
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
         * Converts `(r, g, b)` values to a [Color.Rgb] instance.
         *
         * Mirrors Rust `impl From<(u8, u8, u8)> for Color`.
         */
        fun from(r: UByte, g: UByte, b: UByte): Color = Rgb(r, g, b)

        /**
         * Converts a tuple of 3 `u8` values to a [Color.Rgb] instance.
         */
        fun from(rgb: Triple<UByte, UByte, UByte>): Color = Rgb(rgb.first, rgb.second, rgb.third)

        /**
         * Converts `(r, g, b, a)` values to a [Color.Rgb] instance (ignoring alpha).
         *
         * Mirrors Rust `impl From<(u8, u8, u8, u8)> for Color`.
         */
        fun from(r: UByte, g: UByte, b: UByte, @Suppress("UNUSED_PARAMETER") a: UByte): Color =
            Rgb(r, g, b)

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
         * Converts a HSL representation to a [Color.Rgb] instance.
         *
         * Hue values are normalized by wrapping. Saturation and lightness are clamped to
         * `[0.0..1.0]` before conversion.
         */
        fun fromHsl(hsl: Hsl): Color {
            val hue = wrapHueDegrees(hsl.hue)
            val saturation = hsl.saturation.coerceIn(0.0, 1.0)
            val lightness = hsl.lightness.coerceIn(0.0, 1.0)
            val (r, g, b) = hslToRgb(hue, saturation, lightness)
            return Rgb(r, g, b)
        }

        /**
         * Converts a HSLuv representation to a [Color.Rgb] instance.
         *
         * Hue values are normalized by wrapping. Saturation and lightness are clamped to
         * `[0.0..100.0]` before conversion.
         */
        fun fromHsluv(hsluv: Hsluv): Color {
            val hue = wrapHueDegrees(hsluv.hue)
            val saturation = hsluv.saturation.coerceIn(0.0, 100.0)
            val lightness = hsluv.lightness.coerceIn(0.0, 100.0)
            val (r, g, b) = hsluvToRgb(hue, saturation, lightness)
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

/**
 * Hue, Saturation, Lightness representation.
 *
 * Mirrors the public API used by Ratatui's optional `palette` feature (`palette::Hsl`).
 */
data class Hsl(
    val hue: Double,
    val saturation: Double,
    val lightness: Double
) {
    companion object {
        fun new(hue: Double, saturation: Double, lightness: Double): Hsl = Hsl(hue, saturation, lightness)
    }
}

/**
 * Hue, Saturation, Lightness representation in the HSLuv color space.
 *
 * Mirrors the public API used by Ratatui's optional `palette` feature (`palette::Hsluv`).
 */
data class Hsluv(
    val hue: Double,
    val saturation: Double,
    val lightness: Double
) {
    companion object {
        fun new(hue: Double, saturation: Double, lightness: Double): Hsluv = Hsluv(hue, saturation, lightness)
    }
}

/**
 * Kotlinx-serialization adapter that mirrors Rust's `serde` behavior.
 *
 * Rust serializes [Color] via its Display implementation (a string), and deserializes by parsing
 * that same string form. This serializer also supports the previous map-based encoding:
 * - `{"Rgb":[255,0,255]}`
 * - `{"Indexed":10}`
 */
object ColorSerializer : KSerializer<Color> {
    /**
     * Helper type used to support the previous serde-derived serialization formats.
     *
     * Mirrors the local `ColorWrapper` enum in Rust's `impl serde::Deserialize for Color`.
     */
    private sealed interface ColorWrapper {
        data class Rgb(val red: UByte, val green: UByte, val blue: UByte) : ColorWrapper

        data class Indexed(val index: UByte) : ColorWrapper
    }

    /**
     * Helper type used to support both the current string-based serialization format and the
     * previous serde-derived formats.
     *
     * Mirrors the local untagged `ColorFormat` enum in Rust.
     */
    private sealed interface ColorFormat {
        data class V2(val value: String) : ColorFormat

        data class V1(val wrapper: ColorWrapper) : ColorFormat
    }

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ratatui.style.Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Color {
        if (decoder is JsonDecoder) {
            val multiType = decodeColorFormat(decoder.decodeJsonElement())
            return when (multiType) {
                is ColorFormat.V2 -> {
                    try {
                        Color.fromStr(multiType.value)
                    } catch (e: ParseColorError) {
                        throw SerializationException(e.message ?: "Failed to parse Colors")
                    }
                }
                is ColorFormat.V1 -> {
                    when (val wrapper = multiType.wrapper) {
                        is ColorWrapper.Rgb -> Color.Rgb(wrapper.red, wrapper.green, wrapper.blue)
                        is ColorWrapper.Indexed -> Color.Indexed(wrapper.index)
                    }
                }
            }
        }

        return try {
            Color.fromStr(decoder.decodeString())
        } catch (e: ParseColorError) {
            throw SerializationException(e.message ?: "Failed to parse Colors")
        }
    }

    private fun decodeColorFormat(element: JsonElement): ColorFormat {
        return when (element) {
            is JsonPrimitive -> ColorFormat.V2(element.content)
            is JsonObject -> ColorFormat.V1(decodeColorWrapper(element))
            else -> throw SerializationException("Failed to parse Colors")
        }
    }

    private fun decodeColorWrapper(obj: JsonObject): ColorWrapper {
        obj["Rgb"]?.let { rgb ->
            val arr = rgb.jsonArray
            if (arr.size != 3) throw SerializationException("Failed to parse Colors")
            val red = arr[0].jsonPrimitive.int
            val green = arr[1].jsonPrimitive.int
            val blue = arr[2].jsonPrimitive.int
            if (red !in 0..255 || green !in 0..255 || blue !in 0..255) throw SerializationException("Failed to parse Colors")
            return ColorWrapper.Rgb(red.toUByte(), green.toUByte(), blue.toUByte())
        }

        obj["Indexed"]?.let { indexed ->
            val i = indexed.jsonPrimitive.int
            if (i !in 0..255) throw SerializationException("Failed to parse Colors")
            return ColorWrapper.Indexed(i.toUByte())
        }

        throw SerializationException("Failed to parse Colors")
    }
}

private fun wrapHueDegrees(degrees: Double): Double {
    val wrapped = ((degrees % 360.0) + 360.0) % 360.0
    // Keep 360 as 0 for downstream math.
    return if (wrapped == 360.0) 0.0 else wrapped
}

private fun hslToRgb(hueDegrees: Double, saturation: Double, lightness: Double): Triple<UByte, UByte, UByte> {
    val c = (1.0 - abs(2.0 * lightness - 1.0)) * saturation
    val hPrime = (hueDegrees / 60.0) % 6.0
    val x = c * (1.0 - abs((hPrime % 2.0) - 1.0))

    val (r1, g1, b1) = when {
        hPrime < 1.0 -> Triple(c, x, 0.0)
        hPrime < 2.0 -> Triple(x, c, 0.0)
        hPrime < 3.0 -> Triple(0.0, c, x)
        hPrime < 4.0 -> Triple(0.0, x, c)
        hPrime < 5.0 -> Triple(x, 0.0, c)
        else -> Triple(c, 0.0, x)
    }

    val m = lightness - c / 2.0

    val r = ((r1 + m) * 255.0).roundToInt().coerceIn(0, 255)
    val g = ((g1 + m) * 255.0).roundToInt().coerceIn(0, 255)
    val b = ((b1 + m) * 255.0).roundToInt().coerceIn(0, 255)
    return Triple(r.toUByte(), g.toUByte(), b.toUByte())
}

// --- HSLuv conversion (ported from the HSLuv reference algorithm) ---

private const val HSLUV_REF_U: Double = 0.19783000664283
private const val HSLUV_REF_V: Double = 0.46831999493879
private const val HSLUV_KAPPA: Double = 903.2962962
private const val HSLUV_EPSILON: Double = 0.0088564516

private val HSLUV_M: Array<DoubleArray> = arrayOf(
    doubleArrayOf(3.240969941904521, -1.537383177570093, -0.498610760293),
    doubleArrayOf(-0.96924363628087, 1.87596750150772, 0.041555057407175),
    doubleArrayOf(0.055630079696993, -0.20397695888897, 1.056971514242878)
)

private data class BoundLine(val slope: Double, val intercept: Double)

private fun hsluvToRgb(hueDegrees: Double, saturation: Double, lightness: Double): Triple<UByte, UByte, UByte> {
    if (lightness <= 0.0) return Triple(0u, 0u, 0u)
    if (lightness >= 100.0) return Triple(255u, 255u, 255u)

    val l = lightness
    val s = saturation
    val hRad = hueDegrees / 360.0 * 2.0 * PI

    val c = if (s <= 0.0) {
        0.0
    } else {
        val max = maxChromaForLh(l, hueDegrees)
        max / 100.0 * s
    }

    val u = cos(hRad) * c
    val v = sin(hRad) * c
    val (x, y, z) = luvToXyz(l, u, v)
    return xyzToRgb(x, y, z)
}

private fun maxChromaForLh(lightness: Double, hueDegrees: Double): Double {
    val hRad = hueDegrees / 360.0 * 2.0 * PI
    val bounds = getBounds(lightness)
    var minLen = Double.POSITIVE_INFINITY
    for (line in bounds) {
        val length = lengthOfRayUntilIntersect(hRad, line)
        if (length >= 0.0) {
            minLen = min(minLen, length)
        }
    }
    return minLen
}

private fun lengthOfRayUntilIntersect(theta: Double, line: BoundLine): Double {
    val denominator = sin(theta) - line.slope * cos(theta)
    if (abs(denominator) < 1e-12) return Double.POSITIVE_INFINITY
    return line.intercept / denominator
}

private fun getBounds(lightness: Double): List<BoundLine> {
    val result = ArrayList<BoundLine>(6)

    val sub1 = ((lightness + 16.0).pow(3.0)) / 1560896.0
    val sub2 = if (sub1 > HSLUV_EPSILON) sub1 else lightness / HSLUV_KAPPA

    for (c in 0..2) {
        val m1 = HSLUV_M[c][0]
        val m2 = HSLUV_M[c][1]
        val m3 = HSLUV_M[c][2]

        for (t in 0..1) {
            val top1 = (284517.0 * m1 - 94839.0 * m3) * sub2
            val top2 =
                (838422.0 * m3 + 769860.0 * m2 + 731718.0 * m1) * lightness * sub2 -
                    769860.0 * t.toDouble() * lightness
            val bottom = (632260.0 * m3 - 126452.0 * m2) * sub2 + 126452.0 * t.toDouble()

            result.add(BoundLine(slope = top1 / bottom, intercept = top2 / bottom))
        }
    }

    return result
}

private fun luvToXyz(l: Double, u: Double, v: Double): Triple<Double, Double, Double> {
    if (l == 0.0) return Triple(0.0, 0.0, 0.0)

    val uPrime = u / (13.0 * l) + HSLUV_REF_U
    val vPrime = v / (13.0 * l) + HSLUV_REF_V

    val y = if (l > 8.0) ((l + 16.0) / 116.0).pow(3.0) else l / HSLUV_KAPPA
    if (vPrime == 0.0) return Triple(0.0, y, 0.0)

    val x = y * 9.0 * uPrime / (4.0 * vPrime)
    val z = y * (12.0 - 3.0 * uPrime - 20.0 * vPrime) / (4.0 * vPrime)
    return Triple(x, y, z)
}

private fun xyzToRgb(x: Double, y: Double, z: Double): Triple<UByte, UByte, UByte> {
    val rLinear = HSLUV_M[0][0] * x + HSLUV_M[0][1] * y + HSLUV_M[0][2] * z
    val gLinear = HSLUV_M[1][0] * x + HSLUV_M[1][1] * y + HSLUV_M[1][2] * z
    val bLinear = HSLUV_M[2][0] * x + HSLUV_M[2][1] * y + HSLUV_M[2][2] * z

    val r = fromLinear(rLinear)
    val g = fromLinear(gLinear)
    val b = fromLinear(bLinear)

    return Triple(
        (r * 255.0).roundToInt().coerceIn(0, 255).toUByte(),
        (g * 255.0).roundToInt().coerceIn(0, 255).toUByte(),
        (b * 255.0).roundToInt().coerceIn(0, 255).toUByte()
    )
}

private fun fromLinear(c: Double): Double {
    val clamped = c.coerceIn(0.0, 1.0)
    return if (clamped <= 0.0031308) {
        12.92 * clamped
    } else {
        1.055 * clamped.pow(1.0 / 2.4) - 0.055
    }
}
