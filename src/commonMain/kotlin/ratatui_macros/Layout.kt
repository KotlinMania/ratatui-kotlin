// port-lint: source ratatui-macros/src/layout.rs
package ratatui_macros

import ratatui.layout.Constraint
import ratatui.layout.Layout

/**
 * Create a `>=` (min) constraint.
 */
fun ge(expr: Int): Constraint = Constraint.Min(expr)

/**
 * Create a `<=` (max) constraint.
 */
fun le(expr: Int): Constraint = Constraint.Max(expr)

/**
 * Create a `==` (length) constraint.
 */
fun eq(expr: Int): Constraint = Constraint.Length(expr)

/**
 * Create a `== N%` (percentage) constraint.
 */
fun percent(token: Int): Constraint = Constraint.Percentage(token)

/**
 * Create a `== num/denom` (ratio) constraint.
 */
fun ratio(num: Int, denom: Int): Constraint {
    require(num >= 0) { "ratio numerator must be non-negative, got $num" }
    require(denom >= 0) { "ratio denominator must be non-negative, got $denom" }
    return Constraint.Ratio(num.toUInt(), denom.toUInt())
}

/**
 * Create a `*=` (fill) constraint.
 */
fun fill(expr: Int): Constraint = Constraint.Fill(expr)

/**
 * Create a list of constraints.
 */
fun constraints(vararg constraints: Constraint): List<Constraint> = constraints.toList()

/**
 * Create a list of `n` repeated constraints.
 */
fun constraints(constraint: Constraint, n: Int): List<Constraint> {
    require(n >= 0) { "n must be non-negative, got $n" }
    return List(n) { constraint }
}

/**
 * Create a vertical layout with the specified constraints.
 */
fun vertical(vararg constraints: Constraint): Layout = Layout.vertical(constraints.toList())

/**
 * Create a horizontal layout with the specified constraints.
 */
fun horizontal(vararg constraints: Constraint): Layout = Layout.horizontal(constraints.toList())

