// port-lint: source ratatui-macros/src/row.rs
package ratatui_macros

import ratatui.text.Line
import ratatui.text.Span
import ratatui.text.Text
import ratatui.text.ToText
import ratatui.widgets.table.Cell
import ratatui.widgets.table.Row

/**
 * A macro for creating a [Row] using `vec!` syntax.
 *
 * `row` is similar to the Rust `vec!` macro, but it returns a [Row] instead of a `Vec`.
 *
 * # Examples
 *
 * - Create a [Row] containing a list of [Cell]s:
 *
 *   ```kotlin
 *   val row = row("hello", "world")
 *   val row = row("hello".red(), "world".red().bold())
 *   ```
 *
 * - Create an empty [Row]:
 *
 *   ```kotlin
 *   val emptyRow = row()
 *   ```
 *
 * - Create a [Row] from a given cell-like value repeated some amount of times:
 *
 *   ```kotlin
 *   val row = row("hello", 2)
 *   ```
 *
 * - Use [line] or [span] macro inside [row].
 *
 *   ```kotlin
 *   val row = row(
 *       line("hello", "world"),
 *       span("goodbye {}", "world"),
 *   )
 *   ```
 */
fun row(): Row = Row.default()

/**
 * Create a [Row] from a given cell-like value repeated some amount of times.
 *
 * Mirrors the Rust `row![$cell; $n]` form.
 */
fun row(cell: Any, n: Int): Row {
    require(n >= 0) { "n must be non-negative, got $n" }
    return Row.new(List(n) { intoCell(cell) })
}

/**
 * Create a [Row] containing a list of [Cell]s.
 *
 * Mirrors the Rust `row![$($cell),+]` form.
 */
fun row(vararg cells: Any): Row {
    if (cells.isEmpty()) return Row.default()
    return Row.new(cells.map { intoCell(it) })
}

private fun intoCell(value: Any): Cell = when (value) {
    is Cell -> value
    is String -> Cell.new(value)
    is Span -> Cell.new(value)
    is Line -> Cell.new(value)
    is Text -> Cell.new(value)
    is ToText -> Cell.new(value.toText())
    else -> Cell.new(value.toString())
}

