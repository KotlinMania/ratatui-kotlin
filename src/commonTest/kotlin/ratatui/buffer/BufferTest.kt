package ratatui.buffer

import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BufferTest {
    @Test
    fun debugEmptyBuffer() {
        val buffer = Buffer.empty(Rect.ZERO)
        val expected = "Buffer {\n    area: Rect { x: 0, y: 0, width: 0, height: 0 }\n}"
        assertEquals(expected, buffer.toString())
    }

    @Test
    fun debugSomeExample() {
        val buffer = Buffer.empty(Rect.new(0, 0, 12, 2))
        buffer.setString(0, 0, "Hello World!", Style.default())
        buffer.setString(
            0,
            1,
            "G'day World!",
            Style.default()
                .fg(Color.Green)
                .bg(Color.Yellow)
                .addModifier(Modifier.BOLD)
        )

        val expected = """
            Buffer {
                area: Rect { x: 0, y: 0, width: 12, height: 2 },
                content: [
                    "Hello World!",
                    "G'day World!",
                ],
                styles: [
                    x: 0, y: 0, fg: Reset, bg: Reset, modifier: NONE,
                    x: 0, y: 1, fg: Green, bg: Yellow, modifier: BOLD,
                ]
            }
        """.trimIndent()
        assertEquals(expected, buffer.toString())
    }

    @Test
    fun itTranslatesToAndFromCoordinates() {
        val rect = Rect.new(200, 100, 50, 80)
        val buffer = Buffer.empty(rect)

        assertEquals(Pair(200, 100), buffer.posOf(0))
        assertEquals(0, buffer.indexOf(200, 100))

        assertEquals(Pair(249, 179), buffer.posOf(buffer.content.size - 1))
        assertEquals(buffer.content.size - 1, buffer.indexOf(249, 179))
    }

    @Test
    fun posOfPanicsOnOutOfBounds() {
        val rect = Rect.new(0, 0, 10, 10)
        val buffer = Buffer.empty(rect)

        val ex = assertFailsWith<IllegalArgumentException> {
            buffer.posOf(100)
        }
        check(ex.message?.contains("outside the buffer") == true)
    }

    @Test
    fun indexOfPanicsOnOutOfBounds() {
        val buffer = Buffer.empty(Rect.new(10, 10, 10, 10))

        val ex = assertFailsWith<IllegalStateException> {
            buffer.indexOf(9, 10)
        }
        check(ex.message?.contains("index outside of buffer") == true)
    }

    @Test
    fun testCell() {
        val buffer = Buffer.withLines("Hello", "World")

        val expected = Cell.EMPTY.clone().setSymbol("H")
        assertEquals(expected, buffer.cell(0, 0))
        assertEquals(null, buffer.cell(10, 10))
        assertEquals(expected, buffer.cell(Position(x = 0, y = 0)))
        assertEquals(null, buffer.cell(Position(x = 10, y = 10)))
    }

    @Test
    fun testCellMut() {
        val buffer = Buffer.withLines("Hello", "World")

        val expected = Cell.EMPTY.clone().setSymbol("H")
        assertEquals(expected, buffer.cellMut(0, 0))
        assertEquals(null, buffer.cellMut(10, 10))
        assertEquals(expected, buffer.cellMut(Position(x = 0, y = 0)))
        assertEquals(null, buffer.cellMut(Position(x = 10, y = 10)))
    }

    @Test
    fun indexMut() {
        val buffer = Buffer.withLines("Cat", "Dog")
        buffer[0, 0].setSymbol("B")
        buffer[Position(x = 0, y = 1)].setSymbol("L")
        assertEquals(Buffer.withLines("Bat", "Log"), buffer)
    }

    @Test
    fun setString() {
        val area = Rect.new(0, 0, 5, 1)
        var buffer = Buffer.empty(area)

        // Zero-width
        buffer.setStringn(0, 0, "aaa", 0, Style.default())
        assertEquals(Buffer.withLines("     "), buffer)

        buffer.setString(0, 0, "aaa", Style.default())
        assertEquals(Buffer.withLines("aaa  "), buffer)

        // Width limit
        buffer.setStringn(0, 0, "bbbbbbbbbbbbbb", 4, Style.default())
        assertEquals(Buffer.withLines("bbbb "), buffer)

        buffer.setString(0, 0, "12345", Style.default())
        assertEquals(Buffer.withLines("12345"), buffer)

        // Width truncation
        buffer.setString(0, 0, "123456", Style.default())
        assertEquals(Buffer.withLines("12345"), buffer)

        // multi-line
        buffer = Buffer.empty(Rect.new(0, 0, 5, 2))
        buffer.setString(0, 0, "12345", Style.default())
        buffer.setString(0, 1, "67890", Style.default())
        assertEquals(Buffer.withLines("12345", "67890"), buffer)
    }

    @Test
    fun setStringMultiWidthOverwrite() {
        val area = Rect.new(0, 0, 5, 1)
        val buffer = Buffer.empty(area)
        buffer.setString(0, 0, "aaaaa", Style.default())
        buffer.setString(0, 0, "称号", Style.default())
        assertEquals(Buffer.withLines("称号a"), buffer)
    }

    @Test
    fun diffEmptyEmpty() {
        val area = Rect.new(0, 0, 40, 40)
        val prev = Buffer.empty(area)
        val next = Buffer.empty(area)
        assertEquals(emptyList(), prev.diff(next))
    }

    @Test
    fun diffEmptyFilledCountsAll() {
        val area = Rect.new(0, 0, 10, 10)
        val prev = Buffer.empty(area)
        val next = Buffer.filled(area, Cell.new("a"))
        val diff = prev.diffIter(next)

        var count = 0
        while (diff.hasNext()) {
            diff.next()
            count += 1
        }
        assertEquals(10 * 10, count)
    }

    @Test
    fun diffSingleWidth() {
        val prev = Buffer.withLines(
            "          ",
            "┌Title─┐  ",
            "│      │  ",
            "│      │  ",
            "└──────┘  ",
        )
        val next = Buffer.withLines(
            "          ",
            "┌TITLE─┐  ",
            "│      │  ",
            "│      │  ",
            "└──────┘  ",
        )

        val diff = prev.diff(next)
        val expected = listOf(
            Triple(2, 1, Cell.new("I")),
            Triple(3, 1, Cell.new("T")),
            Triple(4, 1, Cell.new("L")),
            Triple(5, 1, Cell.new("E")),
        )
        assertEquals(expected, diff)
    }

    @Test
    fun mergeDiffIdempotent() {
        val prev = Buffer.withLines("123")
        val next = Buffer.withLines("456")
        prev.merge(next)
        assertEquals(emptyList(), prev.diff(next))
    }

    @Test
    fun mergeDiffForcedWidth() {
        val prev = Buffer.withLines("123")
        val next = Buffer.empty(Rect.new(0, 0, 3, 1))

        val cell = requireNotNull(next.cellMut(0, 0))
        cell.setSymbol("456")
        val width = requireNotNull(NonZeroUShort.new(3u.toUShort()))
        cell.setDiffOption(CellDiffOption.ForcedWidth(width))

        prev.merge(next)
        assertEquals(emptyList(), prev.diff(next))
    }
}
