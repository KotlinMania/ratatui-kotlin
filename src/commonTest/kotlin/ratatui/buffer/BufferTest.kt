// port-lint: source ratatui-core/src/buffer/buffer.rs
package ratatui.buffer

import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.style.blue
import ratatui.style.red
import ratatui.text.Line
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
                    x: 0, y: 0, fg: Reset, bg: Reset, underline: Reset, modifier: NONE,
                    x: 0, y: 1, fg: Green, bg: Yellow, underline: Reset, modifier: BOLD,
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
    fun index() {
        val buffer = Buffer.withLines("Hello", "World")

        val expected = Cell.EMPTY.clone().setSymbol("H")
        assertEquals(expected, buffer[0, 0])
    }

    @Test
    fun indexOutOfBoundsPanics() {
        val rect = Rect.new(10, 10, 10, 10)
        val buffer = Buffer.empty(rect)

        val cases = listOf(
            9 to 10,
            10 to 9,
            20 to 10,
            10 to 20,
        )

        for ((x, y) in cases) {
            val ex = assertFailsWith<IllegalStateException> { buffer[x, y] }
            check(ex.message?.contains("index outside of buffer") == true)
        }
    }

    @Test
    fun indexMutOutOfBoundsPanics() {
        val buffer = Buffer.empty(Rect.new(10, 10, 10, 10))

        val cases = listOf(
            9 to 10,
            10 to 9,
            20 to 10,
            10 to 20,
        )

        for ((x, y) in cases) {
            val ex = assertFailsWith<IllegalStateException> {
                buffer[x, y].setSymbol("A")
            }
            check(ex.message?.contains("index outside of buffer") == true)
        }
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
    fun setStringZeroWidth() {
        assertEquals(0u, "\u200B".cellWidth())

        val area = Rect.new(0, 0, 1, 1)
        val buffer = Buffer.empty(area)

        // Leading grapheme with zero width
        val leading = "\u200Ba"
        buffer.setStringn(0, 0, leading, 1, Style.default())
        assertEquals(Buffer.withLines("a"), buffer)

        // Trailing grapheme with zero width
        val trailing = "a\u200B"
        buffer.setStringn(0, 0, trailing, 1, Style.default())
        assertEquals(Buffer.withLines("a"), buffer)
    }

    @Test
    fun setStringDoubleWidth() {
        val area = Rect.new(0, 0, 5, 1)
        val buffer = Buffer.empty(area)
        buffer.setString(0, 0, "コン", Style.default())
        assertEquals(Buffer.withLines("コン "), buffer)

        // Only 1 space left.
        buffer.setString(0, 0, "コンピ", Style.default())
        assertEquals(Buffer.withLines("コン "), buffer)
    }

    @Test
    fun setLineRaw() {
        val cases = listOf(
            "" to "     ",
            "1" to "1    ",
            "12345" to "12345",
            "123456" to "12345",
        )

        for ((content, expected) in cases) {
            val buffer = Buffer.empty(Rect.new(0, 0, 5, 1))
            val line = Line.raw(content)
            buffer.setLine(0, 0, line, 5)

            val expectedBuffer = Buffer.empty(buffer.area)
            expectedBuffer.setString(0, 0, expected, Style.default())
            assertEquals(expectedBuffer, buffer)
        }
    }

    @Test
    fun setLineStyled() {
        val cases = listOf(
            "" to "     ",
            "1" to "1    ",
            "12345" to "12345",
            "123456" to "12345",
        )

        for ((content, expectedContent) in cases) {
            val buffer = Buffer.empty(Rect.new(0, 0, 5, 1))
            val color = Color.Blue
            val line = Line.styled(content, Style.new().fg(color))
            buffer.setLine(0, 0, line, 5)

            val actualContents = buildString {
                for (cell in buffer.content) {
                    append(cell.symbol())
                }
            }
            val actualStyles = buffer.content.map { it.fg }

            val expectedStyles = buildList {
                val styled = minOf(content.length, 5)
                repeat(styled) { add(color) }
                repeat(5 - styled) { add(Color.Reset) }
            }

            assertEquals(expectedContent, actualContents)
            assertEquals(expectedStyles, actualStyles)
        }
    }

    @Test
    fun setStyle() {
        val buffer = Buffer.withLines("aaaaa", "bbbbb", "ccccc")
        buffer.setStyle(Rect.new(0, 1, 5, 1), Style.new().red())
        val expected = Buffer.withLines(
            listOf(
                Line.from("aaaaa"),
                Line.from("bbbbb".red()),
                Line.from("ccccc"),
            )
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun setStyleDoesNotPanicWhenOutOfArea() {
        val buffer = Buffer.withLines("aaaaa", "bbbbb", "ccccc")
        buffer.setStyle(Rect.new(0, 1, 10, 3), Style.new().red())
        val expected = Buffer.withLines(
            listOf(
                Line.from("aaaaa"),
                Line.from("bbbbb".red()),
                Line.from("ccccc".red()),
            )
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun withLinesComputesArea() {
        val buffer = Buffer.withLines(
            "┌────────┐",
            "│コンピュ│",
            "│ーa 上で│",
            "└────────┘",
        )
        assertEquals(0, buffer.area.x)
        assertEquals(0, buffer.area.y)
        assertEquals(10, buffer.area.width)
        assertEquals(4, buffer.area.height)
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
    fun diffFilledFilled() {
        val area = Rect.new(0, 0, 40, 40)
        val prev = Buffer.filled(area, Cell.new("a"))
        val next = Buffer.filled(area, Cell.new("a"))
        assertEquals(emptyList(), prev.diff(next))
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
            BufferDiff.Item(2, 1, Cell.new("I")),
            BufferDiff.Item(3, 1, Cell.new("T")),
            BufferDiff.Item(4, 1, Cell.new("L")),
            BufferDiff.Item(5, 1, Cell.new("E")),
        )
        assertEquals(expected, diff)
    }

    @Test
    fun diffMultiWidth() {
        val prev = Buffer.withLines(
            "┌Title─┐  ",
            "└──────┘  ",
        )
        val next = Buffer.withLines(
            "┌称号──┐  ",
            "└──────┘  ",
        )

        val diff = prev.diff(next)
        val expected = listOf(
            BufferDiff.Item(1, 0, Cell.new("称")),
            // Skipped "i"
            BufferDiff.Item(3, 0, Cell.new("号")),
            // Skipped "l"
            BufferDiff.Item(5, 0, Cell.new("─")),
        )
        assertEquals(expected, diff)
    }

    @Test
    fun diffMultiWidthOffset() {
        val prev = Buffer.withLines("┌称号──┐")
        val next = Buffer.withLines("┌─称号─┐")

        val diff = prev.diff(next)
        val expected = listOf(
            BufferDiff.Item(1, 0, Cell.new("─")),
            BufferDiff.Item(2, 0, Cell.new("称")),
            BufferDiff.Item(4, 0, Cell.new("号")),
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

    @Test
    fun mergeDiffLink() {
        val prev = Buffer.withLines("_".repeat(4))
        val next = Buffer.empty(Rect.new(0, 0, 4, 1))
        val width = requireNotNull(NonZeroUShort.new(4.toUShort()))

        // Squeeze everything into the first cell, remaining cells are irrelevant.
        requireNotNull(next.cellMut(0, 0))
            .setSymbol("\u001b]8;;http://example.com\u001b\\link\u001b]8;;\u001b\\")
            .setDiffOption(CellDiffOption.ForcedWidth(width))

        prev.merge(next)
        assertEquals(emptyList(), prev.diff(next))
    }

    @Test
    fun mergeDiffSplitLink() {
        val prev = Buffer.withLines("_".repeat(8))
        val next = Buffer.empty(Rect.new(0, 0, 8, 1))

        val width2 = requireNotNull(NonZeroUShort.new(2.toUShort()))

        // Squeeze starting sequence and first character in first cell.
        requireNotNull(next.cellMut(0, 0))
            .setSymbol("\u001b]8;;http://example.com\u001b\\🔗")
            .setDiffOption(CellDiffOption.ForcedWidth(width2))

        // Set inner characters normally.
        requireNotNull(next.cellMut(2, 0)).setSymbol("l")
        requireNotNull(next.cellMut(3, 0)).setSymbol("i")
        requireNotNull(next.cellMut(4, 0)).setSymbol("n")
        requireNotNull(next.cellMut(5, 0)).setSymbol("k")

        // Squeeze closing part into last cell.
        requireNotNull(next.cellMut(7, 0))
            .setSymbol("🔗\u001b]8;;\u001b\\")
            .setDiffOption(CellDiffOption.ForcedWidth(width2))

        prev.merge(next)
        assertEquals(emptyList(), prev.diff(next))
    }

    @Test
    fun mergeDiffImageSequences() {
        val prev = Buffer.empty(Rect.new(0, 0, 20, 1))

        val placeholder = codePointString(0x10EEEE)
        val kittyImagePlaceholderStart = buildString {
            // Store cursor state.
            append("\u001b[s")
            // Set fg color to some 24-bit value (imaginary kitty protocol image id).
            append("\u001b[38;2;0;0;0m")
            // Placeholder symbol + 3 combining marks carrying extra data.
            append(placeholder)
            append("\u0305")
            append("\u0305")
            append("\u0305")
        }

        requireNotNull(prev.cellMut(0, 0))
            .setSymbol(kittyImagePlaceholderStart)
            .setDiffOption(CellDiffOption.ForcedWidth(requireNotNull(NonZeroUShort.new(1.toUShort()))))

        // Add two follow up placeholder symbols that have a natural width of 1.
        requireNotNull(prev.cellMut(1, 0)).setSymbol(placeholder)
        requireNotNull(prev.cellMut(2, 0)).setSymbol(placeholder)

        // Restore cursor state.
        requireNotNull(prev.cellMut(3, 0))
            .setSymbol(placeholder + "\u001b[u")
            .setDiffOption(CellDiffOption.ForcedWidth(requireNotNull(NonZeroUShort.new(1.toUShort()))))

        val buffer = Buffer.filled(Rect.new(0, 0, 20, 1), Cell.new("x"))
        buffer.merge(prev)

        assertEquals(emptyList(), buffer.diff(prev))
    }

    @Test
    fun diffSkip() {
        val prev = Buffer.withLines("123")
        val next = Buffer.withLines("456")
        for (i in 1 until 3) {
            next.content[i].setDiffOption(CellDiffOption.Skip)
        }

        val diff = prev.diff(next)
        assertEquals(listOf(BufferDiff.Item(0, 0, Cell.new("4"))), diff)
    }

    @Test
    fun merge() {
        run {
            val one = Rect.new(0, 0, 2, 2)
            val two = Rect.new(0, 2, 2, 2)
            val buffer = Buffer.filled(one, Cell.new("1"))
            val other = Buffer.filled(two, Cell.new("2"))
            buffer.merge(other)
            assertEquals(Buffer.withLines("11", "11", "22", "22"), buffer)
        }

        run {
            val one = Rect.new(2, 2, 2, 2)
            val two = Rect.new(0, 0, 2, 2)
            val buffer = Buffer.filled(one, Cell.new("1"))
            val other = Buffer.filled(two, Cell.new("2"))
            buffer.merge(other)
            assertEquals(Buffer.withLines("22  ", "22  ", "  11", "  11"), buffer)
        }
    }

    @Test
    fun mergeWithOffset() {
        val one = Buffer.filled(Rect.new(3, 3, 2, 2), Cell.new("1"))
        val two = Buffer.filled(Rect.new(1, 1, 3, 4), Cell.new("2"))
        one.merge(two)

        val expected = Buffer.withLines("222 ", "222 ", "2221", "2221")
        expected.area = Rect.new(1, 1, 4, 4)
        assertEquals(expected, one)
    }

    @Test
    fun mergeSkip() {
        fun run(skipOne: CellDiffOption, skipTwo: CellDiffOption, expected: List<CellDiffOption>) {
            val one = run {
                val area = Rect.new(0, 0, 2, 2)
                val cell = Cell.new("1").setDiffOption(skipOne)
                Buffer.filled(area, cell)
            }
            val two = run {
                val area = Rect.new(0, 1, 2, 2)
                val cell = Cell.new("2").setDiffOption(skipTwo)
                Buffer.filled(area, cell)
            }

            one.merge(two)
            val skipped = one.content().map { it.diffOption }
            assertEquals(expected, skipped)
        }

        run(
            skipOne = CellDiffOption.None,
            skipTwo = CellDiffOption.Skip,
            expected = listOf(
                CellDiffOption.None,
                CellDiffOption.None,
                CellDiffOption.Skip,
                CellDiffOption.Skip,
                CellDiffOption.Skip,
                CellDiffOption.Skip,
            )
        )

        run(
            skipOne = CellDiffOption.Skip,
            skipTwo = CellDiffOption.None,
            expected = listOf(
                CellDiffOption.Skip,
                CellDiffOption.Skip,
                CellDiffOption.None,
                CellDiffOption.None,
                CellDiffOption.None,
                CellDiffOption.None,
            )
        )
    }

    @Test
    fun withLinesAcceptsIntoLines() {
        val buffer = Buffer.empty(Rect.new(0, 0, 3, 2))
        buffer.setString(0, 0, "foo", Style.new().red())
        buffer.setString(0, 1, "bar", Style.new().blue())

        val expected = Buffer.withLines(
            listOf(
                Line.from("foo".red()),
                Line.from("bar".blue()),
            )
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun controlSequenceRenderedFull() {
        val text = "I \u001b[0;36mwas\u001b[0m here!"

        val buffer = Buffer.filled(Rect.new(0, 0, 25, 3), Cell.new("x"))
        buffer.setString(1, 1, text, Style.new())

        val expected = Buffer.withLines(
            "xxxxxxxxxxxxxxxxxxxxxxxxx",
            "xI [0;36mwas[0m here!xxxx",
            "xxxxxxxxxxxxxxxxxxxxxxxxx",
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun controlSequenceRenderedPartially() {
        val text = "I \u001b[0;36mwas\u001b[0m here!"

        val buffer = Buffer.filled(Rect.new(0, 0, 11, 3), Cell.new("x"))
        buffer.setString(1, 1, text, Style.new())

        val expected = Buffer.withLines(
            "xxxxxxxxxxx",
            "xI [0;36mwa",
            "xxxxxxxxxxx",
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun rendersEmoji() {
        val cases = listOf(
            "🤷" to "🤷xxxxx",
            "🐻‍❄️" to "🐻‍❄️xxxxx",
            "👁️‍🗨️" to "👁️‍🗨️xxxxx",
            "⌨️" to "⌨️xxxxx",
        )

        for ((input, expectedLine) in cases) {
            val buffer = Buffer.filled(Rect.new(0, 0, 7, 1), Cell.new("x"))
            buffer.setString(0, 0, input, Style.new())

            val expected = Buffer.withLines(expectedLine)
            assertEquals(expected, buffer)
        }
    }

    @Test
    fun indexPosOfU16Max() {
        val buffer = Buffer.empty(Rect.new(0, 0, 256, 257))
        assertEquals(65535, buffer.indexOf(255, 255))
        assertEquals(Pair(255, 255), buffer.posOf(65535))

        assertEquals(65536, buffer.indexOf(0, 256))
        assertEquals(Pair(0, 256), buffer.posOf(65536))

        assertEquals(65537, buffer.indexOf(1, 256))
        assertEquals(Pair(1, 256), buffer.posOf(65537))

        assertEquals(65791, buffer.indexOf(255, 256))
        assertEquals(Pair(255, 256), buffer.posOf(65791))
    }

    @Test
    fun diffClearsTrailingCellForWideGrapheme() {
        val prev = Buffer.withLines("ab")
        assertEquals(2, prev.area.width)

        val next = Buffer.withLines("  ")
        next.setString(0, 0, "⌨️", Style.new())

        val expectedNext = Buffer.withLines("⌨️")
        assertEquals(expectedNext, next)

        val diff = prev.diff(next)
        check(diff.any { (x, y, c) -> x == 0 && y == 0 && c.symbol() == "⌨️" })
        check(diff.any { (x, y, c) -> x == 1 && y == 0 && c.symbol() == " " })
    }

    @Test
    fun diffIgnoresStyleOnlyChangesInTrailingCells() {
        val prev = Buffer.empty(Rect.new(0, 0, 3, 1))
        prev.content[0].setSymbol(" ").setFg(Color.LightBlue)
        prev.content[1].setSymbol(" ").setFg(Color.LightBlue)
        prev.content[2].setSymbol("x")

        val next = Buffer.empty(Rect.new(0, 0, 3, 1))
        next.content[0].setSymbol("⚠️").setFg(Color.Reset)
        next.content[1].setSymbol(" ").setFg(Color.Reset)
        next.content[2].setSymbol("x")

        val diff = prev.diff(next)

        check(diff.any { (x, y, _c) -> x == 0 && y == 0 }) {
            "Diff should include first cell (0,0) because the symbol changed"
        }

        check(!diff.any { (x, y, _c) -> x == 1 && y == 0 }) {
            "Diff should not include trailing cell (1,0) when only style changed. Found updates: " +
                diff.map { (x, y, c) -> Triple(x, y, "${c.symbol()} ${c.fg}") }
        }
    }

    private fun codePointString(codePoint: Int): String {
        return if (codePoint <= 0xFFFF) {
            codePoint.toChar().toString()
        } else {
            val v = codePoint - 0x10000
            val high = (v ushr 10) + 0xD800
            val low = (v and 0x3FF) + 0xDC00
            charArrayOf(high.toChar(), low.toChar()).concatToString()
        }
    }
}
