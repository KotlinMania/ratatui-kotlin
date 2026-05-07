// port-lint: source ratatui-macros/src/line.rs
package ratatui_macros

import ratatui.text.Line
import ratatui.text.Span

/**
 * A macro for creating a [Line] using `vec!` syntax.
 *
 * `line!` is similar to the Rust `vec!` macro, but it returns a [Line] instead of a `Vec`.
 *
 * # Examples
 *
 * - Create a [Line] containing a vector of [Span]s:
 *
 *   ```kotlin
 *   val line = line("hello", "world")
 *   val line = line("hello".red(), "world".red().bold())
 *   ```
 *
 * - Create a [Line] from a given [Span] repeated some amount of times:
 *
 *   ```kotlin
 *   val line = line("hello", 2)
 *   ```
 *
 * - Use the Rust `span!` macro inside `line!` for formatting.
 *
 *   ```kotlin
 *   val line = line(
 *       Span.styled("hello {}", Style.default().fg(Color.Red)),
 *       Span.styled("goodbye {}", Style.default().addModifier(Modifier.BOLD)),
 *   )
 *   ```
 */
fun line(): Line = Line.default()

/**
 * Create a [Line] from a given [Span] repeated some amount of times.
 *
 * Mirrors the Rust `line![$span; $n]` form.
 */
fun line(span: Any, n: Int): Line {
    return Line.from(List(n) { intoSpan(span) })
}

/**
 * Create a [Line] containing a vector of [Span]s.
 *
 * Mirrors the Rust `line![$($span),+]` form.
 */
fun line(vararg spans: Any): Line {
    return Line.from(spans.map { intoSpan(it) })
}

private fun intoSpan(value: Any): Span = when (value) {
    is Span -> value.copy()
    is String -> Span.from(value)
    else -> error("Unsupported span value: ${value::class.simpleName}")
}
