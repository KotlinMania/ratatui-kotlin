// port-lint: source ratatui-core/src/backend/test.rs
package ratatui.backend

import ratatui.buffer.Buffer
import ratatui.buffer.Cell
import ratatui.buffer.cellWidth
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.layout.Size
import ratatui.text.toLine

/**
 * A [Backend] implementation used for integration testing that renders to an in-memory buffer.
 *
 * Transliteration of `ratatuiCore::backend::TestBackend`.
 */
class TestBackend private constructor(
    private val buffer: Buffer,
    private val scrollback: Buffer,
    private var cursor: Boolean,
    private var pos: Position
) : Backend {
    companion object {
        /** Creates a new [TestBackend] with the specified width and height. */
        fun new(width: Int, height: Int): TestBackend = TestBackend(
            buffer = Buffer.empty(Rect.new(0, 0, width, height)),
            scrollback = Buffer.empty(Rect.new(0, 0, width, 0)),
            cursor = false,
            pos = Position.ORIGIN
        )

        /** Creates a new [TestBackend] with the specified lines as the initial screen state. */
        fun withLines(vararg lines: String): TestBackend {
            val buffer = Buffer.withLines(*lines)
            val scrollback = Buffer.empty(
                Rect(
                    x = 0,
                    y = 0,
                    width = buffer.area.width,
                    height = 0
                )
            )
            return TestBackend(
                buffer = buffer,
                scrollback = scrollback,
                cursor = false,
                pos = Position.ORIGIN
            )
        }
    }

    /** Returns a reference to the internal buffer of the [TestBackend]. */
    fun buffer(): Buffer = buffer

    /** Returns a reference to the internal scrollback buffer of the [TestBackend]. */
    fun scrollback(): Buffer = scrollback

    /** Returns whether the cursor is visible. */
    fun cursorVisible(): Boolean = cursor

    /** Returns the current cursor position. */
    fun cursorPosition(): Position = pos

    /** Resizes the [TestBackend] to the specified width and height. */
    fun resize(width: Int, height: Int) {
        buffer.resize(Rect.new(0, 0, width, height))
        val scrollbackHeight = scrollback.area.height
        scrollback.resize(Rect.new(0, 0, width, scrollbackHeight))
    }

    /** Asserts that the backend buffer is equal to [expected]. */
    fun assertBuffer(expected: Buffer) {
        if (buffer != expected) {
            error(
                "buffer mismatch\n" +
                    "expected:\n${bufferView(expected)}\n" +
                    "actual:\n${bufferView(buffer)}"
            )
        }
    }

    /** Asserts that the backend scrollback buffer is equal to [expected]. */
    fun assertScrollback(expected: Buffer) {
        if (scrollback != expected) {
            error(
                "scrollback mismatch\n" +
                    "expected:\n${bufferView(expected)}\n" +
                    "actual:\n${bufferView(scrollback)}"
            )
        }
    }

    /** Asserts that the backend scrollback buffer is empty. */
    fun assertScrollbackEmpty() {
        val expected = Buffer.empty(
            Rect(
                x = 0,
                y = 0,
                width = scrollback.area.width,
                height = 0
            )
        )
        assertScrollback(expected)
    }

    /** Asserts that the backend buffer is equal to the expected string lines. */
    fun assertBufferLines(lines: List<String>) = assertBuffer(Buffer.withLines(lines.map { it.toLine() }))

    /** Asserts that the backend buffer is equal to the expected string lines. */
    fun assertBufferLines(vararg lines: String) = assertBuffer(Buffer.withLines(*lines))

    /** Asserts that the backend scrollback buffer is equal to the expected string lines. */
    fun assertScrollbackLines(vararg lines: String) = assertScrollback(Buffer.withLines(*lines))

    /** Asserts that the backend scrollback buffer is equal to the expected string lines. */
    fun assertScrollbackLines(lines: List<String>) = assertScrollback(Buffer.withLines(lines.map { it.toLine() }))

    /**
     * Asserts that the cursor position equals [position].
     *
     * @throws IllegalStateException When they are not equal.
     */
    fun assertCursorPosition(position: Position) {
        check(pos == position) { "Cursor position mismatch: expected $position but was $pos" }
    }

    override fun draw(content: Iterator<ratatui.buffer.BufferDiff.Item>) {
        while (content.hasNext()) {
            val item = content.next()
            val index = buffer.indexOf(item.x, item.y)
            buffer.mutableContent[index] = item.cell.clone()
        }
    }

    override fun hideCursor() {
        cursor = false
    }

    override fun showCursor() {
        cursor = true
    }

    override fun getCursorPosition(): Position = pos

    override fun setCursorPosition(position: Position) {
        pos = position
    }

    override fun clear() {
        buffer.reset()
    }

    override fun clearRegion(clearType: ClearType) {
        val width = buffer.area.width
        val height = buffer.area.height
        val (curX, curY) = pos

        val cellRegion = when (clearType) {
            ClearType.All -> 0 until buffer.mutableContent.size
            ClearType.AfterCursor -> buffer.indexOf(curX, curY) until buffer.mutableContent.size
            ClearType.BeforeCursor -> 0..buffer.indexOf(curX, curY)
            ClearType.CurrentLine -> (curY * width) until ((curY + 1) * width)
            ClearType.UntilNewLine -> buffer.indexOf(curX, curY) until ((curY + 1) * width)
        }

        for (i in cellRegion) {
            buffer.mutableContent[i] = Cell.EMPTY.clone()
        }
    }

    /**
     * Inserts `n` line breaks at the current cursor position.
     *
     * This is a transliteration of `TestBackend::appendLines`.
     */
    override fun appendLines(n: UShort) {
        val lineCount = n.toInt()
        if (lineCount <= 0) return

        val curX = pos.x
        val curY = pos.y
        val width = buffer.area.width
        val height = buffer.area.height

        val newCursorX = (curX + 1).coerceAtMost((width - 1).coerceAtLeast(0))

        val maxY = (height - 1).coerceAtLeast(0)
        val linesAfterCursor = (maxY - curY).coerceAtLeast(0)

        if (lineCount > linesAfterCursor) {
            val scrollBy = lineCount - linesAfterCursor
            val maxScrollCells = width * scrollBy
            val cellsToScrollback = minOf(buffer.mutableContent.size, maxScrollCells)

            val removed = buffer.mutableContent.subList(0, cellsToScrollback).map { it.clone() }
            for (i in 0 until cellsToScrollback) {
                buffer.mutableContent[i] = Cell.EMPTY
            }
            appendToScrollback(scrollback, removed)

            if (cellsToScrollback > 0) {
                val rotated = buffer.mutableContent.drop(cellsToScrollback) + buffer.mutableContent.take(cellsToScrollback)
                buffer.mutableContent.clear()
                buffer.mutableContent.addAll(rotated)
            }

            val extra = (width * scrollBy) - cellsToScrollback
            if (extra > 0) {
                appendToScrollback(scrollback, List(extra) { Cell.EMPTY })
            }
        }

        val newCursorY = (curY + lineCount).coerceAtMost(maxY)
        pos = Position(x = newCursorX, y = newCursorY)
    }

    override fun size(): Size = buffer.area.asSize()

    override fun windowSize(): WindowSize {
        // Some arbitrary window pixel size, probably does not need much testing.
        val windowPixelSize = Size(width = 640, height = 480)
        return WindowSize(
            columnsRows = buffer.area.asSize(),
            pixels = windowPixelSize
        )
    }

    override fun flush() {
        // no-op
    }

    /**
     * Scrolls a region of the screen up by the specified amount.
     *
     * Mirrors the upstream `scrolling-regions` feature in `TestBackend::scroll_region_up`.
     */
    fun scrollRegionUp(region: IntRange, scrollBy: UShort) {
        val width = buffer.area.width
        val height = buffer.area.height
        val cellRegionStart = width * minOf(region.first, height)
        val cellRegionEnd = width * minOf(region.last + 1, height)
        val cellRegionLen = cellRegionEnd - cellRegionStart
        val cellsToScrollBy = width * scrollBy.toInt()

        // Deal with the simple case where nothing needs to be copied into scrollback.
        if (cellRegionStart > 0) {
            if (cellsToScrollBy >= cellRegionLen) {
                // The scroll amount is large enough to clear the whole region.
                for (i in cellRegionStart until cellRegionEnd) {
                    buffer.mutableContent[i] = Cell.EMPTY.clone()
                }
            } else {
                // Scroll up by rotating, then filling in the bottom with empty cells.
                rotateLeft(buffer.mutableContent, cellRegionStart, cellRegionEnd, cellsToScrollBy)
                for (i in (cellRegionEnd - cellsToScrollBy) until cellRegionEnd) {
                    buffer.mutableContent[i] = Cell.EMPTY.clone()
                }
            }
            return
        }

        // The rows inserted into the scrollback will first come from the buffer, and if that is
        // insufficient, will then be blank rows.
        val cellsFromRegion = minOf(cellRegionLen, cellsToScrollBy)
        val moved = ArrayList<Cell>(cellsFromRegion)
        for (i in 0 until cellsFromRegion) {
            moved.add(buffer.mutableContent[i].clone())
            buffer.mutableContent[i] = Cell.EMPTY.clone()
        }
        appendToScrollback(scrollback, moved)
        if (cellsToScrollBy < cellRegionLen) {
            // Rotate the remaining cells to the front of the region.
            rotateLeft(buffer.mutableContent, cellRegionStart, cellRegionEnd, cellsFromRegion)
        } else {
            // Splice cleared out the region. Insert empty rows in scrollback.
            appendToScrollback(scrollback, List(cellsToScrollBy - cellRegionLen) { Cell.EMPTY.clone() })
        }
    }

    /**
     * Scrolls a region of the screen down by the specified amount.
     *
     * Mirrors the upstream `scrolling-regions` feature in `TestBackend::scroll_region_down`.
     */
    fun scrollRegionDown(region: IntRange, scrollBy: UShort) {
        val width = buffer.area.width
        val height = buffer.area.height
        val cellRegionStart = width * minOf(region.first, height)
        val cellRegionEnd = width * minOf(region.last + 1, height)
        val cellRegionLen = cellRegionEnd - cellRegionStart
        val cellsToScrollBy = width * scrollBy.toInt()

        if (cellsToScrollBy >= cellRegionLen) {
            // The scroll amount is large enough to clear the whole region.
            for (i in cellRegionStart until cellRegionEnd) {
                buffer.mutableContent[i] = Cell.EMPTY.clone()
            }
        } else {
            // Scroll up by rotating, then filling in the top with empty cells.
            rotateRight(buffer.mutableContent, cellRegionStart, cellRegionEnd, cellsToScrollBy)
            for (i in cellRegionStart until (cellRegionStart + cellsToScrollBy)) {
                buffer.mutableContent[i] = Cell.EMPTY.clone()
            }
        }
    }

    override fun toString(): String = bufferView(buffer)
}

private fun rotateLeft(list: MutableList<Cell>, from: Int, to: Int, distance: Int) {
    val len = to - from
    if (len <= 0 || distance == 0) return
    val d = distance % len
    if (d == 0) return
    val temp = ArrayList<Cell>(d)
    for (i in 0 until d) temp.add(list[from + i])
    for (i in 0 until (len - d)) {
        list[from + i] = list[from + i + d]
    }
    for (i in 0 until d) {
        list[to - d + i] = temp[i]
    }
}

private fun rotateRight(list: MutableList<Cell>, from: Int, to: Int, distance: Int) {
    val len = to - from
    if (len <= 0 || distance == 0) return
    val d = distance % len
    if (d == 0) return
    rotateLeft(list, from, to, len - d)
}

internal fun bufferView(buffer: Buffer): String {
    val view = StringBuilder(buffer.content.size + buffer.area.height * 3)
    val width = buffer.area.width
    if (width <= 0) return view.toString()

    for (cells in buffer.content.chunked(width)) {
        val overwritten = mutableListOf<Pair<Int, String>>()
        var skip = 0
        view.append('"')
        for ((x, cell) in cells.withIndex()) {
            val sym = cell.symbol()
            if (skip == 0) {
                view.append(sym)
            } else {
                overwritten.add(Pair(x, sym))
            }
            skip = (maxOf(skip, cell.cellWidth().toInt()) - 1).coerceAtLeast(0)
        }
        view.append('"')
        if (overwritten.isNotEmpty()) {
            view.append(" Hidden by multi-width symbols: ").append(overwritten.debugString())
        }
        view.append('\n')
    }
    return view.toString()
}

private fun appendToScrollback(scrollback: Buffer, cells: Iterable<Cell>) {
    scrollback.mutableContent.addAll(cells)
    val width = scrollback.area.width
    if (width <= 0) {
        scrollback.area = scrollback.area.copy(height = 0)
        scrollback.mutableContent.clear()
        return
    }

    val maxHeight = UShort.MAX_VALUE.toInt()
    val newHeight = (scrollback.mutableContent.size / width).coerceAtMost(maxHeight)
    val keepFrom = (scrollback.mutableContent.size - (width * maxHeight)).coerceAtLeast(0)
    if (keepFrom > 0) {
        scrollback.mutableContent.subList(0, keepFrom).clear()
    }
    scrollback.area = scrollback.area.copy(height = newHeight)
}

private fun List<Pair<Int, String>>.debugString(): String = joinToString(prefix = "[", postfix = "]") { (x, s) ->
    "($x, ${s.debugQuoted()})"
}

private fun String.debugQuoted(): String {
    val escaped = buildString {
        for (ch in this@debugQuoted) {
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                else -> append(ch)
            }
        }
    }
    return "\"$escaped\""
}
