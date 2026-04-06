package ratatui.backend

import io.github.kotlinmania.crossterm.ansiString
import io.github.kotlinmania.crossterm.cursor.Hide
import io.github.kotlinmania.crossterm.cursor.MoveTo
import io.github.kotlinmania.crossterm.cursor.Show
import io.github.kotlinmania.crossterm.cursor.sys.position
import io.github.kotlinmania.crossterm.style.Attribute
import io.github.kotlinmania.crossterm.style.Attributes
import io.github.kotlinmania.crossterm.style.Color
import io.github.kotlinmania.crossterm.style.Print
import io.github.kotlinmania.crossterm.style.SetAttribute
import io.github.kotlinmania.crossterm.style.SetAttributes
import io.github.kotlinmania.crossterm.style.SetColors
import io.github.kotlinmania.crossterm.terminal.Clear
import io.github.kotlinmania.crossterm.terminal.ClearType as CrosstermClearType
import io.github.kotlinmania.crossterm.terminal.sys.size as terminalSize
import ratatui.layout.Position
import ratatui.layout.Size
import ratatui.style.Modifier
import ratatui.terminal.Backend
import ratatui.terminal.CellUpdate
import ratatui.terminal.ClearType

/**
 * A [Backend] implementation backed by `crossterm-kotlin`.
 *
 * This backend writes ANSI escape sequences to the provided [writer].
 *
 * Note: `crossterm-kotlin` is not available on all targets. This backend is only compiled for
 * desktop native targets (macOS/Linux/Windows).
 */
class CrosstermBackend(
    private val writer: Appendable,
    private val flushFn: () -> Unit = {}
) : Backend {

    override fun draw(updates: List<CellUpdate>) {
        for (update in updates) {
            writer.append(MoveTo(update.x.toUShort(), update.y.toUShort()).ansiString())

            // Reset attributes for each cell to avoid style bleed.
            writer.append(SetAttribute(Attribute.Reset).ansiString())

            val fg = update.fg.toCrosstermColor()
            val bg = update.bg.toCrosstermColor()
            writer.append(SetColors(fg, bg).ansiString())

            val attrs = update.modifiers.toCrosstermAttributes()
            if (!attrs.isEmpty()) {
                writer.append(SetAttributes(attrs).ansiString())
            }

            writer.append(Print(update.symbol).ansiString())
        }
    }

    override fun hideCursor() {
        writer.append(Hide.ansiString())
    }

    override fun showCursor() {
        writer.append(Show.ansiString())
    }

    override fun getCursorPosition(): Position {
        val (col, row) = position()
        return Position(col.toInt(), row.toInt())
    }

    override fun setCursorPosition(position: Position) {
        writer.append(MoveTo(position.x.toUShort(), position.y.toUShort()).ansiString())
    }

    override fun clearRegion(clearType: ClearType) {
        val ct = when (clearType) {
            ClearType.All -> CrosstermClearType.All
            ClearType.AfterCursor -> CrosstermClearType.FromCursorDown
            ClearType.BeforeCursor -> CrosstermClearType.FromCursorUp
            ClearType.CurrentLine -> CrosstermClearType.CurrentLine
            ClearType.UntilNewLine -> CrosstermClearType.UntilNewLine
        }
        writer.append(Clear(ct).ansiString())
    }

    override fun size(): Size {
        val (columns, rows) = terminalSize()
        return Size(columns.toInt(), rows.toInt())
    }

    override fun flush() {
        flushFn()
    }

    private fun ratatui.style.Color.toCrosstermColor(): Color {
        return when (this) {
            ratatui.style.Color.Reset -> Color.Reset
            ratatui.style.Color.Black -> Color.Black
            ratatui.style.Color.Red -> Color.DarkRed
            ratatui.style.Color.Green -> Color.DarkGreen
            ratatui.style.Color.Yellow -> Color.DarkYellow
            ratatui.style.Color.Blue -> Color.DarkBlue
            ratatui.style.Color.Magenta -> Color.DarkMagenta
            ratatui.style.Color.Cyan -> Color.DarkCyan
            ratatui.style.Color.Gray -> Color.Grey
            ratatui.style.Color.DarkGray -> Color.DarkGrey
            ratatui.style.Color.LightRed -> Color.Red
            ratatui.style.Color.LightGreen -> Color.Green
            ratatui.style.Color.LightYellow -> Color.Yellow
            ratatui.style.Color.LightBlue -> Color.Blue
            ratatui.style.Color.LightMagenta -> Color.Magenta
            ratatui.style.Color.LightCyan -> Color.Cyan
            ratatui.style.Color.White -> Color.White
            is ratatui.style.Color.Rgb -> Color.Rgb(r, g, b)
            is ratatui.style.Color.Indexed -> Color.Ansi(index)
        }
    }

    private fun Modifier.toCrosstermAttributes(): Attributes {
        val result = mutableListOf<Attribute>()

        if (contains(Modifier.BOLD)) result.add(Attribute.Bold)
        if (contains(Modifier.DIM)) result.add(Attribute.Dim)
        if (contains(Modifier.ITALIC)) result.add(Attribute.Italic)
        if (contains(Modifier.UNDERLINED)) result.add(Attribute.Underlined)
        if (contains(Modifier.SLOW_BLINK)) result.add(Attribute.SlowBlink)
        if (contains(Modifier.RAPID_BLINK)) result.add(Attribute.RapidBlink)
        if (contains(Modifier.REVERSED)) result.add(Attribute.Reverse)
        if (contains(Modifier.HIDDEN)) result.add(Attribute.Hidden)
        if (contains(Modifier.CROSSED_OUT)) result.add(Attribute.CrossedOut)

        return Attributes.of(*result.toTypedArray())
    }
}

