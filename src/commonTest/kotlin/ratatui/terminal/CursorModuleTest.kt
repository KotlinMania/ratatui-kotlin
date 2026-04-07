package ratatui.terminal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ratatui.backend.TestBackend
import ratatui.layout.Position

class CursorModuleTest {
    @Test
    fun hideCursorUpdatesBackendState() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.showCursor()
        assertTrue(terminal.backend().cursorVisible())

        terminal.hideCursor()
        assertFalse(terminal.backend().cursorVisible())
    }

    @Test
    fun showCursorUpdatesBackendState() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.hideCursor()
        assertFalse(terminal.backend().cursorVisible())

        terminal.showCursor()
        assertTrue(terminal.backend().cursorVisible())
    }

    @Test
    fun setCursorPositionUpdatesBackend() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.setCursorPosition(Position(3, 4))

        assertEquals(Position(3, 4), terminal.getCursorPosition())
        assertEquals(Position(3, 4), terminal.backend().cursorPosition())
    }

    @Test
    @Suppress("DEPRECATION")
    fun deprecatedCursorWrappersDelegateToPositionApis() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.setCursor(4.toUShort(), 1.toUShort())

        assertEquals(Pair(4.toUShort(), 1.toUShort()), terminal.getCursor())
        assertEquals(Position(4, 1), terminal.getCursorPosition())
        assertEquals(Position(4, 1), terminal.backend().cursorPosition())
    }
}

