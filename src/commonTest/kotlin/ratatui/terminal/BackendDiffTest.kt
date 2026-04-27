// port-lint: source ratatui/tests/backendTermion.rs
package ratatui.terminal

import ratatui.backend.Backend
import ratatui.backend.ClearType
import ratatui.backend.WindowSize
import ratatui.buffer.BufferDiff
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.layout.Size
import ratatui.widgets.paragraph.Paragraph
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests that successive draws only emit diffs.
 *
 * The upstream Rust test is scoped to the `termion` backend and asserts the exact bytes written to
 * stdout. This Kotlin transliteration focuses on the shared behavior: [Terminal.flush] computes a
 * diff between the previous and current buffers and calls [Backend.draw] with only the changed
 * cells.
 *
 * Transliteration target: `ratatui/tests/backendTermion.rs`.
 */
class BackendDiffTest {
    private class RecordingBackend(
        private val fixedSize: Size
    ) : Backend {
        val draws: MutableList<List<BufferDiff.Item>> = mutableListOf()

        private var cursorVisible: Boolean = true
        private var cursorPos: Position = Position.ORIGIN

        override fun draw(content: Iterator<BufferDiff.Item>) {
            val items = mutableListOf<BufferDiff.Item>()
            while (content.hasNext()) {
                val item = content.next()
                // BufferDiff.Item holds a reference to the live Cell in the active buffer; clone it
                // so later buffer swaps/resets do not mutate recorded expectations.
                items.add(BufferDiff.Item(item.x, item.y, item.cell.clone()))
            }
            draws.add(items)
        }

        override fun hideCursor() {
            cursorVisible = false
        }

        override fun showCursor() {
            cursorVisible = true
        }

        override fun getCursorPosition(): Position = cursorPos

        override fun setCursorPosition(position: Position) {
            cursorPos = position
        }

        override fun clear() = Unit

        override fun clearRegion(clearType: ClearType) {
            @Suppress("UNUSED_EXPRESSION")
            clearType
        }

        override fun size(): Size = fixedSize

        override fun windowSize(): WindowSize =
            WindowSize(columnsRows = fixedSize, pixels = Size(width = 0, height = 0))

        override fun flush() = Unit

        @Suppress("UNUSED")
        fun isCursorVisible(): Boolean = cursorVisible
    }

    @Test
    fun backendShouldOnlyDrawDiffs() {
        val area = Rect.new(0, 0, 3, 1)
        val backend = RecordingBackend(area.asSize())
        val terminal = Terminal.withOptions(
            backend,
            TerminalOptions(viewport = Viewport.Fixed(area))
        )

        terminal.draw { f ->
            f.renderWidget(Paragraph.new("a"), area)
        }.also { frame1 ->
            // CompletedFrame is only valid until the next draw; assert immediately.
            assertEquals("a", frame1.buffer[0, 0].symbol())
            assertEquals(" ", frame1.buffer[1, 0].symbol())
            assertEquals(" ", frame1.buffer[2, 0].symbol())
        }

        terminal.draw { f ->
            f.renderWidget(Paragraph.new("ab"), area)
        }.also { frame2 ->
            assertEquals("a", frame2.buffer[0, 0].symbol())
            assertEquals("b", frame2.buffer[1, 0].symbol())
            assertEquals(" ", frame2.buffer[2, 0].symbol())
        }

        terminal.draw { f ->
            f.renderWidget(Paragraph.new("abc"), area)
        }.also { frame3 ->
            assertEquals("a", frame3.buffer[0, 0].symbol())
            assertEquals("b", frame3.buffer[1, 0].symbol())
            assertEquals("c", frame3.buffer[2, 0].symbol())
        }

        assertEquals(3, backend.draws.size)

        val first = backend.draws[0]
        assertEquals(1, first.size)
        assertEquals(0, first[0].x)
        assertEquals(0, first[0].y)
        assertEquals("a", first[0].cell.symbol())

        val second = backend.draws[1]
        assertEquals(1, second.size)
        assertEquals(1, second[0].x)
        assertEquals(0, second[0].y)
        assertEquals("b", second[0].cell.symbol())

        val third = backend.draws[2]
        assertEquals(1, third.size)
        assertEquals(2, third[0].x)
        assertEquals(0, third[0].y)
        assertEquals("c", third[0].cell.symbol())
    }
}
