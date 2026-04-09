// port-lint: source ratatui-core/src/text/masked.rs
package ratatui.text

import kotlin.test.Test
import kotlin.test.assertEquals

class MaskedTest {
    @Test
    fun new() {
        val masked = Masked.new("12345", 'x')
        assertEquals('x', masked.maskChar())
        assertEquals("12345", masked.debugString())
    }

    @Test
    fun value() {
        val masked = Masked.new("12345", 'x')
        assertEquals("xxxxx", masked.value())
    }

    @Test
    fun displayToString() {
        val masked = Masked.new("12345", 'x')
        assertEquals("xxxxx", masked.toString())
    }

    @Test
    fun intoText() {
        val masked = Masked.new("12345", 'x')
        val text = masked.toText()
        assertEquals(Text.raw("xxxxx"), text)
    }
}
