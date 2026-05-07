// port-lint: source ratatui-core/src/layout/rect/ops.rs
package ratatui.layout

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RectOpsTest {
    @Test
    fun addOffset() {
        val cases =
            listOf(
                Triple(Rect.new(3, 4, 5, 6), Offset.ZERO, Rect.new(3, 4, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(1, 2), Rect.new(4, 6, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(-1, -2), Rect.new(2, 2, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.MIN, Rect.new(0, 0, 5, 6)),
                Triple(
                    Rect.new(3, 4, 5, 6),
                    Offset.MAX,
                    Rect.new(UShort.MAX_VALUE.toInt() - 5, UShort.MAX_VALUE.toInt() - 6, 5, 6)
                ),
            )

        for ((rect, offset, expected) in cases) {
            assertEquals(expected, rect + offset)
            assertEquals(expected, offset + rect)
        }
    }

    @Test
    fun subOffset() {
        val cases =
            listOf(
                Triple(Rect.new(3, 4, 5, 6), Offset.ZERO, Rect.new(3, 4, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(1, 2), Rect.new(2, 2, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(-1, -2), Rect.new(4, 6, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.MAX, Rect.new(0, 0, 5, 6)),
                Triple(
                    Rect.new(3, 4, 5, 6),
                    -Offset.MAX,
                    Rect.new(UShort.MAX_VALUE.toInt() - 5, UShort.MAX_VALUE.toInt() - 6, 5, 6)
                ),
            )

        for ((rect, offset, expected) in cases) {
            assertEquals(expected, rect - offset)
        }
    }

    @Test
    fun addAssignOffset() {
        val cases =
            listOf(
                Triple(Rect.new(3, 4, 5, 6), Offset.ZERO, Rect.new(3, 4, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(1, 2), Rect.new(4, 6, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(-1, -2), Rect.new(2, 2, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.MIN, Rect.new(0, 0, 5, 6)),
                Triple(
                    Rect.new(3, 4, 5, 6),
                    Offset.MAX,
                    Rect.new(UShort.MAX_VALUE.toInt() - 5, UShort.MAX_VALUE.toInt() - 6, 5, 6)
                ),
            )

        for ((initial, offset, expected) in cases) {
            var rect = initial
            rect += offset
            assertEquals(expected, rect)
        }
    }

    @Test
    fun subAssignOffset() {
        val cases =
            listOf(
                Triple(Rect.new(3, 4, 5, 6), Offset.ZERO, Rect.new(3, 4, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(1, 2), Rect.new(2, 2, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.new(-1, -2), Rect.new(4, 6, 5, 6)),
                Triple(Rect.new(3, 4, 5, 6), Offset.MAX, Rect.new(0, 0, 5, 6)),
                Triple(
                    Rect.new(3, 4, 5, 6),
                    -Offset.MAX,
                    Rect.new(UShort.MAX_VALUE.toInt() - 5, UShort.MAX_VALUE.toInt() - 6, 5, 6)
                ),
            )

        for ((initial, offset, expected) in cases) {
            var rect = initial
            rect -= offset
            assertEquals(expected, rect)
        }
    }

    @Test
    fun offsetNegationPanicsOnOverflow() {
        assertFailsWith<ArithmeticException> { -Offset.MIN }
    }
}

