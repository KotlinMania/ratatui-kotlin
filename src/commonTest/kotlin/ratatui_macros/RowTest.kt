package ratatui_macros

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.Text
import ratatui.widgets.table.Cell
import ratatui.widgets.table.Row

class RowTest {
    @Test
    fun rowLiteral() {
        val actual = row("hello", "world")
        val expected = Row.new(listOf(Cell.new("hello"), Cell.new("world")))
        assertEquals(expected, actual)
    }

    @Test
    fun rowEmpty() {
        val actual = row()
        assertEquals(Row.default(), actual)
    }

    @Test
    fun rowSingleCell() {
        val actual = row(Cell.new("foo"))
        val expected = Row.new(listOf(Cell.new("foo")))
        assertEquals(expected, actual)
    }

    @Test
    fun rowRepeatedCell() {
        val actual = row(Cell.new("foo"), 2)
        val expected = Row.new(listOf(Cell.new("foo"), Cell.new("foo")))
        assertEquals(expected, actual)
    }

    @Test
    fun rowVecCountSyntax() {
        val actual = row("hello", 2)
        val expected = Row.new(listOf(Cell.new("hello"), Cell.new("hello")))
        assertEquals(expected, actual)
    }

    @Test
    fun rowExplicitUseOfSpanAndLine() {
        val actual = row(line("hello"), span("world"))
        val expected = Row.new(
            listOf(
                Cell.new(Line.from("hello")),
                Cell.new(Span.from("world"))
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun multipleRows() {
        val rows = arrayOf(
            row("Find File", Text.raw("ctrl+f").rightAligned()),
            row("Open recent", Text.raw("ctrl+r").rightAligned()),
            row("Open config", Text.raw("ctrl+k").rightAligned())
        )
        assertContentEquals(
            rows,
            arrayOf(
                Row.new(listOf(Cell.new("Find File"), Cell.new(Text.raw("ctrl+f").rightAligned()))),
                Row.new(listOf(Cell.new("Open recent"), Cell.new(Text.raw("ctrl+r").rightAligned()))),
                Row.new(listOf(Cell.new("Open config"), Cell.new(Text.raw("ctrl+k").rightAligned())))
            )
        )
    }
}
