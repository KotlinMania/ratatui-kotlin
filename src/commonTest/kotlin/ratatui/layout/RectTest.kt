// port-lint: source ratatui-core/tests/rect.rs
package ratatui.layout

import ratatui.buffer.Buffer
import ratatui.widgets.Widget
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration tests for Rect operations visualized with buffers.
 *
 * Transliteration of `ratatui-core/tests/rect.rs`.
 */
class RectTest {
    /**
     * A minimal widget that fills its entire area with the given symbol.
     */
    private class Filled(
        private val symbol: String
    ) : Widget {
        override fun render(area: Rect, buf: Buffer) {
            for (y in area.top() until area.bottom()) {
                for (x in area.left() until area.right()) {
                    buf.cellMut(Position(x, y))?.setSymbol(symbol)
                }
            }
        }
    }

    @Test
    fun inner() {
        val base = Rect.new(2, 2, 10, 6)
        val inner = base.inner(Margin.new(2, 1))

        val buf = Buffer.empty(Rect.new(0, 0, 15, 10))
        Filled("█").render(base, buf)
        Filled("░").render(inner, buf)

        val expected = Buffer.withLines(
            "               ",
            "               ",
            "  ██████████   ",
            "  ██░░░░░░██   ",
            "  ██░░░░░░██   ",
            "  ██░░░░░░██   ",
            "  ██░░░░░░██   ",
            "  ██████████   ",
            "               ",
            "               "
        )
        assertEquals(expected, buf)
    }

    @Test
    fun outer() {
        val base = Rect.new(4, 3, 6, 4)
        val outer = base.outer(Margin.new(2, 1))

        val buf = Buffer.empty(Rect.new(0, 0, 15, 10))
        Filled("░").render(outer, buf)
        Filled("█").render(base, buf)

        val expected = Buffer.withLines(
            "               ",
            "               ",
            "  ░░░░░░░░░░   ",
            "  ░░██████░░   ",
            "  ░░██████░░   ",
            "  ░░██████░░   ",
            "  ░░██████░░   ",
            "  ░░░░░░░░░░   ",
            "               ",
            "               "
        )
        assertEquals(expected, buf)
    }

    @Test
    fun offset() {
        val base = Rect.new(2, 2, 5, 3)
        val moved = base + Offset.new(4, 2)

        val buf = Buffer.empty(Rect.new(0, 0, 15, 10))
        Filled("░").render(base, buf)
        Filled("█").render(moved, buf)

        val expected = Buffer.withLines(
            "               ",
            "               ",
            "  ░░░░░        ",
            "  ░░░░░        ",
            "  ░░░░█████    ",
            "      █████    ",
            "      █████    ",
            "               ",
            "               ",
            "               "
        )
        assertEquals(expected, buf)
    }

    @Test
    fun intersection() {
        val a = Rect.new(2, 2, 6, 4)
        val b = Rect.new(5, 3, 6, 4)
        val inter = a.intersection(b)

        val buf = Buffer.empty(Rect.new(0, 0, 15, 10))
        Filled("░").render(a, buf)
        Filled("▒").render(b, buf)
        Filled("█").render(inter, buf)

        val expected = Buffer.withLines(
            "               ",
            "               ",
            "  ░░░░░░       ",
            "  ░░░███▒▒▒    ",
            "  ░░░███▒▒▒    ",
            "  ░░░███▒▒▒    ",
            "     ▒▒▒▒▒▒    ",
            "               ",
            "               ",
            "               "
        )
        assertEquals(expected, buf)
    }

    @Test
    fun clamp() {
        val area = Rect.new(2, 2, 10, 6)
        val rect = Rect.new(8, 5, 8, 4)
        val clamped = rect.clamp(area)

        val buf = Buffer.empty(Rect.new(0, 0, 20, 12))
        Filled("█").render(area, buf)
        Filled("▒").render(rect, buf)
        Filled("░").render(clamped, buf)

        val expected = Buffer.withLines(
            "                    ",
            "                    ",
            "  ██████████        ",
            "  ██████████        ",
            "  ██░░░░░░░░        ",
            "  ██░░░░░░░░▒▒▒▒    ",
            "  ██░░░░░░░░▒▒▒▒    ",
            "  ██░░░░░░░░▒▒▒▒    ",
            "        ▒▒▒▒▒▒▒▒    ",
            "                    ",
            "                    ",
            "                    "
        )
        assertEquals(expected, buf)
    }
}

