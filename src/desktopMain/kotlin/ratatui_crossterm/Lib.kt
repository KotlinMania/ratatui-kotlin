// port-lint: source ratatui-crossterm/src/lib.rs
/**
 * This crate provides [CrosstermBackend], an implementation of the [Backend] trait for the Ratatui
 * library. It uses the Crossterm library for all terminal manipulation.
 *
 * ## Crossterm Version and Re-export
 *
 * Rust `ratatui-crossterm` uses feature flags (`crossterm_0_xx`) to select a Crossterm version and
 * re-exports it as `ratatui_crossterm::crossterm` to avoid dependency graph version conflicts.
 *
 * This Kotlin port targets the published `io.github.kotlinmania:crossterm-kotlin` dependency.
 *
 * # Crate Organization
 *
 * `ratatui-crossterm` is part of the Ratatui workspace (modularized in 0.30.0). This file is a
 * “crate root” mirror for parity and will be split later.
 */
package ratatui_crossterm

import io.github.kotlinmania.crossterm.Command
import io.github.kotlinmania.crossterm.csi
import io.github.kotlinmania.crossterm.cursor.Hide
import io.github.kotlinmania.crossterm.cursor.MoveTo
import io.github.kotlinmania.crossterm.cursor.Show
import io.github.kotlinmania.crossterm.cursor.sys.position
import io.github.kotlinmania.crossterm.style.Attributes as CrosstermAttributes
import io.github.kotlinmania.crossterm.style.ContentStyle as CrosstermContentStyle
import io.github.kotlinmania.crossterm.style.Print
import io.github.kotlinmania.crossterm.style.SetAttribute
import io.github.kotlinmania.crossterm.style.SetBackgroundColor
import io.github.kotlinmania.crossterm.style.SetColors
import io.github.kotlinmania.crossterm.style.SetForegroundColor
import io.github.kotlinmania.crossterm.style.types.Attribute as CrosstermAttribute
import io.github.kotlinmania.crossterm.style.types.Color as CrosstermColor
import io.github.kotlinmania.crossterm.style.types.Colors as CrosstermColors
import io.github.kotlinmania.crossterm.terminal.Clear
import io.github.kotlinmania.crossterm.terminal.ClearType as CrosstermClearType
import io.github.kotlinmania.crossterm.terminal.sys.size as terminalSize
import io.github.kotlinmania.crossterm.terminal.windowSize as crosstermWindowSize
import ratatui.backend.Backend
import ratatui.backend.ClearType
import ratatui.layout.Position
import ratatui.layout.Size
import ratatui.buffer.Cell
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style

/**
 * A [Backend] implementation that uses Crossterm to render to the terminal.
 *
 * Transliteration target: `tmp/ratatui/ratatui-crossterm/src/lib.rs`.
 */
class CrosstermBackend(
    private val writer: Appendable,
    private val flushFn: () -> Unit = {}
) : Backend {

    override fun draw(content: Iterator<Triple<Int, Int, Cell>>) {
        var fg: Color = Color.Reset
        var bg: Color = Color.Reset
        var modifier: Modifier = Modifier.empty()
        var lastPos: Position? = null

        while (content.hasNext()) {
            val (x, y, cell) = content.next()
            // Move the cursor if the previous location was not (x - 1, y)
            val prev = lastPos
            if (!(prev != null && x == prev.x + 1 && y == prev.y)) {
                MoveTo(x.toUShort(), y.toUShort()).writeAnsi(writer)
            }
            lastPos = Position(x, y)

            if (cell.modifier != modifier) {
                ModifierDiff(from = modifier, to = cell.modifier).queue(writer)
                modifier = cell.modifier
            }

            if (cell.fg != fg || cell.bg != bg) {
                val colors = CrosstermColors(
                    foreground = cell.fg.intoCrossterm(),
                    background = cell.bg.intoCrossterm()
                )
                SetColors(colors).writeAnsi(writer)
                fg = cell.fg
                bg = cell.bg
            }

            Print(cell.symbol()).writeAnsi(writer)
        }

        SetForegroundColor(CrosstermColor.Reset).writeAnsi(writer)
        SetBackgroundColor(CrosstermColor.Reset).writeAnsi(writer)
        SetAttribute(CrosstermAttribute.Reset).writeAnsi(writer)
    }

    override fun hideCursor() {
        Hide.writeAnsi(writer)
    }

    override fun showCursor() {
        Show.writeAnsi(writer)
    }

    override fun getCursorPosition(): Position {
        val (x, y) = position()
        return Position(x.toInt(), y.toInt())
    }

    override fun setCursorPosition(position: Position) {
        MoveTo(position.x.toUShort(), position.y.toUShort()).writeAnsi(writer)
    }

    override fun clearRegion(clearType: ClearType) {
        val ct = when (clearType) {
            ClearType.All -> CrosstermClearType.All
            ClearType.AfterCursor -> CrosstermClearType.FromCursorDown
            ClearType.BeforeCursor -> CrosstermClearType.FromCursorUp
            ClearType.CurrentLine -> CrosstermClearType.CurrentLine
            ClearType.UntilNewLine -> CrosstermClearType.UntilNewLine
        }
        Clear(ct).writeAnsi(writer)
    }

    override fun size(): Size {
        val (width, height) = terminalSize()
        return Size(width.toInt(), height.toInt())
    }

    override fun flush() {
        flushFn()
    }

    fun clear() {
        clearRegion(ClearType.All)
    }

    override fun appendLines(n: UShort) {
        for (i in 0 until n.toInt()) {
            Print("\n").writeAnsi(writer)
        }
        flushFn()
    }

    fun windowSize(): io.github.kotlinmania.crossterm.terminal.WindowSize = crosstermWindowSize()

    fun scrollRegionUp(region: UShortRange, amount: UShort) {
        ScrollUpInRegion(
            firstRow = region.start,
            lastRow = region.endExclusive.saturatingSub(1u).toUShort(),
            linesToScroll = amount
        ).writeAnsi(writer)
        flushFn()
    }

    fun scrollRegionDown(region: UShortRange, amount: UShort) {
        ScrollDownInRegion(
            firstRow = region.start,
            lastRow = region.endExclusive.saturatingSub(1u).toUShort(),
            linesToScroll = amount
        ).writeAnsi(writer)
        flushFn()
    }
}

/**
 * Kotlin replacement for Rust `std::ops::Range<u16>` (end is exclusive).
 */
data class UShortRange(
    val start: UShort,
    val endExclusive: UShort
)

/**
 * Transliteration of `ratatui-crossterm/src/lib.rs` `ModifierDiff`.
 */
private data class ModifierDiff(
    val from: Modifier,
    val to: Modifier
) {
    fun queue(writer: Appendable) {
        val removed = from.difference(to)

        if (removed.contains(Modifier.REVERSED)) {
            SetAttribute(CrosstermAttribute.NoReverse).writeAnsi(writer)
        }

        val resetIntensity = removed.contains(Modifier.BOLD) || removed.contains(Modifier.DIM)
        if (resetIntensity) {
            // Bold and Dim are both reset by applying the Normal intensity.
            SetAttribute(CrosstermAttribute.NormalIntensity).writeAnsi(writer)

            // The remaining Bold and Dim attributes must be reapplied after the intensity reset.
            if (to.contains(Modifier.DIM)) {
                SetAttribute(CrosstermAttribute.Dim).writeAnsi(writer)
            }
            if (to.contains(Modifier.BOLD)) {
                SetAttribute(CrosstermAttribute.Bold).writeAnsi(writer)
            }
        }

        if (removed.contains(Modifier.ITALIC)) SetAttribute(CrosstermAttribute.NoItalic).writeAnsi(writer)
        if (removed.contains(Modifier.UNDERLINED)) SetAttribute(CrosstermAttribute.NoUnderline).writeAnsi(writer)
        if (removed.contains(Modifier.CROSSED_OUT)) SetAttribute(CrosstermAttribute.NotCrossedOut).writeAnsi(writer)
        if (removed.contains(Modifier.HIDDEN)) SetAttribute(CrosstermAttribute.NoHidden).writeAnsi(writer)
        if (removed.contains(Modifier.SLOW_BLINK) || removed.contains(Modifier.RAPID_BLINK)) {
            SetAttribute(CrosstermAttribute.NoBlink).writeAnsi(writer)
        }

        val added = to.difference(from)
        if (added.contains(Modifier.REVERSED)) SetAttribute(CrosstermAttribute.Reverse).writeAnsi(writer)
        if (added.contains(Modifier.BOLD) && !resetIntensity) SetAttribute(CrosstermAttribute.Bold).writeAnsi(writer)
        if (added.contains(Modifier.ITALIC)) SetAttribute(CrosstermAttribute.Italic).writeAnsi(writer)
        if (added.contains(Modifier.UNDERLINED)) SetAttribute(CrosstermAttribute.Underlined).writeAnsi(writer)
        if (added.contains(Modifier.DIM) && !resetIntensity) SetAttribute(CrosstermAttribute.Dim).writeAnsi(writer)
        if (added.contains(Modifier.CROSSED_OUT)) SetAttribute(CrosstermAttribute.CrossedOut).writeAnsi(writer)
        if (added.contains(Modifier.HIDDEN)) SetAttribute(CrosstermAttribute.Hidden).writeAnsi(writer)
        if (added.contains(Modifier.SLOW_BLINK)) SetAttribute(CrosstermAttribute.SlowBlink).writeAnsi(writer)
        if (added.contains(Modifier.RAPID_BLINK)) SetAttribute(CrosstermAttribute.RapidBlink).writeAnsi(writer)
    }
}

private data class ScrollUpInRegion(
    val firstRow: UShort,
    val lastRow: UShort,
    val linesToScroll: UShort
) : Command {
    override fun writeAnsi(writer: Appendable) {
        if (linesToScroll == 0.toUShort()) return
        writer.append(csi("${firstRow.saturatingAdd(1u)};${lastRow.saturatingAdd(1u)}r"))
        writer.append(csi("${linesToScroll}S"))
        writer.append(csi("r"))
    }
}

private data class ScrollDownInRegion(
    val firstRow: UShort,
    val lastRow: UShort,
    val linesToScroll: UShort
) : Command {
    override fun writeAnsi(writer: Appendable) {
        if (linesToScroll == 0.toUShort()) return
        writer.append(csi("${firstRow.saturatingAdd(1u)};${lastRow.saturatingAdd(1u)}r"))
        writer.append(csi("${linesToScroll}T"))
        writer.append(csi("r"))
    }
}

private fun UShort.saturatingAdd(other: UInt): UInt {
    val sum = this.toUInt() + other
    return if (sum > UShort.MAX_VALUE.toUInt()) UShort.MAX_VALUE.toUInt() else sum
}

private fun UShort.saturatingSub(other: UInt): UInt {
    val value = this.toUInt()
    return if (other > value) 0u else value - other
}

private fun Color.intoCrossterm(): CrosstermColor {
    return when (this) {
        Color.Reset -> CrosstermColor.Reset
        Color.Black -> CrosstermColor.Black
        Color.Red -> CrosstermColor.DarkRed
        Color.Green -> CrosstermColor.DarkGreen
        Color.Yellow -> CrosstermColor.DarkYellow
        Color.Blue -> CrosstermColor.DarkBlue
        Color.Magenta -> CrosstermColor.DarkMagenta
        Color.Cyan -> CrosstermColor.DarkCyan
        Color.Gray -> CrosstermColor.Grey
        Color.DarkGray -> CrosstermColor.DarkGrey
        Color.LightRed -> CrosstermColor.Red
        Color.LightGreen -> CrosstermColor.Green
        Color.LightBlue -> CrosstermColor.Blue
        Color.LightYellow -> CrosstermColor.Yellow
        Color.LightMagenta -> CrosstermColor.Magenta
        Color.LightCyan -> CrosstermColor.Cyan
        Color.White -> CrosstermColor.White
        is Color.Rgb -> CrosstermColor.Rgb(r, g, b)
        is Color.Indexed -> CrosstermColor.AnsiValue(index)
    }
}

private fun Color.Companion.fromCrossterm(value: CrosstermColor): Color {
    return when (value) {
        CrosstermColor.Reset -> Color.Reset
        CrosstermColor.Black -> Color.Black
        CrosstermColor.DarkRed -> Color.Red
        CrosstermColor.DarkGreen -> Color.Green
        CrosstermColor.DarkYellow -> Color.Yellow
        CrosstermColor.DarkBlue -> Color.Blue
        CrosstermColor.DarkMagenta -> Color.Magenta
        CrosstermColor.DarkCyan -> Color.Cyan
        CrosstermColor.Grey -> Color.Gray
        CrosstermColor.DarkGrey -> Color.DarkGray
        CrosstermColor.Red -> Color.LightRed
        CrosstermColor.Green -> Color.LightGreen
        CrosstermColor.Blue -> Color.LightBlue
        CrosstermColor.Yellow -> Color.LightYellow
        CrosstermColor.Magenta -> Color.LightMagenta
        CrosstermColor.Cyan -> Color.LightCyan
        CrosstermColor.White -> Color.White
        is CrosstermColor.Rgb -> Color.Rgb(value.r, value.g, value.b)
        is CrosstermColor.AnsiValue -> Color.Indexed(value.value)
    }
}

private fun Modifier.Companion.fromCrossterm(value: CrosstermAttribute): Modifier {
    // `Attribute*s*` contains multiple `Attribute`. Convert to `Attribute*s*` (containing only one value)
    // to avoid implementing the conversion again.
    return fromCrossterm(CrosstermAttributes.from(value))
}

private fun Modifier.Companion.fromCrossterm(value: CrosstermAttributes): Modifier {
    var res = Modifier.empty()
    if (value.has(CrosstermAttribute.Bold)) res = res or Modifier.BOLD
    if (value.has(CrosstermAttribute.Dim)) res = res or Modifier.DIM
    if (value.has(CrosstermAttribute.Italic)) res = res or Modifier.ITALIC
    if (
        value.has(CrosstermAttribute.Underlined) ||
        value.has(CrosstermAttribute.DoubleUnderlined) ||
        value.has(CrosstermAttribute.Undercurled) ||
        value.has(CrosstermAttribute.Underdotted) ||
        value.has(CrosstermAttribute.Underdashed)
    ) {
        res = res or Modifier.UNDERLINED
    }
    if (value.has(CrosstermAttribute.SlowBlink)) res = res or Modifier.SLOW_BLINK
    if (value.has(CrosstermAttribute.RapidBlink)) res = res or Modifier.RAPID_BLINK
    if (value.has(CrosstermAttribute.Reverse)) res = res or Modifier.REVERSED
    if (value.has(CrosstermAttribute.Hidden)) res = res or Modifier.HIDDEN
    if (value.has(CrosstermAttribute.CrossedOut)) res = res or Modifier.CROSSED_OUT
    return res
}

private fun Style.Companion.fromCrossterm(value: CrosstermContentStyle): Style {
    var subModifier = Modifier.empty()
    if (value.attributes.has(CrosstermAttribute.NoBold)) subModifier = subModifier or Modifier.BOLD
    if (value.attributes.has(CrosstermAttribute.NoItalic)) subModifier = subModifier or Modifier.ITALIC
    if (value.attributes.has(CrosstermAttribute.NotCrossedOut)) subModifier = subModifier or Modifier.CROSSED_OUT
    if (value.attributes.has(CrosstermAttribute.NoUnderline)) subModifier = subModifier or Modifier.UNDERLINED
    if (value.attributes.has(CrosstermAttribute.NoHidden)) subModifier = subModifier or Modifier.HIDDEN
    if (value.attributes.has(CrosstermAttribute.NoBlink)) {
        subModifier = subModifier or (Modifier.RAPID_BLINK or Modifier.SLOW_BLINK)
    }
    if (value.attributes.has(CrosstermAttribute.NoReverse)) subModifier = subModifier or Modifier.REVERSED

    return Style(
        fg = value.foregroundColor?.let { Color.fromCrossterm(it) },
        bg = value.backgroundColor?.let { Color.fromCrossterm(it) },
        underlineColor = value.underlineColor?.let { Color.fromCrossterm(it) },
        addModifier = Modifier.fromCrossterm(value.attributes),
        subModifier = subModifier
    )
}
