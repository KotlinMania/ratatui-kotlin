package ratatui.widgets

import kotlin.test.Test
import kotlin.test.assertEquals
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.text.Line

class StatefulWidgetTest {
    @Test
    fun render() {
        val buffer = Buffer.empty(Rect.new(0, 0, 20, 1))
        val state = "world"

        val widget = object : StatefulWidget<String> {
            override fun render(area: Rect, buf: Buffer, state: String) {
                Line.from("Hello $state").render(area, buf)
            }
        }

        widget.render(buffer.area, buffer, state)

        assertEquals(Buffer.withLines("Hello world         "), buffer)
    }

    @Test
    fun renderUnsizedStateType() {
        val buffer = Buffer.empty(Rect.new(0, 0, 20, 1))
        val state = "hello".encodeToByteArray()

        val widget = object : StatefulWidget<ByteArray> {
            override fun render(area: Rect, buf: Buffer, state: ByteArray) {
                val slice = state.decodeToString()
                Line.from("Bytes: $slice").render(area, buf)
            }
        }

        widget.render(buffer.area, buffer, state)

        assertEquals(Buffer.withLines("Bytes: hello        "), buffer)
    }
}

