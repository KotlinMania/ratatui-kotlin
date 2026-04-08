// port-lint: source ratatui/tests/widgets_canvas.rs
package ratatui.widgets.canvas

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.style.Color
import ratatui.style.Style
import ratatui.text.Line
import ratatui.text.Span
import ratatui.terminal.Terminal

class CanvasTest {
    @Test
    fun widgetsCanvasDrawLabels() {
        val backend = TestBackend.new(5, 5)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val label = "test"
            val canvas = Canvas.default()
                .backgroundColor(Color.Yellow)
                .xBounds(doubleArrayOf(0.0, 5.0))
                .yBounds(doubleArrayOf(0.0, 5.0))
                .paint { ctx ->
                    ctx.print(
                        0.0,
                        0.0,
                        Line.from(Span.styled(label, Style.default().fg(Color.Blue)))
                    )
                }
            f.renderWidget(canvas, f.area())
        }

        val expected = Buffer.withLines("", "", "", "", "test ")
        for (row in 0 until 5) {
            for (col in 0 until 5) {
                expected[col, row].setBg(Color.Yellow)
            }
        }
        for (col in 0 until 4) {
            expected[col, 4].setFg(Color.Blue)
        }
        terminal.backend().assertBuffer(expected)
    }
}

