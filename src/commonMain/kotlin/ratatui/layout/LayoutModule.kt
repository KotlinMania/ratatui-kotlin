// port-lint: source ratatui-core/src/layout.rs
/**
 * Layout and positioning in terminal user interfaces.
 *
 * This module provides a comprehensive set of types and traits for working with layout and
 * positioning in terminal applications. It implements a flexible layout system that allows you to
 * divide the terminal screen into different areas using constraints, manage positioning and
 * sizing, and handle complex UI arrangements.
 *
 * The layout system in Ratatui is based on the Cassowary constraint solver algorithm, implemented
 * through the [`kasuari`] crate. This allows for sophisticated constraint-based layouts where
 * multiple requirements can be satisfied simultaneously, with priorities determining which
 * constraints take precedence when conflicts arise.
 *
 * [`kasuari`]: https://crates.io/crates/kasuari
 *
 * # Core Concepts
 *
 * ## Coordinate System
 *
 * The coordinate system runs left to right, top to bottom, with the origin `(0, 0)` in the top
 * left corner of the terminal.
 *
 * ```text
 *      x (columns)
 *   ┌─────────────→
 * y │ (0,0)
 *   │
 * (rows)
 *   ↓
 * ```
 *
 * ## Layout Fundamentals
 *
 * Layouts form the structural foundation of your terminal UI. The [Layout] struct divides
 * available screen space into rectangular areas using a constraint-based approach.
 *
 * Note that the [Layout] struct is not required to create layouts - you can also manually
 * calculate and create [Rect] areas using simple mathematics to divide up the terminal space
 * if you prefer direct control over positioning and sizing.
 *
 * ## Rectangular Areas
 *
 * All layout operations work with rectangular areas represented by the [Rect] type. A [Rect]
 * defines a position and size in the terminal, specified by its top-left corner coordinates and
 * dimensions.
 *
 * # Available Types
 *
 * ## Core Layout Types
 *
 * - [Layout] - The primary layout engine that divides space using constraints and direction
 * - [Rect] - Represents a rectangular area with position and dimensions
 * - [Constraint] - Defines how space should be allocated (length, percentage, ratio, etc.)
 * - [Direction] - Specifies layout orientation (horizontal or vertical)
 * - [Flex] - Controls space distribution when constraints are satisfied
 *
 * ## Positioning and Sizing
 *
 * - [Position] - Represents a point in the terminal coordinate system
 * - [Size] - Represents dimensions (width and height)
 * - [Margin] - Defines spacing around rectangular areas
 * - [Offset] - Represents relative movement in the coordinate system
 *
 * ## Alignment
 *
 * - [Alignment] (alias for [HorizontalAlignment]) - Horizontal text/content alignment
 * - [HorizontalAlignment] - Horizontal alignment options (left, center, right)
 * - [VerticalAlignment] - Vertical alignment options (top, center, bottom)
 *
 * ## Iteration Support
 *
 * - [Rows] - Iterator over horizontal rows within a rectangular area
 * - [Columns] - Iterator over vertical columns within a rectangular area
 * - [Positions] - Iterator over all positions within a rectangular area
 *
 * # Quick Start
 *
 * Here's a simple example of creating a basic layout using the [Layout] struct:
 *
 * ```rust
 * use ratatui_core::layout::{Constraint, Direction, Layout, Rect};
 *
 * // Create a terminal area
 * let area = Rect::new(0, 0, 80, 24);
 *
 * // Divide it vertically into two equal parts using Layout
 * let layout = Layout::vertical([Constraint::Percentage(50), Constraint::Percentage(50)]);
 * let [top, bottom] = layout.areas(area);
 *
 * // Now you have two areas: top and bottom
 * ```
 *
 * Use `Layout::split` when the number of areas is only known at runtime.
 *
 * Alternatively, you can create layouts manually using mathematics:
 *
 * ```rust
 * use ratatui_core::layout::Rect;
 *
 * // Create a terminal area
 * let area = Rect::new(0, 0, 80, 24);
 *
 * // Manually divide into two equal parts
 * let top_half = Rect::new(area.x, area.y, area.width, area.height / 2);
 * let bottom_half = Rect::new(
 *     area.x,
 *     area.y + area.height / 2,
 *     area.width,
 *     area.height / 2,
 * );
 * ```
 *
 * # Related Documentation
 *
 * For more detailed information and practical examples, see the upstream Ratatui documentation:
 * - Layout Concepts: https://ratatui.rs/concepts/layout/
 * - Layout Recipes: https://ratatui.rs/recipes/layout/
 *
 * This file mirrors `ratatui-core`'s `layout.rs` module entrypoint and exists to keep the port
 * aligned with the Rust module structure without relying on Kotlin `typealias` re-exports.
 */
package ratatui.layout
