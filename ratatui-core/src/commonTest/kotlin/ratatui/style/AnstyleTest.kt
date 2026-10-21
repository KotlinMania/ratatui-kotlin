// port-lint: source ratatui-core/src/style/anstyle.rs
package ratatui.style

import ai.solace.tui.anstyle.Ansi256Color
import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.Effects
import ai.solace.tui.anstyle.RgbColor
import ai.solace.tui.anstyle.or
import ai.solace.tui.anstyle.Color as AnstyleColor
import ai.solace.tui.anstyle.Style as AnstyleStyle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AnstyleTest {
    @Test
    fun anstyleToColor() {
        val anstyleColor = Ansi256Color(42u)
        val color = Color.from(anstyleColor)
        assertEquals(Color.Indexed(42u), color)
    }

    @Test
    fun colorToAnsi256Color() {
        val color = Color.Indexed(42u)
        val anstyleColor = Ansi256Color.tryFrom(color).unwrap()
        assertEquals(Ansi256Color(42u), anstyleColor)
    }

    @Test
    fun colorToAnsi256ColorError() {
        val color = Color.Rgb(0u, 0u, 0u)
        val result = Ansi256Color.tryFrom(color)
        assertTrue(result.isErr)
        assertEquals(TryFromColorError.Ansi256, result.error)
    }

    @Test
    fun ansiColorToColor() {
        val ansiColor = AnsiColor.Red
        val color = Color.from(ansiColor)
        assertEquals(Color.Red, color)
    }

    @Test
    fun colorToAnsiColor() {
        val color = Color.Red
        val ansiColor = AnsiColor.tryFrom(color).unwrap()
        assertEquals(AnsiColor.Red, ansiColor)
    }

    @Test
    fun colorToAnsiColorError() {
        val color = Color.Rgb(0u, 0u, 0u)
        val result = AnsiColor.tryFrom(color)
        assertTrue(result.isErr)
        assertEquals(TryFromColorError.Ansi, result.error)
    }

    @Test
    fun rgbColorToColor() {
        val rgbColor = RgbColor(255u, 0u, 0u)
        val color = Color.from(rgbColor)
        assertEquals(Color.Rgb(255u, 0u, 0u), color)
    }

    @Test
    fun colorToRgbColor() {
        val color = Color.Rgb(255u, 0u, 0u)
        val rgbColor = RgbColor.tryFrom(color).unwrap()
        assertEquals(RgbColor(255u, 0u, 0u), rgbColor)
    }

    @Test
    fun colorToRgbColorError() {
        val color = Color.Indexed(42u)
        val result = RgbColor.tryFrom(color)
        assertTrue(result.isErr)
        assertEquals(TryFromColorError.RgbColor, result.error)
    }

    @Test
    fun effectsToModifier() {
        val effects = Effects.BOLD or Effects.ITALIC
        val modifier = Modifier.from(effects)
        assertTrue(modifier.contains(Modifier.BOLD))
        assertTrue(modifier.contains(Modifier.ITALIC))
    }

    @Test
    fun modifierToEffects() {
        val modifier = Modifier.BOLD or Modifier.ITALIC
        val effects = Effects.from(modifier)
        assertTrue(effects.contains(Effects.BOLD))
        assertTrue(effects.contains(Effects.ITALIC))
    }

    @Test
    fun anstyleStyleToStyle() {
        val anstyleStyle = AnstyleStyle()
            .fgColor(AnstyleColor.Ansi(AnsiColor.Red))
            .bgColor(AnstyleColor.Ansi(AnsiColor.Blue))
            .underlineColor(AnstyleColor.Ansi(AnsiColor.Green))
            .effects(Effects.BOLD or Effects.ITALIC)

        val style = Style.from(anstyleStyle)

        assertEquals(Color.Red, style.fg)
        assertEquals(Color.Blue, style.bg)
        assertEquals(Color.Green, style.underlineColor)
        assertTrue(style.addModifier.contains(Modifier.BOLD))
        assertTrue(style.addModifier.contains(Modifier.ITALIC))
    }

    @Test
    fun styleToAnstyleStyle() {
        val style = Style(
            fg = Color.Red,
            bg = Color.Blue,
            underlineColor = Color.Green,
            addModifier = Modifier.BOLD or Modifier.ITALIC
        )

        val anstyleStyle = AnstyleStyle.from(style)

        assertEquals(AnstyleColor.Ansi(AnsiColor.Red), anstyleStyle.getFgColor())
        assertEquals(AnstyleColor.Ansi(AnsiColor.Blue), anstyleStyle.getBgColor())
        assertEquals(AnstyleColor.Ansi(AnsiColor.Green), anstyleStyle.getUnderlineColor())
        assertTrue(anstyleStyle.getEffects().contains(Effects.BOLD))
        assertTrue(anstyleStyle.getEffects().contains(Effects.ITALIC))
    }

    @Test
    fun convertingResetPanicsExplicitly() {
        val ex = assertFailsWith<IllegalStateException> { AnstyleColor.from(Color.Reset) }
        assertEquals("Color::Reset has no equivalent in anstyle", ex.message)
    }
}

