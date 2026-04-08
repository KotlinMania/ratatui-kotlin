package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals

class PositionTest {
    @Test
    fun new() {
        val position = Position.new(1, 2)
        assertEquals(Position(x = 1, y = 2), position)
    }

    @Test
    fun fromTuple() {
        val position = Position.from(Pair(1, 2))
        assertEquals(Position(x = 1, y = 2), position)
    }

    @Test
    fun intoTuple() {
        val position = Position.new(1, 2)
        val (x, y) = position.toPair()
        assertEquals(1, x)
        assertEquals(2, y)
    }

    @Test
    fun fromRect() {
        val rect = Rect.new(1, 2, 3, 4)
        val position = Position.from(rect)
        assertEquals(Position(x = 1, y = 2), position)
    }

    @Test
    fun toStringRendersCoordinates() {
        val position = Position.new(1, 2)
        assertEquals("(1, 2)", position.toString())
    }

    @Test
    fun offsetMovesPosition() {
        val position = Position.new(2, 3).offset(Offset.new(5, 7))
        assertEquals(Position.new(7, 10), position)
    }

    @Test
    fun offsetClampsToBounds() {
        val position = Position.new(1, 1).offset(Offset.MAX)
        assertEquals(Position.MAX, position)
    }

    @Test
    fun addAndSubtractOffset() {
        val position = Position.new(10, 10) + Offset.new(-3, 4) - Offset.new(5, 20)
        assertEquals(Position.new(2, 0), position)
    }

    @Test
    fun addAssignAndSubAssignOffset() {
        val position = Position.new(5, 5)
        position += Offset.new(2, 3)
        position -= Offset.new(10, 1)
        assertEquals(Position.new(0, 7), position)
    }
}

