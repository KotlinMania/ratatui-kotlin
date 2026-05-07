// port-lint: source ratatui/tests/stateful_widget_ref_dyn.rs
package ratatui.widgets

import kotlin.test.Test
import kotlin.test.assertEquals
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.text.Line

private interface AnyWindow : StatefulWidgetRef<Any> {
    fun title(): String = "stateful_widget_ref_dyn::${this::class.simpleName}"
}

private class Window1State(val value: Int)

private class Window1 : AnyWindow {
    override fun renderRef(area: Rect, buf: Buffer, state: Any) {
        val windowState = state as Window1State
        Line.from("${title()}, u32: ${windowState.value}").render(area, buf)
    }
}

private class Window2State(val value: String)

private class Window2 : AnyWindow {
    override fun renderRef(area: Rect, buf: Buffer, state: Any) {
        val windowState = state as Window2State
        Line.from("${title()}, String: ${windowState.value}").render(area, buf)
    }
}

class StatefulWidgetRefDynTest {
    @Test
    fun renderDynWidgets() {
        val windows = listOf(
            Window1() to Window1State(32),
            Window2() to Window2State("Some"),
            Window1() to Window1State(42),
        )

        val buf = Buffer.empty(Rect.new(0, 0, 50, 3))

        var area = Rect.new(0, 0, 50, 1)
        for ((w, s) in windows) {
            w.renderRef(area, buf, s)
            area = area.copy(y = area.y + 1)
        }

        assertEquals(
            Buffer.withLines(
                "stateful_widget_ref_dyn::Window1, u32: 32         ",
                "stateful_widget_ref_dyn::Window2, String: Some    ",
                "stateful_widget_ref_dyn::Window1, u32: 42         ",
            ),
            buf,
        )
    }
}

