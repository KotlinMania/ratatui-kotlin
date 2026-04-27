// port-lint: source ratatui-core/src/widgets/statefulWidget.rs
package ratatui.widgets

import kotlin.test.Test
import kotlin.test.assertEquals
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.text.Line

private fun buf(): Buffer = Buffer.empty(Rect.new(0, 0, 20, 1))

private fun state(): String = "world"

private class PersonalGreeting : StatefulWidget<String> {
    override fun render(area: Rect, buf: Buffer, state: String) {
        Line.from("Hello $state").render(area, buf)
    }
}

private class Bytes : StatefulWidget<ByteArray> {
    override fun render(area: Rect, buf: Buffer, state: ByteArray) {
        val slice = state.decodeToString()
        Line.from("Bytes: $slice").render(area, buf)
    }
}

class StatefulWidgetTest {
    @Test
    fun render() {
        val buffer = buf()
        val state = state()

        val widget = PersonalGreeting()
        widget.render(buffer.area, buffer, state)
        assertEquals(Buffer.withLines("Hello world         "), buffer)
    }

    @Test
    fun renderUnsizedStateType() {
        val buffer = buf()
        val state = "hello".encodeToByteArray()

        val widget = Bytes()
        widget.render(buffer.area, buffer, state)
        assertEquals(Buffer.withLines("Bytes: hello        "), buffer)
    }
}
