// port-lint: source ratatui-macros/src/line.rs
package ratatui_macros

import ratatui.text.Line
import ratatui.text.Span
import kotlin.test.Test
import kotlin.test.assertEquals

class LineTest {
    @Test
    fun lineLiteral() {
        val actual = line("hello", "world")
        val expected = Line.from(listOf(Span.from("hello"), Span.from("world")))
        assertEquals(expected, actual)
    }

    @Test
    fun lineRawInsteadOfLiteral() {
        val actual = line(Span.raw("hello"), "world")
        val expected = Line.from(listOf(Span.from("hello"), Span.from("world")))
        assertEquals(expected, actual)
    }

    @Test
    fun lineVecCountSyntax() {
        val actual = line("hello", 2)
        val expected = Line.from(listOf(Span.from("hello"), Span.from("hello")))
        assertEquals(expected, actual)
    }

    @Test
    fun lineVecCountSyntaxWithSpan() {
        val actual = line(Span.raw("hello"), 2)
        val expected = Line.from(listOf(Span.from("hello"), Span.from("hello")))
        assertEquals(expected, actual)
    }

    @Test
    fun lineEmpty() {
        val actual = line()
        assertEquals(Line.default(), actual)
    }

    @Test
    fun lineSingleSpan() {
        val actual = line(Span.raw("foo"))
        val expected = Line.from(listOf(Span.from("foo")))
        assertEquals(expected, actual)
    }

    @Test
    fun lineRepeatedSpan() {
        val actual = line(Span.raw("foo"), 2)
        val expected = Line.from(listOf(Span.from("foo"), Span.from("foo")))
        assertEquals(expected, actual)
    }
}
