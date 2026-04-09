package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for `Alignment` (Rust alias) and parsing helpers.
 *
 * Rust keeps `pub type Alignment = HorizontalAlignment` for backwards compatibility, and provides
 * string parsing via `strum`. This Kotlin port mirrors the behavior using [Alignment] as a
 * value-level alias and `fromString` helpers.
 */
class AlignmentAliasTest {
    @Test
    fun alignmentToString() {
        assertEquals("Left", Alignment.Left.toString())
        assertEquals("Center", Alignment.Center.toString())
        assertEquals("Right", Alignment.Right.toString())
    }

    @Test
    fun alignmentFromString() {
        assertEquals(HorizontalAlignment.Left, Alignment.fromString("Left"))
        assertEquals(HorizontalAlignment.Center, Alignment.fromString("Center"))
        assertEquals(HorizontalAlignment.Right, Alignment.fromString("Right"))
        assertFailsWith<IllegalArgumentException> { Alignment.fromString("") }
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

