// port-lint: source ratatui/tests/widgets_chart.rs
package ratatui.widgets.chart

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.HorizontalAlignment
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.symbols.Marker
import ratatui.text.Line
import ratatui.text.Span
import ratatui.terminal.Terminal
import ratatui.widgets.block.Block

private fun axisTestCase(
    width: Int,
    height: Int,
    xAxis: Axis,
    yAxis: Axis,
    expected: List<String>,
) {
    val backend = TestBackend.new(width, height)
    val terminal = Terminal(backend)
    terminal.draw { f ->
        val chart = Chart.new(emptyList()).xAxis(xAxis).yAxis(yAxis)
        f.renderWidget(chart, f.area())
    }
    terminal.backend().assertBufferLines(expected)
}

class ChartTest {
    @Test
    fun widgetsChartCanRenderOnSmallAreas() {
        val cases = listOf(
            0 to 0,
            0 to 1,
            1 to 0,
            1 to 1,
            2 to 2,
        )

        for ((width, height) in cases) {
            val backend = TestBackend.new(width, height)
            val terminal = Terminal(backend)
            terminal.draw { f ->
                val datasets = listOf(
                    Dataset.default()
                        .marker(Marker.Braille)
                        .style(Style.default().fg(Color.Magenta))
                        .data(0.0 to 0.0),
                )
                val chart = Chart.new(datasets)
                    .block(Block.bordered().title("Plot"))
                    .xAxis(Axis.default().bounds(doubleArrayOf(0.0, 0.0)).labels("0.0", "1.0"))
                    .yAxis(Axis.default().bounds(doubleArrayOf(0.0, 0.0)).labels("0.0", "1.0"))
                f.renderWidget(chart, f.area())
            }
        }
    }

    @Test
    fun widgetsChartHandlesLongLabels() {
        data class Case(
            val xLabels: Pair<String, String>?,
            val yLabels: Pair<String, String>?,
            val xAlignment: HorizontalAlignment,
            val expected: List<String>,
        )

        val cases = listOf(
            Case(
                xLabels = "AAAA" to "B",
                yLabels = null,
                xAlignment = HorizontalAlignment.Left,
                expected = listOf(
                    "          ",
                    "          ",
                    "          ",
                    "   ───────",
                    "AAA      B",
                ),
            ),
            Case(
                xLabels = "A" to "BBBB",
                yLabels = null,
                xAlignment = HorizontalAlignment.Left,
                expected = listOf(
                    "          ",
                    "          ",
                    "          ",
                    " ─────────",
                    "A     BBBB",
                ),
            ),
            Case(
                xLabels = "AAAAAAAAAAA" to "B",
                yLabels = null,
                xAlignment = HorizontalAlignment.Left,
                expected = listOf(
                    "          ",
                    "          ",
                    "          ",
                    "   ───────",
                    "AAA      B",
                ),
            ),
            Case(
                xLabels = "A" to "B",
                yLabels = "CCCCCCC" to "D",
                xAlignment = HorizontalAlignment.Left,
                expected = listOf(
                    "D  │      ",
                    "   │      ",
                    "CCC│      ",
                    "   └──────",
                    "   A     B",
                ),
            ),
            Case(
                xLabels = "AAAAAAAAAA" to "B",
                yLabels = "C" to "D",
                xAlignment = HorizontalAlignment.Center,
                expected = listOf(
                    "D  │      ",
                    "   │      ",
                    "C  │      ",
                    "   └──────",
                    "AAAAAAA  B",
                ),
            ),
            Case(
                xLabels = "AAAAAAA" to "B",
                yLabels = "C" to "D",
                xAlignment = HorizontalAlignment.Right,
                expected = listOf(
                    "D│        ",
                    " │        ",
                    "C│        ",
                    " └────────",
                    " AAAAA   B",
                ),
            ),
            Case(
                xLabels = "AAAAAAA" to "BBBBBBB",
                yLabels = "C" to "D",
                xAlignment = HorizontalAlignment.Right,
                expected = listOf(
                    "D│        ",
                    " │        ",
                    "C│        ",
                    " └────────",
                    " AAAAABBBB",
                ),
            ),
        )

        for (case in cases) {
            var xAxis = Axis.default().bounds(0.0 to 1.0)
            case.xLabels?.let { (leftLabel, rightLabel) ->
                xAxis = xAxis.labels(leftLabel, rightLabel).labelsAlignment(case.xAlignment)
            }

            var yAxis = Axis.default().bounds(0.0 to 1.0)
            case.yLabels?.let { (bottomLabel, topLabel) ->
                yAxis = yAxis.labels(bottomLabel, topLabel)
            }

            axisTestCase(10, 5, xAxis, yAxis, case.expected)
        }
    }

    @Test
    fun widgetsChartHandlesXAxisLabelsAlignments() {
        data class Case(val alignment: HorizontalAlignment, val expected: List<String>)

        val cases = listOf(
            Case(
                HorizontalAlignment.Left,
                listOf(
                    "          ",
                    "          ",
                    "          ",
                    "   ───────",
                    "AAA   B  C",
                ),
            ),
            Case(
                HorizontalAlignment.Center,
                listOf(
                    "          ",
                    "          ",
                    "          ",
                    "  ────────",
                    "AAAA B   C",
                ),
            ),
            Case(
                HorizontalAlignment.Right,
                listOf(
                    "          ",
                    "          ",
                    "          ",
                    "──────────",
                    "AAA B    C",
                ),
            ),
        )

        for (case in cases) {
            val xAxis = Axis.default()
                .labels("AAAA", "B", "C")
                .labelsAlignment(case.alignment)
            val yAxis = Axis.default()
            axisTestCase(10, 5, xAxis, yAxis, case.expected)
        }
    }

    @Test
    fun widgetsChartHandlesYAxisLabelsAlignments() {
        data class Case(val alignment: HorizontalAlignment, val expected: List<String>)

        val cases = listOf(
            Case(
                HorizontalAlignment.Left,
                listOf(
                    "D   │               ",
                    "    │               ",
                    "C   │               ",
                    "    └───────────────",
                    "AAAAA              B",
                ),
            ),
            Case(
                HorizontalAlignment.Center,
                listOf(
                    " D  │               ",
                    "    │               ",
                    " C  │               ",
                    "    └───────────────",
                    "AAAAA              B",
                ),
            ),
            Case(
                HorizontalAlignment.Right,
                listOf(
                    "   D│               ",
                    "    │               ",
                    "   C│               ",
                    "    └───────────────",
                    "AAAAA              B",
                ),
            ),
        )

        for (case in cases) {
            val xAxis = Axis.default().labels("AAAAA", "B")
            val yAxis = Axis.default().labels("C", "D").labelsAlignment(case.alignment)
            axisTestCase(20, 5, xAxis, yAxis, case.expected)
        }
    }

    @Test
    fun widgetsChartCanHaveAxisWithZeroLengthBounds() {
        val backend = TestBackend.new(100, 100)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val datasets = listOf(
                Dataset.default()
                    .marker(Marker.Braille)
                    .style(Style.default().fg(Color.Magenta))
                    .data(0.0 to 0.0),
            )
            val chart = Chart.new(datasets)
                .block(Block.bordered().title("Plot"))
                .xAxis(Axis.default().bounds(doubleArrayOf(0.0, 0.0)).labels("0.0", "1.0"))
                .yAxis(Axis.default().bounds(doubleArrayOf(0.0, 0.0)).labels("0.0", "1.0"))
            f.renderWidget(chart, Rect(x = 0, y = 0, width = 100, height = 100))
        }
    }

    @Test
    fun widgetsChartHandlesOverflows() {
        val backend = TestBackend.new(80, 30)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val datasets = listOf(
                Dataset.default()
                    .marker(Marker.Braille)
                    .style(Style.default().fg(Color.Magenta))
                    .data(
                        1588298471.0 to 1.0,
                        1588298473.0 to 0.0,
                        1588298496.0 to 1.0,
                    ),
            )
            val chart = Chart.new(datasets)
                .block(Block.bordered().title("Plot"))
                .xAxis(
                    Axis.default()
                        .bounds(1588298471.0 to 1588992600.0)
                        .labels("1588298471.0", "1588992600.0"),
                )
                .yAxis(
                    Axis.default()
                        .bounds(0.0 to 1.0)
                        .labels("0.0", "1.0"),
                )
            f.renderWidget(chart, Rect(x = 0, y = 0, width = 80, height = 30))
        }
    }

    @Test
    fun widgetsChartCanHaveEmptyDatasets() {
        val backend = TestBackend.new(100, 100)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val datasets = listOf(
                Dataset.default().data(emptyList()).graphType(GraphType.Line),
            )
            val chart = Chart.new(datasets)
                .block(Block.bordered().title("Empty Dataset With Line"))
                .xAxis(Axis.default().bounds(doubleArrayOf(0.0, 0.0)).labels("0.0", "1.0"))
                .yAxis(Axis.default().bounds(0.0 to 1.0).labels("0.0", "1.0"))
            f.renderWidget(chart, Rect(x = 0, y = 0, width = 100, height = 100))
        }
    }

    @Test
    fun widgetsChartTopLineStylingIsCorrect() {
        val backend = TestBackend.new(9, 5)
        val terminal = Terminal(backend)

        val titleStyle = Style.default().fg(Color.Red).bg(Color.LightRed)
        val dataStyle = Style.default().fg(Color.Blue)

        terminal.draw { f ->
            val data = listOf(0.0 to 1.0, 1.0 to 1.0)
            val widget = Chart.new(
                listOf(
                    Dataset.default()
                        .data(data)
                        .graphType(GraphType.Line)
                        .style(dataStyle),
                )
            )
                .yAxis(
                    Axis.default()
                        .title(Line.from(Span.styled("abc", titleStyle)))
                        .bounds(0.0 to 1.0)
                        .labels("a", "b"),
                )
                .xAxis(Axis.default().bounds(0.0 to 1.0))
            f.renderWidget(widget, f.area())
        }

        val expected = Buffer.withLines(
            "b│abc••••",
            " │       ",
            " │       ",
            " │       ",
            "a│       ",
        )
        expected.setStyle(Rect.new(2, 0, 3, 1), titleStyle)
        expected.setStyle(Rect.new(5, 0, 4, 1), dataStyle)
        terminal.backend().assertBuffer(expected)
    }
}

