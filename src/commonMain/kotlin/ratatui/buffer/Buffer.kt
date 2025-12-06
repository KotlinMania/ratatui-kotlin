package ratatui.buffer

import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.symbols.merge.MergeStrategy
import ratatui.text.Line
import ratatui.text.Span

/**
 * A buffer cell.
 *
 * Each cell in the buffer contains a symbol (grapheme cluster), foreground color,
 * background color, and text modifiers.
 *
 * This is a minimal stub implementation. The full implementation will be ported
 * from the Rust ratatui-core crate.
 */
data class Cell(
    /** The string to be drawn in the cell */
    var symbol: String = " ",
    /** The foreground color of the cell */
    var fg: ratatui.style.Color = ratatui.style.Color.Reset,
    /** The background color of the cell */
    var bg: ratatui.style.Color = ratatui.style.Color.Reset,
    /** The modifier of the cell */
    var modifier: ratatui.style.Modifier = ratatui.style.Modifier.empty(),
    /** Whether the cell should be skipped when diffing */
    var skip: Boolean = false
) {
    companion object {
        /** An empty Cell */
        val EMPTY: Cell = Cell()

        /** Creates a new Cell with the given symbol */
        fun new(symbol: String): Cell = Cell(symbol = symbol)
    }

    /** Gets the symbol of the cell */
    fun symbol(): String = symbol

    /** Sets the symbol of the cell */
    fun setSymbol(symbol: String): Cell {
        this.symbol = symbol
        return this
    }

    /** Appends a symbol to the cell (for zero-width characters) */
    fun appendSymbol(symbol: String): Cell {
        this.symbol += symbol
        return this
    }

    /** Sets the style of the cell */
    fun setStyle(style: Style): Cell {
        style.fg?.let { this.fg = it }
        style.bg?.let { this.bg = it }
        // TODO: handle modifiers
        return this
    }

    /**
     * Merges the symbol of the cell with the one already on the cell, using the provided
     * [MergeStrategy].
     *
     * Merges Box Drawing Unicode block characters to create a single character representing
     * their combination, useful for border collapsing.
     *
     * @param newSymbol The symbol to merge with the existing one
     * @param strategy The merge strategy to use
     * @return This cell for chaining
     */
    fun mergeSymbol(newSymbol: String, strategy: MergeStrategy): Cell {
        val mergedSymbol = strategy.merge(symbol, newSymbol)
        symbol = mergedSymbol
        return this
    }

    /** Resets the cell to empty state */
    fun reset() {
        symbol = " "
        fg = ratatui.style.Color.Reset
        bg = ratatui.style.Color.Reset
        modifier = ratatui.style.Modifier.empty()
        skip = false
    }
}

/**
 * A buffer that maps to the desired content of the terminal after the draw call.
 *
 * No widget in the library interacts directly with the terminal. Instead each of them
 * is required to draw their state to an intermediate buffer. It is basically a grid
 * where each cell contains a grapheme, a foreground color and a background color.
 *
 * This is a minimal stub implementation. The full implementation will be ported
 * from the Rust ratatui-core crate.
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
            val content = MutableList(size) { Cell.EMPTY.copy() }
            return Buffer(area, content)
        }

        /** Returns a Buffer with all cells initialized with the given Cell */
        fun filled(area: Rect, cell: Cell): Buffer {
            val size = area.area().toInt()
            val content = MutableList(size) { cell.copy() }
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

    /** Returns the index in the content list for the given coordinates */
    fun indexOf(x: Int, y: Int): Int {
        require(area.contains(Position(x, y))) {
            "index outside of buffer: the area is $area but index is ($x, $y)"
        }
        val relY = y - area.y
        val relX = x - area.x
        val width = area.width
        return relY * width + relX
    }

    /** Returns the cell at the given position, or null if outside bounds */
    fun cell(position: Position): Cell? {
        if (!area.contains(position)) return null
        val index = indexOf(position.x, position.y)
        return content.getOrNull(index)
    }

    /** Returns the mutable cell at the given position, or null if outside bounds */
    fun cellMut(position: Position): Cell? = cell(position)

    /** Indexing operator for (x, y) pairs */
    operator fun get(x: Int, y: Int): Cell {
        val index = indexOf(x, y)
        return content[index]
    }

    /** Indexing operator for Position */
    operator fun get(position: Position): Cell = get(position.x, position.y)

    /** Print a string, starting at the position (x, y) */
    fun setString(x: Int, y: Int, string: String, style: Style) {
        setStringn(x, y, string, Int.MAX_VALUE, style)
    }

    /** Print at most the first n characters of a string */
    fun setStringn(x: Int, y: Int, string: String, maxWidth: Int, style: Style): Pair<Int, Int> {
        var currentX = x
        val right = area.right()
        val remainingWidth = (right - x).coerceAtMost(maxWidth)

        var used = 0
        for (char in string) {
            if (used >= remainingWidth) break
            if (char.isISOControl()) continue

            val index = indexOf(currentX, y)
            content[index].setSymbol(char.toString()).setStyle(style)
            currentX++
            used++
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
            while (content.size < length) content.add(Cell.EMPTY.copy())
        }
        this.area = area
    }

    /** Reset all cells in the buffer */
    fun reset() {
        for (cell in content) {
            cell.reset()
        }
    }
}
