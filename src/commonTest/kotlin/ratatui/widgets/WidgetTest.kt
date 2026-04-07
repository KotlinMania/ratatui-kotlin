package ratatui.widgets

import kotlin.test.Test
import kotlin.test.assertEquals
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.text.Line

private fun buf(): Buffer = Buffer.empty(Rect.new(0, 0, 20, 1))

private class Greeting : Widget {
    override fun render(area: Rect, buf: Buffer) {
        Line.from("Hello").render(area, buf)
    }
}

class WidgetTest {
    @Test
    fun render() {
        val buffer = buf()
        val widget = Greeting()
        widget.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("Hello               "), buffer)
    }

    @Test
    fun renderStr() {
        val buffer = buf()
        "hello world".render(buffer.area, buffer)
        assertEquals(Buffer.withLines("hello world         "), buffer)
    }

    @Test
    fun renderStrTruncate() {
        val buffer = buf()
        val area = Rect.new(buffer.area.x, buffer.area.y, 11, buffer.area.height)
        "hello world, just hello".render(area, buffer)
        assertEquals(Buffer.withLines("hello world         "), buffer)
    }

    @Test
    fun renderOptionStr() {
        val buffer = buf()
        val widget: String? = "hello world"
        widget.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("hello world         "), buffer)
    }

    @Test
    fun renderString() {
        val buffer = buf()
        val widget = "hello world"
        widget.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("hello world         "), buffer)
    }

    @Test
    fun renderStringTruncate() {
        val buffer = buf()
        val area = Rect.new(buffer.area.x, buffer.area.y, 11, buffer.area.height)
        val widget = "hello world, just hello"
        widget.render(area, buffer)
        assertEquals(Buffer.withLines("hello world         "), buffer)
    }

    @Test
    fun renderOptionString() {
        val buffer = buf()
        val widget: String? = "hello world"
        widget.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("hello world         "), buffer)
    }
}
