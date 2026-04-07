package ratatui.layout.rect

import ratatui.layout.Position
import ratatui.layout.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class IterTest {
    @Test
    fun rows() {
        val rect = Rect.new(0, 0, 2, 3)
        val rows = Rows.new(rect)
        assertEquals(Pair(3, 3), rows.sizeHint())
        assertEquals(Rect.new(0, 0, 2, 1), rows.next())
        assertEquals(Pair(2, 2), rows.sizeHint())
        assertEquals(Rect.new(0, 1, 2, 1), rows.next())
        assertEquals(Pair(1, 1), rows.sizeHint())
        assertEquals(Rect.new(0, 2, 2, 1), rows.next())
        assertEquals(Pair(0, 0), rows.sizeHint())
        assertEquals(null, rows.next())
        assertEquals(Pair(0, 0), rows.sizeHint())
        assertEquals(null, rows.nextBack())
        assertEquals(Pair(0, 0), rows.sizeHint())
    }

    @Test
    fun rowsBack() {
        val rect = Rect.new(0, 0, 2, 3)
        val rows = Rows.new(rect)
        assertEquals(Pair(3, 3), rows.sizeHint())
        assertEquals(Rect.new(0, 2, 2, 1), rows.nextBack())
        assertEquals(Pair(2, 2), rows.sizeHint())
        assertEquals(Rect.new(0, 1, 2, 1), rows.nextBack())
        assertEquals(Pair(1, 1), rows.sizeHint())
        assertEquals(Rect.new(0, 0, 2, 1), rows.nextBack())
        assertEquals(Pair(0, 0), rows.sizeHint())
        assertEquals(null, rows.nextBack())
        assertEquals(Pair(0, 0), rows.sizeHint())
        assertEquals(null, rows.next())
        assertEquals(Pair(0, 0), rows.sizeHint())
    }

    @Test
    fun rowsMeetInTheMiddle() {
        val rect = Rect.new(0, 0, 2, 4)
        val rows = Rows.new(rect)
        assertEquals(Pair(4, 4), rows.sizeHint())
        assertEquals(Rect.new(0, 0, 2, 1), rows.next())
        assertEquals(Pair(3, 3), rows.sizeHint())
        assertEquals(Rect.new(0, 3, 2, 1), rows.nextBack())
        assertEquals(Pair(2, 2), rows.sizeHint())
        assertEquals(Rect.new(0, 1, 2, 1), rows.next())
        assertEquals(Pair(1, 1), rows.sizeHint())
        assertEquals(Rect.new(0, 2, 2, 1), rows.nextBack())
        assertEquals(Pair(0, 0), rows.sizeHint())
        assertEquals(null, rows.next())
        assertEquals(Pair(0, 0), rows.sizeHint())
        assertEquals(null, rows.nextBack())
        assertEquals(Pair(0, 0), rows.sizeHint())
    }

    @Test
    fun columns() {
        val rect = Rect.new(0, 0, 3, 2)
        val columns = Columns.new(rect)
        assertEquals(Pair(3, 3), columns.sizeHint())
        assertEquals(Rect.new(0, 0, 1, 2), columns.next())
        assertEquals(Pair(2, 2), columns.sizeHint())
        assertEquals(Rect.new(1, 0, 1, 2), columns.next())
        assertEquals(Pair(1, 1), columns.sizeHint())
        assertEquals(Rect.new(2, 0, 1, 2), columns.next())
        assertEquals(Pair(0, 0), columns.sizeHint())
        assertEquals(null, columns.next())
        assertEquals(Pair(0, 0), columns.sizeHint())
        assertEquals(null, columns.nextBack())
        assertEquals(Pair(0, 0), columns.sizeHint())
    }

    @Test
    fun columnsBack() {
        val rect = Rect.new(0, 0, 3, 2)
        val columns = Columns.new(rect)
        assertEquals(Pair(3, 3), columns.sizeHint())
        assertEquals(Rect.new(2, 0, 1, 2), columns.nextBack())
        assertEquals(Pair(2, 2), columns.sizeHint())
        assertEquals(Rect.new(1, 0, 1, 2), columns.nextBack())
        assertEquals(Pair(1, 1), columns.sizeHint())
        assertEquals(Rect.new(0, 0, 1, 2), columns.nextBack())
        assertEquals(Pair(0, 0), columns.sizeHint())
        assertEquals(null, columns.nextBack())
        assertEquals(Pair(0, 0), columns.sizeHint())
        assertEquals(null, columns.next())
        assertEquals(Pair(0, 0), columns.sizeHint())
    }

    @Test
    fun columnsMeetInTheMiddle() {
        val rect = Rect.new(0, 0, 4, 2)
        val columns = Columns.new(rect)
        assertEquals(Pair(4, 4), columns.sizeHint())
        assertEquals(Rect.new(0, 0, 1, 2), columns.next())
        assertEquals(Pair(3, 3), columns.sizeHint())
        assertEquals(Rect.new(3, 0, 1, 2), columns.nextBack())
        assertEquals(Pair(2, 2), columns.sizeHint())
        assertEquals(Rect.new(1, 0, 1, 2), columns.next())
        assertEquals(Pair(1, 1), columns.sizeHint())
        assertEquals(Rect.new(2, 0, 1, 2), columns.nextBack())
        assertEquals(Pair(0, 0), columns.sizeHint())
        assertEquals(null, columns.next())
        assertEquals(Pair(0, 0), columns.sizeHint())
        assertEquals(null, columns.nextBack())
        assertEquals(Pair(0, 0), columns.sizeHint())
    }

    @Test
    fun positions() {
        val rect = Rect.new(0, 0, 2, 2)
        val positions = Positions.new(rect)
        assertEquals(Pair(4, 4), positions.sizeHint())
        assertEquals(Position(0, 0), positions.next())
        assertEquals(Pair(3, 3), positions.sizeHint())
        assertEquals(Position(1, 0), positions.next())
        assertEquals(Pair(2, 2), positions.sizeHint())
        assertEquals(Position(0, 1), positions.next())
        assertEquals(Pair(1, 1), positions.sizeHint())
        assertEquals(Position(1, 1), positions.next())
        assertEquals(Pair(0, 0), positions.sizeHint())
        assertEquals(null, positions.next())
        assertEquals(Pair(0, 0), positions.sizeHint())
    }

    @Test
    fun positionsZeroWidth() {
        val rect = Rect.new(0, 0, 0, 1)
        val positions = Positions.new(rect)
        assertEquals(Pair(0, 0), positions.sizeHint())
        assertEquals(null, positions.next())
        assertEquals(Pair(0, 0), positions.sizeHint())
    }

    @Test
    fun positionsZeroHeight() {
        val rect = Rect.new(0, 0, 1, 0)
        val positions = Positions.new(rect)
        assertEquals(Pair(0, 0), positions.sizeHint())
        assertEquals(null, positions.next())
        assertEquals(Pair(0, 0), positions.sizeHint())
    }

    @Test
    fun positionsZeroByZero() {
        val rect = Rect.new(0, 0, 0, 0)
        val positions = Positions.new(rect)
        assertEquals(Pair(0, 0), positions.sizeHint())
        assertEquals(null, positions.next())
        assertEquals(Pair(0, 0), positions.sizeHint())
    }
}

