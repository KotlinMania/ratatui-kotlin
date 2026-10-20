package ratatui_crossterm

import ratatui.layout.Position
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class CrosstermBackendTest {
    @Test fun cursorCommandsUsePublishedCrossterm() {
        val output = StringBuilder()
        val backend = CrosstermBackend(output)
        backend.hideCursor()
        backend.showCursor()
        backend.setCursorPosition(Position(2, 3))
        assertEquals("\u001b[?25l\u001b[?25h\u001b[4;3H", output.toString())
    }
    @Test fun appendLinesWritesAndFlushes() {
        val output = StringBuilder()
        var flushes = 0
        val backend = CrosstermBackend(output) { flushes++ }
        backend.appendLines(2u)
        assertEquals("\n\n", output.toString())
        assertEquals(1, flushes)
    }
    @Test fun flushFailurePropagatesWithoutAnotherBackend() {
        val failure = IllegalStateException("writer failed")
        val backend = CrosstermBackend(StringBuilder()) { throw failure }
        assertSame(failure, assertFailsWith<IllegalStateException> { backend.flush() })
    }
}
