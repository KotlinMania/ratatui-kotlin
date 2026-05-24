// port-lint: source layout/margin.rs
package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals

class MarginTest {
    @Test
    fun marginToString() {
        assertEquals("1x2", Margin.new(1, 2).toString())
    }

    @Test
    fun marginNew() {
        assertEquals(
            Margin(horizontal = 1, vertical = 2),
            Margin.new(1, 2),
        )
    }

    @Test
    fun fromU16() {
        val m: Margin = Margin.from(5)
        assertEquals(
            Margin(horizontal = 5, vertical = 5),
            m,
        )
    }
}
