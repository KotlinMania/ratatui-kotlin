// port-lint: source ratatui/tests/widgetsBarchart.rs
package ratatui.widgets.barchart

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.style.Color
import ratatui.style.Style
import ratatui.terminal.Terminal
import ratatui.widgets.block.Block

class BarChartTest {
    @Test
    fun widgetsBarchartNotFullBelowMaxValue() {
        val backend = TestBackend.new(30, 10)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val barchart = BarChart.default()
                .block(Block.bordered())
                .data(BarGroup.from("empty" to 0L, "half" to 50L, "almost" to 99L, "full" to 100L))
                .max(100)
                .barWidth(7)
                .barGap(0)
            f.renderWidget(barchart, f.area())
        }

        terminal.backend().assertBufferLines(
            "┌────────────────────────────┐",
            "│              ▇▇▇▇▇▇▇███████│",
            "│              ██████████████│",
            "│              ██████████████│",
            "│       ▄▄▄▄▄▄▄██████████████│",
            "│       █████████████████████│",
            "│       █████████████████████│",
            "│       ██50█████99█████100██│",
            "│ empty  half  almost  full  │",
            "└────────────────────────────┘",
        )
    }

    @Test
    fun widgetsBarchartGroup() {
        val terminalHeight = 11
        val backend = TestBackend.new(35, terminalHeight)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val barchart = BarChart.default()
                .block(Block.bordered())
                .data(
                    BarGroup.default().label("Mar").bars(
                        Bar.default()
                            .value(10)
                            .label("C1")
                            .style(Style.default().fg(Color.Red))
                            .valueStyle(Style.default().fg(Color.Blue)),
                        Bar.default()
                            .value(20)
                            .style(Style.default().fg(Color.Green))
                            .textValue("20M"),
                    )
                )
                .data(BarGroup.from("C1" to 50L, "C2" to 40L))
                .data(BarGroup.from("C1" to 60L, "C2" to 90L))
                .data(BarGroup.from("xx" to 10L, "xx" to 10L))
                .groupGap(2)
                .barWidth(4)
                .barGap(1)
            f.renderWidget(barchart, f.area())
        }

        val expected = Buffer.withLines(
            "┌─────────────────────────────────┐",
            "│                             ████│",
            "│                             ████│",
            "│                        ▅▅▅▅ ████│",
            "│            ▇▇▇▇        ████ ████│",
            "│            ████ ████   ████ ████│",
            "│     ▄▄▄▄   ████ ████   ████ ████│",
            "│▆10▆ 20M█   █50█ █40█   █60█ █90█│",
            "│ C1          C1   C2     C1   C2 │",
            "│Mar                              │",
            "└─────────────────────────────────┘",
        )

        for (y in 1 until (terminalHeight - 3)) {
            for (x in 1 until 5) {
                expected[x, y].setFg(Color.Red)
                expected[x + 5, y].setFg(Color.Green)
            }
        }

        expected[2, 7].setFg(Color.Blue)
        expected[3, 7].setFg(Color.Blue)

        terminal.backend().assertBuffer(expected)
    }
}
