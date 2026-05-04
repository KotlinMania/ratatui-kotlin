/**
 * This module provides the [TestBackend] implementation for the [Backend] interface.
 * It is used in the integration tests to verify the correctness of the library.
 */
package ai.solace.coder.tui.backend

/**
 * A [Backend] implementation used for integration testing that renders to an in-memory buffer.
 *
 * Note: that although many of the integration and unit tests in ratatui are written using this
 * backend, it is preferable to write unit tests for widgets directly against the buffer rather
 * than using this backend. This backend is intended for integration tests that test the entire
 * terminal UI.
 *
 * Example:
 * ```kotlin
 * val backend = TestBackend.new(10u, 2u)
 * backend.clear()
 * backend.assertBufferLines(listOf("          ", "          "))
 * ```
 */
data class TestBackend(
    var buffer: Buffer,
    var scrollback: Buffer,
    var cursor: Boolean,
    var pos: Pair<UShort, UShort>
) : Backend {

    companion object {
    /**
     * Creates a new [TestBackend] with the specified width and height.
     */
    fun new(width: UShort, height: UShort): TestBackend {
        return TestBackend(
            buffer = Buffer.empty(Rect.new(0u, 0u, width, height)),
            scrollback = Buffer.empty(Rect.new(0u, 0u, width, 0u)),
            cursor = false,
            pos = Pair(0u, 0u)
        )
    }

    /**
     * Creates a new [TestBackend] with the specified lines as the initial screen state.
     *
     * The backend's screen size is determined from the initial lines.
     */
    fun <L : Line> withLines(lines: Iterable<L>): TestBackend {
        val buffer = Buffer.withLines(lines)
        val scrollback = Buffer.empty(Rect(
            x = 0u,
            y = 0u,
            width = buffer.area.width,
            height = 0u
        ))
        return TestBackend(
            buffer = buffer,
            scrollback = scrollback,
            cursor = false,
            pos = Pair(0u, 0u)
        )
    }
}

/**
 * Returns a reference to the internal buffer of the [TestBackend].
 */
fun buffer(): Buffer {
    return buffer
}

/**
 * Returns a reference to the internal scrollback buffer of the [TestBackend].
 *
 * The scrollback buffer represents the part of the screen that is currently hidden from view,
 * but that could be accessed by scrolling back in the terminal's history. This would normally
 * be done using the terminal's scrollbar or an equivalent keyboard shortcut.
 *
 * The scrollback buffer starts out empty. Lines are appended when they scroll off the top of
 * the main buffer. This happens when lines are appended to the bottom of the main buffer
 * using [Backend.appendLines].
 *
 * The scrollback buffer has a maximum height of [UShort.MAX_VALUE]. If lines are appended to the
 * bottom of the scrollback buffer when it is at its maximum height, a corresponding number of
 * lines will be removed from the top.
 */
fun scrollback(): Buffer {
    return scrollback
}

/**
 * Resizes the [TestBackend] to the specified width and height.
 */
fun resize(width: UShort, height: UShort) {
    buffer.resize(Rect.new(0u, 0u, width, height))
    val scrollbackHeight = scrollback.area.height
    scrollback.resize(Rect.new(0u, 0u, width, scrollbackHeight))
}

/**
 * Asserts that the [TestBackend]'s buffer is equal to the expected buffer.
 *
 * This is a shortcut for `check(buffer() == expected)`.
 *
 * @throws IllegalStateException When they are not equal, an exception is thrown with a detailed
 * error message showing the differences between the expected and actual buffers.
 */
fun assertBuffer(expected: Buffer) {
    // TODO: use check() or assertEquals()
    assertBufferEq(buffer, expected)
}

/**
 * Asserts that the [TestBackend]'s scrollback buffer is equal to the expected buffer.
 *
 * This is a shortcut for `check(scrollback() == expected)`.
 *
 * @throws IllegalStateException When they are not equal, an exception is thrown with a detailed
 * error message showing the differences between the expected and actual buffers.
 */
fun assertScrollback(expected: Buffer) {
    check(scrollback == expected)
}

/**
 * Asserts that the [TestBackend]'s scrollback buffer is empty.
 *
 * @throws IllegalStateException When the scrollback buffer is not empty, an exception is thrown
 * with a detailed error message showing the differences between the expected and actual buffers.
 */
fun assertScrollbackEmpty() {
    val expected = Buffer(
        area = Rect(
            width = scrollback.area.width,
            x = Rect.ZERO.x,
            y = Rect.ZERO.y,
            height = Rect.ZERO.height
        ),
        content = mutableListOf()
    )
    assertScrollback(expected)
}

/**
 * Asserts that the [TestBackend]'s buffer is equal to the expected lines.
 *
 * This is a shortcut for `assertBuffer(Buffer.withLines(expected))`.
 *
 * @throws IllegalStateException When they are not equal, an exception is thrown with a detailed
 * error message showing the differences between the expected and actual buffers.
 */
fun <L : Line> assertBufferLines(expected: Iterable<L>) {
    assertBuffer(Buffer.withLines(expected))
}

/**
 * Asserts that the [TestBackend]'s scrollback buffer is equal to the expected lines.
 *
 * This is a shortcut for `assertScrollback(Buffer.withLines(expected))`.
 *
 * @throws IllegalStateException When they are not equal, an exception is thrown with a detailed
 * error message showing the differences between the expected and actual buffers.
 */
fun <L : Line> assertScrollbackLines(expected: Iterable<L>) {
    assertScrollback(Buffer.withLines(expected))
}

/**
 * Asserts that the [TestBackend]'s cursor position is equal to the expected one.
 *
 * This is a shortcut for `check(getCursorPosition() == expected)`.
 *
 * @throws IllegalStateException When they are not equal, an exception is thrown with a detailed
 * error message showing the differences between the expected and actual position.
 */
fun assertCursorPosition(position: Position) {
    val actual = getCursorPosition()
    check(actual == position) { "Cursor position mismatch: expected $position but was $actual" }
}

/**
 * Returns a string representation of the [TestBackend] by calling [bufferView]
 * on its internal buffer.
 */
override fun toString(): String {
    return bufferView(buffer)
}

// Backend interface implementation
// Note: In Kotlin, TestBackend implements the Backend interface. The methods below
// are part of that implementation.

override fun draw(content: Iterator<Triple<UShort, UShort, Cell>>) {
    for ((x, y, c) in content) {
        buffer[x, y] = c.copy()
    }
}

override fun hideCursor() {
    cursor = false
}

override fun showCursor() {
    cursor = true
}

override fun getCursorPosition(): Position {
    return Position(pos.first, pos.second)
}

override fun setCursorPosition(position: Position) {
    pos = Pair(position.x, position.y)
}

override fun clear() {
    buffer.reset()
}

override fun clearRegion(clearType: ClearType) {
    val region: MutableList<Cell> = when (clearType) {
        ClearType.All -> {
            clear()
            return
        }
        ClearType.AfterCursor -> {
            val index = buffer.indexOf(pos.first, pos.second) + 1
            buffer.content.subList(index, buffer.content.size)
        }
        ClearType.BeforeCursor -> {
            val index = buffer.indexOf(pos.first, pos.second)
            buffer.content.subList(0, index)
        }
        ClearType.CurrentLine -> {
            val lineStartIndex = buffer.indexOf(0u, pos.second)
            val lineEndIndex = buffer.indexOf((buffer.area.width - 1u).toUShort(), pos.second)
            buffer.content.subList(lineStartIndex, lineEndIndex + 1)
        }
        ClearType.UntilNewLine -> {
            val index = buffer.indexOf(pos.first, pos.second)
            val lineEndIndex = buffer.indexOf((buffer.area.width - 1u).toUShort(), pos.second)
            buffer.content.subList(index, lineEndIndex + 1)
        }
    }
    for (cell in region) {
        cell.reset()
    }
}

/**
 * Inserts n line breaks at the current cursor position.
 *
 * After the insertion, the cursor x position will be incremented by 1 (unless it's already
 * at the end of line). This is a common behaviour of terminals in raw mode.
 *
 * If the number of lines to append is fewer than the number of lines in the buffer after the
 * cursor y position then the cursor is moved down by n rows.
 *
 * If the number of lines to append is greater than the number of lines in the buffer after
 * the cursor y position then that number of empty lines (at most the buffer's height in this
 * case but this limit is instead replaced with scrolling in most backend implementations) will
 * be added after the current position and the cursor will be moved to the last row.
 */
override fun appendLines(lineCount: UShort) {
    val cursorPos = getCursorPosition()
    val curX = cursorPos.x
    val curY = cursorPos.y
    val width = buffer.area.width
    val height = buffer.area.height

    // the next column ensuring that we don't go past the last column
    val newCursorX = (curX + 1u).coerceAtMost((width - 1u).coerceAtLeast(0u))

    val maxY = (height - 1u).coerceAtLeast(0u)
    val linesAfterCursor = (maxY - curY).coerceAtLeast(0u)

    if (lineCount > linesAfterCursor) {
        // We need to insert blank lines at the bottom and scroll the lines from the top into
        // scrollback.
        val scrollBy = (lineCount - linesAfterCursor).toInt()
        val widthInt = buffer.area.width.toInt()
        val cellsToScrollback = minOf(buffer.content.size, widthInt * scrollBy)

        // Move cells to scrollback
        val cellsToMove = buffer.content.subList(0, cellsToScrollback).toList()
        appendToScrollback(scrollback, cellsToMove)

        // Replace with default cells and rotate
        for (i in 0 until cellsToScrollback) {
            buffer.content[i] = Cell()
        }
        rotateLeft(buffer.content, cellsToScrollback)

        // Append additional empty rows if needed
        val additionalCells = widthInt * scrollBy - cellsToScrollback
        if (additionalCells > 0) {
            appendToScrollback(scrollback, List(additionalCells) { Cell() })
        }
    }

    val newCursorY = (curY + lineCount).coerceAtMost(maxY)
    setCursorPosition(Position.new(newCursorX, newCursorY))
}

override fun size(): Size {
    return buffer.area.asSize()
}

override fun windowSize(): WindowSize {
    // Some arbitrary window pixel size, probably doesn't need much testing.
    val windowPixelSize = Size(width = 640u, height = 480u)
    return WindowSize(
        columnsRows = buffer.area.asSize(),
        pixels = windowPixelSize
    )
}

override fun flush() {
    // No-op for test backend
}

// scrolling-regions feature methods

/**
 * Scrolls a region of the screen up by the specified amount.
 */
fun scrollRegionUp(region: IntRange, scrollBy: UShort) {
    val widthInt = buffer.area.width.toInt()
    val cellRegionStart = widthInt * minOf(region.first, buffer.area.height.toInt())
    val cellRegionEnd = widthInt * minOf(region.last + 1, buffer.area.height.toInt())
    val cellRegionLen = cellRegionEnd - cellRegionStart
    val cellsToScrollBy = widthInt * scrollBy.toInt()

    // Deal with the simple case where nothing needs to be copied into scrollback.
    if (cellRegionStart > 0) {
        if (cellsToScrollBy >= cellRegionLen) {
            // The scroll amount is large enough to clear the whole region.
            for (i in cellRegionStart until cellRegionEnd) {
                buffer.content[i] = Cell()
            }
        } else {
            // Scroll up by rotating, then filling in the bottom with empty cells.
            rotateLeft(buffer.content.subList(cellRegionStart, cellRegionEnd), cellsToScrollBy)
            for (i in (cellRegionEnd - cellsToScrollBy) until cellRegionEnd) {
                buffer.content[i] = Cell()
            }
        }
        return
    }

    // The rows inserted into the scrollback will first come from the buffer, and if that is
    // insufficient, will then be blank rows.
    val cellsFromRegion = minOf(cellRegionLen, cellsToScrollBy)
    val cellsToMove = buffer.content.subList(0, cellsFromRegion).toList()
    appendToScrollback(scrollback, cellsToMove)

    // Replace with default cells
    for (i in 0 until cellsFromRegion) {
        buffer.content[i] = Cell()
    }

    if (cellsToScrollBy < cellRegionLen) {
        // Rotate the remaining cells to the front of the region.
        rotateLeft(buffer.content.subList(cellRegionStart, cellRegionEnd), cellsFromRegion)
    } else {
        // Splice cleared out the region. Insert empty rows in scrollback.
        appendToScrollback(scrollback, List(cellsToScrollBy - cellRegionLen) { Cell() })
    }
}

/**
 * Scrolls a region of the screen down by the specified amount.
 */
fun scrollRegionDown(region: IntRange, scrollBy: UShort) {
    val widthInt = buffer.area.width.toInt()
    val cellRegionStart = widthInt * minOf(region.first, buffer.area.height.toInt())
    val cellRegionEnd = widthInt * minOf(region.last + 1, buffer.area.height.toInt())
    val cellRegionLen = cellRegionEnd - cellRegionStart
    val cellsToScrollBy = widthInt * scrollBy.toInt()

    if (cellsToScrollBy >= cellRegionLen) {
        // The scroll amount is large enough to clear the whole region.
        for (i in cellRegionStart until cellRegionEnd) {
            buffer.content[i] = Cell()
        }
    } else {
        // Scroll down by rotating right, then filling in the top with empty cells.
        rotateRight(buffer.content.subList(cellRegionStart, cellRegionEnd), cellsToScrollBy)
        for (i in cellRegionStart until (cellRegionStart + cellsToScrollBy)) {
            buffer.content[i] = Cell()
        }
    }
}

} // End of TestBackend class

/**
 * Append the provided cells to the bottom of a scrollback buffer. The number of cells must be a
 * multiple of the buffer's width. If the scrollback buffer ends up larger than 65535 lines tall,
 * then lines will be removed from the top to get it down to size.
 */
internal fun appendToScrollback(scrollback: Buffer, cells: Iterable<Cell>) {
    scrollback.content.addAll(cells)
    val width = scrollback.area.width.toInt()
    val newHeight = minOf(scrollback.content.size / width, UShort.MAX_VALUE.toInt())
    val keepFrom = (scrollback.content.size - width * UShort.MAX_VALUE.toInt()).coerceAtLeast(0)
    if (keepFrom > 0) {
        scrollback.content.subList(0, keepFrom).clear()
    }
    scrollback.area = scrollback.area.copy(height = newHeight.toUShort())
}

/**
 * Returns a string representation of the given buffer for debugging purpose.
 *
 * This function is used to visualize the buffer content in a human-readable format.
 * It iterates through the buffer content and appends each cell's symbol to the view StringBuilder.
 * If a cell is hidden by a multi-width symbol, it is added to the overwritten list and
 * displayed at the end of the line.
 */
internal fun bufferView(buffer: Buffer): String {
    val view = StringBuilder(buffer.content.size + buffer.area.height.toInt() * 3)
    for (cells in buffer.content.chunked(buffer.area.width.toInt())) {
        val overwritten = mutableListOf<Pair<Int, String>>()
        var skip: Int = 0
        view.append('"')
        for ((x, c) in cells.withIndex()) {
            if (skip == 0) {
                view.append(c.symbol())
            } else {
                overwritten.add(Pair(x, c.symbol()))
            }
            skip = maxOf(skip, c.symbol().width()).coerceAtLeast(1) - 1
        }
        view.append('"')
        if (overwritten.isNotEmpty()) {
            view.append(" Hidden by multi-width symbols: $overwritten")
        }
        view.append('\n')
    }
    return view.toString()
}

/**
 * Rotates the elements of a mutable list to the left by the specified distance.
 */
internal fun <T> rotateLeft(list: MutableList<T>, distance: Int) {
    if (list.isEmpty() || distance == 0) return
    val n = list.size
    val d = distance % n
    if (d == 0) return
    val temp = list.subList(0, d).toList()
    for (i in 0 until (n - d)) {
        list[i] = list[i + d]
    }
    for (i in 0 until d) {
        list[n - d + i] = temp[i]
    }
}

/**
 * Rotates the elements of a mutable list to the right by the specified distance.
 */
internal fun <T> rotateRight(list: MutableList<T>, distance: Int) {
    if (list.isEmpty() || distance == 0) return
    val n = list.size
    val d = distance % n
    if (d == 0) return
    rotateLeft(list, n - d)
}
