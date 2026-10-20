// port-lint: source ratatui-core/src/layout/rect/ops.rs
package ratatui.layout

private fun saturatingAddInt(a: Int, b: Int): Int {
    val sum = a.toLong() + b.toLong()
    return when {
        sum > Int.MAX_VALUE.toLong() -> Int.MAX_VALUE
        sum < Int.MIN_VALUE.toLong() -> Int.MIN_VALUE
        else -> sum.toInt()
    }
}

private fun saturatingSubInt(a: Int, b: Int): Int {
    val diff = a.toLong() - b.toLong()
    return when {
        diff > Int.MAX_VALUE.toLong() -> Int.MAX_VALUE
        diff < Int.MIN_VALUE.toLong() -> Int.MIN_VALUE
        else -> diff.toInt()
    }
}

/**
 * Negates the offset.
 *
 * Transliteration of `impl Neg for Offset`.
 *
 * Panics in Rust if the negated value overflows (i.e. `x` or `y` is `i32::MIN`). In Kotlin we
 * throw an [ArithmeticException] in that case.
 */
operator fun Offset.unaryMinus(): Offset {
    if (x == Int.MIN_VALUE || y == Int.MIN_VALUE) {
        throw ArithmeticException("negation overflow for Offset(x=$x, y=$y)")
    }
    return Offset(x = -x, y = -y)
}

/**
 * Moves the rect by an offset without changing its size.
 *
 * Transliteration of `impl Add<Offset> for Rect`.
 */
operator fun Rect.plus(offset: Offset): Rect {
    val maxX = (UShort.MAX_VALUE.toInt() - width).coerceAtLeast(0)
    val maxY = (UShort.MAX_VALUE.toInt() - height).coerceAtLeast(0)
    val newX = saturatingAddInt(x, offset.x).coerceIn(0, maxX)
    val newY = saturatingAddInt(y, offset.y).coerceIn(0, maxY)
    return copy(x = newX, y = newY)
}

/**
 * Moves the rect by an offset without changing its size.
 *
 * Transliteration of `impl Add<Rect> for Offset`.
 */
operator fun Offset.plus(rect: Rect): Rect = rect + this

/**
 * Subtracts an offset from the rect without changing its size.
 *
 * Transliteration of `impl Sub<Offset> for Rect`.
 */
operator fun Rect.minus(offset: Offset): Rect {
    val maxX = (UShort.MAX_VALUE.toInt() - width).coerceAtLeast(0)
    val maxY = (UShort.MAX_VALUE.toInt() - height).coerceAtLeast(0)
    val newX = saturatingSubInt(x, offset.x).coerceIn(0, maxX)
    val newY = saturatingSubInt(y, offset.y).coerceIn(0, maxY)
    return copy(x = newX, y = newY)
}

// Note: Rust implements `AddAssign`/`SubAssign` for `Rect`. In Kotlin, `+=`/`-=` works via `plus`
// and `minus` when used with a `var` and is lowered to `rect = rect + offset` / `rect = rect - offset`.
