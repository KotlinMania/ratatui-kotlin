// port-lint: source ratatui-core/src/layout/offset.rs
package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals

class OffsetTest {
    @Test
    fun newSetsComponents() {
        assertEquals(Offset(x = -3, y = 7), Offset.new(-3, 7))
    }

    @Test
    fun constantsMatchExpectedValues() {
        assertEquals(Offset.new(0, 0), Offset.ZERO)
        assertEquals(Offset.new(Int.MIN_VALUE, Int.MIN_VALUE), Offset.MIN)
        assertEquals(Offset.new(Int.MAX_VALUE, Int.MAX_VALUE), Offset.MAX)
    }

    @Test
    fun fromPositionConvertsCoordinates() {
        val position = Position.new(4, 9)
        val offset = Offset.from(position)
        assertEquals(Offset.new(4, 9), offset)
    }
}
