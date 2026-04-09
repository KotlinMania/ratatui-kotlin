// port-lint: source ratatui-core/src/style.rs
package ratatui.style

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StyleTest {
    @Test
    fun debug() {
        assertEquals("Style.new()", Style.new().toString())
        assertEquals("Style.new()", Style.default().toString())
        assertEquals("Style.new().red()", Style.new().red().toString())
        assertEquals("Style.new().onBlue()", Style.new().onBlue().toString())
        assertEquals("Style.new().bold()", Style.new().bold().toString())
        assertEquals("Style.new().notItalic()", Style.new().notItalic().toString())
        assertEquals(
            "Style.new().red().onBlue().bold().italic().notDim().notHidden()",
            Style.new().red().onBlue().bold().italic().notDim().notHidden().toString()
        )
    }

    @Test
    fun combinedPatchGivesSameResultAsIndividualPatch() {
        val styles = listOf(
            Style.new(),
            Style.new().fg(Color.Yellow),
            Style.new().bg(Color.Yellow),
            Style.new().addModifier(Modifier.BOLD),
            Style.new().removeModifier(Modifier.BOLD),
            Style.new().addModifier(Modifier.ITALIC),
            Style.new().removeModifier(Modifier.ITALIC),
            Style.new().addModifier(Modifier.ITALIC or Modifier.BOLD),
            Style.new().removeModifier(Modifier.ITALIC or Modifier.BOLD),
        )

        for (a in styles) {
            for (b in styles) {
                for (c in styles) {
                    for (d in styles) {
                        assertEquals(
                            Style.new().patch(a).patch(b).patch(c).patch(d),
                            Style.new().patch(a.patch(b.patch(c.patch(d))))
                        )
                    }
                }
            }
        }
    }

    @Test
    fun modifierDebug() {
        assertEquals("NONE", Modifier.empty().toString())
        assertEquals("BOLD", Modifier.BOLD.toString())
        assertEquals("DIM", Modifier.DIM.toString())
        assertEquals("ITALIC", Modifier.ITALIC.toString())
        assertEquals("UNDERLINED", Modifier.UNDERLINED.toString())
        assertEquals("SLOW_BLINK", Modifier.SLOW_BLINK.toString())
        assertEquals("RAPID_BLINK", Modifier.RAPID_BLINK.toString())
        assertEquals("REVERSED", Modifier.REVERSED.toString())
        assertEquals("HIDDEN", Modifier.HIDDEN.toString())
        assertEquals("CROSSED_OUT", Modifier.CROSSED_OUT.toString())
        assertEquals("BOLD | DIM", (Modifier.BOLD or Modifier.DIM).toString())
        assertEquals(
            "BOLD | DIM | ITALIC | UNDERLINED | SLOW_BLINK | RAPID_BLINK | REVERSED | HIDDEN | CROSSED_OUT",
            Modifier.all().toString()
        )
    }

    @Test
    fun hasModifierChecks() {
        val style = Style.default().addModifier(Modifier.BOLD or Modifier.ITALIC)
        assertTrue(style.hasModifier(Modifier.BOLD))
        assertTrue(style.hasModifier(Modifier.ITALIC))
        assertFalse(style.hasModifier(Modifier.UNDERLINED))

        val removed = Style.default().addModifier(Modifier.BOLD).removeModifier(Modifier.BOLD)
        assertFalse(removed.hasModifier(Modifier.BOLD))
    }

    @Test
    fun fgCanBeStylized() {
        assertEquals(Style.new().fg(Color.Black), Style.new().black())
        assertEquals(Style.new().fg(Color.Red), Style.new().red())
        assertEquals(Style.new().fg(Color.Green), Style.new().green())
        assertEquals(Style.new().fg(Color.Yellow), Style.new().yellow())
        assertEquals(Style.new().fg(Color.Blue), Style.new().blue())
        assertEquals(Style.new().fg(Color.Magenta), Style.new().magenta())
        assertEquals(Style.new().fg(Color.Cyan), Style.new().cyan())
        assertEquals(Style.new().fg(Color.White), Style.new().white())
        assertEquals(Style.new().fg(Color.Gray), Style.new().gray())
        assertEquals(Style.new().fg(Color.DarkGray), Style.new().darkGray())
        assertEquals(Style.new().fg(Color.LightRed), Style.new().lightRed())
        assertEquals(Style.new().fg(Color.LightGreen), Style.new().lightGreen())
        assertEquals(Style.new().fg(Color.LightYellow), Style.new().lightYellow())
        assertEquals(Style.new().fg(Color.LightBlue), Style.new().lightBlue())
        assertEquals(Style.new().fg(Color.LightMagenta), Style.new().lightMagenta())
        assertEquals(Style.new().fg(Color.LightCyan), Style.new().lightCyan())
    }

    @Test
    fun bgCanBeStylized() {
        assertEquals(Style.new().bg(Color.Black), Style.new().onBlack())
        assertEquals(Style.new().bg(Color.Red), Style.new().onRed())
        assertEquals(Style.new().bg(Color.Green), Style.new().onGreen())
        assertEquals(Style.new().bg(Color.Yellow), Style.new().onYellow())
        assertEquals(Style.new().bg(Color.Blue), Style.new().onBlue())
        assertEquals(Style.new().bg(Color.Magenta), Style.new().onMagenta())
        assertEquals(Style.new().bg(Color.Cyan), Style.new().onCyan())
        assertEquals(Style.new().bg(Color.White), Style.new().onWhite())
        assertEquals(Style.new().bg(Color.Gray), Style.new().onGray())
        assertEquals(Style.new().bg(Color.DarkGray), Style.new().onDarkGray())
        assertEquals(Style.new().bg(Color.LightRed), Style.new().onLightRed())
        assertEquals(Style.new().bg(Color.LightGreen), Style.new().onLightGreen())
        assertEquals(Style.new().bg(Color.LightYellow), Style.new().onLightYellow())
        assertEquals(Style.new().bg(Color.LightBlue), Style.new().onLightBlue())
        assertEquals(Style.new().bg(Color.LightMagenta), Style.new().onLightMagenta())
        assertEquals(Style.new().bg(Color.LightCyan), Style.new().onLightCyan())
    }

    @Test
    fun addModifierCanBeStylized() {
        assertEquals(Style.new().addModifier(Modifier.BOLD), Style.new().bold())
        assertEquals(Style.new().addModifier(Modifier.DIM), Style.new().dim())
        assertEquals(Style.new().addModifier(Modifier.ITALIC), Style.new().italic())
        assertEquals(Style.new().addModifier(Modifier.UNDERLINED), Style.new().underlined())
        assertEquals(Style.new().addModifier(Modifier.SLOW_BLINK), Style.new().slowBlink())
        assertEquals(Style.new().addModifier(Modifier.RAPID_BLINK), Style.new().rapidBlink())
        assertEquals(Style.new().addModifier(Modifier.REVERSED), Style.new().reversed())
        assertEquals(Style.new().addModifier(Modifier.HIDDEN), Style.new().hidden())
        assertEquals(Style.new().addModifier(Modifier.CROSSED_OUT), Style.new().crossedOut())
    }

    @Test
    fun removeModifierCanBeStylized() {
        assertEquals(Style.new().removeModifier(Modifier.BOLD), Style.new().notBold())
        assertEquals(Style.new().removeModifier(Modifier.DIM), Style.new().notDim())
        assertEquals(Style.new().removeModifier(Modifier.ITALIC), Style.new().notItalic())
        assertEquals(Style.new().removeModifier(Modifier.UNDERLINED), Style.new().notUnderlined())
        assertEquals(Style.new().removeModifier(Modifier.SLOW_BLINK), Style.new().notSlowBlink())
        assertEquals(Style.new().removeModifier(Modifier.RAPID_BLINK), Style.new().notRapidBlink())
        assertEquals(Style.new().removeModifier(Modifier.REVERSED), Style.new().notReversed())
        assertEquals(Style.new().removeModifier(Modifier.HIDDEN), Style.new().notHidden())
        assertEquals(Style.new().removeModifier(Modifier.CROSSED_OUT), Style.new().notCrossedOut())
    }

    @Test
    fun fromColor() {
        assertEquals(Style.new().fg(Color.Red), Style.from(Color.Red))
    }

    @Test
    fun fromColorColor() {
        assertEquals(Style.new().fg(Color.Red).bg(Color.Blue), Style.from(Color.Red, Color.Blue))
    }

    @Test
    fun fromModifier() {
        assertEquals(
            Style.new().addModifier(Modifier.BOLD).addModifier(Modifier.ITALIC),
            Style.from(Modifier.BOLD or Modifier.ITALIC)
        )
    }

    @Test
    fun fromModifierModifier() {
        assertEquals(
            Style.new()
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC)
                .removeModifier(Modifier.DIM),
            Style.from(Modifier.BOLD or Modifier.ITALIC, Modifier.DIM)
        )
    }

    @Test
    fun fromColorModifier() {
        assertEquals(
            Style.new()
                .fg(Color.Red)
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC),
            Style.from(Color.Red, Modifier.BOLD or Modifier.ITALIC)
        )
    }

    @Test
    fun fromColorColorModifier() {
        assertEquals(
            Style.new()
                .fg(Color.Red)
                .bg(Color.Blue)
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC),
            Style.from(Color.Red, Color.Blue, Modifier.BOLD or Modifier.ITALIC)
        )
    }

    @Test
    fun fromColorColorModifierModifier() {
        assertEquals(
            Style.new()
                .fg(Color.Red)
                .bg(Color.Blue)
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC)
                .removeModifier(Modifier.DIM),
            Style.from(Color.Red, Color.Blue, Modifier.BOLD or Modifier.ITALIC, Modifier.DIM)
        )
    }
}
