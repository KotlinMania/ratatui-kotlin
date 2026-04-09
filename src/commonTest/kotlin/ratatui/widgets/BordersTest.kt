package ratatui.widgets

import kotlin.test.Test
import kotlin.test.assertEquals

class BordersTest {
    @Test
    fun testBordersToString() {
        assertEquals("NONE", Borders.empty().toString())
        assertEquals("NONE", Borders.NONE.toString())
        assertEquals("TOP", Borders.TOP.toString())
        assertEquals("BOTTOM", Borders.BOTTOM.toString())
        assertEquals("LEFT", Borders.LEFT.toString())
        assertEquals("RIGHT", Borders.RIGHT.toString())
        assertEquals("ALL", Borders.ALL.toString())
        assertEquals("ALL", Borders.all().toString())

        assertEquals("TOP | BOTTOM", (Borders.TOP or Borders.BOTTOM).toString())
    }

    @Test
    fun borderEmpty() {
        val empty = Borders.NONE
        assertEquals(empty, border())
    }

    @Test
    fun borderAll() {
        val all = Borders.ALL
        assertEquals(all, border(Borders.ALL))
        assertEquals(all, border(Borders.TOP, Borders.BOTTOM, Borders.LEFT, Borders.RIGHT))
    }

    @Test
    fun borderLeftRight() {
        val leftRight = Borders.fromBits((Borders.LEFT.bits or Borders.RIGHT.bits).toUByte())
        assertEquals(leftRight, Borders.fromBits(border(Borders.RIGHT, Borders.LEFT).bits))
    }
}

