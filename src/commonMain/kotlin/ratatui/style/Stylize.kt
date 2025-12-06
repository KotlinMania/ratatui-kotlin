package ratatui.style

import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.Text

/**
 * A trait for objects that have a [Style].
 *
 * This trait enables generic code to be written that can interact with any object that has a
 * `Style`. This is used by the [Stylize] interface to allow generic code to be written that can
 * interact with any object that can be styled.
 */
interface Styled<T> {
    /**
     * Returns the style of the object.
     */
    fun getStyle(): Style

    /**
     * Sets the style of the object.
     *
     * `style` accepts any type that is convertible to [Style] (e.g. [Style], [Color], or
     * your own type that implements conversion to Style).
     */
    fun setStyle(style: Style): T
}

/**
 * A helper enum to make it easy to debug using the `Stylize` method names.
 */
enum class ColorDebugKind {
    Foreground,
    Background,
    Underline
}

/**
 * A helper class to make it easy to debug using the `Stylize` method names.
 */
data class ColorDebug(
    val kind: ColorDebugKind,
    val color: Color
) {
    override fun toString(): String {
        val isUnderline = kind == ColorDebugKind.Underline
        if (isUnderline || color is Color.Reset || color is Color.Indexed || color is Color.Rgb) {
            val prefix = when (kind) {
                ColorDebugKind.Foreground -> ".fg("
                ColorDebugKind.Background -> ".bg("
                ColorDebugKind.Underline -> ".underlineColor("
            }
            return "${prefix}Color.${colorDebugName(color)})"
        }

        val prefix = when (kind) {
            ColorDebugKind.Foreground -> "."
            ColorDebugKind.Background -> ".on_"
            ColorDebugKind.Underline -> error("covered by the first part of the if statement")
        }
        return "$prefix${colorMethodName(color)}()"
    }

    private fun colorDebugName(color: Color): String = when (color) {
        is Color.Reset -> "Reset"
        is Color.Black -> "Black"
        is Color.Red -> "Red"
        is Color.Green -> "Green"
        is Color.Yellow -> "Yellow"
        is Color.Blue -> "Blue"
        is Color.Magenta -> "Magenta"
        is Color.Cyan -> "Cyan"
        is Color.Gray -> "Gray"
        is Color.DarkGray -> "DarkGray"
        is Color.LightRed -> "LightRed"
        is Color.LightGreen -> "LightGreen"
        is Color.LightYellow -> "LightYellow"
        is Color.LightBlue -> "LightBlue"
        is Color.LightMagenta -> "LightMagenta"
        is Color.LightCyan -> "LightCyan"
        is Color.White -> "White"
        is Color.Indexed -> "Indexed(${color.index})"
        is Color.Rgb -> "Rgb(${color.r}, ${color.g}, ${color.b})"
    }

    private fun colorMethodName(color: Color): String = when (color) {
        is Color.Black -> "black"
        is Color.Red -> "red"
        is Color.Green -> "green"
        is Color.Yellow -> "yellow"
        is Color.Blue -> "blue"
        is Color.Magenta -> "magenta"
        is Color.Cyan -> "cyan"
        is Color.Gray -> "gray"
        is Color.DarkGray -> "dark_gray"
        is Color.LightRed -> "light_red"
        is Color.LightGreen -> "light_green"
        is Color.LightYellow -> "light_yellow"
        is Color.LightBlue -> "light_blue"
        is Color.LightMagenta -> "light_magenta"
        is Color.LightCyan -> "light_cyan"
        is Color.White -> "white"
        else -> error("covered by the first part of the if statement")
    }
}

/**
 * An extension trait for styling objects.
 *
 * For any type that implements `Stylize`, the provided methods in this trait can be used to style
 * the type further. This trait is automatically implemented for any type that implements the
 * [Styled] trait which e.g.: [String], [Span], [Style] and many Widget types.
 *
 * This results in much more ergonomic styling of text and widgets. For example, instead of
 * writing:
 *
 * ```kotlin
 * val text = Span.styled("Hello", Style.default().fg(Color.Red).bg(Color.Blue))
 * ```
 *
 * You can write:
 *
 * ```kotlin
 * val text = "Hello".red().onBlue()
 * ```
 *
 * This trait implements a provided method for every color as both foreground and background
 * (prefixed by `on`), and all modifiers as both an additive and subtractive modifier (prefixed
 * by `not`). The `reset()` method is also provided to reset the style.
 *
 * # Examples
 * ```kotlin
 * val span = "hello".red().onBlue().bold()
 * val line = Line.from(listOf(
 *     "hello".red().onBlue().bold(),
 *     "world".green().onYellow().notBold(),
 * ))
 * val paragraph = Paragraph.new(line).italic().underlined()
 * val block = Block.bordered().title("Title").onWhite().bold()
 * ```
 */
interface Stylize<T> {
    fun bg(color: Color): T
    fun fg(color: Color): T
    fun reset(): T
    fun addModifier(modifier: Modifier): T
    fun removeModifier(modifier: Modifier): T

    // Color methods - foreground
    fun black(): T = fg(Color.Black)
    fun red(): T = fg(Color.Red)
    fun green(): T = fg(Color.Green)
    fun yellow(): T = fg(Color.Yellow)
    fun blue(): T = fg(Color.Blue)
    fun magenta(): T = fg(Color.Magenta)
    fun cyan(): T = fg(Color.Cyan)
    fun gray(): T = fg(Color.Gray)
    fun darkGray(): T = fg(Color.DarkGray)
    fun lightRed(): T = fg(Color.LightRed)
    fun lightGreen(): T = fg(Color.LightGreen)
    fun lightYellow(): T = fg(Color.LightYellow)
    fun lightBlue(): T = fg(Color.LightBlue)
    fun lightMagenta(): T = fg(Color.LightMagenta)
    fun lightCyan(): T = fg(Color.LightCyan)
    fun white(): T = fg(Color.White)

    // Color methods - background
    fun onBlack(): T = bg(Color.Black)
    fun onRed(): T = bg(Color.Red)
    fun onGreen(): T = bg(Color.Green)
    fun onYellow(): T = bg(Color.Yellow)
    fun onBlue(): T = bg(Color.Blue)
    fun onMagenta(): T = bg(Color.Magenta)
    fun onCyan(): T = bg(Color.Cyan)
    fun onGray(): T = bg(Color.Gray)
    fun onDarkGray(): T = bg(Color.DarkGray)
    fun onLightRed(): T = bg(Color.LightRed)
    fun onLightGreen(): T = bg(Color.LightGreen)
    fun onLightYellow(): T = bg(Color.LightYellow)
    fun onLightBlue(): T = bg(Color.LightBlue)
    fun onLightMagenta(): T = bg(Color.LightMagenta)
    fun onLightCyan(): T = bg(Color.LightCyan)
    fun onWhite(): T = bg(Color.White)

    // Modifier methods - add
    fun bold(): T = addModifier(Modifier.BOLD)
    fun dim(): T = addModifier(Modifier.DIM)
    fun italic(): T = addModifier(Modifier.ITALIC)
    fun underlined(): T = addModifier(Modifier.UNDERLINED)
    fun slowBlink(): T = addModifier(Modifier.SLOW_BLINK)
    fun rapidBlink(): T = addModifier(Modifier.RAPID_BLINK)
    fun reversed(): T = addModifier(Modifier.REVERSED)
    fun hidden(): T = addModifier(Modifier.HIDDEN)
    fun crossedOut(): T = addModifier(Modifier.CROSSED_OUT)

    // Modifier methods - remove
    fun notBold(): T = removeModifier(Modifier.BOLD)
    fun notDim(): T = removeModifier(Modifier.DIM)
    fun notItalic(): T = removeModifier(Modifier.ITALIC)
    fun notUnderlined(): T = removeModifier(Modifier.UNDERLINED)
    fun notSlowBlink(): T = removeModifier(Modifier.SLOW_BLINK)
    fun notRapidBlink(): T = removeModifier(Modifier.RAPID_BLINK)
    fun notReversed(): T = removeModifier(Modifier.REVERSED)
    fun notHidden(): T = removeModifier(Modifier.HIDDEN)
    fun notCrossedOut(): T = removeModifier(Modifier.CROSSED_OUT)
}

/**
 * Default implementation of Stylize for any Styled type.
 */
class StylizeImpl<T>(private val styled: Styled<T>) : Stylize<T> {
    override fun bg(color: Color): T {
        val style = styled.getStyle().bg(color)
        return styled.setStyle(style)
    }

    override fun fg(color: Color): T {
        val style = styled.getStyle().fg(color)
        return styled.setStyle(style)
    }

    override fun addModifier(modifier: Modifier): T {
        val style = styled.getStyle().addModifier(modifier)
        return styled.setStyle(style)
    }

    override fun removeModifier(modifier: Modifier): T {
        val style = styled.getStyle().removeModifier(modifier)
        return styled.setStyle(style)
    }

    override fun reset(): T {
        return styled.setStyle(Style.reset())
    }
}

// Extension functions for String to enable styling
fun String.toStyled(): StringStyled = StringStyled(this)

/**
 * Wrapper class to make String implement Styled.
 */
class StringStyled(private val content: String) : Styled<Span> {
    override fun getStyle(): Style = Style.default()

    override fun setStyle(style: Style): Span = Span.styled(content, style)
}

// String extension functions for styling
fun String.fg(color: Color): Span = Span.styled(this, Style.default().fg(color))
fun String.bg(color: Color): Span = Span.styled(this, Style.default().bg(color))
fun String.addModifier(modifier: Modifier): Span = Span.styled(this, Style.default().addModifier(modifier))
fun String.removeModifier(modifier: Modifier): Span = Span.styled(this, Style.default().removeModifier(modifier))
fun String.styleReset(): Span = Span.styled(this, Style.reset())

// Foreground color shortcuts for String
fun String.black(): Span = fg(Color.Black)
fun String.red(): Span = fg(Color.Red)
fun String.green(): Span = fg(Color.Green)
fun String.yellow(): Span = fg(Color.Yellow)
fun String.blue(): Span = fg(Color.Blue)
fun String.magenta(): Span = fg(Color.Magenta)
fun String.cyan(): Span = fg(Color.Cyan)
fun String.gray(): Span = fg(Color.Gray)
fun String.darkGray(): Span = fg(Color.DarkGray)
fun String.lightRed(): Span = fg(Color.LightRed)
fun String.lightGreen(): Span = fg(Color.LightGreen)
fun String.lightYellow(): Span = fg(Color.LightYellow)
fun String.lightBlue(): Span = fg(Color.LightBlue)
fun String.lightMagenta(): Span = fg(Color.LightMagenta)
fun String.lightCyan(): Span = fg(Color.LightCyan)
fun String.white(): Span = fg(Color.White)

// Background color shortcuts for String
fun String.onBlack(): Span = bg(Color.Black)
fun String.onRed(): Span = bg(Color.Red)
fun String.onGreen(): Span = bg(Color.Green)
fun String.onYellow(): Span = bg(Color.Yellow)
fun String.onBlue(): Span = bg(Color.Blue)
fun String.onMagenta(): Span = bg(Color.Magenta)
fun String.onCyan(): Span = bg(Color.Cyan)
fun String.onGray(): Span = bg(Color.Gray)
fun String.onDarkGray(): Span = bg(Color.DarkGray)
fun String.onLightRed(): Span = bg(Color.LightRed)
fun String.onLightGreen(): Span = bg(Color.LightGreen)
fun String.onLightYellow(): Span = bg(Color.LightYellow)
fun String.onLightBlue(): Span = bg(Color.LightBlue)
fun String.onLightMagenta(): Span = bg(Color.LightMagenta)
fun String.onLightCyan(): Span = bg(Color.LightCyan)
fun String.onWhite(): Span = bg(Color.White)

// Modifier shortcuts for String
fun String.bold(): Span = addModifier(Modifier.BOLD)
fun String.dim(): Span = addModifier(Modifier.DIM)
fun String.italic(): Span = addModifier(Modifier.ITALIC)
fun String.underlined(): Span = addModifier(Modifier.UNDERLINED)
fun String.slowBlink(): Span = addModifier(Modifier.SLOW_BLINK)
fun String.rapidBlink(): Span = addModifier(Modifier.RAPID_BLINK)
fun String.reversed(): Span = addModifier(Modifier.REVERSED)
fun String.hidden(): Span = addModifier(Modifier.HIDDEN)
fun String.crossedOut(): Span = addModifier(Modifier.CROSSED_OUT)

fun String.notBold(): Span = removeModifier(Modifier.BOLD)
fun String.notDim(): Span = removeModifier(Modifier.DIM)
fun String.notItalic(): Span = removeModifier(Modifier.ITALIC)
fun String.notUnderlined(): Span = removeModifier(Modifier.UNDERLINED)
fun String.notSlowBlink(): Span = removeModifier(Modifier.SLOW_BLINK)
fun String.notRapidBlink(): Span = removeModifier(Modifier.RAPID_BLINK)
fun String.notReversed(): Span = removeModifier(Modifier.REVERSED)
fun String.notHidden(): Span = removeModifier(Modifier.HIDDEN)
fun String.notCrossedOut(): Span = removeModifier(Modifier.CROSSED_OUT)

// Extension functions for Span to chain styles
fun Span.fg(color: Color): Span = patchStyle(Style.default().fg(color))
fun Span.bg(color: Color): Span = patchStyle(Style.default().bg(color))
fun Span.addModifier(modifier: Modifier): Span = patchStyle(Style.default().addModifier(modifier))
fun Span.removeModifier(modifier: Modifier): Span = patchStyle(Style.default().removeModifier(modifier))

// Foreground color shortcuts for Span
fun Span.black(): Span = fg(Color.Black)
fun Span.red(): Span = fg(Color.Red)
fun Span.green(): Span = fg(Color.Green)
fun Span.yellow(): Span = fg(Color.Yellow)
fun Span.blue(): Span = fg(Color.Blue)
fun Span.magenta(): Span = fg(Color.Magenta)
fun Span.cyan(): Span = fg(Color.Cyan)
fun Span.gray(): Span = fg(Color.Gray)
fun Span.darkGray(): Span = fg(Color.DarkGray)
fun Span.lightRed(): Span = fg(Color.LightRed)
fun Span.lightGreen(): Span = fg(Color.LightGreen)
fun Span.lightYellow(): Span = fg(Color.LightYellow)
fun Span.lightBlue(): Span = fg(Color.LightBlue)
fun Span.lightMagenta(): Span = fg(Color.LightMagenta)
fun Span.lightCyan(): Span = fg(Color.LightCyan)
fun Span.white(): Span = fg(Color.White)

// Background color shortcuts for Span
fun Span.onBlack(): Span = bg(Color.Black)
fun Span.onRed(): Span = bg(Color.Red)
fun Span.onGreen(): Span = bg(Color.Green)
fun Span.onYellow(): Span = bg(Color.Yellow)
fun Span.onBlue(): Span = bg(Color.Blue)
fun Span.onMagenta(): Span = bg(Color.Magenta)
fun Span.onCyan(): Span = bg(Color.Cyan)
fun Span.onGray(): Span = bg(Color.Gray)
fun Span.onDarkGray(): Span = bg(Color.DarkGray)
fun Span.onLightRed(): Span = bg(Color.LightRed)
fun Span.onLightGreen(): Span = bg(Color.LightGreen)
fun Span.onLightYellow(): Span = bg(Color.LightYellow)
fun Span.onLightBlue(): Span = bg(Color.LightBlue)
fun Span.onLightMagenta(): Span = bg(Color.LightMagenta)
fun Span.onLightCyan(): Span = bg(Color.LightCyan)
fun Span.onWhite(): Span = bg(Color.White)

// Modifier shortcuts for Span
fun Span.bold(): Span = addModifier(Modifier.BOLD)
fun Span.dim(): Span = addModifier(Modifier.DIM)
fun Span.italic(): Span = addModifier(Modifier.ITALIC)
fun Span.underlined(): Span = addModifier(Modifier.UNDERLINED)
fun Span.slowBlink(): Span = addModifier(Modifier.SLOW_BLINK)
fun Span.rapidBlink(): Span = addModifier(Modifier.RAPID_BLINK)
fun Span.reversed(): Span = addModifier(Modifier.REVERSED)
fun Span.hidden(): Span = addModifier(Modifier.HIDDEN)
fun Span.crossedOut(): Span = addModifier(Modifier.CROSSED_OUT)

fun Span.notBold(): Span = removeModifier(Modifier.BOLD)
fun Span.notDim(): Span = removeModifier(Modifier.DIM)
fun Span.notItalic(): Span = removeModifier(Modifier.ITALIC)
fun Span.notUnderlined(): Span = removeModifier(Modifier.UNDERLINED)
fun Span.notSlowBlink(): Span = removeModifier(Modifier.SLOW_BLINK)
fun Span.notRapidBlink(): Span = removeModifier(Modifier.RAPID_BLINK)
fun Span.notReversed(): Span = removeModifier(Modifier.REVERSED)
fun Span.notHidden(): Span = removeModifier(Modifier.HIDDEN)
fun Span.notCrossedOut(): Span = removeModifier(Modifier.CROSSED_OUT)

// Extension functions for primitive types to create styled spans
fun Boolean.red(): Span = this.toString().red()
fun Boolean.green(): Span = this.toString().green()
fun Boolean.blue(): Span = this.toString().blue()
fun Boolean.yellow(): Span = this.toString().yellow()
fun Boolean.cyan(): Span = this.toString().cyan()
fun Boolean.magenta(): Span = this.toString().magenta()
fun Boolean.white(): Span = this.toString().white()
fun Boolean.black(): Span = this.toString().black()
fun Boolean.gray(): Span = this.toString().gray()

fun Char.red(): Span = this.toString().red()
fun Char.green(): Span = this.toString().green()
fun Char.blue(): Span = this.toString().blue()
fun Char.yellow(): Span = this.toString().yellow()
fun Char.cyan(): Span = this.toString().cyan()
fun Char.magenta(): Span = this.toString().magenta()
fun Char.white(): Span = this.toString().white()
fun Char.black(): Span = this.toString().black()
fun Char.gray(): Span = this.toString().gray()

fun Int.red(): Span = this.toString().red()
fun Int.green(): Span = this.toString().green()
fun Int.blue(): Span = this.toString().blue()
fun Int.yellow(): Span = this.toString().yellow()
fun Int.cyan(): Span = this.toString().cyan()
fun Int.magenta(): Span = this.toString().magenta()
fun Int.white(): Span = this.toString().white()
fun Int.black(): Span = this.toString().black()
fun Int.gray(): Span = this.toString().gray()

fun Long.red(): Span = this.toString().red()
fun Long.green(): Span = this.toString().green()
fun Long.blue(): Span = this.toString().blue()
fun Long.yellow(): Span = this.toString().yellow()
fun Long.cyan(): Span = this.toString().cyan()
fun Long.magenta(): Span = this.toString().magenta()
fun Long.white(): Span = this.toString().white()
fun Long.black(): Span = this.toString().black()
fun Long.gray(): Span = this.toString().gray()

fun Float.red(): Span = this.toString().red()
fun Float.green(): Span = this.toString().green()
fun Float.blue(): Span = this.toString().blue()
fun Float.yellow(): Span = this.toString().yellow()
fun Float.cyan(): Span = this.toString().cyan()
fun Float.magenta(): Span = this.toString().magenta()
fun Float.white(): Span = this.toString().white()
fun Float.black(): Span = this.toString().black()
fun Float.gray(): Span = this.toString().gray()

fun Double.red(): Span = this.toString().red()
fun Double.green(): Span = this.toString().green()
fun Double.blue(): Span = this.toString().blue()
fun Double.yellow(): Span = this.toString().yellow()
fun Double.cyan(): Span = this.toString().cyan()
fun Double.magenta(): Span = this.toString().magenta()
fun Double.white(): Span = this.toString().white()
fun Double.black(): Span = this.toString().black()
fun Double.gray(): Span = this.toString().gray()

// Color helper extension for stylize debug
fun Color.stylizeDebug(kind: ColorDebugKind): ColorDebug = ColorDebug(kind, this)

// Extension functions for Line to enable styling
fun Line.fg(color: Color): Line = patchStyle(Style.default().fg(color))
fun Line.bg(color: Color): Line = patchStyle(Style.default().bg(color))
fun Line.addModifier(modifier: Modifier): Line = patchStyle(Style.default().addModifier(modifier))
fun Line.removeModifier(modifier: Modifier): Line = patchStyle(Style.default().removeModifier(modifier))

// Foreground color shortcuts for Line
fun Line.black(): Line = fg(Color.Black)
fun Line.red(): Line = fg(Color.Red)
fun Line.green(): Line = fg(Color.Green)
fun Line.yellow(): Line = fg(Color.Yellow)
fun Line.blue(): Line = fg(Color.Blue)
fun Line.magenta(): Line = fg(Color.Magenta)
fun Line.cyan(): Line = fg(Color.Cyan)
fun Line.gray(): Line = fg(Color.Gray)
fun Line.darkGray(): Line = fg(Color.DarkGray)
fun Line.lightRed(): Line = fg(Color.LightRed)
fun Line.lightGreen(): Line = fg(Color.LightGreen)
fun Line.lightYellow(): Line = fg(Color.LightYellow)
fun Line.lightBlue(): Line = fg(Color.LightBlue)
fun Line.lightMagenta(): Line = fg(Color.LightMagenta)
fun Line.lightCyan(): Line = fg(Color.LightCyan)
fun Line.white(): Line = fg(Color.White)

// Background color shortcuts for Line
fun Line.onBlack(): Line = bg(Color.Black)
fun Line.onRed(): Line = bg(Color.Red)
fun Line.onGreen(): Line = bg(Color.Green)
fun Line.onYellow(): Line = bg(Color.Yellow)
fun Line.onBlue(): Line = bg(Color.Blue)
fun Line.onMagenta(): Line = bg(Color.Magenta)
fun Line.onCyan(): Line = bg(Color.Cyan)
fun Line.onGray(): Line = bg(Color.Gray)
fun Line.onDarkGray(): Line = bg(Color.DarkGray)
fun Line.onLightRed(): Line = bg(Color.LightRed)
fun Line.onLightGreen(): Line = bg(Color.LightGreen)
fun Line.onLightYellow(): Line = bg(Color.LightYellow)
fun Line.onLightBlue(): Line = bg(Color.LightBlue)
fun Line.onLightMagenta(): Line = bg(Color.LightMagenta)
fun Line.onLightCyan(): Line = bg(Color.LightCyan)
fun Line.onWhite(): Line = bg(Color.White)

// Modifier shortcuts for Line
fun Line.bold(): Line = addModifier(Modifier.BOLD)
fun Line.dim(): Line = addModifier(Modifier.DIM)
fun Line.italic(): Line = addModifier(Modifier.ITALIC)
fun Line.underlined(): Line = addModifier(Modifier.UNDERLINED)
fun Line.slowBlink(): Line = addModifier(Modifier.SLOW_BLINK)
fun Line.rapidBlink(): Line = addModifier(Modifier.RAPID_BLINK)
fun Line.reversed(): Line = addModifier(Modifier.REVERSED)
fun Line.hidden(): Line = addModifier(Modifier.HIDDEN)
fun Line.crossedOut(): Line = addModifier(Modifier.CROSSED_OUT)

fun Line.notBold(): Line = removeModifier(Modifier.BOLD)
fun Line.notDim(): Line = removeModifier(Modifier.DIM)
fun Line.notItalic(): Line = removeModifier(Modifier.ITALIC)
fun Line.notUnderlined(): Line = removeModifier(Modifier.UNDERLINED)
fun Line.notSlowBlink(): Line = removeModifier(Modifier.SLOW_BLINK)
fun Line.notRapidBlink(): Line = removeModifier(Modifier.RAPID_BLINK)
fun Line.notReversed(): Line = removeModifier(Modifier.REVERSED)
fun Line.notHidden(): Line = removeModifier(Modifier.HIDDEN)
fun Line.notCrossedOut(): Line = removeModifier(Modifier.CROSSED_OUT)

// Extension functions for Text to enable styling
fun Text.fg(color: Color): Text = patchStyle(Style.default().fg(color))
fun Text.bg(color: Color): Text = patchStyle(Style.default().bg(color))
fun Text.addModifier(modifier: Modifier): Text = patchStyle(Style.default().addModifier(modifier))
fun Text.removeModifier(modifier: Modifier): Text = patchStyle(Style.default().removeModifier(modifier))

// Foreground color shortcuts for Text
fun Text.black(): Text = fg(Color.Black)
fun Text.red(): Text = fg(Color.Red)
fun Text.green(): Text = fg(Color.Green)
fun Text.yellow(): Text = fg(Color.Yellow)
fun Text.blue(): Text = fg(Color.Blue)
fun Text.magenta(): Text = fg(Color.Magenta)
fun Text.cyan(): Text = fg(Color.Cyan)
fun Text.gray(): Text = fg(Color.Gray)
fun Text.darkGray(): Text = fg(Color.DarkGray)
fun Text.lightRed(): Text = fg(Color.LightRed)
fun Text.lightGreen(): Text = fg(Color.LightGreen)
fun Text.lightYellow(): Text = fg(Color.LightYellow)
fun Text.lightBlue(): Text = fg(Color.LightBlue)
fun Text.lightMagenta(): Text = fg(Color.LightMagenta)
fun Text.lightCyan(): Text = fg(Color.LightCyan)
fun Text.white(): Text = fg(Color.White)

// Background color shortcuts for Text
fun Text.onBlack(): Text = bg(Color.Black)
fun Text.onRed(): Text = bg(Color.Red)
fun Text.onGreen(): Text = bg(Color.Green)
fun Text.onYellow(): Text = bg(Color.Yellow)
fun Text.onBlue(): Text = bg(Color.Blue)
fun Text.onMagenta(): Text = bg(Color.Magenta)
fun Text.onCyan(): Text = bg(Color.Cyan)
fun Text.onGray(): Text = bg(Color.Gray)
fun Text.onDarkGray(): Text = bg(Color.DarkGray)
fun Text.onLightRed(): Text = bg(Color.LightRed)
fun Text.onLightGreen(): Text = bg(Color.LightGreen)
fun Text.onLightYellow(): Text = bg(Color.LightYellow)
fun Text.onLightBlue(): Text = bg(Color.LightBlue)
fun Text.onLightMagenta(): Text = bg(Color.LightMagenta)
fun Text.onLightCyan(): Text = bg(Color.LightCyan)
fun Text.onWhite(): Text = bg(Color.White)

// Modifier shortcuts for Text
fun Text.bold(): Text = addModifier(Modifier.BOLD)
fun Text.dim(): Text = addModifier(Modifier.DIM)
fun Text.italic(): Text = addModifier(Modifier.ITALIC)
fun Text.underlined(): Text = addModifier(Modifier.UNDERLINED)
fun Text.slowBlink(): Text = addModifier(Modifier.SLOW_BLINK)
fun Text.rapidBlink(): Text = addModifier(Modifier.RAPID_BLINK)
fun Text.reversed(): Text = addModifier(Modifier.REVERSED)
fun Text.hidden(): Text = addModifier(Modifier.HIDDEN)
fun Text.crossedOut(): Text = addModifier(Modifier.CROSSED_OUT)

fun Text.notBold(): Text = removeModifier(Modifier.BOLD)
fun Text.notDim(): Text = removeModifier(Modifier.DIM)
fun Text.notItalic(): Text = removeModifier(Modifier.ITALIC)
fun Text.notUnderlined(): Text = removeModifier(Modifier.UNDERLINED)
fun Text.notSlowBlink(): Text = removeModifier(Modifier.SLOW_BLINK)
fun Text.notRapidBlink(): Text = removeModifier(Modifier.RAPID_BLINK)
fun Text.notReversed(): Text = removeModifier(Modifier.REVERSED)
fun Text.notHidden(): Text = removeModifier(Modifier.HIDDEN)
fun Text.notCrossedOut(): Text = removeModifier(Modifier.CROSSED_OUT)
