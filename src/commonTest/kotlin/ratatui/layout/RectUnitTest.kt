// port-lint: source layout/rect.rs (tests)
package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Inline unit tests transliterated from the `#[cfg(test)] mod tests` block in
 * `ratatui-core/src/layout/rect.rs`.
 *
 * Integration tests from `ratatui-core/tests/rect.rs` live in [RectTest].
 */
class RectUnitTest {
    @Test
    fun toStringFormat() {
        assertEquals("3x4+1+2", Rect.new(1, 2, 3, 4).toString())
    }

    @Test
    fun new() {
        assertEquals(
            Rect(x = 1, y = 2, width = 3, height = 4),
            Rect.new(1, 2, 3, 4),
        )
    }

    @Test
    fun area() {
        assertEquals(12L, Rect.new(1, 2, 3, 4).area())
    }

    @Test
    fun isEmpty() {
        assertFalse(Rect.new(1, 2, 3, 4).isEmpty())
        assertTrue(Rect.new(1, 2, 0, 4).isEmpty())
        assertTrue(Rect.new(1, 2, 3, 0).isEmpty())
    }

    @Test
    fun left() {
        assertEquals(1, Rect.new(1, 2, 3, 4).left())
    }

    @Test
    fun right() {
        assertEquals(4, Rect.new(1, 2, 3, 4).right())
    }

    @Test
    fun top() {
        assertEquals(2, Rect.new(1, 2, 3, 4).top())
    }

    @Test
    fun bottom() {
        assertEquals(6, Rect.new(1, 2, 3, 4).bottom())
    }

    @Test
    fun inner() {
        assertEquals(
            Rect.new(2, 4, 1, 0),
            Rect.new(1, 2, 3, 4).inner(Margin.new(1, 2)),
        )
    }

    @Test
    fun outer() {
        // enough space to grow on all sides
        assertEquals(
            Rect.new(80, 170, 50, 80),
            Rect.new(100, 200, 10, 20).outer(Margin.new(20, 30)),
        )

        // left / top saturation should truncate the size (10 less on left / top)
        assertEquals(
            Rect.new(0, 0, 40, 70),
            Rect.new(10, 20, 10, 20).outer(Margin.new(20, 30)),
        )

        // right / bottom saturation should truncate the size (10 less on bottom / right)
        assertEquals(
            Rect.new(Int.MAX_VALUE - 40, Int.MAX_VALUE - 70, 40, 70),
            Rect.new(Int.MAX_VALUE - 20, Int.MAX_VALUE - 40, 10, 20).outer(Margin.new(20, 30)),
        )
    }

    @Test
    fun offset() {
        assertEquals(
            Rect.new(6, 8, 3, 4),
            Rect.new(1, 2, 3, 4).offset(Offset(x = 5, y = 6)),
        )
    }

    @Test
    fun negativeOffset() {
        assertEquals(
            Rect.new(2, 2, 3, 4),
            Rect.new(4, 3, 3, 4).offset(Offset(x = -2, y = -1)),
        )
    }

    @Test
    fun negativeOffsetSaturate() {
        assertEquals(
            Rect.new(0, 0, 3, 4),
            Rect.new(1, 2, 3, 4).offset(Offset(x = -5, y = -6)),
        )
    }

    /**
     * Offsets a [Rect] making it go outside the upper bound, it should keep its size. The Rust
     * upstream uses `u16::MAX`; the Kotlin operator clamps to `UShort.MAX_VALUE.toInt()` so we
     * mirror that bound here.
     */
    @Test
    fun offsetSaturateMax() {
        val uMax = UShort.MAX_VALUE.toInt()
        assertEquals(
            Rect.new(uMax - 100, uMax - 100, 100, 100),
            Rect.new(uMax - 500, uMax - 500, 100, 100)
                .offset(Offset(x = 1000, y = 1000)),
        )
    }

    @Test
    fun union() {
        assertEquals(
            Rect.new(1, 2, 5, 6),
            Rect.new(1, 2, 3, 4).union(Rect.new(2, 3, 4, 5)),
        )
    }

    @Test
    fun intersection() {
        assertEquals(
            Rect.new(2, 3, 2, 3),
            Rect.new(1, 2, 3, 4).intersection(Rect.new(2, 3, 4, 5)),
        )
    }

    @Test
    fun intersectionUnderflow() {
        assertEquals(
            Rect.new(4, 4, 0, 0),
            Rect.new(1, 1, 2, 2).intersection(Rect.new(4, 4, 2, 2)),
        )
    }

    @Test
    fun intersects() {
        assertTrue(Rect.new(1, 2, 3, 4).intersects(Rect.new(2, 3, 4, 5)))
        assertFalse(Rect.new(1, 2, 3, 4).intersects(Rect.new(5, 6, 7, 8)))
    }

    @Test
    fun mutualIntersect() {
        val cases = listOf(
            // corner
            Rect.new(0, 0, 10, 10) to Rect.new(10, 10, 20, 20),
            // edge
            Rect.new(0, 0, 10, 10) to Rect.new(10, 0, 20, 10),
            // no_intersect
            Rect.new(0, 0, 10, 10) to Rect.new(11, 11, 20, 20),
            // contains
            Rect.new(0, 0, 20, 20) to Rect.new(5, 5, 10, 10),
        )
        for ((rect0, rect1) in cases) {
            assertEquals(rect0.intersection(rect1), rect1.intersection(rect0))
            assertEquals(rect0.intersects(rect1), rect1.intersects(rect0))
        }
    }

    // the bounds of this rect are x: [1..=3], y: [2..=5]
    @Test
    fun contains() {
        val rect = Rect.new(1, 2, 3, 4)
        val cases = listOf(
            Triple(rect, Position(x = 1, y = 2), true),  // inside_top_left
            Triple(rect, Position(x = 3, y = 2), true),  // inside_top_right
            Triple(rect, Position(x = 1, y = 5), true),  // inside_bottom_left
            Triple(rect, Position(x = 3, y = 5), true),  // inside_bottom_right
            Triple(rect, Position(x = 0, y = 2), false), // outside_left
            Triple(rect, Position(x = 4, y = 2), false), // outside_right
            Triple(rect, Position(x = 1, y = 1), false), // outside_top
            Triple(rect, Position(x = 1, y = 6), false), // outside_bottom
            Triple(rect, Position(x = 0, y = 1), false), // outside_top_left
            Triple(rect, Position(x = 4, y = 6), false), // outside_bottom_right
        )
        for ((r, position, expected) in cases) {
            assertEquals(
                expected,
                r.contains(position),
                "rect: $r, position: $position",
            )
        }
    }

    @Test
    fun sizeTruncation() {
        assertEquals(
            Rect(x = Int.MAX_VALUE - 100, y = Int.MAX_VALUE - 1000, width = 100, height = 1000),
            Rect.new(Int.MAX_VALUE - 100, Int.MAX_VALUE - 1000, 200, 2000),
        )
    }

    @Test
    fun sizePreservation() {
        assertEquals(
            Rect(x = Int.MAX_VALUE - 100, y = Int.MAX_VALUE - 1000, width = 100, height = 1000),
            Rect.new(Int.MAX_VALUE - 100, Int.MAX_VALUE - 1000, 100, 1000),
        )
    }

    @Test
    fun resizeUpdatesSize() {
        val rect = Rect.new(10, 20, 5, 5).resize(Size.new(30, 40))
        assertEquals(Rect.new(10, 20, 30, 40), rect)
    }

    @Test
    fun resizeClampsAtBounds() {
        val rect = Rect.new(Int.MAX_VALUE - 2, Int.MAX_VALUE - 3, 1, 1).resize(Size.new(10, 10))
        assertEquals(Rect.new(Int.MAX_VALUE - 2, Int.MAX_VALUE - 3, 2, 3), rect)
    }

    /**
     * The Rust upstream test verifies that `Rect`'s methods can be evaluated in const context. Kotlin
     * has no `const fn`; we keep the test by evaluating the same chain at runtime to confirm the
     * methods stay total and pure.
     */
    @Test
    fun canBeConst() {
        val rect = Rect(x = 0, y = 0, width = 10, height = 10)
        val area = rect.area()
        val left = rect.left()
        val right = rect.right()
        val top = rect.top()
        val bottom = rect.bottom()
        assertEquals(100L, area)
        assertEquals(0, left)
        assertEquals(10, right)
        assertEquals(0, top)
        assertEquals(10, bottom)
        assertTrue(rect.intersects(rect))
    }

    @Test
    fun split() {
        val areas = Layout
            .horizontal(listOf(Constraint.Percentage(50), Constraint.Percentage(50)))
            .split(Rect.new(0, 0, 2, 1))
        assertEquals(2, areas.size)
        assertEquals(Rect.new(0, 0, 1, 1), areas[0])
        assertEquals(Rect.new(1, 0, 1, 1), areas[1])
    }

    /**
     * The Rust test is `#[should_panic(expected = "invalid number of rects")]` — destructuring
     * a fixed-size array `[_a, _b, _c]` from a 2-element split. Kotlin has no compile-time array
     * length, so we mirror the failure mode with an out-of-bounds index access.
     */
    @Test
    fun splitInvalidNumberOfRecs() {
        val layout = Layout.horizontal(
            listOf(Constraint.Percentage(50), Constraint.Percentage(50)),
        )
        val areas = layout.split(Rect.new(0, 0, 2, 1))
        assertFailsWith<IndexOutOfBoundsException> {
            areas[2]
        }
    }

    @Test
    fun clamp() {
        val other = Rect.new(10, 10, 100, 100)
        val cases = listOf(
            // inside
            Rect.new(20, 20, 10, 10) to Rect.new(20, 20, 10, 10),
            // up_left
            Rect.new(5, 5, 10, 10) to Rect.new(10, 10, 10, 10),
            // up
            Rect.new(20, 5, 10, 10) to Rect.new(20, 10, 10, 10),
            // up_right
            Rect.new(105, 5, 10, 10) to Rect.new(100, 10, 10, 10),
            // left
            Rect.new(5, 20, 10, 10) to Rect.new(10, 20, 10, 10),
            // right
            Rect.new(105, 20, 10, 10) to Rect.new(100, 20, 10, 10),
            // down_left
            Rect.new(5, 105, 10, 10) to Rect.new(10, 100, 10, 10),
            // down
            Rect.new(20, 105, 10, 10) to Rect.new(20, 100, 10, 10),
            // down_right
            Rect.new(105, 105, 10, 10) to Rect.new(100, 100, 10, 10),
            // too_wide
            Rect.new(5, 20, 200, 10) to Rect.new(10, 20, 100, 10),
            // too_tall
            Rect.new(20, 5, 10, 200) to Rect.new(20, 10, 10, 100),
            // too_large
            Rect.new(0, 0, 200, 200) to Rect.new(10, 10, 100, 100),
        )
        for ((rect, expected) in cases) {
            assertEquals(expected, rect.clamp(other))
        }
    }

    @Test
    fun rows() {
        val area = Rect.new(0, 0, 3, 2)
        val rows = area.rows().asSequence().toList()

        val expectedRows = listOf(Rect.new(0, 0, 3, 1), Rect.new(0, 1, 3, 1))

        assertEquals(expectedRows, rows)
    }

    @Test
    fun columns() {
        val area = Rect.new(0, 0, 3, 2)
        val columns = area.columns().asSequence().toList()

        val expectedColumns = listOf(
            Rect.new(0, 0, 1, 2),
            Rect.new(1, 0, 1, 2),
            Rect.new(2, 0, 1, 2),
        )

        assertEquals(expectedColumns, columns)
    }

    @Test
    fun asPosition() {
        val rect = Rect.new(1, 2, 3, 4)
        val position = rect.asPosition()
        assertEquals(1, position.x)
        assertEquals(2, position.y)
    }

    @Test
    fun asSize() {
        assertEquals(
            Size(width = 3, height = 4),
            Rect.new(1, 2, 3, 4).asSize(),
        )
    }

    @Test
    fun fromPositionAndSize() {
        val position = Position(x = 1, y = 2)
        val size = Size(width = 3, height = 4)
        assertEquals(
            Rect(x = 1, y = 2, width = 3, height = 4),
            Rect.from(Pair(position, size)),
        )
    }

    @Test
    fun fromSize() {
        val size = Size(width = 3, height = 4)
        assertEquals(
            Rect(x = 0, y = 0, width = 3, height = 4),
            Rect.from(size),
        )
    }

    @Test
    fun centeredHorizontally() {
        val rect = Rect.new(0, 0, 5, 5)
        assertEquals(
            Rect.new(1, 0, 3, 5),
            rect.centeredHorizontally(Constraint.Length(3)),
        )
    }

    @Test
    fun centeredVertically() {
        val rect = Rect.new(0, 0, 5, 5)
        assertEquals(
            Rect.new(0, 2, 5, 1),
            rect.centeredVertically(Constraint.Length(1)),
        )
    }

    @Test
    fun centered() {
        val rect = Rect.new(0, 0, 5, 5)
        assertEquals(
            Rect.new(1, 2, 3, 1),
            rect.centered(Constraint.Length(3), Constraint.Length(1)),
        )
    }

    @Test
    fun layout() {
        val layout = Layout.horizontal(listOf(Constraint.Length(3), Constraint.Min(0)))

        val areas1 = Rect.new(0, 0, 10, 10).layout(layout)
        assertEquals(Rect.new(0, 0, 3, 10), areas1[0])
        assertEquals(Rect.new(3, 0, 7, 10), areas1[1])

        val areas2 = Rect.new(0, 0, 10, 10).layout(layout)
        assertEquals(Rect.new(0, 0, 3, 10), areas2[0])
        assertEquals(Rect.new(3, 0, 7, 10), areas2[1])
    }

    @Test
    fun layoutInvalidNumberOfRects() {
        val layout = Layout.horizontal(listOf(Constraint.Length(1)))
        val areas = Rect.new(0, 0, 10, 10).layout(layout)
        assertFailsWith<IndexOutOfBoundsException> {
            areas[2]
        }
    }

    @Test
    fun layoutVec() {
        val layout = Layout.horizontal(listOf(Constraint.Length(3), Constraint.Min(0)))

        val areas = Rect.new(0, 0, 10, 10).layoutVec(layout)
        assertEquals(Rect.new(0, 0, 3, 10), areas[0])
        assertEquals(Rect.new(3, 0, 7, 10), areas[1])
    }

    @Test
    fun tryLayout() {
        val layout = Layout.horizontal(listOf(Constraint.Length(3), Constraint.Min(0)))

        val areas1 = Rect.new(0, 0, 10, 10).tryLayout(layout).getOrThrow()
        assertEquals(Rect.new(0, 0, 3, 10), areas1[0])
        assertEquals(Rect.new(3, 0, 7, 10), areas1[1])

        val areas2 = Rect.new(0, 0, 10, 10).tryLayout(layout).getOrThrow()
        assertEquals(Rect.new(0, 0, 3, 10), areas2[0])
        assertEquals(Rect.new(3, 0, 7, 10), areas2[1])
    }

    /**
     * The Rust test calls `try_layout::<3>(...).unwrap_err()` — the `try_into` of a 1-element slice
     * into a 3-element array fails. In Kotlin without const generics, `tryLayout` always succeeds,
     * so we mirror the original failure surface by accessing an out-of-bounds element afterwards.
     */
    @Test
    fun tryLayoutInvalidNumberOfRects() {
        val layout = Layout.horizontal(listOf(Constraint.Length(1)))
        val areas = Rect.new(0, 0, 10, 10).tryLayout(layout).getOrThrow()
        assertFailsWith<IndexOutOfBoundsException> {
            areas[2]
        }
    }
}
