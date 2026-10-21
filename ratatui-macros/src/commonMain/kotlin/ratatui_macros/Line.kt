// port-lint: source ratatui-macros/src/line.rs
package ratatui_macros

import ratatui.text.Line
import ratatui.text.Span

/**
 * Creates a [Line] from spans or strings using list syntax.
 *
 * ```kotlin
 * val greeting = line("hello", "world")
 * val repeated = line(Span.raw("hello"), 2)
 * ```
 */
fun line(): Line = Line.default()

/**
 * Create a [Line] from a given [Span] repeated some amount of times.
 */
fun line(span: Any, n: Int): Line {
    return Line.from(List(n) { intoSpan(span) })
}

/**
 * Create a [Line] containing a list of [Span]s.
 */
fun line(vararg spans: Any): Line {
    return Line.from(spans.map { intoSpan(it) })
}

private fun intoSpan(value: Any): Span = when (value) {
    is Span -> Span.from(value)
    is String -> Span.from(value)
    else -> error("Unsupported span value: ${value::class.simpleName}")
}
