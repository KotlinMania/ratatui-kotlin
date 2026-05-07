// port-lint: source ratatui-macros/tests/macros.rs
package ratatui_macros

import ratatui.layout.Constraint
import ratatui.layout.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the Kotlin transliteration of the `ratatui-macros` crate.
 *
 * Transliteration target: `ratatui-macros/tests/macros.rs`.
 */
class MacrosTest {
    @Test
    fun layoutConstraintsMacro() {
        val rect = Rect(
            x = 0,
            y = 0,
            width = 10,
            height = 10
        )

        run {
            val parts = vertical(eq(7), le(3)).split(rect)
            assertEquals(Rect.new(0, 0, 10, 7), parts[0])
            assertEquals(Rect.new(0, 7, 10, 3), parts[1])
        }

        run {
            val parts = horizontal(eq(7), le(3)).split(rect)
            assertEquals(Rect.new(0, 0, 7, 10), parts[0])
            assertEquals(Rect.new(7, 0, 3, 10), parts[1])
        }

        run {
            val one = 1
            val two = 2
            val ten = 10
            val zero = 0
            val parts = horizontal(
                eq(one),
                ge(one),
                le(one),
                ratio(1, two),
                percent(ten),
                ge(zero)
            ).split(rect)

            assertEquals(Rect.new(0, 0, 1, 10), parts[0])
            assertEquals(Rect.new(1, 0, 1, 10), parts[1])
            assertEquals(Rect.new(2, 0, 1, 10), parts[2])
            assertEquals(Rect.new(3, 0, 5, 10), parts[3])
            assertEquals(Rect.new(8, 0, 1, 10), parts[4])
            assertEquals(Rect.new(9, 0, 1, 10), parts[5])
        }

        run {
            val one = 1
            val two = 2
            val ten = 10
            val zero = 0
            val parts = horizontal(
                eq(one * one),
                ge(one + zero),
                le(one - zero),
                ratio(1, two),
                percent(ten),
                ge(zero)
            ).split(rect)

            assertEquals(Rect.new(0, 0, 1, 10), parts[0])
            assertEquals(Rect.new(1, 0, 1, 10), parts[1])
            assertEquals(Rect.new(2, 0, 1, 10), parts[2])
            assertEquals(Rect.new(3, 0, 5, 10), parts[3])
            assertEquals(Rect.new(8, 0, 1, 10), parts[4])
            assertEquals(Rect.new(9, 0, 1, 10), parts[5])
        }

        run {
            val list = constraints(ge(0), eq(1), le(5), percent(10), ratio(1, 2))
            assertEquals(Constraint.Min(0), list[0])
            assertEquals(Constraint.Length(1), list[1])
            assertEquals(Constraint.Max(5), list[2])
            assertEquals(Constraint.Percentage(10), list[3])
            assertEquals(Constraint.Ratio(1u, 2u), list[4])
        }

        run {
            val list = constraints(ge(0), 5)
            for (c in list) assertEquals(Constraint.Min(0), c)
        }

        run {
            val list = constraints(le(0), 5)
            for (c in list) assertEquals(Constraint.Max(0), c)
        }

        run {
            val list = constraints(eq(0), 2)
            for (c in list) assertEquals(Constraint.Length(0), c)
        }

        run {
            val list = constraints(percent(50), 2)
            for (c in list) assertEquals(Constraint.Percentage(50), c)
        }

        run {
            val list = constraints(ratio(1, 2), 2)
            for (c in list) assertEquals(Constraint.Ratio(1u, 2u), c)
        }
    }

    @Test
    fun fails() {
        // Kotlin cannot mirror Rust macro parsing errors exactly, but we keep the same intent:
        // invalid uses should fail fast and loudly.
        assertFailsWith<IllegalArgumentException> {
            span(format = "hello", args = arrayOf("hello world"))
        }
    }
}
