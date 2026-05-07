// port-lint: source ratatui/tests/stylize.rs
package ratatui

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.style.cyan
import ratatui.style.lightBlue
import ratatui.style.onCyan
import ratatui.style.onWhite
import ratatui.text.Line
import ratatui.text.Text
import ratatui.terminal.Terminal
import ratatui.widgets.barchart.BarChart
import ratatui.widgets.barchart.BarGroup
import ratatui.widgets.block.Block
import ratatui.widgets.paragraph.Paragraph

class StylizeWidgetTest {
    @Test
    fun barchartCanBeStylized() {
        val barchart = BarChart.default()
            .onWhite()
            .barStyle(Style.new().red())
            .barWidth(2)
            .valueStyle(Style.new().green())
            .labelStyle(Style.new().blue())
            .data(BarGroup.from("A" to 1L, "B" to 2L, "C" to 3L))
            .max(3)

        val area = Rect.new(0, 0, 9, 5)
        val terminal = Terminal(TestBackend.new(9, 6))

        terminal.draw { f ->
            f.renderWidget(barchart, area)
        }

        val expected = Buffer.withLines(
            "      ██ ",
            "   ▅▅ ██ ",
            "▂▂ ██ ██ ",
            "1█ 2█ 3█ ",
            "A  B  C  ",
            "         ",
        )

        for (y in area.y until area.height) {
            // Background.
            for (x in area.x until area.width) {
                expected[x, y].setBg(Color.White)
            }
            // Bars.
            for (x in listOf(0, 1, 3, 4, 6, 7)) {
                expected[x, y].setFg(Color.Red)
            }
        }

        // Values.
        for (x in 0 until 3) {
            expected[x * 3, 3].setFg(Color.Green)
        }

        // Labels.
        for (x in 0 until 3) {
            expected[x * 3, 4].setFg(Color.Blue)
            expected[x * 3 + 1, 4].setFg(Color.Reset)
        }

        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun blockCanBeStylized() {
        val block = Block.bordered()
            .title(Line.from("Title".lightBlue()))
            .onCyan()
            .cyan()

        val area = Rect.new(0, 0, 8, 3)
        val terminal = Terminal(TestBackend.new(11, 4))

        terminal.draw { f ->
            f.renderWidget(block, area)
        }

        val expected = Buffer.withLines(
            "┌Title─┐   ",
            "│      │   ",
            "└──────┘   ",
            "           ",
        )

        for (x in area.x until area.width) {
            for (y in area.y until area.height) {
                expected[x, y].setFg(Color.Cyan).setBg(Color.Cyan)
            }
        }

        for (x in 1..5) {
            expected[x, 0].setFg(Color.LightBlue)
        }

        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun paragraphCanBeStylized() {
        val paragraph = Paragraph.new(Text.from("Text".cyan()))

        val area = Rect.new(0, 0, 10, 1)
        val terminal = Terminal(TestBackend.new(10, 1))

        terminal.draw { f ->
            f.renderWidget(paragraph, area)
        }

        val expected = Buffer.withLines("Text      ")
        for (x in 0 until 4) {
            expected[x, 0].setFg(Color.Cyan)
        }

        terminal.backend().assertBuffer(expected)
    }
}
