// port-lint: source ratatui/tests/widgets_tabs.rs
package ratatui.widgets.tabs

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.symbols.line.Line as LineSymbols
import ratatui.terminal.Terminal

class TabsTest {
    @Test
    fun widgetsTabsShouldNotPanicOnNarrowAreas() {
        val backend = TestBackend.new(1, 1)
        val terminal = Terminal(backend)
        terminal.draw { f ->
            val tabs = Tabs.new("Tab1", "Tab2")
            f.renderWidget(tabs, Rect(x = 0, y = 0, width = 1, height = 1))
        }
        terminal.backend().assertBufferLines(" ")
    }

    @Test
    fun widgetsTabsShouldTruncateTheLastItem() {
        val backend = TestBackend.new(10, 1)
        val terminal = Terminal(backend)
        terminal.draw { f ->
            val tabs = Tabs.new("Tab1", "Tab2")
            f.renderWidget(tabs, Rect(x = 0, y = 0, width = 9, height = 1))
        }
        val expected = Buffer.withLines(" Tab1 ${LineSymbols.VERTICAL} T ")
        expected.setStyle(Rect.new(1, 0, 4, 1), Style.new().reversed())
        terminal.backend().assertBuffer(expected)
    }
}
