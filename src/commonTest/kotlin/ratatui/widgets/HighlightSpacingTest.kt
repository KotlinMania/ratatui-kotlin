package ratatui.widgets

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HighlightSpacingTest {
    @Test
    fun toStringMatchesRustDisplay() {
        assertEquals("Always", HighlightSpacing.Always.toString())
        assertEquals("WhenSelected", HighlightSpacing.WhenSelected.toString())
        assertEquals("Never", HighlightSpacing.Never.toString())
    }

    @Test
    fun fromStrParsesVariants() {
        assertEquals(HighlightSpacing.Always, HighlightSpacing.fromStr("Always"))
        assertEquals(HighlightSpacing.WhenSelected, HighlightSpacing.fromStr("WhenSelected"))
        assertEquals(HighlightSpacing.Never, HighlightSpacing.fromStr("Never"))
        assertFailsWith<IllegalArgumentException> {
            HighlightSpacing.fromStr("")
        }
    }
}

