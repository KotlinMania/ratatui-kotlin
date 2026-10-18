// port-lint: tests ratatui-core/src/layout/alignment.rs
package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for [HorizontalAlignment] and [VerticalAlignment] parsing helpers.
 */
class AlignmentTest {
    @Test
    fun horizontalAlignmentToString() {
        assertEquals("Left", HorizontalAlignment.Left.toString())
        assertEquals("Center", HorizontalAlignment.Center.toString())
        assertEquals("Right", HorizontalAlignment.Right.toString())
    }

    @Test
    fun horizontalAlignmentFromString() {
        assertEquals(HorizontalAlignment.Left, HorizontalAlignment.fromString("Left"))
        assertEquals(HorizontalAlignment.Center, HorizontalAlignment.fromString("Center"))
        assertEquals(HorizontalAlignment.Right, HorizontalAlignment.fromString("Right"))
        assertFailsWith<IllegalArgumentException> { HorizontalAlignment.fromString("") }
    }

    @Test
    fun verticalAlignmentToString() {
        assertEquals("Top", VerticalAlignment.Top.toString())
        assertEquals("Center", VerticalAlignment.Center.toString())
        assertEquals("Bottom", VerticalAlignment.Bottom.toString())
    }

    @Test
    fun verticalAlignmentFromString() {
        assertEquals(VerticalAlignment.Top, VerticalAlignment.fromString("Top"))
        assertEquals(VerticalAlignment.Center, VerticalAlignment.fromString("Center"))
        assertEquals(VerticalAlignment.Bottom, VerticalAlignment.fromString("Bottom"))
        assertFailsWith<IllegalArgumentException> { VerticalAlignment.fromString("") }
    }
}
