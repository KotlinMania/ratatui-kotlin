// port-lint: source ratatui-core/src/layout/rect/iter.rs
package ratatui.layout.rect

import ratatui.layout.Position
import ratatui.layout.Rect

/**
 * An iterator over rows within a Rect.
 *
 * Transliteration of `ratatui_core::layout::rect::iter::Rows`.
 */
class Rows private constructor(
    private val rect: Rect,
    private var currentRowFwd: Int,
    private var currentRowBack: Int
) {
    companion object {
        fun new(rect: Rect): Rows = Rows(
            rect = rect,
            currentRowFwd = rect.y,
            currentRowBack = rect.bottom()
        )
    }

    fun next(): Rect? {
        if (currentRowFwd >= currentRowBack) return null
        val row = Rect.new(rect.x, currentRowFwd, rect.width, 1)
        currentRowFwd += 1
        return row
    }

    fun nextBack(): Rect? {
        if (currentRowBack <= currentRowFwd) return null
        currentRowBack -= 1
        return Rect.new(rect.x, currentRowBack, rect.width, 1)
    }

    fun sizeHint(): Pair<Int, Int?> {
        val startCount = (currentRowFwd - rect.top()).coerceAtLeast(0)
        val endCount = (rect.bottom() - currentRowBack).coerceAtLeast(0)
        val count = (rect.height - startCount - endCount).coerceAtLeast(0)
        return Pair(count, count)
    }
}

/**
 * An iterator over columns within a Rect.
 *
 * Transliteration of `ratatui_core::layout::rect::iter::Columns`.
 */
class Columns private constructor(
    private val rect: Rect,
    private var currentColumnFwd: Int,
    private var currentColumnBack: Int
) {
    companion object {
        fun new(rect: Rect): Columns = Columns(
            rect = rect,
            currentColumnFwd = rect.x,
            currentColumnBack = rect.right()
        )
    }

    fun next(): Rect? {
        if (currentColumnFwd >= currentColumnBack) return null
        val column = Rect.new(currentColumnFwd, rect.y, 1, rect.height)
        currentColumnFwd += 1
        return column
    }

    fun nextBack(): Rect? {
        if (currentColumnBack <= currentColumnFwd) return null
        currentColumnBack -= 1
        return Rect.new(currentColumnBack, rect.y, 1, rect.height)
    }

    fun sizeHint(): Pair<Int, Int?> {
        val startCount = (currentColumnFwd - rect.left()).coerceAtLeast(0)
        val endCount = (rect.right() - currentColumnBack).coerceAtLeast(0)
        val count = (rect.width - startCount - endCount).coerceAtLeast(0)
        return Pair(count, count)
    }
}

/**
 * An iterator over positions within a Rect.
 *
 * The iterator yields positions in row-major order.
 *
 * Transliteration of `ratatui_core::layout::rect::iter::Positions`.
 */
class Positions private constructor(
    private val rect: Rect,
    private var currentPosition: Position
) {
    companion object {
        fun new(rect: Rect): Positions = Positions(
            rect = rect,
            currentPosition = Position(rect.x, rect.y)
        )
    }

    fun next(): Position? {
        if (!rect.contains(currentPosition)) return null
        val position = currentPosition
        currentPosition = Position(currentPosition.x + 1, currentPosition.y)
        if (currentPosition.x >= rect.right()) {
            currentPosition = Position(rect.x, currentPosition.y + 1)
        }
        return position
    }

    fun sizeHint(): Pair<Int, Int?> {
        val rowCount = (rect.bottom() - currentPosition.y).coerceAtLeast(0)
        if (rowCount == 0) return Pair(0, 0)
        val columnCount = (rect.right() - currentPosition.x).coerceAtLeast(0)
        val count = ((rowCount - 1) * rect.width + columnCount).coerceAtLeast(0)
        return Pair(count, count)
    }
}
