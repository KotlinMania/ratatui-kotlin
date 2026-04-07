package ratatui.style

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
        val color: Color = Color.fromStr("#FF0000")
        assertEquals(Color.Rgb(255u, 0u, 0u), color)
    }

    @Test
    fun fromIndexedColor() {
        val color: Color = Color.fromStr("10")
        assertEquals(Color.Indexed(10u), color)
    }

    @Test
    fun fromAnsiColor() {
        assertEquals(Color.Reset, Color.fromStr("reset"))
        assertEquals(Color.Black, Color.fromStr("black"))
        assertEquals(Color.Red, Color.fromStr("red"))
        assertEquals(Color.Green, Color.fromStr("green"))
        assertEquals(Color.Yellow, Color.fromStr("yellow"))
        assertEquals(Color.Blue, Color.fromStr("blue"))
        assertEquals(Color.Magenta, Color.fromStr("magenta"))
        assertEquals(Color.Cyan, Color.fromStr("cyan"))
        assertEquals(Color.Gray, Color.fromStr("gray"))
        assertEquals(Color.DarkGray, Color.fromStr("darkgray"))
        assertEquals(Color.LightRed, Color.fromStr("lightred"))
        assertEquals(Color.LightGreen, Color.fromStr("lightgreen"))
        assertEquals(Color.LightYellow, Color.fromStr("lightyellow"))
        assertEquals(Color.LightBlue, Color.fromStr("lightblue"))
        assertEquals(Color.LightMagenta, Color.fromStr("lightmagenta"))
        assertEquals(Color.LightCyan, Color.fromStr("lightcyan"))
        assertEquals(Color.White, Color.fromStr("white"))

        // aliases
        assertEquals(Color.DarkGray, Color.fromStr("lightblack"))
        assertEquals(Color.White, Color.fromStr("lightwhite"))
        assertEquals(Color.White, Color.fromStr("lightgray"))

        // silver = grey = gray
        assertEquals(Color.Gray, Color.fromStr("grey"))
        assertEquals(Color.Gray, Color.fromStr("silver"))

        // spaces are ignored
        assertEquals(Color.DarkGray, Color.fromStr("light black"))
        assertEquals(Color.White, Color.fromStr("light white"))
        assertEquals(Color.White, Color.fromStr("light gray"))

        // dashes are ignored
        assertEquals(Color.DarkGray, Color.fromStr("light-black"))
        assertEquals(Color.White, Color.fromStr("light-white"))
        assertEquals(Color.White, Color.fromStr("light-gray"))

        // underscores are ignored
        assertEquals(Color.DarkGray, Color.fromStr("light_black"))
        assertEquals(Color.White, Color.fromStr("light_white"))
        assertEquals(Color.White, Color.fromStr("light_gray"))

        // bright = light
        assertEquals(Color.DarkGray, Color.fromStr("bright-black"))
        assertEquals(Color.White, Color.fromStr("bright-white"))
        assertEquals(Color.DarkGray, Color.fromStr("brightblack"))
        assertEquals(Color.White, Color.fromStr("brightwhite"))
    }

    @Test
    fun fromInvalidColors() {
        val badColors = listOf(
            "invalid_color", // not a color string
            "abcdef0",       // 7 chars is not a color
            " bcdefa",       // doesn't start with a '#'
            "#abcdef00",     // too many chars
            "#1\uD83E\uDD80" + "2", // len 7 but on char boundaries shouldn't panic
            "resets",        // typo
            "lightblackk",   // typo
        )

        for (badColor in badColors) {
            assertFailsWith<ParseColorError>("bad color: '$badColor'") {
                Color.fromStr(badColor)
            }
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

        val fromTuple3 = Color.from(Triple(89u, 76u, 54u))
        assertEquals(Color.Rgb(89u, 76u, 54u), fromTuple3)

        val fromArray4 = Color.from(ubyteArrayOf(10u, 20u, 30u, 255u))
        assertEquals(Color.Rgb(10u, 20u, 30u), fromArray4)

        val fromTuple4 = Color.from(ubyteArrayOf(200u, 150u, 100u, 0u))
        assertEquals(Color.Rgb(200u, 150u, 100u), fromTuple4)
    }
}

