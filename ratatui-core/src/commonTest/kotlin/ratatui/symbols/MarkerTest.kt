// port-lint: source ratatui-core/src/symbols/marker.rs
package ratatui.symbols

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkerTest {
    @Test
    fun markerToString() {
        assertEquals("Dot", Marker.Dot.toString())
        assertEquals("Block", Marker.Block.toString())
        assertEquals("Bar", Marker.Bar.toString())
        assertEquals("Braille", Marker.Braille.toString())
        assertEquals("Custom", Marker.Custom('+').toString())
    }

    @Test
    fun markerFromStr() {
        assertEquals(Marker.Dot, Marker.fromStr("Dot"))
        assertEquals(Marker.Block, Marker.fromStr("Block"))
        assertEquals(Marker.Bar, Marker.fromStr("Bar"))
        assertEquals(Marker.Braille, Marker.fromStr("Braille"))
        assertEquals(null, Marker.fromStr(""))
    }
}
