// port-lint: source ratatui-macros/src/line.rs
package ratatui_macros

import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.ToSpan

/**
 * A macro for creating a [Line] using `vec!`-style syntax.
 *
 * Transliteration of the Rust `line!` macro.
 */
fun line(): Line = Line.default()

/**
 * Create a [Line] from a given span repeated `n` times.
 *
 * Transliteration of the Rust `line![$span; $n]` form.
 */
fun line(span: Any, n: Int): Line {
    if (n < 0) {
        throw IllegalArgumentException("n must be non-negative, got $n")
    }
    return Line.from(List(n) { intoSpan(span) })
}

/**
 * Create a [Line] containing a vector of [Span]s.
 *
 * Transliteration of the Rust `line![$($span),+]` form.
 */
fun line(vararg spans: Any): Line {
    if (spans.isEmpty()) {
        return Line.default()
    }
    return Line.from(spans.map { intoSpan(it) })
}

private fun intoSpan(value: Any): Span = when (value) {
    is Span -> value
    is String -> Span.from(value)
    is ToSpan -> value.toSpan()
    else -> Span.raw(value.toString())
}

