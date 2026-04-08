// port-lint: source ratatui-core/src/buffer/diff.rs
package ratatui.buffer

import ratatui.layout.Rect

/**
 * A zero-allocation iterator over the differences between two buffers of the same width.
 *
 * Transliteration of `ratatui_core::buffer::diff::BufferDiff`.
 *
 * Yields `(x, y, cell)` tuples for each cell in `next` that differs from the corresponding cell in
 * `prev`. Handles multi-width characters (including VS16 emoji trailing cells) and CellDiffOption
 * directives.
 */
class BufferDiff private constructor(
    private val next: List<Cell>,
    private val prev: List<Cell>,
    private val area: Rect
) : Iterator<BufferDiff.Item> {
    /**
     * Transliteration of Rust's iterator item: `(x, y, &Cell)`.
     *
     * Rust declares this as `type Item = (u16, u16, &'next Cell)` in the `Iterator` implementation.
     */
    data class Item(
        val x: Int,
        val y: Int,
        val cell: Cell
    )

    private var pos: Int = 0
    private var trailing: TrailingState? = null
    private var cached: Item? = null
    private var cacheValid: Boolean = false

    private data class TrailingState(
        var nextIndex: Int,
        val end: Int
    )

    companion object {
        fun new(prev: Buffer, next: Buffer): BufferDiff {
            check(prev.area.x == next.area.x && prev.area.y == next.area.y && prev.area.width == next.area.width) {
                "buffer areas must have the same x, y, and width: prev=${prev.area}, next=${next.area}"
            }

            val area = Rect(
                x = prev.area.x,
                y = prev.area.y,
                width = prev.area.width,
                height = minOf(prev.area.height, next.area.height)
            )

            return BufferDiff(next = next.content, prev = prev.content, area = area)
        }
    }

    private fun posOf(index: Int): Pair<Int, Int> {
        val w = area.width
        val x = (index % w) + area.x
        val y = (index / w) + area.y
        return Pair(x, y)
    }

    override fun hasNext(): Boolean {
        if (!cacheValid) {
            cached = nextDiffOrNull()
            cacheValid = true
        }
        return cached != null
    }

    override fun next(): Item {
        if (!hasNext()) throw NoSuchElementException()
        val result = cached ?: throw NoSuchElementException()
        cached = null
        cacheValid = false
        return result
    }

    private fun nextDiffOrNull(): Item? {
        // First, yield any pending VS16 trailing cells.
        trailing?.let { state ->
            while (state.nextIndex < state.end) {
                val j = state.nextIndex
                state.nextIndex += 1

                // Only emit update if the symbol has changed.
                if (!isSkip(next[j]) && prev[j].symbol() != next[j].symbol()) {
                    val (tx, ty) = posOf(j)
                    return Item(tx, ty, next[j])
                }
            }

            // Done with trailing cells; resume main loop past the wide character.
            pos = state.end
            trailing = null
        }

        val len = minOf(next.size, prev.size)
        while (pos < len) {
            val i = pos
            pos += 1

            val current = next[i]
            val previous = prev[i]

            when (val option = current.diffOption) {
                CellDiffOption.Skip -> Unit
                else -> {
                    if (isSkip(current)) {
                        // deprecated skip handling
                    } else if (option is CellDiffOption.ForcedWidth) {
                        val width = option.width.get().toInt()
                        pos += (width - 1).coerceAtLeast(0)
                    if (current != previous) {
                        val (x, y) = posOf(i)
                        return Item(x, y, current)
                    }
                } else {
                    if (current != previous) {
                        val cellWidth = current.cellWidth().toInt()
                        val containsVs16 = cellWidth > 1 && current.symbol().any { c -> c == '\uFE0F' }

                            if (containsVs16) {
                                val trailingEnd = (i + cellWidth).coerceAtMost(len)
                                trailing = TrailingState(nextIndex = i + 1, end = trailingEnd)
                            } else if (cellWidth > 1) {
                            pos += (cellWidth - 1).coerceAtLeast(0)
                        }

                        val (x, y) = posOf(i)
                        return Item(x, y, current)
                    } else {
                        // No change, but still need to skip width to keep position aligned.
                        val cellWidth = current.cellWidth().toInt()
                        if (cellWidth > 1) {
                                pos += (cellWidth - 1).coerceAtLeast(0)
                            }
                        }
                    }
                }
            }
        }

        return null
    }
}

@Suppress("DEPRECATION")
private fun isSkip(cell: Cell): Boolean {
    return cell.diffOption is CellDiffOption.Skip ||
        (cell.skip && cell.diffOption is CellDiffOption.None)
}
