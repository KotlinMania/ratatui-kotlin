package ratatui.backend

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClearTypeTest {
    @Test
    fun clearTypeToString() {
        assertEquals("All", ClearType.All.toString())
        assertEquals("AfterCursor", ClearType.AfterCursor.toString())
        assertEquals("BeforeCursor", ClearType.BeforeCursor.toString())
        assertEquals("CurrentLine", ClearType.CurrentLine.toString())
        assertEquals("UntilNewLine", ClearType.UntilNewLine.toString())
    }

    @Test
    fun clearTypeFromString() {
        assertEquals(ClearType.All, ClearType.fromString("All"))
        assertEquals(ClearType.AfterCursor, ClearType.fromString("AfterCursor"))
        assertEquals(ClearType.BeforeCursor, ClearType.fromString("BeforeCursor"))
        assertEquals(ClearType.CurrentLine, ClearType.fromString("CurrentLine"))
        assertEquals(ClearType.UntilNewLine, ClearType.fromString("UntilNewLine"))
        assertNull(ClearType.fromString(""))
    }
}

