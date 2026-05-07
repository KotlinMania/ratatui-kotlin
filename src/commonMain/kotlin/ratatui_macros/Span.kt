// port-lint: source ratatui-macros/src/span.rs
package ratatui_macros

import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.text.Span

/**
 * A macro for creating a [Span] using formatting syntax.
 *
 * `span` is similar in spirit to Rust's `format!`, but it returns a [Span] instead of a [String].
 *
 * Rust supports compile-time format string checking; Kotlin does not. This Kotlin transliteration
 * provides a small subset of the Rust formatting surface used in Ratatui's docs/tests:
 * - positional placeholders: `{}` (consume `args` in order)
 * - named placeholders: `{name}` (resolved via `namedArgs`)
 * - zero padding width specifier for numbers: `{name:04}`
 *
 * If you pass a style as the first argument, a styled [Span] is created. Otherwise a raw [Span] is
 * created.
 *
 * Transliteration target: `ratatui-macros/src/span.rs`.
 */
fun span(string: String): Span = Span.raw(string)

/** Positional formatting (`{}`) into a raw [Span]. */
fun span(format: String, vararg args: Any?): Span =
    Span.raw(rustFormat(format, emptyMap(), args))

/** Named formatting (`{name}`) into a raw [Span]. */
fun span(format: String, namedArgs: Map<String, Any?>): Span =
    Span.raw(rustFormat(format, namedArgs, emptyArray()))

/** Mixed named + positional formatting into a raw [Span]. */
fun span(format: String, namedArgs: Map<String, Any?>, vararg args: Any?): Span =
    Span.raw(rustFormat(format, namedArgs, args))

/** Expression formatting into a raw [Span]. */
fun span(expr: Any?): Span = Span.raw(expr.toString())

/** Styled literal string. */
fun span(style: Any, string: String): Span = Span.styled(string, intoStyle(style))

/** Styled positional formatting (`{}`) into a [Span]. */
fun span(style: Any, format: String, vararg args: Any?): Span =
    Span.styled(rustFormat(format, emptyMap(), args), intoStyle(style))

/** Styled named formatting (`{name}`) into a [Span]. */
fun span(style: Any, format: String, namedArgs: Map<String, Any?>): Span =
    Span.styled(rustFormat(format, namedArgs, emptyArray()), intoStyle(style))

/** Styled mixed named + positional formatting into a [Span]. */
fun span(style: Any, format: String, namedArgs: Map<String, Any?>, vararg args: Any?): Span =
    Span.styled(rustFormat(format, namedArgs, args), intoStyle(style))

/** Styled expression formatting into a [Span]. */
fun span(style: Any, expr: Any?): Span = Span.styled(expr.toString(), intoStyle(style))

private fun intoStyle(value: Any): Style = when (value) {
    is Style -> value
    is Color -> Style.from(value)
    is Modifier -> Style.from(value)
    else -> error("Unsupported style value: ${value::class.simpleName}")
}

private fun rustFormat(format: String, namedArgs: Map<String, Any?>, args: Array<out Any?>): String {
    val out = StringBuilder(format.length + 16)
    var i = 0
    var positionalIndex = 0

    while (i < format.length) {
        val ch = format[i]
        if (ch != '{' && ch != '}') {
            out.append(ch)
            i += 1
            continue
        }

        if (ch == '{') {
            if (i + 1 < format.length && format[i + 1] == '{') {
                out.append('{')
                i += 2
                continue
            }

            val end = format.indexOf('}', startIndex = i + 1)
            require(end >= 0) { "Unclosed '{' in format string: $format" }

            val inside = format.substring(i + 1, end)
            val (namePart, specPart) = splitOnce(inside, ':')
            val name = namePart.takeIf { it.isNotBlank() }

            val value = if (name == null) {
                require(positionalIndex < args.size) { "Not enough format args for: $format" }
                args[positionalIndex++]
            } else {
                require(name in namedArgs) { "Missing named arg '$name' for: $format" }
                namedArgs[name]
            }

            val rendered = applySpec(value, specPart)
            out.append(rendered)

            i = end + 1
            continue
        }

        // ch == '}'
        if (i + 1 < format.length && format[i + 1] == '}') {
            out.append('}')
            i += 2
            continue
        }
        error("Unmatched '}' in format string: $format")
    }

    // Mirror Rust's strictness loosely: extra args are likely a bug.
    require(positionalIndex == args.size) {
        "Too many format args for: $format (expected $positionalIndex, got ${args.size})"
    }

    return out.toString()
}

private fun splitOnce(s: String, delim: Char): Pair<String, String?> {
    val idx = s.indexOf(delim)
    return if (idx < 0) {
        Pair(s, null)
    } else {
        Pair(s.substring(0, idx), s.substring(idx + 1))
    }
}

private fun applySpec(value: Any?, spec: String?): String {
    val raw = value.toString()
    if (spec == null || spec.isBlank()) return raw

    // Very small subset: {name:04} -> zero-pad to width 4.
    val digits = spec.trim()
    val zeroPad = digits.startsWith('0')
    val width = digits.toIntOrNull()
        ?: digits.dropWhile { it == '0' }.toIntOrNull()
        ?: return raw

    val padChar = if (zeroPad) '0' else ' '
    return raw.padStart(width, padChar)
}

