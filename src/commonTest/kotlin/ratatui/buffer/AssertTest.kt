package ratatui.buffer

import kotlin.test.Test
import kotlin.test.assertFailsWith
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style

class AssertTest {
    @Test
    fun assertBufferEqDoesNotThrowOnEqualBuffers() {
        val buffer = Buffer.empty(Rect.new(0, 0, 5, 1))
        val otherBuffer = Buffer.empty(Rect.new(0, 0, 5, 1))
        assertBufferEq(buffer, otherBuffer)
    }

    @Test
    fun assertBufferEqThrowsOnUnequalArea() {
        val buffer = Buffer.empty(Rect.new(0, 0, 5, 1))
        val otherBuffer = Buffer.empty(Rect.new(0, 0, 6, 1))
        val ex = assertFailsWith<AssertionError> { assertBufferEq(buffer, otherBuffer) }
        check(ex.message?.contains("buffer areas not equal") == true) { "unexpected message: ${ex.message}" }
    }

    @Test
    fun assertBufferEqThrowsOnUnequalStyle() {
        val buffer = Buffer.empty(Rect.new(0, 0, 5, 1))
        val otherBuffer = Buffer.empty(Rect.new(0, 0, 5, 1))
        otherBuffer.setString(0, 0, " ", Style.new().fg(Color.Red))

        val ex = assertFailsWith<AssertionError> { assertBufferEq(buffer, otherBuffer) }
        check(ex.message?.contains("buffer contents not equal") == true) { "unexpected message: ${ex.message}" }
    }
}
