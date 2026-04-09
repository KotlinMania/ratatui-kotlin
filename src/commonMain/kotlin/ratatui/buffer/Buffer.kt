// port-lint: source ratatui-core/src/buffer/buffer.rs
package ratatui.buffer

import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.graphemes
import ratatui.text.graphemeCellWidth

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
class Buffer(
    /** The area represented by this buffer */
    var area: Rect,
    /** The content of the buffer */
    val content: MutableList<Cell>
) {
    companion object {
        /** Returns a Buffer with all cells set to the default one */
        fun empty(area: Rect): Buffer {
            val size = area.area().toInt()
            val content = MutableList(size) { Cell.EMPTY }
            return Buffer(area, content)
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
        return indexOfOpt(Position(x, y)) ?: error(
            "index outside of buffer: the area is ${area.debugString()} but index is ($x, $y)"
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

    /** Indexing operator for (x, y) pairs */
    operator fun get(x: Int, y: Int): Cell {
        val index = indexOf(x, y)
        return content[index]
    }

    /** Indexing operator for Position */
    operator fun get(position: Position): Cell = get(position.x, position.y)

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
        var currentX = x

        val remainingWidthMax = maxWidth.coerceAtLeast(0)
        val remainingWidthToRight = (area.right() - currentX).coerceAtLeast(0)
        var remainingWidth = minOf(remainingWidthToRight, remainingWidthMax)

        val graphemes = graphemes(string)
            .asSequence()
            .filter { symbol -> symbol.none { it.isISOControl() } }
            .map { symbol -> symbol to graphemeCellWidth(symbol) }
            .filter { (_symbol, width) -> width > 0 }

        for ((symbol, width) in graphemes) {
            val nextRemainingWidth = remainingWidth - width
            if (nextRemainingWidth < 0) {
                break
            }
            remainingWidth = nextRemainingWidth

            this[currentX, y].setSymbol(symbol).setStyle(style)
            val nextSymbol = currentX + width
            currentX += 1

            // Reset following cells if multi-width (they would be hidden by the grapheme).
            while (currentX < nextSymbol) {
                this[currentX, y].reset()
                currentX += 1
            }
        }

        return Pair(currentX, y)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Buffer
        if (area != other.area) return false
        if (content != other.content) return false
        return true
    }

    override fun hashCode(): Int {
        var result = area.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Buffer {\n    area: ${area.debugString()}")

        if (area.isEmpty()) {
            sb.append("\n}")
            return sb.toString()
        }

        sb.append(",\n    content: [\n")
        val styles = mutableListOf<StyleEntry>()
        var lastStyle: StyleKey? = null

        for (y in 0 until area.height) {
            val overwritten = mutableListOf<Pair<Int, String>>()
            var skip = 0
            sb.append("        \"")
            for (x in 0 until area.width) {
                val cell = this[area.x + x, area.y + y]
                val sym = cell.symbol()
                if (skip == 0) {
                    sb.append(sym)
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
            sb.append("\",")
            if (overwritten.isNotEmpty()) {
                sb.append(" // hidden by multi-width symbols: ${overwritten.debugString()}")
            }
            sb.append("\n")
        }

        sb.append("    ],\n    styles: [\n")
        for (s in styles) {
            sb.append("        x: ${s.x}, y: ${s.y}, fg: ${s.fg}, bg: ${s.bg}, modifier: ${s.modifier},\n")
        }
        sb.append("    ]\n}")
        return sb.toString()
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

private fun Rect.debugString(): String = "Rect { x: $x, y: $y, width: $width, height: $height }"

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
