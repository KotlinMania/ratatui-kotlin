// port-lint: source ratatui-core/src/buffer/buffer.rs
package ratatui.buffer

import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.graphemes

/**
 * A buffer that maps to the desired content of the terminal after the draw call.
 *
 * No widget in the library interacts directly with the terminal. Instead each of them
 * is required to draw their state to an intermediate buffer. It is basically a grid
 * where each cell contains a grapheme, a foreground color and a background color. This grid will
 * then be used to output the appropriate escape sequences and characters to draw the UI as the
 * user has defined it.
 *
 * ## Examples
 *
 * ```kotlin
 * val buf = Buffer.empty(Rect.new(0, 0, 10, 5))
 *
 * // indexing using Position
 * buf[Position(x = 0, y = 0)].setSymbol("A")
 * check(buf[Position(x = 0, y = 0)].symbol() == "A")
 *
 * // indexing using (x, y)
 * buf[0, 1].setSymbol("B")
 * check(buf[0, 1].symbol() == "B")
 *
 * // getting a nullable cell instead of throwing if outside the buffer
 * val cell = requireNotNull(buf.cellMut(Position(x = 0, y = 2))) { "cell not found" }
 * cell.setSymbol("C")
 *
 * val c = requireNotNull(buf.cell(Position(x = 0, y = 2))) { "cell not found" }
 * check(c.symbol() == "C")
 *
 * buf.setString(3, 0, "string", Style.default())
 * val r = buf[5, 0]
 * check(r.symbol() == "r")
 * ```
 */
data class Buffer(
    /** The area represented by this buffer */
    var area: Rect,
    /** The content of the buffer */
    val content: MutableList<Cell>
) {
    companion object {
        /** Returns a Buffer with all cells set to the default one */
        fun empty(area: Rect): Buffer {
            return filled(area, Cell.EMPTY)
        }

        /** Returns a Buffer with all cells initialized with the given Cell */
        fun filled(area: Rect, cell: Cell): Buffer {
            val size = area.area().toInt()
            val content = MutableList(size) { cell.clone() }
            return Buffer(area, content)
        }

        /** Returns a Buffer containing the given lines */
        fun withLines(lines: List<Line>): Buffer {
            val height = lines.size
            val width = lines.maxOfOrNull { it.width() } ?: 0
            val buffer = empty(Rect.new(0, 0, width, height))
            for ((y, line) in lines.withIndex()) {
                buffer.setLine(0, y, line, width)
            }
            return buffer
        }

        /** Returns a Buffer containing the given string lines */
        fun withLines(vararg lines: String): Buffer {
            return withLines(lines.map { Line.from(it) })
        }
    }

    /** Returns the content of the buffer as a list */
    fun content(): List<Cell> = content

    /** Returns the area covered by this buffer */
    fun area(): Rect = area

    /**
     * Returns a reference to the [Cell] at the given coordinates.
     *
     * Note: idiomatically methods named `get` usually return a nullable value, but this method
     * throws instead. This is kept for parity with Rust and backwards compatibility.
     *
     * Prefer using `buf[x, y]` or [cell] / [cellMut] instead.
     */
    @Deprecated(
        message = "Use buf[x, y] instead. To avoid throwing, use cell(x, y).",
        replaceWith = ReplaceWith("this[x.toInt(), y.toInt()]")
    )
    fun get(x: UShort, y: UShort): Cell = this[x.toInt(), y.toInt()]

    /**
     * Returns a mutable reference to the [Cell] at the given coordinates.
     *
     * Kotlin does not distinguish between shared and mutable references in the same way as Rust,
     * but [Cell] is mutable, so returning the cell instance provides the same effect.
     *
     * Prefer using `buf[x, y]` or [cellMut] instead.
     */
    @Deprecated(
        message = "Use buf[x, y] instead. To avoid throwing, use cellMut(x, y).",
        replaceWith = ReplaceWith("this[x.toInt(), y.toInt()]")
    )
    fun getMut(x: UShort, y: UShort): Cell = this[x.toInt(), y.toInt()]

    private fun indexOfOpt(position: Position): Int? {
        if (!area.contains(position)) return null
        val relY = position.y - area.y
        val relX = position.x - area.x
        val width = area.width
        return relY * width + relX
    }

    /** Returns the index in the content list for the given coordinates */
    fun indexOf(x: Int, y: Int): Int {
        return indexOfOpt(Position(x, y))
            ?: error(
                "index outside of buffer: the area is Rect { x: ${area.x}, y: ${area.y}, width: ${area.width}, height: ${area.height} } " +
                    "but index is ($x, $y)"
            )
    }

    /** Returns the cell at the given position, or null if outside bounds */
    fun cell(position: Position): Cell? {
        val index = indexOfOpt(position) ?: return null
        return content.getOrNull(index)
    }

    /**
     * Returns the cell at the given coordinates, or null if outside bounds.
     *
     * This mirrors Rust's `Buffer::cell((x, y))` overloads via Kotlin [Pair]s.
     */
    fun cell(position: Pair<Int, Int>): Cell? = cell(Position(position.first, position.second))

    /** Returns the cell at the given coordinates, or null if outside bounds */
    fun cell(x: Int, y: Int): Cell? = cell(Position(x, y))

    /** Returns the mutable cell at the given position, or null if outside bounds */
    fun cellMut(position: Position): Cell? = cell(position)

    /**
     * Returns the mutable cell at the given coordinates, or null if outside bounds.
     *
     * This mirrors Rust's `Buffer::cell_mut((x, y))` overloads via Kotlin [Pair]s.
     */
    fun cellMut(position: Pair<Int, Int>): Cell? = cellMut(Position(position.first, position.second))

    /** Returns the mutable cell at the given coordinates, or null if outside bounds */
    fun cellMut(x: Int, y: Int): Cell? = cellMut(Position(x, y))

    @Suppress("unused")
    private fun index(position: Position): Cell {
        val index = indexOf(position.x, position.y)
        return content[index]
    }

    @Suppress("unused")
    private fun index(position: Pair<Int, Int>): Cell {
        val position = Position(position.first, position.second)
        val index = indexOf(position.x, position.y)
        return content[index]
    }

    /** Indexing operator for Pair coordinates */
    operator fun get(position: Pair<Int, Int>): Cell {
        return index(position)
    }

    /** Indexing operator for (x, y) pairs */
    operator fun get(x: Int, y: Int): Cell {
        val index = indexOf(x, y)
        return content[index]
    }

    /** Indexing operator for Position */
    operator fun get(position: Position): Cell {
        return index(position)
    }

    @Suppress("unused")
    private fun indexMut(position: Position): Cell {
        val index = indexOf(position.x, position.y)
        return content[index]
    }

    @Suppress("unused")
    private fun indexMut(position: Pair<Int, Int>): Cell {
        val position = Position(position.first, position.second)
        val index = indexOf(position.x, position.y)
        return content[index]
    }

    /**
     * Returns the (global) coordinates of a cell given its index.
     *
     * Global coordinates are offset by the Buffer's area offset (`x`/`y`).
     */
    fun posOf(index: Int): Pair<Int, Int> {
        require(index >= 0 && index < content.size) {
            "Trying to get the coords of a cell outside the buffer: i=$index len=${content.size}"
        }
        val x = (index % area.width) + area.x
        val y = (index / area.width) + area.y
        return Pair(x, y)
    }

    /** Print a string, starting at the position (x, y) */
    fun setString(x: Int, y: Int, string: String, style: Style) {
        setStringn(x, y, string, Int.MAX_VALUE, style)
    }

    /**
     * Print at most the first `n` characters of a string if enough space is available until the end of the line.
     *
     * Skips zero-width graphemes and control characters.
     *
     * Use [setString] when the maximum amount of characters can be printed.
     */
    fun setStringn(x: Int, y: Int, string: String, maxWidth: Int, style: Style): Pair<Int, Int> {
        var xVar = x
        val maxWidthU16 = maxWidth.coerceAtLeast(0).coerceAtMost(UShort.MAX_VALUE.toInt())
        var remainingWidth = (area.right() - xVar).coerceAtLeast(0).coerceAtMost(maxWidthU16)

        for (symbol in graphemes(string)) {
            if (symbol.any { it.isISOControl() }) continue

            val width = symbol.cellWidth().toInt()
            if (width <= 0) continue

            val nextRemainingWidth = remainingWidth - width
            if (nextRemainingWidth < 0) {
                break
            }
            remainingWidth = nextRemainingWidth

            this[xVar, y].setSymbol(symbol).setStyle(style)
            val nextSymbol = xVar + width
            xVar += 1

            // Reset following cells if multi-width (they would be hidden by the grapheme),
            while (xVar < nextSymbol) {
                this[xVar, y].reset()
                xVar += 1
            }
        }

        return Pair(xVar, y)
    }

    /** Print a line, starting at the position (x, y) */
    fun setLine(x: Int, y: Int, line: Line, maxWidth: Int): Pair<Int, Int> {
        var remainingWidth = maxWidth
        var currentX = x
        for (span in line) {
            if (remainingWidth == 0) break
            val (newX, _) = setStringn(
                currentX,
                y,
                span.content,
                remainingWidth,
                line.style.patch(span.style)
            )
            val w = newX - currentX
            currentX = newX
            remainingWidth = (remainingWidth - w).coerceAtLeast(0)
        }
        return Pair(currentX, y)
    }

    /** Print a span, starting at the position (x, y) */
    fun setSpan(x: Int, y: Int, span: Span, maxWidth: Int): Pair<Int, Int> {
        return setStringn(x, y, span.content, maxWidth, span.style)
    }

    /** Set the style of all cells in the given area */
    fun setStyle(area: Rect, style: Style) {
        val intersection = this.area.intersection(area)
        for (y in intersection.top()..<intersection.bottom()) {
            for (x in intersection.left()..<intersection.right()) {
                this[x, y].setStyle(style)
            }
        }
    }

    /** Resize the buffer */
    fun resize(area: Rect) {
        val length = area.area().toInt()
        if (content.size > length) {
            while (content.size > length) content.removeLast()
        } else {
            while (content.size < length) content.add(Cell.EMPTY)
        }
        this.area = area
    }

    /** Reset all cells in the buffer */
    fun reset() {
        for (cell in content) {
            cell.reset()
        }
    }

    /** Merge another buffer into this one. */
    fun merge(other: Buffer) {
        val newArea = this.area.union(other.area)

        val newLength = newArea.area().toInt()
        while (content.size < newLength) content.add(Cell.EMPTY)
        while (content.size > newLength) content.removeLast()

        // Move original content to the appropriate space.
        val size = this.area.area().toInt()
        for (i in (0 until size).reversed()) {
            val (x, y) = posOf(i)
            val k = ((y - newArea.y) * newArea.width + (x - newArea.x))
            if (i != k) {
                content[k] = content[i].clone()
                content[i].reset()
            }
        }

        // Push content of the other buffer into this one (may erase previous data).
        val otherSize = other.area.area().toInt()
        for (i in 0 until otherSize) {
            val (x, y) = other.posOf(i)
            val k = ((y - newArea.y) * newArea.width + (x - newArea.x))
            content[k] = other.content[i].clone()
        }

        this.area = newArea
    }

    /**
     * Builds a minimal sequence of updates necessary to update the UI from this buffer to [other].
     *
     * Prefer [diffIter] to avoid the intermediate allocation.
     */
    fun diff(other: Buffer): List<BufferDiff.Item> {
        val updates = mutableListOf<BufferDiff.Item>()
        val diff = diffIter(other)
        while (diff.hasNext()) {
            updates.add(diff.next())
        }
        return updates
    }

    /**
     * Builds a minimal sequence of coordinates and Cells necessary to update the UI from this
     * buffer to [other].
     *
     * Transliteration of `ratatui-core`'s `Buffer::diff_iter` implementation.
     */
    fun diffIter(other: Buffer): BufferDiff = BufferDiff.new(this, other)

    @Suppress("unused")
    private fun fmt(f: StringBuilder) {
        f.append(
            "Buffer {\n    area: Rect { x: ${area.x}, y: ${area.y}, width: ${area.width}, height: ${area.height} }"
        )

        if (area.isEmpty()) {
            f.append("\n}")
            return
        }

        f.append(",\n    content: [\n")
        val styles = mutableListOf<StyleEntry>()
        var lastStyle: StyleKey? = null

        for (y in 0 until area.height) {
            val overwritten = mutableListOf<Pair<Int, String>>()
            var skip = 0
            f.append("        \"")
            for (x in 0 until area.width) {
                val cell = this[area.x + x, area.y + y]
                val sym = cell.symbol()
                if (skip == 0) {
                    f.append(sym)
                } else {
                    overwritten.add(Pair(x, sym))
                }
                skip = (maxOf(skip, cell.cellWidth().toInt()) - 1).coerceAtLeast(0)

                val styleKey = StyleKey(fg = cell.fg, bg = cell.bg, modifier = cell.modifier)
                if (lastStyle != styleKey) {
                    lastStyle = styleKey
                    styles.add(
                        StyleEntry(
                            x = x,
                            y = y,
                            fg = cell.fg,
                            bg = cell.bg,
                            modifier = cell.modifier
                        )
                    )
                }
            }
            f.append("\",")
            if (overwritten.isNotEmpty()) {
                val overwrittenString = StringBuilder()
                overwrittenString.append('[')
                for (i in overwritten.indices) {
                    if (i != 0) overwrittenString.append(", ")
                    val (xx, s) = overwritten[i]
                    overwrittenString.append('(')
                    overwrittenString.append(xx)
                    overwrittenString.append(", \"")
                    for (ch in s) {
                        when (ch) {
                            '\\' -> overwrittenString.append("\\\\")
                            '"' -> overwrittenString.append("\\\"")
                            else -> overwrittenString.append(ch)
                        }
                    }
                    overwrittenString.append("\")")
                }
                overwrittenString.append(']')
                f.append(" // hidden by multi-width symbols: $overwrittenString")
            }
            f.append("\n")
        }

        f.append("    ],\n    styles: [\n")
        for (s in styles) {
            f.append("        x: ${s.x}, y: ${s.y}, fg: ${s.fg}, bg: ${s.bg}, modifier: ${s.modifier},\n")
        }
        f.append("    ]\n}")
    }

    override fun toString(): String {
        val f = StringBuilder()
        fmt(f)
        return f.toString()
    }
}

private data class StyleKey(
    val fg: ratatui.style.Color,
    val bg: ratatui.style.Color,
    val modifier: ratatui.style.Modifier
)

private data class StyleEntry(
    val x: Int,
    val y: Int,
    val fg: ratatui.style.Color,
    val bg: ratatui.style.Color,
    val modifier: ratatui.style.Modifier
)
