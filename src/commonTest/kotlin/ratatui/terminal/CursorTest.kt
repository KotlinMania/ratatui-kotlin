// port-lint: source ratatui-core/src/terminal/cursor.rs
package ratatui.terminal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ratatui.backend.TestBackend
import ratatui.layout.Position

class CursorTest {
    @Test
    fun hideCursorUpdatesTerminalState() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.hideCursor()

        assertTrue(terminal.hiddenCursor())
        assertFalse(terminal.backend().cursorVisible())
    }

    @Test
    fun showCursorUpdatesTerminalState() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.hideCursor()
        terminal.showCursor()

        assertFalse(terminal.hiddenCursor())
        assertTrue(terminal.backend().cursorVisible())
    }

    @Test
    fun setCursorPositionUpdatesBackendAndTracking() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.setCursorPosition(Position(3, 4))

        assertEquals(Position(3, 4), terminal.lastKnownCursorPos())
        assertEquals(Position(3, 4), terminal.backend().cursorPosition())
    }

    @Test
    fun getCursorPositionQueriesBackend() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.backend().setCursorPosition(Position(7, 2))

        assertEquals(Position(7, 2), terminal.getCursorPosition())
    }

    @Test
    @Suppress("DEPRECATION")
    fun deprecatedCursorWrappersDelegateToPositionApis() {
        val backend = TestBackend.new(10, 5)
        val terminal = Terminal.new(backend)

        terminal.setCursor(4.toUShort(), 1.toUShort())

        assertEquals(Pair(4.toUShort(), 1.toUShort()), terminal.getCursor())
        assertEquals(Position(4, 1), terminal.lastKnownCursorPos())
        assertEquals(Position(4, 1), terminal.backend().cursorPosition())
    }
}
