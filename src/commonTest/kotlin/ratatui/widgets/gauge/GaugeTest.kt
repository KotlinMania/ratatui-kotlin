// port-lint: source ratatui/tests/widgetsGauge.rs
package ratatui.widgets.gauge

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.Constraint
import ratatui.layout.Direction
import ratatui.layout.Layout
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.symbols.line.Line as LineSymbols
import ratatui.text.Span
import ratatui.terminal.Terminal
import ratatui.widgets.block.Block

class GaugeTest {
    @Test
    fun widgetsGaugeRenders() {
        val backend = TestBackend.new(40, 10)
        val terminal = Terminal(backend)
        terminal.draw { f ->
            val chunks = Layout.default()
                .direction(Direction.Vertical)
                .margin(2)
                .constraints(listOf(Constraint.Percentage(50), Constraint.Percentage(50)))
                .split(f.area())

            val gauge1 = Gauge.default()
                .block(Block.bordered().title("Percentage"))
                .gaugeStyle(Style.default().bg(Color.Blue).fg(Color.Red))
                .useUnicode(true)
                .percent(43)
            f.renderWidget(gauge1, chunks[0])

            val gauge2 = Gauge.default()
                .block(Block.bordered().title("Ratio"))
                .gaugeStyle(Style.default().bg(Color.Blue).fg(Color.Red))
                .useUnicode(true)
                .ratio(0.5113139343131)
            f.renderWidget(gauge2, chunks[1])
        }

        val expected = Buffer.withLines(
            "                                        ",
            "                                        ",
            "  ┌Percentage────────────────────────┐  ",
            "  │██████████████▋43%                │  ",
            "  └──────────────────────────────────┘  ",
            "  ┌Ratio─────────────────────────────┐  ",
            "  │███████████████51%                │  ",
            "  └──────────────────────────────────┘  ",
            "                                        ",
            "                                        ",
        )

        expected.setStyle(Rect.new(3, 3, 34, 1), Style.new().red().onBlue())

        expected.setStyle(Rect.new(3, 6, 15, 1), Style.new().red().onBlue())
        // Note that filled part of the gauge only covers the 5 and the 1, not the % symbol.
        expected.setStyle(Rect.new(18, 6, 2, 1), Style.new().blue().onRed())
        expected.setStyle(Rect.new(20, 6, 17, 1), Style.new().red().onBlue())

        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsGaugeRendersNoUnicode() {
        val backend = TestBackend.new(40, 10)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val chunks = Layout.default()
                .direction(Direction.Vertical)
                .margin(2)
                .constraints(listOf(Constraint.Percentage(50), Constraint.Percentage(50)))
                .split(f.area())

            val gauge1 = Gauge.default()
                .block(Block.bordered().title("Percentage"))
                .percent(43)
                .useUnicode(false)
            f.renderWidget(gauge1, chunks[0])

            val gauge2 = Gauge.default()
                .block(Block.bordered().title("Ratio"))
                .ratio(0.2113139343131)
                .useUnicode(false)
            f.renderWidget(gauge2, chunks[1])
        }

        terminal.backend().assertBufferLines(
            "                                        ",
            "                                        ",
            "  ┌Percentage────────────────────────┐  ",
            "  │███████████████43%                │  ",
            "  └──────────────────────────────────┘  ",
            "  ┌Ratio─────────────────────────────┐  ",
            "  │███████        21%                │  ",
            "  └──────────────────────────────────┘  ",
            "                                        ",
            "                                        ",
        )
    }

    @Test
    fun widgetsGaugeAppliesStyles() {
        val backend = TestBackend.new(12, 5)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val gauge = Gauge.default()
                .block(
                    Block.bordered().title(Span.styled("Test", Style.default().fg(Color.Red)))
                )
                .gaugeStyle(Style.default().fg(Color.Blue).bg(Color.Red))
                .percent(43)
                .label(
                    Span.styled(
                        "43%",
                        Style.default()
                            .fg(Color.Green)
                            .addModifier(Modifier.BOLD)
                    )
                )
            f.renderWidget(gauge, f.area())
        }

        val expected = Buffer.withLines(
            "┌Test──────┐",
            "│████      │",
            "│███43%    │",
            "│████      │",
            "└──────────┘",
        )

        // Title.
        expected.setStyle(Rect.new(1, 0, 4, 1), Style.default().fg(Color.Red))
        // Gauge area.
        expected.setStyle(Rect.new(1, 1, 10, 3), Style.default().fg(Color.Blue).bg(Color.Red))
        // Filled area.
        expected.setStyle(Rect.new(1, 1, 4, 3), Style.default().fg(Color.Blue).bg(Color.Red))
        // Label (foreground and modifier from label style).
        expected.setStyle(
            Rect.new(4, 2, 1, 1),
            Style.default()
                .fg(Color.Green)
                // "4" is in the filled area so background is gaugeStyle foreground.
                .bg(Color.Blue)
                .addModifier(Modifier.BOLD),
        )
        expected.setStyle(
            Rect.new(5, 2, 2, 1),
            Style.default()
                .fg(Color.Green)
                // "3%" is not in the filled area so background is gaugeStyle background.
                .bg(Color.Red)
                .addModifier(Modifier.BOLD),
        )

        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsGaugeSupportsLargeLabels() {
        val backend = TestBackend.new(10, 1)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val gauge = Gauge.default()
                .percent(43)
                .label("43333333333333333333333333333%")
            f.renderWidget(gauge, f.area())
        }

        terminal.backend().assertBufferLines("4333333333")
    }

    @Test
    fun widgetsLineGaugeRenders() {
        val backend = TestBackend.new(20, 6)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val gauge1 = LineGauge.default()
                .filledStyle(Style.default().fg(Color.Green))
                .unfilledStyle(Style.default().fg(Color.White))
                .ratio(0.43)
            f.renderWidget(gauge1, Rect(x = 0, y = 0, width = 20, height = 1))

            // Custom (same) symbols for filled and unfilled parts.
            val gauge2 = LineGauge.default()
                .block(Block.bordered().title("Gauge 2"))
                .filledStyle(Style.default().fg(Color.Green))
                .filledSymbol(LineSymbols.THICK_HORIZONTAL)
                .unfilledSymbol(LineSymbols.THICK_HORIZONTAL)
                .ratio(0.2113139343131)
            f.renderWidget(gauge2, Rect(x = 0, y = 1, width = 20, height = 3))

            // Default symbol for filled part, but empty for unfilled part.
            val gauge3 = LineGauge.default().unfilledSymbol(" ").ratio(0.50)
            f.renderWidget(gauge3, Rect(x = 0, y = 4, width = 20, height = 1))

            // Different custom symbols for filled/unfilled parts.
            val gauge4 = LineGauge.default()
                .filledSymbol("█") // similar to `symbols::bar::FULL`
                .unfilledSymbol("░") // similar to `symbols::shade::LIGHT`
                .ratio(0.80)
            f.renderWidget(gauge4, Rect(x = 0, y = 5, width = 20, height = 1))
        }

        val expected = Buffer.withLines(
            " 43% ───────────────",
            "┌Gauge 2───────────┐",
            "│ 21% ━━━━━━━━━━━━━│",
            "└──────────────────┘",
            " 50% ───────        ",
            " 80% ████████████░░░",
        )

        for (col in 5 until 11) {
            expected[col, 0].setFg(Color.Green)
        }
        for (col in 11 until 20) {
            expected[col, 0].setFg(Color.White)
        }
        for (col in 6 until 8) {
            expected[col, 2].setFg(Color.Green)
        }

        terminal.backend().assertBuffer(expected)
    }
}

