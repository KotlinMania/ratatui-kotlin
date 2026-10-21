package consumer

import io.github.kotlinmania.bitflags.Flag
import io.github.kotlinmania.unicodewidth.UnicodeWidthStr
import io.github.kotlinmania.serdecore.de.Deserialize
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.ParseColorError
import ratatui.style.tryFrom
import ratatui.text.Text

fun publicTextWidth(value: String): Int {
    val text: UnicodeWidthStr = Text.from(value)
    return text.width()
}

fun publicModifierFlags(): List<Flag<Modifier>> = Modifier.BOLD.flags()

fun publicParseError(): ParseColorError = checkNotNull(Color.fromStr("invalid_color").error)

fun publicColorDeserializer(): Deserialize<Color> = Color

fun publicAnsiConversion(): ai.solace.tui.anstyle.Ansi256Color =
    ai.solace.tui.anstyle.Ansi256Color.tryFrom(Color.Indexed(42u)).unwrap()
