// port-lint: source ratatui/src/widgets.rs
/**
 * Widgets are the building blocks of user interfaces in Ratatui.
 *
 * They are used to create and manage the layout and style of the terminal interface. Widgets can
 * be combined and nested to create complex UIs, and can be easily customized to suit the needs of
 * your application.
 *
 * Ratatui provides a wide variety of built-in widgets that can be used to quickly create UIs.
 *
 * # Crate Organization
 *
 * Starting with Ratatui 0.30.0, the project was split into multiple crates for better modularity:
 *
 * - `ratatui-core`: Contains the core widget traits (`Widget`, `StatefulWidget`) and text-related
 *   types (`Span`, `Line`, `Text`).
 * - `ratatui-widgets`: Contains all the built-in widget implementations (`Block`, `Paragraph`,
 *   `List`, etc.).
 * - `ratatui`: The main crate that re-exports everything for convenience. The unstable `WidgetRef`
 *   and `StatefulWidgetRef` traits are defined in the main crate.
 *
 * This Kotlin project follows the same high-level structure: core traits live alongside the
 * widget implementations in the `ratatui.widgets` package, and other foundational primitives
 * live under `ratatui.buffer`, `ratatui.layout`, `ratatui.style`, and `ratatui.text`.
 *
 * # Built-in Widgets
 *
 * Ratatui provides a comprehensive set of built-in widgets, such as:
 *
 * - `Block`
 * - `Paragraph`
 * - `List`
 * - `Table`
 * - `Tabs`
 * - `Scrollbar`
 * - `Canvas`
 * - `Chart`
 * - `Gauge` / `LineGauge`
 * - `Sparkline`
 *
 * For more information, see the upstream Ratatui widget documentation.
 *
 * This file mirrors `ratatui`'s `widgets.rs` module entrypoint and exists to keep the port aligned
 * with the Rust module structure without relying on Kotlin `typealias` re-exports.
 */
package ratatui.widgets
