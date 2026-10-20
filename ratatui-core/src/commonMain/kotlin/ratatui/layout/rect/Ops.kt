// port-lint: source layout/rect/ops.rs
package ratatui.layout.rect

import ratatui.layout.Offset
import ratatui.layout.Rect

/**
 * Negates the offset.
 *
 * # Panics
 *
 * Panics if the negated value overflows (i.e. `x` or `y` is `Int.MIN_VALUE`).
 */
operator fun Offset.unaryMinus(): Offset = Offset(
    x = -this.x,
    y = -this.y,
)

/**
 * Moves the rect by an offset without changing its size.
 *
 * If the offset would move any of the rect edges outside the bounds of `u16`, the
 * rect position is clamped to the nearest edge.
 */
operator fun Rect.plus(offset: Offset): Rect {
    val maxX = UShort.MAX_VALUE.toInt() - this.width
    val maxY = UShort.MAX_VALUE.toInt() - this.height
    val x = (this.x.toLong() + offset.x.toLong())
        .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
        .toInt()
        .coerceIn(0, maxX)
    val y = (this.y.toLong() + offset.y.toLong())
        .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
        .toInt()
        .coerceIn(0, maxY)
    return this.copy(x = x, y = y)
}

/**
 * Moves the rect by an offset without changing its size.
 *
 * If the offset would move any of the rect edges outside the bounds of `u16`, the
 * rect position is clamped to the nearest edge.
 */
operator fun Offset.plus(rect: Rect): Rect = rect + this

/**
 * Subtracts an offset from the rect without changing its size.
 *
 * If the offset would move any of the rect edges outside the bounds of `u16`, the
 * rect position is clamped to the nearest
 */
operator fun Rect.minus(offset: Offset): Rect {
    // Note this cannot be simplified to `this + -offset` because `Offset.MIN` would overflow
    val maxX = UShort.MAX_VALUE.toInt() - this.width
    val maxY = UShort.MAX_VALUE.toInt() - this.height
    val x = (this.x.toLong() - offset.x.toLong())
        .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
        .toInt()
        .coerceIn(0, maxX)
    val y = (this.y.toLong() - offset.y.toLong())
        .coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())
        .toInt()
        .coerceIn(0, maxY)
    return this.copy(x = x, y = y)
}
