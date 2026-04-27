// port-lint: source ratatui-core/src/buffer/cellWidth.rs
package ratatui.buffer

import kotlin.test.Test
import kotlin.test.assertEquals

class CellWidthTest {
    @Test
    fun ascii() {
        assertEquals(1u, "a".cellWidth())
    }

    @Test
    fun wideChar() {
        assertEquals(2u, "あ".cellWidth())
    }

    @Test
    fun empty() {
        assertEquals(0u, "".cellWidth())
    }
}
