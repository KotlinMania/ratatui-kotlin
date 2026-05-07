// port-lint: source ratatui-core/src/buffer/cell.rs
package ratatui.buffer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import ratatui.style.Color
import ratatui.style.Style

class CellTest {
    @Test
    fun new() {
        val cell = Cell.new("あ")
        assertEquals("あ", cell.symbol())
        assertEquals(Color.Reset, cell.fg)
        assertEquals(Color.Reset, cell.bg)
        assertEquals(CellDiffOption.None, cell.diffOption)
    }

    @Test
    fun empty() {
        val cell = Cell.EMPTY
        assertEquals(" ", cell.symbol())
    }

    @Test
    fun emptyIsNotSharedMutableInstance() {
        val a = Cell.EMPTY
        val b = Cell.EMPTY
        assertNotSame(a, b)

        a.setSymbol("x")
        assertEquals("x", a.symbol())
        assertEquals(" ", b.symbol())
    }

    @Test
    fun setSymbol() {
        val cell = Cell.EMPTY
        cell.setSymbol("あ") // Multi-byte character
        assertEquals("あ", cell.symbol())
        cell.setSymbol("👨‍👩‍👧‍👦") // Multiple code units combined with ZWJ
        assertEquals("👨‍👩‍👧‍👦", cell.symbol())
    }

    @Test
    fun appendSymbol() {
        val cell = Cell.EMPTY
        cell.setSymbol("あ")
        cell.appendSymbol("\u200B") // zero-width space
        assertEquals("あ\u200B", cell.symbol())
    }

    @Test
    fun setChar() {
        val cell = Cell.EMPTY
        cell.setChar('あ')
        assertEquals("あ", cell.symbol())
    }

    @Test
    fun setFg() {
        val cell = Cell.EMPTY
        cell.setFg(Color.Red)
        assertEquals(Color.Red, cell.fg)
    }

    @Test
    fun setBg() {
        val cell = Cell.EMPTY
        cell.setBg(Color.Red)
        assertEquals(Color.Red, cell.bg)
    }

    @Test
    fun setStyle() {
        val cell = Cell.EMPTY
        cell.setStyle(Style.new().fg(Color.Red).bg(Color.Blue))
        assertEquals(Color.Red, cell.fg)
        assertEquals(Color.Blue, cell.bg)
    }

    @Test
    fun setSkip() {
        val cell = Cell.EMPTY
        cell.setDiffOption(CellDiffOption.Skip)
        assertEquals(CellDiffOption.Skip, cell.diffOption)
    }

    @Test
    fun reset() {
        val cell = Cell.EMPTY
        cell.setSymbol("あ")
        cell.setFg(Color.Red)
        cell.setBg(Color.Blue)
        cell.setDiffOption(CellDiffOption.Skip)
        cell.reset()
        assertEquals(" ", cell.symbol())
        assertEquals(Color.Reset, cell.fg)
        assertEquals(Color.Reset, cell.bg)
        assertEquals(CellDiffOption.None, cell.diffOption)
    }

    @Test
    fun style() {
        val cell = Cell.EMPTY
        assertEquals(
            Style(
                fg = Color.Reset,
                bg = Color.Reset,
                addModifier = ratatui.style.Modifier.empty(),
                subModifier = ratatui.style.Modifier.empty()
            ),
            cell.style()
        )
    }

    @Test
    fun default() {
        val cell = Cell.default()
        assertEquals(" ", cell.symbol())
    }

    @Test
    fun cellEq() {
        val cell1 = Cell.new("あ")
        val cell2 = Cell.new("あ")
        assertEquals(cell1, cell2)
    }

    @Test
    fun cellNe() {
        val cell1 = Cell.new("あ")
        val cell2 = Cell.new("い")
        assertNotEquals(cell1, cell2)
    }
}
