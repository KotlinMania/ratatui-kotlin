// port-lint: source ratatui-macros/src/layout.rs
package ratatui_macros

import ratatui.layout.Constraint
import ratatui.layout.Layout

/**
 * Create a `>=` (min) constraint.
 *
 * Mirrors the Rust `constraint!(>= expr)` / `constraints![ >=expr, ... ]` forms.
 */
fun ge(expr: Int): Constraint = Constraint.Min(expr)

/**
 * Create a `<=` (max) constraint.
 *
 * Mirrors the Rust `constraint!(<= expr)` / `constraints![ <=expr, ... ]` forms.
 */
fun le(expr: Int): Constraint = Constraint.Max(expr)

/**
 * Create a `==` (length) constraint.
 *
 * Mirrors the Rust `constraint!(== expr)` / `constraints![ ==expr, ... ]` forms.
 */
fun eq(expr: Int): Constraint = Constraint.Length(expr)

/**
 * Create a `== N%` (percentage) constraint.
 *
 * Mirrors the Rust `constraint!(== token %)` / `constraints![ ==token%, ... ]` forms.
 */
fun percent(token: Int): Constraint = Constraint.Percentage(token)

/**
 * Create a `== num/denom` (ratio) constraint.
 *
 * Mirrors the Rust `constraint!(== num / denom)` / `constraints![ ==num/denom, ... ]` forms.
 */
fun ratio(num: Int, denom: Int): Constraint {
    require(num >= 0) { "ratio numerator must be non-negative, got $num" }
    require(denom >= 0) { "ratio denominator must be non-negative, got $denom" }
    return Constraint.Ratio(num.toUInt(), denom.toUInt())
}

/**
 * Create a `*=` (fill) constraint.
 *
 * Mirrors the Rust `constraint!(*= expr)` / `constraints![ *=expr, ... ]` forms.
 */
fun fill(expr: Int): Constraint = Constraint.Fill(expr)

/**
 * Create a list of constraints.
 *
 * Transliteration of the Rust `constraints![]` macro: `constraints![a, b, c]`.
 */
fun constraints(vararg constraints: Constraint): List<Constraint> = constraints.toList()

/**
 * Create a list of `n` repeated constraints.
 *
 * Transliteration of the Rust `constraints![]` repetition form: `constraints![a; n]`.
 */
fun constraints(constraint: Constraint, n: Int): List<Constraint> {
    require(n >= 0) { "n must be non-negative, got $n" }
    return List(n) { constraint }
}

/**
 * Create a vertical layout with the specified constraints.
 *
 * Transliteration of the Rust `vertical![]` macro:
 * `vertical![constraints...]` -> `Layout::vertical(constraints![...])`.
 */
fun vertical(vararg constraints: Constraint): Layout = Layout.vertical(constraints.toList())

/**
 * Create a horizontal layout with the specified constraints.
 *
 * Transliteration of the Rust `horizontal![]` macro:
 * `horizontal![constraints...]` -> `Layout::horizontal(constraints![...])`.
 */
fun horizontal(vararg constraints: Constraint): Layout = Layout.horizontal(constraints.toList())

