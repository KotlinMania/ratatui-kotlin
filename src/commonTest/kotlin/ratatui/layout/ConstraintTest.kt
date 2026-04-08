package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals

class ConstraintTest {
    @Test
    fun default() {
        assertEquals(Constraint.Percentage(100), Constraint.default())
    }

    @Test
    fun toStringRendersVariants() {
        assertEquals("Percentage(50)", Constraint.Percentage(50).toString())
        assertEquals("Ratio(1, 2)", Constraint.Ratio(1u, 2u).toString())
        assertEquals("Length(10)", Constraint.Length(10).toString())
        assertEquals("Max(10)", Constraint.Max(10).toString())
        assertEquals("Min(10)", Constraint.Min(10).toString())
    }

    @Test
    fun fromLengths() {
        val expected = listOf(Constraint.Length(1), Constraint.Length(2), Constraint.Length(3))
        assertEquals(expected, Constraint.fromLengths(listOf(1, 2, 3)))
        assertEquals(expected, Constraint.fromLengths(intArrayOf(1, 2, 3).toList()))
    }

    @Test
    fun fromRatios() {
        val expected = listOf(
            Constraint.Ratio(1u, 4u),
            Constraint.Ratio(1u, 2u),
            Constraint.Ratio(1u, 4u)
        )
        assertEquals(expected, Constraint.fromRatios(listOf(Pair(1u, 4u), Pair(1u, 2u), Pair(1u, 4u))))
    }

    @Test
    fun fromPercentages() {
        val expected = listOf(
            Constraint.Percentage(25),
            Constraint.Percentage(50),
            Constraint.Percentage(25)
        )
        assertEquals(expected, Constraint.fromPercentages(listOf(25, 50, 25)))
    }

    @Test
    fun fromMaxes() {
        val expected = listOf(Constraint.Max(1), Constraint.Max(2), Constraint.Max(3))
        assertEquals(expected, Constraint.fromMaxes(listOf(1, 2, 3)))
    }

    @Test
    fun fromMins() {
        val expected = listOf(Constraint.Min(1), Constraint.Min(2), Constraint.Min(3))
        assertEquals(expected, Constraint.fromMins(listOf(1, 2, 3)))
    }

    @Test
    fun fromFills() {
        val expected = listOf(Constraint.Fill(1), Constraint.Fill(2), Constraint.Fill(3))
        assertEquals(expected, Constraint.fromFills(listOf(1, 2, 3)))
    }

    @Suppress("DEPRECATION")
    @Test
    fun apply() {
        assertEquals(0, Constraint.Percentage(0).apply(100))
        assertEquals(50, Constraint.Percentage(50).apply(100))
        assertEquals(100, Constraint.Percentage(100).apply(100))
        assertEquals(100, Constraint.Percentage(200).apply(100))
        assertEquals(100, Constraint.Percentage(Int.MAX_VALUE).apply(100))

        // 0/0 intentionally avoids a crash by returning 0.
        assertEquals(0, Constraint.Ratio(0u, 0u).apply(100))
        // 1/0 intentionally avoids a crash by returning 100% of the length.
        assertEquals(100, Constraint.Ratio(1u, 0u).apply(100))
        assertEquals(0, Constraint.Ratio(0u, 1u).apply(100))
        assertEquals(50, Constraint.Ratio(1u, 2u).apply(100))
        assertEquals(100, Constraint.Ratio(2u, 2u).apply(100))
        assertEquals(100, Constraint.Ratio(3u, 2u).apply(100))
        assertEquals(100, Constraint.Ratio(UInt.MAX_VALUE, 2u).apply(100))

        assertEquals(0, Constraint.Length(0).apply(100))
        assertEquals(50, Constraint.Length(50).apply(100))
        assertEquals(100, Constraint.Length(100).apply(100))
        assertEquals(100, Constraint.Length(200).apply(100))
        assertEquals(100, Constraint.Length(Int.MAX_VALUE).apply(100))

        assertEquals(0, Constraint.Max(0).apply(100))
        assertEquals(50, Constraint.Max(50).apply(100))
        assertEquals(100, Constraint.Max(100).apply(100))
        assertEquals(100, Constraint.Max(200).apply(100))
        assertEquals(100, Constraint.Max(Int.MAX_VALUE).apply(100))

        assertEquals(100, Constraint.Min(0).apply(100))
        assertEquals(100, Constraint.Min(50).apply(100))
        assertEquals(100, Constraint.Min(100).apply(100))
        assertEquals(200, Constraint.Min(200).apply(100))
        assertEquals(Int.MAX_VALUE, Constraint.Min(Int.MAX_VALUE).apply(100))
    }
}
