// port-lint: source ratatui-core/src/style/color.rs
package ratatui.style

import io.github.kotlinmania.serdejson.fromStr
import io.github.kotlinmania.serdejson.toStr
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {
    @Test
    fun fromU32() {
        assertEquals(Color.Rgb(0u, 0u, 0u), Color.fromU32(0x000000u))
        assertEquals(Color.Rgb(255u, 0u, 0u), Color.fromU32(0xFF0000u))
        assertEquals(Color.Rgb(0u, 255u, 0u), Color.fromU32(0x00FF00u))
        assertEquals(Color.Rgb(0u, 0u, 255u), Color.fromU32(0x0000FFu))
        assertEquals(Color.Rgb(255u, 255u, 255u), Color.fromU32(0xFFFFFFu))
    }

    @Test
    fun fromRgbColor() {
        val color: Color = Color.fromStr("#FF0000").unwrap()
        assertEquals(Color.Rgb(255u, 0u, 0u), color)
    }

    @Test
    fun fromIndexedColor() {
        val color: Color = Color.fromStr("10").unwrap()
        assertEquals(Color.Indexed(10u), color)
    }

    @Test
    fun fromAnsiColor() {
        assertEquals(Color.Reset, Color.fromStr("reset").unwrap())
        assertEquals(Color.Black, Color.fromStr("black").unwrap())
        assertEquals(Color.Red, Color.fromStr("red").unwrap())
        assertEquals(Color.Green, Color.fromStr("green").unwrap())
        assertEquals(Color.Yellow, Color.fromStr("yellow").unwrap())
        assertEquals(Color.Blue, Color.fromStr("blue").unwrap())
        assertEquals(Color.Magenta, Color.fromStr("magenta").unwrap())
        assertEquals(Color.Cyan, Color.fromStr("cyan").unwrap())
        assertEquals(Color.Gray, Color.fromStr("gray").unwrap())
        assertEquals(Color.DarkGray, Color.fromStr("darkgray").unwrap())
        assertEquals(Color.LightRed, Color.fromStr("lightred").unwrap())
        assertEquals(Color.LightGreen, Color.fromStr("lightgreen").unwrap())
        assertEquals(Color.LightYellow, Color.fromStr("lightyellow").unwrap())
        assertEquals(Color.LightBlue, Color.fromStr("lightblue").unwrap())
        assertEquals(Color.LightMagenta, Color.fromStr("lightmagenta").unwrap())
        assertEquals(Color.LightCyan, Color.fromStr("lightcyan").unwrap())
        assertEquals(Color.White, Color.fromStr("white").unwrap())

        // aliases
        assertEquals(Color.DarkGray, Color.fromStr("lightblack").unwrap())
        assertEquals(Color.White, Color.fromStr("lightwhite").unwrap())
        assertEquals(Color.White, Color.fromStr("lightgray").unwrap())

        // silver = grey = gray
        assertEquals(Color.Gray, Color.fromStr("grey").unwrap())
        assertEquals(Color.Gray, Color.fromStr("silver").unwrap())

        // spaces are ignored
        assertEquals(Color.DarkGray, Color.fromStr("light black").unwrap())
        assertEquals(Color.White, Color.fromStr("light white").unwrap())
        assertEquals(Color.White, Color.fromStr("light gray").unwrap())

        // dashes are ignored
        assertEquals(Color.DarkGray, Color.fromStr("light-black").unwrap())
        assertEquals(Color.White, Color.fromStr("light-white").unwrap())
        assertEquals(Color.White, Color.fromStr("light-gray").unwrap())

        // underscores are ignored
        assertEquals(Color.DarkGray, Color.fromStr("light_black").unwrap())
        assertEquals(Color.White, Color.fromStr("light_white").unwrap())
        assertEquals(Color.White, Color.fromStr("light_gray").unwrap())

        // bright = light
        assertEquals(Color.DarkGray, Color.fromStr("bright-black").unwrap())
        assertEquals(Color.White, Color.fromStr("bright-white").unwrap())
        assertEquals(Color.DarkGray, Color.fromStr("brightblack").unwrap())
        assertEquals(Color.White, Color.fromStr("brightwhite").unwrap())
    }

    @Test
    fun fromInvalidColors() {
        val badColors = listOf(
            "invalid_color", // not a color string
            "abcdef0", // 7 chars is not a color
            " bcdefa", // does not start with a '#'
            "#abcdef00", // too many chars
            "#1\uD83E\uDD80" + "2", // len 7 but on char boundaries should not panic
            "resets", // typo
            "lightblackk", // typo
        )

        for (badColor in badColors) {
            kotlin.test.assertTrue(Color.fromStr(badColor).isErr, "bad color: '$badColor'")
        }
    }

    @Test
    fun display() {
        assertEquals("Black", Color.Black.toString())
        assertEquals("Red", Color.Red.toString())
        assertEquals("Green", Color.Green.toString())
        assertEquals("Yellow", Color.Yellow.toString())
        assertEquals("Blue", Color.Blue.toString())
        assertEquals("Magenta", Color.Magenta.toString())
        assertEquals("Cyan", Color.Cyan.toString())
        assertEquals("Gray", Color.Gray.toString())
        assertEquals("DarkGray", Color.DarkGray.toString())
        assertEquals("LightRed", Color.LightRed.toString())
        assertEquals("LightGreen", Color.LightGreen.toString())
        assertEquals("LightYellow", Color.LightYellow.toString())
        assertEquals("LightBlue", Color.LightBlue.toString())
        assertEquals("LightMagenta", Color.LightMagenta.toString())
        assertEquals("LightCyan", Color.LightCyan.toString())
        assertEquals("White", Color.White.toString())
        assertEquals("10", Color.Indexed(10u).toString())
        assertEquals("#FF0000", Color.Rgb(255u, 0u, 0u).toString())
        assertEquals("Reset", Color.Reset.toString())
    }

    @Test
    fun fromArrayAndTupleConversions() {
        val fromArray3 = Color.from(ubyteArrayOf(123u, 45u, 67u))
        assertEquals(Color.Rgb(123u, 45u, 67u), fromArray3)

        val fromTuple3 = Color.from(89u, 76u, 54u)
        assertEquals(Color.Rgb(89u, 76u, 54u), fromTuple3)

        val fromArray4 = Color.from(ubyteArrayOf(10u, 20u, 30u, 255u))
        assertEquals(Color.Rgb(10u, 20u, 30u), fromArray4)

        val fromTuple4 = Color.from(200u, 150u, 100u, 0u)
        assertEquals(Color.Rgb(200u, 150u, 100u), fromTuple4)
    }

    @Test
    fun serializeThenDeserialize() {
        assertEquals("\"Red\"", toStr(Color.Red).getOrThrow())
        assertEquals(Color.Red, fromStr("\"Red\"", Color).getOrThrow())
        assertEquals(Color.Rgb(255u, 0u, 255u), fromStr("\"#FF00FF\"", Color).getOrThrow())
        assertEquals(Color.Indexed(10u), fromStr("\"10\"", Color).getOrThrow())
    }

    @Test
    fun deserializeWithPreviousFormat() {
        assertEquals(Color.Rgb(255u, 0u, 255u), fromStr("{\"Rgb\":[255,0,255]}", Color).getOrThrow())
        assertEquals(Color.Indexed(10u), fromStr("{\"Indexed\":10}", Color).getOrThrow())
    }

    @Test
    fun fromHsl() {
        assertEquals(Color.Rgb(0u, 0u, 0u), Color.fromHsl(Hsl.new(0.0, 0.0, 0.0)))
        assertEquals(Color.Rgb(255u, 255u, 255u), Color.fromHsl(Hsl.new(0.0, 0.0, 1.0)))
        assertEquals(Color.Rgb(128u, 128u, 128u), Color.fromHsl(Hsl.new(0.0, 0.0, 0.5)))
        assertEquals(Color.Rgb(255u, 0u, 0u), Color.fromHsl(Hsl.new(0.0, 1.0, 0.5)))
        assertEquals(Color.Rgb(0u, 0u, 255u), Color.fromHsl(Hsl.new(-120.0, 1.0, 0.5)))
    }

    @Test
    fun fromHsluv() {
        assertEquals(Color.Rgb(0u, 0u, 0u), Color.fromHsluv(Hsluv.new(0.0, 100.0, 0.0)))
        assertEquals(Color.Rgb(255u, 255u, 255u), Color.fromHsluv(Hsluv.new(0.0, 0.0, 100.0)))
        assertEquals(Color.Rgb(119u, 119u, 119u), Color.fromHsluv(Hsluv.new(0.0, 0.0, 50.0)))
        assertEquals(Color.Rgb(255u, 0u, 0u), Color.fromHsluv(Hsluv.new(12.18, 100.0, 53.2)))
        assertEquals(Color.Rgb(0u, 0u, 255u), Color.fromHsluv(Hsluv.new(-94.13, 100.0, 32.3)))
    }
}
