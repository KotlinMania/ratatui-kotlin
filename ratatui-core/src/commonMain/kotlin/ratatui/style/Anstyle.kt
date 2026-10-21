// port-lint: source ratatui-core/src/style/anstyle.rs
package ratatui.style

import ai.solace.tui.anstyle.Ansi256Color
import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.Effects
import ai.solace.tui.anstyle.RgbColor
import ai.solace.tui.anstyle.Color as AnstyleColor
import ai.solace.tui.anstyle.Style as AnstyleStyle

/** Conversion functions for styles from the anstyle library. */

/** Error type for converting between anstyle colors and Color. */
enum class TryFromColorError(
    val message: String,
) {
    Ansi256("cannot convert Ratatui Color to an Ansi256Color as it is not an indexed color"),
    Ansi("cannot convert Ratatui Color to AnsiColor as it is not a 4-bit color"),
    RgbColor("cannot convert Ratatui Color to RgbColor as it is not an RGB color"),
    ;

    fun source(): TryFromColorError? = null

    override fun toString(): String = message
}

fun Color.Companion.from(color: Ansi256Color): Color = Color.Indexed(color.index)

fun Ansi256Color.Companion.tryFrom(color: Color): Ansi256ColorResult =
    when (color) {
        is Color.Indexed -> Ansi256ColorResult.ok(Ansi256Color(color.index))
        else -> Ansi256ColorResult.err(TryFromColorError.Ansi256)
    }

fun Color.Companion.from(color: AnsiColor): Color =
    when (color) {
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

fun AnsiColor.Companion.tryFrom(color: Color): AnsiColorResult {
    val converted = when (color) {
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
        else -> return AnsiColorResult.err(TryFromColorError.Ansi)
    }
    return AnsiColorResult.ok(converted)
}

fun Color.Companion.from(color: RgbColor): Color = Color.Rgb(color.r, color.g, color.b)

fun RgbColor.Companion.tryFrom(color: Color): RgbColorResult =
    when (color) {
        is Color.Rgb -> RgbColorResult.ok(RgbColor(color.r, color.g, color.b))
        else -> RgbColorResult.err(TryFromColorError.RgbColor)
    }

fun Color.Companion.from(color: AnstyleColor): Color =
    when (color) {
        is AnstyleColor.Ansi -> from(color.color)
        is AnstyleColor.Ansi256 -> from(color.color)
        is AnstyleColor.Rgb -> from(color.color)
    }

/**
 * Converts a Ratatui [Color] into an [AnstyleColor].
 *
 * # Panics
 *
 * This method will throw if the input is [Color.Reset], as there is no equivalent representation
 * for a reset color in `anstyle`.
 */
fun AnstyleColor.Companion.from(color: Color): AnstyleColor =
    when (color) {
        Color.Reset -> error("Color::Reset has no equivalent in anstyle")
        is Color.Rgb -> AnstyleColor.Rgb(RgbColor.tryFrom(color).unwrap())
        is Color.Indexed -> AnstyleColor.Ansi256(Ansi256Color.tryFrom(color).unwrap())
        else -> AnstyleColor.Ansi(AnsiColor.tryFrom(color).unwrap())
    }

fun Modifier.Companion.from(effect: Effects): Modifier {
    var modifier = Modifier.empty()

    if (effect.contains(Effects.BOLD)) {
        modifier = modifier or Modifier.BOLD
    }
    if (effect.contains(Effects.DIMMED)) {
        modifier = modifier or Modifier.DIM
    }
    if (effect.contains(Effects.ITALIC)) {
        modifier = modifier or Modifier.ITALIC
    }
    if (
        effect.contains(Effects.UNDERLINE) ||
        effect.contains(Effects.DOUBLE_UNDERLINE) ||
        effect.contains(Effects.CURLY_UNDERLINE) ||
        effect.contains(Effects.DOTTED_UNDERLINE) ||
        effect.contains(Effects.DASHED_UNDERLINE)
    ) {
        modifier = modifier or Modifier.UNDERLINED
    }
    if (effect.contains(Effects.BLINK)) {
        modifier = modifier or Modifier.SLOW_BLINK
    }
    if (effect.contains(Effects.INVERT)) {
        modifier = modifier or Modifier.REVERSED
    }
    if (effect.contains(Effects.HIDDEN)) {
        modifier = modifier or Modifier.HIDDEN
    }
    if (effect.contains(Effects.STRIKETHROUGH)) {
        modifier = modifier or Modifier.CROSSED_OUT
    }

    return modifier
}

fun Effects.Companion.from(modifier: Modifier): Effects {
    var effects = Effects.new()

    if (modifier.contains(Modifier.BOLD)) {
        effects = effects.insert(Effects.BOLD)
    }
    if (modifier.contains(Modifier.DIM)) {
        effects = effects.insert(Effects.DIMMED)
    }
    if (modifier.contains(Modifier.ITALIC)) {
        effects = effects.insert(Effects.ITALIC)
    }
    if (modifier.contains(Modifier.UNDERLINED)) {
        effects = effects.insert(Effects.UNDERLINE)
    }
    if (modifier.contains(Modifier.SLOW_BLINK) || modifier.contains(Modifier.RAPID_BLINK)) {
        effects = effects.insert(Effects.BLINK)
    }
    if (modifier.contains(Modifier.REVERSED)) {
        effects = effects.insert(Effects.INVERT)
    }
    if (modifier.contains(Modifier.HIDDEN)) {
        effects = effects.insert(Effects.HIDDEN)
    }
    if (modifier.contains(Modifier.CROSSED_OUT)) {
        effects = effects.insert(Effects.STRIKETHROUGH)
    }

    return effects
}

fun Style.Companion.from(style: AnstyleStyle): Style =
    Style(
        fg = style.getFgColor()?.let { Color.from(it) },
        bg = style.getBgColor()?.let { Color.from(it) },
        underlineColor = style.getUnderlineColor()?.let { Color.from(it) },
        addModifier = Modifier.from(style.getEffects())
    )

fun AnstyleStyle.Companion.from(style: Style): AnstyleStyle {
    var anstyleStyle = AnstyleStyle()

    style.fg?.let { fg ->
        anstyleStyle = anstyleStyle.fgColor(AnstyleColor.from(fg))
    }
    style.bg?.let { bg ->
        anstyleStyle = anstyleStyle.bgColor(AnstyleColor.from(bg))
    }
    style.underlineColor?.let { underline ->
        anstyleStyle = anstyleStyle.underlineColor(AnstyleColor.from(underline))
    }

    anstyleStyle = anstyleStyle.effects(Effects.from(style.addModifier))

    return anstyleStyle
}
