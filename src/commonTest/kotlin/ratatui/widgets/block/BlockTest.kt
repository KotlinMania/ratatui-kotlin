// port-lint: source ratatui/tests/widgets_block.rs
package ratatui.widgets.block

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.HorizontalAlignment
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.text.Line
import ratatui.text.Span
import ratatui.terminal.Terminal
import ratatui.widgets.Borders

class BlockTest {
    @Test
    fun widgetsBlockRenders() {
        val backend = TestBackend.new(10, 10)
        val terminal = Terminal(backend)
        val block = Block.bordered().title(Line.from(Span.styled("Title", Style.default().fg(Color.LightBlue))))

        terminal.draw { frame ->
            frame.renderWidget(block, Rect.new(0, 0, 8, 8))
        }

        val expected = Buffer.withLines(
            "┌Title─┐  ",
            "│      │  ",
            "│      │  ",
            "│      │  ",
            "│      │  ",
            "│      │  ",
            "│      │  ",
            "└──────┘  ",
            "          ",
            "          ",
        )
        for (x in 1..5) {
            expected[x, 0].setFg(Color.LightBlue)
        }

        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsBlockTitlesOverlap() {
        fun testCase(block: Block, area: Rect, expected: List<String>) {
            val backend = TestBackend.new(area.width, area.height)
            val terminal = Terminal(backend)
            terminal.draw { frame ->
                frame.renderWidget(block, area)
            }
            terminal.backend().assertBufferLines(expected)
        }

        // Center overrides left titles.
        testCase(
            Block.new()
                .title(Line.from("aaaaa").leftAligned())
                .title(Line.from("bbb").centered())
                .title(Line.from("ccc").rightAligned()),
            Rect.new(0, 0, 10, 1),
            listOf("aaabbb ccc"),
        )

        // Right overrides center overrides left.
        testCase(
            Block.new()
                .title(Line.from("aaaaa").leftAligned())
                .title(Line.from("bbbbb").centered())
                .title(Line.from("ccccc").rightAligned()),
            Rect.new(0, 0, 11, 1),
            listOf("aaabbbccccc"),
        )

        // Multiple left titles overwritten by center, right overwrites center.
        testCase(
            Block.new()
                .title(Line.from("aaaaa").leftAligned())
                .title(Line.from("aaaaa").leftAligned())
                .title(Line.from("bbbbb").centered())
                .title(Line.from("ccccc").rightAligned()),
            Rect.new(0, 0, 11, 1),
            listOf("aaabbbccccc"),
        )

        // Right overrides center.
        testCase(
            Block.new()
                .title(Line.from("bbbbb").centered())
                .title(Line.from("ccccccccccc").rightAligned()),
            Rect.new(0, 0, 11, 1),
            listOf("ccccccccccc"),
        )
    }

    @Test
    fun widgetsBlockRendersOnSmallAreas() {
        fun testCase(block: Block, area: Rect, expected: Buffer) {
            val backend = TestBackend.new(area.width, area.height)
            val terminal = Terminal(backend)
            terminal.draw { frame ->
                frame.renderWidget(block, area)
            }
            terminal.backend().assertBuffer(expected)
        }

        val oneCellTestCases = listOf(
            Borders.NONE to "T",
            Borders.LEFT to "│",
            Borders.TOP to "T",
            Borders.RIGHT to "│",
            Borders.BOTTOM to "T",
            Borders.ALL to "┌",
        )
        for ((borders, symbol) in oneCellTestCases) {
            testCase(
                Block.new().borders(borders).title("Test"),
                Rect.new(0, 0, 0, 0),
                Buffer.empty(Rect.new(0, 0, 0, 0)),
            )
            testCase(
                Block.new().borders(borders).title("Test"),
                Rect.new(0, 0, 1, 0),
                Buffer.empty(Rect.new(0, 0, 1, 0)),
            )
            testCase(
                Block.new().borders(borders).title("Test"),
                Rect.new(0, 0, 0, 1),
                Buffer.empty(Rect.new(0, 0, 0, 1)),
            )
            testCase(
                Block.new().borders(borders).title("Test"),
                Rect.new(0, 0, 1, 1),
                Buffer.withLines(symbol),
            )
        }

        testCase(
            Block.new().borders(Borders.LEFT).title("Test"),
            Rect.new(0, 0, 4, 1),
            Buffer.withLines("│Tes"),
        )
        testCase(
            Block.new().borders(Borders.RIGHT).title("Test"),
            Rect.new(0, 0, 4, 1),
            Buffer.withLines("Tes│"),
        )
        testCase(
            Block.new().borders(Borders.RIGHT).title("Test"),
            Rect.new(0, 0, 4, 1),
            Buffer.withLines("Tes│"),
        )
        testCase(
            Block.new().borders(Borders.LEFT or Borders.RIGHT).title("Test"),
            Rect.new(0, 0, 4, 1),
            Buffer.withLines("│Te│"),
        )
        testCase(
            Block.new().borders(Borders.TOP).title("Test"),
            Rect.new(0, 0, 4, 1),
            Buffer.withLines("Test"),
        )
        testCase(
            Block.new().borders(Borders.TOP).title("Test"),
            Rect.new(0, 0, 5, 1),
            Buffer.withLines("Test─"),
        )
        testCase(
            Block.new().borders(Borders.LEFT or Borders.TOP).title("Test"),
            Rect.new(0, 0, 5, 1),
            Buffer.withLines("┌Test"),
        )
        testCase(
            Block.new().borders(Borders.LEFT or Borders.TOP).title("Test"),
            Rect.new(0, 0, 6, 1),
            Buffer.withLines("┌Test─"),
        )
    }

    @Test
    fun widgetsBlockTitleAlignmentTop() {
        data class Case(val alignment: HorizontalAlignment, val borders: Borders, val expected: List<String>)

        val cases = listOf(
            Case(HorizontalAlignment.Left, Borders.ALL, listOf(
                " ┌Title──────┐ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(HorizontalAlignment.Left, Borders.LEFT or Borders.BOTTOM or Borders.RIGHT, listOf(
                " │Title      │ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(HorizontalAlignment.Left, Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " Title───────┐ ",
                "             │ ",
                " ────────────┘ ",
            )),
            Case(HorizontalAlignment.Left, Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌Title─────── ",
                " │             ",
                " └──────────── ",
            )),
            Case(HorizontalAlignment.Left, Borders.NONE, listOf(
                " Title         ",
                "               ",
                "               ",
            )),

            Case(HorizontalAlignment.Center, Borders.ALL, listOf(
                " ┌───Title───┐ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(HorizontalAlignment.Center, Borders.LEFT or Borders.BOTTOM or Borders.RIGHT, listOf(
                " │   Title   │ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(HorizontalAlignment.Center, Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ───Title────┐ ",
                "             │ ",
                " ────────────┘ ",
            )),
            Case(HorizontalAlignment.Center, Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌───Title──── ",
                " │             ",
                " └──────────── ",
            )),
            Case(HorizontalAlignment.Center, Borders.NONE, listOf(
                "     Title     ",
                "               ",
                "               ",
            )),

            Case(HorizontalAlignment.Right, Borders.ALL, listOf(
                " ┌──────Title┐ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(HorizontalAlignment.Right, Borders.LEFT or Borders.BOTTOM or Borders.RIGHT, listOf(
                " │      Title│ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(HorizontalAlignment.Right, Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ───────Title┐ ",
                "             │ ",
                " ────────────┘ ",
            )),
            Case(HorizontalAlignment.Right, Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌───────Title ",
                " │             ",
                " └──────────── ",
            )),
            Case(HorizontalAlignment.Right, Borders.NONE, listOf(
                "         Title ",
                "               ",
                "               ",
            )),
        )

        val backend = TestBackend.new(15, 3)
        val terminal = Terminal(backend)
        val area = Rect.new(1, 0, 13, 3)

        for (case in cases) {
            val block1 = Block.new().borders(case.borders).title(Line.from("Title").alignment(case.alignment))
            val block2 = Block.new().borders(case.borders).titleAlignment(case.alignment).title("Title")
            val expected = Buffer.withLines(*case.expected.toTypedArray())

            for (block in listOf(block1, block2)) {
                terminal.draw { frame ->
                    frame.renderWidget(block, area)
                }
                terminal.backend().assertBuffer(expected)
            }
        }
    }

    @Test
    fun widgetsBlockTitleAlignmentBottom() {
        data class Case(val alignment: HorizontalAlignment, val borders: Borders, val expected: List<String>)

        val cases = listOf(
            Case(HorizontalAlignment.Left, Borders.ALL, listOf(
                " ┌───────────┐ ",
                " │           │ ",
                " └Title──────┘ ",
            )),
            Case(HorizontalAlignment.Left, Borders.LEFT or Borders.TOP or Borders.RIGHT, listOf(
                " ┌───────────┐ ",
                " │           │ ",
                " │Title      │ ",
            )),
            Case(HorizontalAlignment.Left, Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ────────────┐ ",
                "             │ ",
                " Title───────┘ ",
            )),
            Case(HorizontalAlignment.Left, Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌──────────── ",
                " │             ",
                " └Title─────── ",
            )),
            Case(HorizontalAlignment.Left, Borders.NONE, listOf(
                "               ",
                "               ",
                " Title         ",
            )),

            Case(HorizontalAlignment.Center, Borders.ALL, listOf(
                " ┌───────────┐ ",
                " │           │ ",
                " └───Title───┘ ",
            )),
            Case(HorizontalAlignment.Center, Borders.LEFT or Borders.TOP or Borders.RIGHT, listOf(
                " ┌───────────┐ ",
                " │           │ ",
                " │   Title   │ ",
            )),
            Case(HorizontalAlignment.Center, Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ────────────┐ ",
                "             │ ",
                " ───Title────┘ ",
            )),
            Case(HorizontalAlignment.Center, Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌──────────── ",
                " │             ",
                " └───Title──── ",
            )),
            Case(HorizontalAlignment.Center, Borders.NONE, listOf(
                "               ",
                "               ",
                "     Title     ",
            )),

            Case(HorizontalAlignment.Right, Borders.ALL, listOf(
                " ┌───────────┐ ",
                " │           │ ",
                " └──────Title┘ ",
            )),
            Case(HorizontalAlignment.Right, Borders.LEFT or Borders.TOP or Borders.RIGHT, listOf(
                " ┌───────────┐ ",
                " │           │ ",
                " │      Title│ ",
            )),
            Case(HorizontalAlignment.Right, Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ────────────┐ ",
                "             │ ",
                " ───────Title┘ ",
            )),
            Case(HorizontalAlignment.Right, Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌──────────── ",
                " │             ",
                " └───────Title ",
            )),
            Case(HorizontalAlignment.Right, Borders.NONE, listOf(
                "               ",
                "               ",
                "         Title ",
            )),
        )

        val backend = TestBackend.new(15, 3)
        val terminal = Terminal(backend)
        val area = Rect.new(1, 0, 13, 3)

        for (case in cases) {
            val title = Line.from("Title").alignment(case.alignment)
            val block = Block.default().titleBottom(title).borders(case.borders)
            terminal.draw { frame ->
                frame.renderWidget(block, area)
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsBlockMultipleTitles() {
        data class Case(val titleA: Line, val titleB: Line, val borders: Borders, val expected: List<String>)

        val cases = listOf(
            Case(Line.from("foo"), Line.from("bar"), Borders.ALL, listOf(
                " ┌foo─bar────┐ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(Line.from("foo"), Line.from("bar"), Borders.LEFT or Borders.BOTTOM or Borders.RIGHT, listOf(
                " │foo bar    │ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(Line.from("foo"), Line.from("bar"), Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " foo─bar─────┐ ",
                "             │ ",
                " ────────────┘ ",
            )),
            Case(Line.from("foo"), Line.from("bar"), Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌foo─bar───── ",
                " │             ",
                " └──────────── ",
            )),
            Case(Line.from("foo"), Line.from("bar"), Borders.NONE, listOf(
                " foo bar       ",
                "               ",
                "               ",
            )),

            Case(Line.from("foo").centered(), Line.from("bar").centered(), Borders.ALL, listOf(
                " ┌──foo─bar──┐ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(Line.from("foo").centered(), Line.from("bar").centered(), Borders.LEFT or Borders.BOTTOM or Borders.RIGHT, listOf(
                " │  foo bar  │ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(Line.from("foo").centered(), Line.from("bar").centered(), Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ──foo─bar───┐ ",
                "             │ ",
                " ────────────┘ ",
            )),
            Case(Line.from("foo").centered(), Line.from("bar").centered(), Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌──foo─bar─── ",
                " │             ",
                " └──────────── ",
            )),
            Case(Line.from("foo").centered(), Line.from("bar").centered(), Borders.NONE, listOf(
                "    foo bar    ",
                "               ",
                "               ",
            )),

            Case(Line.from("foo").rightAligned(), Line.from("bar").rightAligned(), Borders.ALL, listOf(
                " ┌────foo─bar┐ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(Line.from("foo").rightAligned(), Line.from("bar").rightAligned(), Borders.LEFT or Borders.BOTTOM or Borders.RIGHT, listOf(
                " │    foo bar│ ",
                " │           │ ",
                " └───────────┘ ",
            )),
            Case(Line.from("foo").rightAligned(), Line.from("bar").rightAligned(), Borders.TOP or Borders.RIGHT or Borders.BOTTOM, listOf(
                " ─────foo─bar┐ ",
                "             │ ",
                " ────────────┘ ",
            )),
            Case(Line.from("foo").rightAligned(), Line.from("bar").rightAligned(), Borders.LEFT or Borders.TOP or Borders.BOTTOM, listOf(
                " ┌─────foo─bar ",
                " │             ",
                " └──────────── ",
            )),
            Case(Line.from("foo").rightAligned(), Line.from("bar").rightAligned(), Borders.NONE, listOf(
                "       foo bar ",
                "               ",
                "               ",
            )),
        )

        val backend = TestBackend.new(15, 3)
        val terminal = Terminal(backend)
        val area = Rect.new(1, 0, 13, 3)

        for (case in cases) {
            val block = Block.default()
                .title(case.titleA)
                .title(case.titleB)
                .borders(case.borders)
            terminal.draw { frame ->
                frame.renderWidget(block, area)
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }
}
