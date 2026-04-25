// port-lint: source ratatui-core/src/style/anstyle.rs
package ratatui.style

import ai.solace.tui.anstyle.Ansi256Color
import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.Effects
import ai.solace.tui.anstyle.RgbColor
import ai.solace.tui.anstyle.Color as AnstyleColor
import ai.solace.tui.anstyle.Style as AnstyleStyle

/**
 * This module contains conversion functions for styles from the `anstyle` crate.
 *
 * Transliteration of `ratatui-core/src/style/anstyle.rs`.
 */

/** Error type for converting between `anstyle` colors and [Color]. */
enum class TryFromColorError(val description: String) {
    Ansi256("cannot convert Ratatui Color to an Ansi256Color as it is not an indexed color"),
    Ansi("cannot convert Ratatui Color to AnsiColor as it is not a 4-bit color"),
    RgbColor("cannot convert Ratatui Color to RgbColor as it is not an RGB color")
}

class TryFromColorException(val error: TryFromColorError) : IllegalArgumentException(error.description)

fun Ansi256Color.toRatatuiColor(): Color = Color.Indexed(index)

@Throws(TryFromColorException::class)
fun Color.toAnsi256Color(): Ansi256Color =
    when (this) {
        is Color.Indexed -> Ansi256Color(index)
        else -> throw TryFromColorException(TryFromColorError.Ansi256)
    }

fun AnsiColor.toRatatuiColor(): Color =
    when (this) {
        AnsiColor.Black -> Color.Black
        AnsiColor.Red -> Color.Red
        AnsiColor.Green -> Color.Green
        AnsiColor.Yellow -> Color.Yellow
        AnsiColor.Blue -> Color.Blue
        AnsiColor.Magenta -> Color.Magenta
        AnsiColor.Cyan -> Color.Cyan
        AnsiColor.White -> Color.Gray
        AnsiColor.BrightBlack -> Color.DarkGray
        AnsiColor.BrightRed -> Color.LightRed
        AnsiColor.BrightGreen -> Color.LightGreen
        AnsiColor.BrightYellow -> Color.LightYellow
        AnsiColor.BrightBlue -> Color.LightBlue
        AnsiColor.BrightMagenta -> Color.LightMagenta
        AnsiColor.BrightCyan -> Color.LightCyan
        AnsiColor.BrightWhite -> Color.White
    }

@Throws(TryFromColorException::class)
fun Color.toAnsiColor(): AnsiColor =
    when (this) {
        Color.Black -> AnsiColor.Black
        Color.Red -> AnsiColor.Red
        Color.Green -> AnsiColor.Green
        Color.Yellow -> AnsiColor.Yellow
        Color.Blue -> AnsiColor.Blue
        Color.Magenta -> AnsiColor.Magenta
        Color.Cyan -> AnsiColor.Cyan
        Color.Gray -> AnsiColor.White
        Color.DarkGray -> AnsiColor.BrightBlack
        Color.LightRed -> AnsiColor.BrightRed
        Color.LightGreen -> AnsiColor.BrightGreen
        Color.LightYellow -> AnsiColor.BrightYellow
        Color.LightBlue -> AnsiColor.BrightBlue
        Color.LightMagenta -> AnsiColor.BrightMagenta
        Color.LightCyan -> AnsiColor.BrightCyan
        Color.White -> AnsiColor.BrightWhite
        else -> throw TryFromColorException(TryFromColorError.Ansi)
    }

fun RgbColor.toRatatuiColor(): Color = Color.Rgb(r, g, b)

@Throws(TryFromColorException::class)
fun Color.toRgbColor(): RgbColor =
    when (this) {
        is Color.Rgb -> RgbColor(r, g, b)
        else -> throw TryFromColorException(TryFromColorError.RgbColor)
    }

fun AnstyleColor.toRatatuiColor(): Color =
    when (this) {
        is AnstyleColor.Ansi -> color.toRatatuiColor()
        is AnstyleColor.Ansi256 -> color.toRatatuiColor()
        is AnstyleColor.Rgb -> color.toRatatuiColor()
    }

/**
 * Converts a Ratatui [Color] into an [AnstyleColor].
 *
 * # Panics
 *
 * This method will throw if the input is [Color.Reset], as there is no equivalent representation
 * for a reset color in `anstyle`.
 */
fun Color.toAnstyleColor(): AnstyleColor =
    when (this) {
        Color.Reset -> error("Color::Reset has no equivalent in anstyle")
        is Color.Rgb -> AnstyleColor.Rgb(toRgbColor())
        is Color.Indexed -> AnstyleColor.Ansi256(toAnsi256Color())
        else -> AnstyleColor.Ansi(toAnsiColor())
    }

fun Effects.toModifier(): Modifier {
    var modifier = Modifier.empty()

    if (contains(Effects.BOLD)) {
        modifier = modifier or Modifier.BOLD
    }
    if (contains(Effects.DIMMED)) {
        modifier = modifier or Modifier.DIM
    }
    if (contains(Effects.ITALIC)) {
        modifier = modifier or Modifier.ITALIC
    }
    if (
        contains(Effects.UNDERLINE) ||
        contains(Effects.DOUBLE_UNDERLINE) ||
        contains(Effects.CURLY_UNDERLINE) ||
        contains(Effects.DOTTED_UNDERLINE) ||
        contains(Effects.DASHED_UNDERLINE)
    ) {
        modifier = modifier or Modifier.UNDERLINED
    }
    if (contains(Effects.BLINK)) {
        modifier = modifier or Modifier.SLOW_BLINK
    }
    if (contains(Effects.INVERT)) {
        modifier = modifier or Modifier.REVERSED
    }
    if (contains(Effects.HIDDEN)) {
        modifier = modifier or Modifier.HIDDEN
    }
    if (contains(Effects.STRIKETHROUGH)) {
        modifier = modifier or Modifier.CROSSED_OUT
    }

    return modifier
}

fun Modifier.toEffects(): Effects {
    var effects = Effects.new()

    if (contains(Modifier.BOLD)) {
        effects = effects.insert(Effects.BOLD)
    }
    if (contains(Modifier.DIM)) {
        effects = effects.insert(Effects.DIMMED)
    }
    if (contains(Modifier.ITALIC)) {
        effects = effects.insert(Effects.ITALIC)
    }
    if (contains(Modifier.UNDERLINED)) {
        effects = effects.insert(Effects.UNDERLINE)
    }
    if (contains(Modifier.SLOW_BLINK) || contains(Modifier.RAPID_BLINK)) {
        effects = effects.insert(Effects.BLINK)
    }
    if (contains(Modifier.REVERSED)) {
        effects = effects.insert(Effects.INVERT)
    }
    if (contains(Modifier.HIDDEN)) {
        effects = effects.insert(Effects.HIDDEN)
    }
    if (contains(Modifier.CROSSED_OUT)) {
        effects = effects.insert(Effects.STRIKETHROUGH)
    }

    return effects
}

fun AnstyleStyle.toRatatuiStyle(): Style =
    Style(
        fg = getFgColor()?.toRatatuiColor(),
        bg = getBgColor()?.toRatatuiColor(),
        underlineColor = getUnderlineColor()?.toRatatuiColor(),
        addModifier = getEffects().toModifier()
    )

fun Style.toAnstyleStyle(): AnstyleStyle {
    var anstyleStyle = AnstyleStyle()

    fg?.let { fg ->
        anstyleStyle = anstyleStyle.fgColor(fg.toAnstyleColor())
    }
    bg?.let { bg ->
        anstyleStyle = anstyleStyle.bgColor(bg.toAnstyleColor())
    }
    underlineColor?.let { underline ->
        anstyleStyle = anstyleStyle.underlineColor(underline.toAnstyleColor())
    }

    anstyleStyle = anstyleStyle.effects(addModifier.toEffects())

    return anstyleStyle
}
