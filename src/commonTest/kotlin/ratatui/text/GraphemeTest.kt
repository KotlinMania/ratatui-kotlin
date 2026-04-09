package ratatui.text

import ratatui.style.Style
import ratatui.style.green
import ratatui.style.onRed
import ratatui.style.yellow
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphemeTest {
    @Test
    fun new() {
        val style = Style.new().yellow()
        val sg = StyledGrapheme.new("a", style)
        assertEquals("a", sg.symbol)
        assertEquals(style, sg.style)
    }

    @Test
    fun style() {
        val style = Style.new().yellow()
        val sg = StyledGrapheme.new("a", style)
        assertEquals(style, sg.style())
    }

    @Test
    fun setStyle() {
        val style = Style.new().yellow().onRed()
        val style2 = Style.new().green()
        val sg = StyledGrapheme.new("a", style).setStyle(style2)
        assertEquals(style2, sg.style)
    }

    @Test
    fun stylize() {
        val style = Style.new().yellow().onRed()
        val sg = StyledGrapheme.new("a", style).green()
        assertEquals(Style.new().green().onRed(), sg.style)
    }
}

