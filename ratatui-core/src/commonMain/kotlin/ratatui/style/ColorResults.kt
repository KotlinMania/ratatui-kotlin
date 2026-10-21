package ratatui.style

import ai.solace.tui.anstyle.Ansi256Color
import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.RgbColor

/** A successful Color value or its conversion error. */
class ColorParseResult private constructor(
    val value: Color?,
    val error: ParseColorError?,
) {
    val isOk: Boolean get() = error == null
    val isErr: Boolean get() = error != null

    fun unwrap(): Color = value ?: throw ColorParseException(checkNotNull(error))

    companion object {
        fun ok(value: Color): ColorParseResult = ColorParseResult(value, null)
        fun err(error: ParseColorError): ColorParseResult = ColorParseResult(null, error)
    }
}

/** A successful Ansi256Color value or its conversion error. */
class Ansi256ColorResult private constructor(
    val value: Ansi256Color?,
    val error: TryFromColorError?,
) {
    val isOk: Boolean get() = error == null
    val isErr: Boolean get() = error != null

    fun unwrap(): Ansi256Color = value ?: throw TryFromColorException(checkNotNull(error))

    companion object {
        fun ok(value: Ansi256Color): Ansi256ColorResult = Ansi256ColorResult(value, null)
        fun err(error: TryFromColorError): Ansi256ColorResult = Ansi256ColorResult(null, error)
    }
}

/** A successful AnsiColor value or its conversion error. */
class AnsiColorResult private constructor(
    val value: AnsiColor?,
    val error: TryFromColorError?,
) {
    val isOk: Boolean get() = error == null
    val isErr: Boolean get() = error != null

    fun unwrap(): AnsiColor = value ?: throw TryFromColorException(checkNotNull(error))

    companion object {
        fun ok(value: AnsiColor): AnsiColorResult = AnsiColorResult(value, null)
        fun err(error: TryFromColorError): AnsiColorResult = AnsiColorResult(null, error)
    }
}

/** A successful RgbColor value or its conversion error. */
class RgbColorResult private constructor(
    val value: RgbColor?,
    val error: TryFromColorError?,
) {
    val isOk: Boolean get() = error == null
    val isErr: Boolean get() = error != null

    fun unwrap(): RgbColor = value ?: throw TryFromColorException(checkNotNull(error))

    companion object {
        fun ok(value: RgbColor): RgbColorResult = RgbColorResult(value, null)
        fun err(error: TryFromColorError): RgbColorResult = RgbColorResult(null, error)
    }
}

internal class ColorParseException(val error: ParseColorError) : IllegalArgumentException(error.message)

internal class TryFromColorException(val error: TryFromColorError) : IllegalArgumentException(error.message)
