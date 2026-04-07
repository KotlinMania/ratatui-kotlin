// port-lint: source ratatui/tests/terminal.rs
package ratatui.terminal

import ratatui.backend.TestBackend
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.text.toLine
import ratatui.widgets.block.Block
import ratatui.widgets.paragraph.Paragraph
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration tests for [Terminal].
 *
 * Transliteration of `ratatui/tests/terminal.rs`.
 */
class TerminalTest {
    @Test
    fun swapBufferClearsPrevBuffer() {
        val backend = TestBackend.new(100, 50)
        val terminal = Terminal.new(backend)

        terminal.currentBuffer().setString(0, 0, "Hello", Style.reset())
        assertEquals("H", terminal.currentBuffer().content[0].symbol())

        terminal.swapBuffers()

        assertEquals(" ", terminal.currentBuffer().content[0].symbol())
    }

    @Test
    fun terminalDrawReturnsTheCompletedFrame() {
        val backend = TestBackend.new(10, 10)
        val terminal = Terminal.new(backend)

        val frame = terminal.draw { f ->
            val paragraph = Paragraph.new("Test")
            f.renderWidget(paragraph, f.area())
        }
        assertEquals("T", frame.buffer[0, 0].symbol())
        assertEquals(Rect.new(0, 0, 10, 10), frame.area)

        terminal.backend().resize(8, 8)
        val frame2 = terminal.draw { f ->
            val paragraph = Paragraph.new("test")
            f.renderWidget(paragraph, f.area())
        }
        assertEquals("t", frame2.buffer[0, 0].symbol())
        assertEquals(Rect.new(0, 0, 8, 8), frame2.area)
    }

    @Test
    fun terminalDrawIncrementsFrameCount() {
        val backend = TestBackend.new(10, 10)
        val terminal = Terminal.new(backend)

        val frame0 = terminal.draw { f ->
            assertEquals(0, f.count())
            val paragraph = Paragraph.new("Test")
            f.renderWidget(paragraph, f.area())
        }
        assertEquals(0, frame0.count)

        val frame1 = terminal.draw { f ->
            assertEquals(1, f.count())
            val paragraph = Paragraph.new("test")
            f.renderWidget(paragraph, f.area())
        }
        assertEquals(1, frame1.count)

        val frame2 = terminal.draw { f ->
            assertEquals(2, f.count())
            val paragraph = Paragraph.new("test")
            f.renderWidget(paragraph, f.area())
        }
        assertEquals(2, frame2.count)
    }

    @Test
    fun terminalInsertBeforeMovesViewport() {
        // When we have a terminal with 5 lines, and a single line viewport, if we insert a
        // number of lines less than the `terminal height - viewport height` it should move
        // viewport down to accommodate the new lines.

        val backend = TestBackend.new(20, 5)
        val terminal = Terminal.withOptions(
            backend,
            TerminalOptions(
                viewport = Viewport.Inline(1.toUShort())
            )
        )

        // insert_before cannot guarantee the contents of the viewport remain unharmed
        // by potential scrolling as such it is necessary to call draw afterwards to
        // redraw the contents of the viewport over the newly designated area.
        terminal.insertBefore(2.toUShort()) { buf ->
            Paragraph.new(
                listOf(
                    "------ Line 1 ------".toLine(),
                    "------ Line 2 ------".toLine()
                )
            ).render(buf.area, buf)
        }

        terminal.draw { f ->
            val paragraph = Paragraph.new("[---- Viewport ----]")
            f.renderWidget(paragraph, f.area())
        }

        terminal.backend().assertBufferLines(
            "------ Line 1 ------",
            "------ Line 2 ------",
            "[---- Viewport ----]",
            "                    ",
            "                    "
        )
        terminal.backend().assertScrollbackEmpty()
    }

    @Test
    fun terminalInsertBeforeScrollsOnLargeInput() {
        // When we have a terminal with 5 lines, and a single line viewport, if we insert many
        // lines before the viewport (greater than `terminal height - viewport height`) it should
        // move the viewport down to the bottom of the terminal and scroll all lines above the viewport
        // until all have been added to the buffer.

        val backend = TestBackend.new(20, 5)
        val terminal = Terminal.withOptions(
            backend,
            TerminalOptions(
                viewport = Viewport.Inline(1.toUShort())
            )
        )

        terminal.insertBefore(5.toUShort()) { buf ->
            Paragraph.new(
                listOf(
                    "------ Line 1 ------".toLine(),
                    "------ Line 2 ------".toLine(),
                    "------ Line 3 ------".toLine(),
                    "------ Line 4 ------".toLine(),
                    "------ Line 5 ------".toLine()
                )
            ).render(buf.area, buf)
        }

        terminal.draw { f ->
            val paragraph = Paragraph.new("[---- Viewport ----]")
            f.renderWidget(paragraph, f.area())
        }

        terminal.backend().assertBufferLines(
            "------ Line 2 ------",
            "------ Line 3 ------",
            "------ Line 4 ------",
            "------ Line 5 ------",
            "[---- Viewport ----]"
        )
        terminal.backend().assertScrollbackLines("------ Line 1 ------")
    }

    @Test
    fun terminalInsertBeforeScrollsOnManyInserts() {
        // This test ensures similar behaviour to `terminal_insert_before_scrolls_on_large_input`
        // but covers a bug previously present whereby multiple small insertions
        // (less than `terminal height - viewport height`) would have disparate behaviour to one large
        // insertion. This was caused by an undocumented cap on the height to be inserted, which has now
        // been removed.

        val backend = TestBackend.new(20, 5)
        val terminal = Terminal.withOptions(
            backend,
            TerminalOptions(
                viewport = Viewport.Inline(1.toUShort())
            )
        )

        terminal.insertBefore(1.toUShort()) { buf ->
            Paragraph.new(listOf("------ Line 1 ------".toLine())).render(buf.area, buf)
        }

        terminal.insertBefore(1.toUShort()) { buf ->
            Paragraph.new(listOf("------ Line 2 ------".toLine())).render(buf.area, buf)
        }

        terminal.insertBefore(1.toUShort()) { buf ->
            Paragraph.new(listOf("------ Line 3 ------".toLine())).render(buf.area, buf)
        }

        terminal.insertBefore(1.toUShort()) { buf ->
            Paragraph.new(listOf("------ Line 4 ------".toLine())).render(buf.area, buf)
        }

        terminal.insertBefore(1.toUShort()) { buf ->
            Paragraph.new(listOf("------ Line 5 ------".toLine())).render(buf.area, buf)
        }

        terminal.draw { f ->
            val paragraph = Paragraph.new("[---- Viewport ----]")
            f.renderWidget(paragraph, f.area())
        }

        terminal.backend().assertBufferLines(
            "------ Line 2 ------",
            "------ Line 3 ------",
            "------ Line 4 ------",
            "------ Line 5 ------",
            "[---- Viewport ----]"
        )
        terminal.backend().assertScrollbackLines("------ Line 1 ------")
    }

    @Test
    fun terminalInsertBeforeLargeViewport() {
        // This test covers a bug previously present whereby doing an insert_before when the
        // viewport covered the entire screen would cause a panic.

        val backend = TestBackend.new(20, 3)
        val terminal = Terminal.withOptions(
            backend,
            TerminalOptions(
                viewport = Viewport.Inline(3.toUShort())
            )
        )

        terminal.insertBefore(1.toUShort()) { buf ->
            Paragraph.new(listOf("------ Line 1 ------".toLine())).render(buf.area, buf)
        }

        terminal.insertBefore(3.toUShort()) { buf ->
            Paragraph.new(
                listOf(
                    "------ Line 2 ------".toLine(),
                    "------ Line 3 ------".toLine(),
                    "------ Line 4 ------".toLine()
                )
            ).render(buf.area, buf)
        }

        terminal.insertBefore(7.toUShort()) { buf ->
            Paragraph.new(
                listOf(
                    "------ Line 5 ------".toLine(),
                    "------ Line 6 ------".toLine(),
                    "------ Line 7 ------".toLine(),
                    "------ Line 8 ------".toLine(),
                    "------ Line 9 ------".toLine(),
                    "----- Line 10 ------".toLine(),
                    "----- Line 11 ------".toLine()
                )
            ).render(buf.area, buf)
        }

        terminal.draw { f ->
            val paragraph = Paragraph.new("Viewport")
                .centered()
                .block(Block.bordered())
            f.renderWidget(paragraph, f.area())
        }

        terminal.backend().assertBufferLines(
            "┌──────────────────┐",
            "│     Viewport     │",
            "└──────────────────┘"
        )
        terminal.backend().assertScrollbackLines(
            "------ Line 1 ------",
            "------ Line 2 ------",
            "------ Line 3 ------",
            "------ Line 4 ------",
            "------ Line 5 ------",
            "------ Line 6 ------",
            "------ Line 7 ------",
            "------ Line 8 ------",
            "------ Line 9 ------",
            "----- Line 10 ------",
            "----- Line 11 ------"
        )
    }

    // -------------------------------------------------------------------------
    // "scrolling-regions" feature gated tests (Rust) — ignored in Kotlin for now.
    // -------------------------------------------------------------------------

    @Ignore
    @Test
    fun terminalInsertBeforeMovesViewportDoesNotClobber() {
    }

    @Ignore
    @Test
    fun terminalInsertBeforeScrollsOnLargeInputDoesNotClobber() {
    }

    @Ignore
    @Test
    fun terminalInsertBeforeScrollsOnManyInsertsDoesNotClobber() {
    }

    @Ignore
    @Test
    fun terminalInsertBeforeLargeViewportDoesNotClobber() {
    }
}
