// port-lint: source ratatui/tests/widgetsList.rs
package ratatui.widgets.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.symbols.line.Line as LineSymbols
import ratatui.text.Line
import ratatui.terminal.Terminal
import ratatui.widgets.Borders
import ratatui.widgets.HighlightSpacing
import ratatui.widgets.block.Block

class ListTest {
    @Test
    fun listShouldShowsTheLength() {
        val items = listOf(
            ListItem.new("Item 1"),
            ListItem.new("Item 2"),
            ListItem.new("Item 3"),
        )
        val list = List.new(items)
        assertEquals(3, list.len())
        assertFalse(list.isEmpty())

        val emptyList = List.default()
        assertEquals(0, emptyList.len())
        assertTrue(emptyList.isEmpty())
    }

    @Test
    fun widgetsListShouldHighlightTheSelectedItem() {
        val backend = TestBackend.new(10, 3)
        val terminal = Terminal(backend)
        val state = ListState.default()
        state.select(1)

        terminal.draw { f ->
            val items = listOf(
                ListItem.new("Item 1"),
                ListItem.new("Item 2"),
                ListItem.new("Item 3"),
            )
            val list = List.new(items)
                .highlightStyle(Style.default().bg(Color.Yellow))
                .highlightSymbol(">> ")
            f.renderStatefulWidget(list, f.area(), state)
        }

        val expected = Buffer.withLines(
            "   Item 1 ",
            ">> Item 2 ",
            "   Item 3 ",
        )
        for (x in 0 until 10) {
            expected[x, 1].setBg(Color.Yellow)
        }
        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsListShouldHighlightTheSelectedItemWideSymbol() {
        val backend = TestBackend.new(10, 3)
        val terminal = Terminal(backend)
        val state = ListState.default()

        val wideSymbol = "▶  "
        state.select(1)

        terminal.draw { f ->
            val items = listOf(
                ListItem.new("Item 1"),
                ListItem.new("Item 2"),
                ListItem.new("Item 3"),
            )
            val list = List.new(items)
                .highlightStyle(Style.default().bg(Color.Yellow))
                .highlightSymbol(wideSymbol)
            f.renderStatefulWidget(list, f.area(), state)
        }

        val expected = Buffer.withLines(
            "   Item 1 ",
            "▶  Item 2 ",
            "   Item 3 ",
        )
        for (x in 0 until 10) {
            expected[x, 1].setBg(Color.Yellow)
        }
        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsListShouldTruncateItems() {
        data class TruncateTestCase(
            val selected: Int?,
            val items: kotlin.collections.List<ListItem>,
            val expected: Buffer
        )

        val backend = TestBackend.new(10, 2)
        val terminal = Terminal(backend)

        val cases = listOf(
            // An item is selected.
            TruncateTestCase(
                selected = 0,
                items = listOf(
                    ListItem.new("A very long line"),
                    ListItem.new("A very long line"),
                ),
                expected = Buffer.withLines(
                    ">> A ve${LineSymbols.VERTICAL}  ",
                    "   A ve${LineSymbols.VERTICAL}  ",
                ),
            ),
            // No item is selected.
            TruncateTestCase(
                selected = null,
                items = listOf(
                    ListItem.new("A very long line"),
                    ListItem.new("A very long line"),
                ),
                expected = Buffer.withLines(
                    "A very ${LineSymbols.VERTICAL}  ",
                    "A very ${LineSymbols.VERTICAL}  ",
                ),
            ),
        )

        for (case in cases) {
            val state = ListState.default()
            state.select(case.selected)

            terminal.draw { f ->
                val list = List.new(case.items)
                    .block(Block.new().borders(Borders.RIGHT))
                    .highlightSymbol(">> ")
                f.renderStatefulWidget(list, Rect.new(0, 0, 8, 2), state)
            }
            terminal.backend().assertBuffer(case.expected)
        }
    }

    @Test
    fun widgetsListShouldClampOffsetIfItemsAreRemoved() {
        val backend = TestBackend.new(10, 4)
        val terminal = Terminal(backend)
        val state = ListState.default()

        // Render with 6 items => offset will be at 2.
        state.select(5)
        terminal.draw { f ->
            val items = listOf(
                ListItem.new("Item 0"),
                ListItem.new("Item 1"),
                ListItem.new("Item 2"),
                ListItem.new("Item 3"),
                ListItem.new("Item 4"),
                ListItem.new("Item 5"),
            )
            val list = List.new(items).highlightSymbol(">> ")
            f.renderStatefulWidget(list, f.area(), state)
        }
        terminal.backend().assertBufferLines(
            "   Item 2 ",
            "   Item 3 ",
            "   Item 4 ",
            ">> Item 5 ",
        )

        // Render again with 1 item => check offset is clamped to 1.
        state.select(1)
        terminal.draw { f ->
            val items = listOf(ListItem.new("Item 3"))
            val list = List.new(items).highlightSymbol(">> ")
            f.renderStatefulWidget(list, f.area(), state)
        }
        terminal.backend().assertBufferLines(
            ">> Item 3 ",
            "          ",
            "          ",
            "          ",
        )
    }

    @Test
    fun widgetsListShouldDisplayMultilineItems() {
        val backend = TestBackend.new(10, 6)
        val terminal = Terminal(backend)
        val state = ListState.default()
        state.select(1)

        terminal.draw { f ->
            val items = listOf(
                ListItem.new(listOf(Line.from("Item 1"), Line.from("Item 1a"))),
                ListItem.new(listOf(Line.from("Item 2"), Line.from("Item 2b"))),
                ListItem.new(listOf(Line.from("Item 3"), Line.from("Item 3c"))),
            )
            val list = List.new(items)
                .highlightStyle(Style.default().bg(Color.Yellow))
                .highlightSymbol(">> ")
            f.renderStatefulWidget(list, f.area(), state)
        }

        val expected = Buffer.withLines(
            "   Item 1 ",
            "   Item 1a",
            ">> Item 2 ",
            "   Item 2b",
            "   Item 3 ",
            "   Item 3c",
        )
        for (x in 0 until 10) {
            expected[x, 2].setBg(Color.Yellow)
            expected[x, 3].setBg(Color.Yellow)
        }
        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsListShouldRepeatHighlightSymbol() {
        val backend = TestBackend.new(10, 6)
        val terminal = Terminal(backend)
        val state = ListState.default()
        state.select(1)

        terminal.draw { f ->
            val items = listOf(
                ListItem.new(listOf(Line.from("Item 1"), Line.from("Item 1a"))),
                ListItem.new(listOf(Line.from("Item 2"), Line.from("Item 2b"))),
                ListItem.new(listOf(Line.from("Item 3"), Line.from("Item 3c"))),
            )
            val list = List.new(items)
                .highlightStyle(Style.default().bg(Color.Yellow))
                .highlightSymbol(">> ")
                .repeatHighlightSymbol(true)
            f.renderStatefulWidget(list, f.area(), state)
        }

        val expected = Buffer.withLines(
            "   Item 1 ",
            "   Item 1a",
            ">> Item 2 ",
            ">> Item 2b",
            "   Item 3 ",
            "   Item 3c",
        )
        for (x in 0 until 10) {
            expected[x, 2].setBg(Color.Yellow)
            expected[x, 3].setBg(Color.Yellow)
        }
        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetListShouldNotIgnoreEmptyStringItems() {
        val backend = TestBackend.new(6, 4)
        val terminal = Terminal(backend)
        terminal.draw { f ->
            val items = listOf(
                ListItem.new("Item 1"),
                ListItem.new(""),
                ListItem.new(""),
                ListItem.new("Item 4"),
            )
            val list = List.new(items)
                .style(Style.default())
                .highlightStyle(Style.default())
            f.renderWidget(list, f.area())
        }
        terminal.backend().assertBufferLines("Item 1", "", "", "Item 4")
    }

    @Test
    fun widgetsListEnableAlwaysHighlightSpacing() {
        data class Case(val selected: Int?, val space: HighlightSpacing, val expected: kotlin.collections.List<String>)

        val cases = listOf(
            Case(null, HighlightSpacing.WhenSelected, listOf(
                "┌─────────────┐",
                "│Item 1       │",
                "│Item 1a      │",
                "│Item 2       │",
                "│Item 2b      │",
                "│Item 3       │",
                "│Item 3c      │",
                "└─────────────┘",
            )),
            Case(null, HighlightSpacing.Always, listOf(
                "┌─────────────┐",
                "│   Item 1    │",
                "│   Item 1a   │",
                "│   Item 2    │",
                "│   Item 2b   │",
                "│   Item 3    │",
                "│   Item 3c   │",
                "└─────────────┘",
            )),
            Case(null, HighlightSpacing.Never, listOf(
                "┌─────────────┐",
                "│Item 1       │",
                "│Item 1a      │",
                "│Item 2       │",
                "│Item 2b      │",
                "│Item 3       │",
                "│Item 3c      │",
                "└─────────────┘",
            )),
            Case(0, HighlightSpacing.WhenSelected, listOf(
                "┌─────────────┐",
                "│>> Item 1    │",
                "│   Item 1a   │",
                "│   Item 2    │",
                "│   Item 2b   │",
                "│   Item 3    │",
                "│   Item 3c   │",
                "└─────────────┘",
            )),
            Case(0, HighlightSpacing.Always, listOf(
                "┌─────────────┐",
                "│>> Item 1    │",
                "│   Item 1a   │",
                "│   Item 2    │",
                "│   Item 2b   │",
                "│   Item 3    │",
                "│   Item 3c   │",
                "└─────────────┘",
            )),
            Case(0, HighlightSpacing.Never, listOf(
                "┌─────────────┐",
                "│Item 1       │",
                "│Item 1a      │",
                "│Item 2       │",
                "│Item 2b      │",
                "│Item 3       │",
                "│Item 3c      │",
                "└─────────────┘",
            )),
        )

        for (case in cases) {
            val state = ListState.default().withSelected(case.selected)
            val backend = TestBackend.new(15, 8)
            val terminal = Terminal(backend)
            terminal.draw { f ->
                val list = List.new(listOf(
                    ListItem.new(listOf(Line.from("Item 1"), Line.from("Item 1a"))),
                    ListItem.new(listOf(Line.from("Item 2"), Line.from("Item 2b"))),
                    ListItem.new(listOf(Line.from("Item 3"), Line.from("Item 3c"))),
                ))
                    .block(Block.bordered())
                    .highlightSymbol(">> ")
                    .highlightSpacing(case.space)
                f.renderStatefulWidget(list, f.area(), state)
            }
            terminal.backend().assertBuffer(Buffer.withLines(*case.expected.toTypedArray()))
        }
    }
}
