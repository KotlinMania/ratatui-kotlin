package consumer

import io.github.kotlinmania.bitflags.Flag
import io.github.kotlinmania.thiserror.StdError
import io.github.kotlinmania.unicodewidth.UnicodeWidthStr
import kotlinx.serialization.KSerializer
import ratatui.style.Color
import ratatui.style.ColorSerializer
import ratatui.style.Modifier
import ratatui.style.ParseColorError
import ratatui.text.Text

fun publicTextWidth(value: String): Int {
    val text: UnicodeWidthStr = Text.from(value)
    return text.width()
}

fun publicModifierFlags(): List<Flag<Modifier>> = Modifier.BOLD.flags()

fun publicParseError(): StdError = ParseColorError()

fun publicColorSerializer(): KSerializer<Color> = ColorSerializer
