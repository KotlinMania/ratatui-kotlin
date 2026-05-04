// port-lint: source ratatui-core/src/backend/test.rs (tests)
package ratatui.backend

import ratatui.buffer.Buffer
import ratatui.buffer.BufferDiff
import ratatui.buffer.Cell
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.layout.Size
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestBackendTest {

    @Test
    fun testNew() {
        val backend = TestBackend.new(10, 2)
        assertEquals(Buffer.withLines("          ", "          "), backend.buffer())
        assertEquals(Buffer.empty(Rect.new(0, 0, 10, 0)), backend.scrollback())
        assertFalse(backend.cursorVisible())
        assertEquals(Position.ORIGIN, backend.cursorPosition())
    }

    @Test
    fun testBufferView() {
        val buffer = Buffer.withLines("aaaa", "aaaa")
        assertEquals("\"aaaa\"\n\"aaaa\"\n", bufferView(buffer))
    }

    @Test
    fun testBuffer() {
        val backend = TestBackend.new(10, 2)
        backend.assertBufferLines("          ", "          ")
    }

    @Test
    fun testResize() {
        val backend = TestBackend.new(10, 2)
        backend.resize(5, 5)
        backend.assertBufferLines("     ", "     ", "     ", "     ", "     ")
    }

    @Test
    fun testAssertBuffer() {
        val backend = TestBackend.new(10, 2)
        backend.assertBufferLines("          ", "          ")
    }

    @Test
    fun testAssertBufferPanics() {
        val backend = TestBackend.new(10, 2)
        assertFailsWith<IllegalStateException> {
            backend.assertBufferLines("aaaaaaaaaa", "aaaaaaaaaa")
        }
    }

    @Test
    fun testAssertScrollbackPanics() {
        val backend = TestBackend.new(10, 2)
        assertFailsWith<IllegalStateException> {
            backend.assertScrollbackLines("aaaaaaaaaa", "aaaaaaaaaa")
        }
    }

    @Test
    fun testDisplay() {
        val backend = TestBackend.new(10, 2)
        assertEquals("\"          \"\n\"          \"\n", backend.toString())
    }

    @Test
    fun testDraw() {
        val backend = TestBackend.new(10, 2)
        val cell = Cell.new("a")
        backend.draw(listOf(BufferDiff.Item(0, 0, cell)).iterator())
        backend.draw(listOf(BufferDiff.Item(0, 1, cell)).iterator())
        backend.assertBufferLines("a         ", "a         ")
    }

    @Test
    fun testHideCursor() {
        val backend = TestBackend.new(10, 2)
        backend.hideCursor()
        assertFalse(backend.cursorVisible())
    }

    @Test
    fun testShowCursor() {
        val backend = TestBackend.new(10, 2)
        backend.showCursor()
        assertTrue(backend.cursorVisible())
    }

    @Test
    fun testGetCursorPosition() {
        val backend = TestBackend.new(10, 2)
        assertEquals(Position.ORIGIN, backend.getCursorPosition())
    }

    @Test
    fun testAssertCursorPosition() {
        val backend = TestBackend.new(10, 2)
        backend.assertCursorPosition(Position.ORIGIN)
    }

    @Test
    fun testSetCursorPosition() {
        val backend = TestBackend.new(10, 10)
        backend.setCursorPosition(Position(x = 5, y = 5))
        assertEquals(Position(x = 5, y = 5), backend.cursorPosition())
    }

    @Test
    fun testClear() {
        val backend = TestBackend.new(4, 2)
        val cell = Cell.new("a")
        backend.draw(listOf(BufferDiff.Item(0, 0, cell)).iterator())
        backend.draw(listOf(BufferDiff.Item(0, 1, cell)).iterator())
        backend.clear()
        backend.assertBufferLines("    ", "    ")
    }

    @Test
    fun testClearRegionAll() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )

        backend.clearRegion(ClearType.All)
        backend.assertBufferLines(
            "          ",
            "          ",
            "          ",
            "          ",
            "          "
        )
    }

    @Test
    fun testClearRegionAfterCursor() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )

        backend.setCursorPosition(Position(x = 3, y = 2))
        backend.clearRegion(ClearType.AfterCursor)
        backend.assertBufferLines(
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaa       ",
            "          ",
            "          "
        )
    }

    @Test
    fun testClearRegionBeforeCursor() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )

        backend.setCursorPosition(Position(x = 5, y = 3))
        backend.clearRegion(ClearType.BeforeCursor)
        backend.assertBufferLines(
            "          ",
            "          ",
            "          ",
            "      aaaa",
            "aaaaaaaaaa"
        )
    }

    @Test
    fun testClearRegionCurrentLine() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )

        backend.setCursorPosition(Position(x = 3, y = 1))
        backend.clearRegion(ClearType.CurrentLine)
        backend.assertBufferLines(
            "aaaaaaaaaa",
            "          ",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )
    }

    @Test
    fun testClearRegionUntilNewLine() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )

        backend.setCursorPosition(Position(x = 3, y = 0))
        backend.clearRegion(ClearType.UntilNewLine)
        backend.assertBufferLines(
            "aaa       ",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa",
            "aaaaaaaaaa"
        )
    }

    @Test
    fun testAppendLinesNotAtLastLine() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        backend.setCursorPosition(Position.ORIGIN)

        // If the cursor is not at the last line in the terminal the addition of a
        // newline simply moves the cursor down and to the right
        backend.appendLines(1u)
        backend.assertCursorPosition(Position(x = 1, y = 1))

        backend.appendLines(1u)
        backend.assertCursorPosition(Position(x = 2, y = 2))

        backend.appendLines(1u)
        backend.assertCursorPosition(Position(x = 3, y = 3))

        backend.appendLines(1u)
        backend.assertCursorPosition(Position(x = 4, y = 4))

        // As such the buffer should remain unchanged
        backend.assertBufferLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )
        backend.assertScrollbackEmpty()
    }

    @Test
    fun testAppendLinesAtLastLine() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        // If the cursor is at the last line in the terminal the addition of a
        // newline will scroll the contents of the buffer
        backend.setCursorPosition(Position(x = 0, y = 4))

        backend.appendLines(1u)

        backend.assertBufferLines(
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee",
            "          "
        )
        backend.assertScrollbackLines("aaaaaaaaaa")

        // It also moves the cursor to the right, as is common of the behaviour of
        // terminals in raw-mode
        backend.assertCursorPosition(Position(x = 1, y = 4))
    }

    @Test
    fun testAppendMultipleLinesNotAtLastLine() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        backend.setCursorPosition(Position.ORIGIN)

        // If the cursor is not at the last line in the terminal the addition of multiple
        // newlines simply moves the cursor n lines down and to the right by 1
        backend.appendLines(4u)
        backend.assertCursorPosition(Position(x = 1, y = 4))

        // As such the buffer should remain unchanged
        backend.assertBufferLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )
        backend.assertScrollbackEmpty()
    }

    @Test
    fun testAppendMultipleLinesPastLastLine() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        backend.setCursorPosition(Position(x = 0, y = 3))

        backend.appendLines(3u)
        backend.assertCursorPosition(Position(x = 1, y = 4))

        backend.assertBufferLines(
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee",
            "          ",
            "          "
        )
        backend.assertScrollbackLines("aaaaaaaaaa", "bbbbbbbbbb")
    }

    @Test
    fun testAppendMultipleLinesWhereCursorAtEndAppendsHeightLines() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        backend.setCursorPosition(Position(x = 0, y = 4))

        backend.appendLines(5u)
        backend.assertCursorPosition(Position(x = 1, y = 4))

        backend.assertBufferLines(
            "          ",
            "          ",
            "          ",
            "          ",
            "          "
        )
        backend.assertScrollbackLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )
    }

    @Test
    fun testAppendMultipleLinesWhereCursorAppendsHeightLines() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        backend.setCursorPosition(Position.ORIGIN)

        backend.appendLines(5u)
        backend.assertCursorPosition(Position(x = 1, y = 4))

        backend.assertBufferLines(
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee",
            "          "
        )
        backend.assertScrollbackLines("aaaaaaaaaa")
    }

    @Test
    fun testAppendMultipleLinesWhereCursorAtEndAppendsMoreThanHeightLines() {
        val backend = TestBackend.withLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee"
        )

        backend.setCursorPosition(Position(x = 0, y = 4))

        backend.appendLines(8u)
        backend.assertCursorPosition(Position(x = 1, y = 4))

        backend.assertBufferLines(
            "          ",
            "          ",
            "          ",
            "          ",
            "          "
        )
        backend.assertScrollbackLines(
            "aaaaaaaaaa",
            "bbbbbbbbbb",
            "cccccccccc",
            "dddddddddd",
            "eeeeeeeeee",
            "          ",
            "          ",
            "          "
        )
    }

    @Test
    fun testSize() {
        val backend = TestBackend.new(10, 2)
        assertEquals(Size(width = 10, height = 2), backend.size())
    }

    @Test
    fun testFlush() {
        val backend = TestBackend.new(10, 2)
        backend.flush()
    }

    // scrolling-regions feature tests

    companion object {
        private const val A = "aaaa"
        private const val B = "bbbb"
        private const val C = "cccc"
        private const val D = "dddd"
        private const val E = "eeee"
        private const val S = "    "
    }

    @Test
    fun testScrollRegionUp_0to5_by0() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionUp(0..4, 0u)
        backend.assertScrollbackEmpty()
        backend.assertBufferLines(A, B, C, D, E)
    }

    @Test
    fun testScrollRegionUp_0to5_by2() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionUp(0..4, 2u)
        backend.assertScrollbackLines(A, B)
        backend.assertBufferLines(C, D, E, S, S)
    }

    @Test
    fun testScrollRegionUp_0to5_by5() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionUp(0..4, 5u)
        backend.assertScrollbackLines(A, B, C, D, E)
        backend.assertBufferLines(S, S, S, S, S)
    }

    @Test
    fun testScrollRegionUp_0to5_by7() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionUp(0..4, 7u)
        backend.assertScrollbackLines(A, B, C, D, E, S, S)
        backend.assertBufferLines(S, S, S, S, S)
    }

    @Test
    fun testScrollRegionUp_1to4_by2() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionUp(1..3, 2u)
        backend.assertScrollbackEmpty()
        backend.assertBufferLines(A, D, S, S, E)
    }

    @Test
    fun testScrollRegionDown_0to5_by0() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionDown(0..4, 0u)
        backend.assertScrollbackEmpty()
        backend.assertBufferLines(A, B, C, D, E)
    }

    @Test
    fun testScrollRegionDown_0to5_by2() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionDown(0..4, 2u)
        backend.assertScrollbackEmpty()
        backend.assertBufferLines(S, S, A, B, C)
    }

    @Test
    fun testScrollRegionDown_0to5_by5() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionDown(0..4, 5u)
        backend.assertScrollbackEmpty()
        backend.assertBufferLines(S, S, S, S, S)
    }

    @Test
    fun testScrollRegionDown_1to4_by2() {
        val backend = TestBackend.withLines(A, B, C, D, E)
        backend.scrollRegionDown(1..3, 2u)
        backend.assertScrollbackEmpty()
        backend.assertBufferLines(A, S, S, B, E)
    }
}
