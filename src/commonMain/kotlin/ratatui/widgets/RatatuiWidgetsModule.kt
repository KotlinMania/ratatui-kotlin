// port-lint: source ratatui/src/widgets.rs
@file:Suppress("unused")
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

// Rust:
//   pub use ratatui_core::widgets::{StatefulWidget, Widget};
private val widget = Widget::class
private val statefulWidget = StatefulWidget::class

// Rust:
//   pub use ratatui_widgets::barchart::{Bar, BarChart, BarGroup};
private val bar = ratatui.widgets.barchart.Bar::class
private val barChart = ratatui.widgets.barchart.BarChart::class
private val barGroup = ratatui.widgets.barchart.BarGroup::class

// Rust:
//   pub use ratatui_widgets::block::{Block, Padding, TitlePosition, ...};
private val block = ratatui.widgets.block.Block::class
private val padding = ratatui.widgets.block.Padding::class
private val titlePosition = ratatui.widgets.block.TitlePosition::class

// Rust:
//   pub use ratatui_widgets::borders::{BorderType, Borders};
private val borderType = BorderType::class
private val borders = Borders::class

// Rust:
//   pub use ratatui_widgets::calendar;
private val calendar = ratatui.widgets.calendar.Monthly::class

// Rust:
//   pub use ratatui_widgets::canvas;
private val canvas = ratatui.widgets.canvas.Canvas::class

// Rust:
//   pub use ratatui_widgets::chart::{Axis, Chart, Dataset, GraphType, LegendPosition};
private val axis = ratatui.widgets.chart.Axis::class
private val chart = ratatui.widgets.chart.Chart::class
private val dataset = ratatui.widgets.chart.Dataset::class
private val graphType = ratatui.widgets.chart.GraphType::class
private val legendPosition = ratatui.widgets.chart.LegendPosition::class

// Rust:
//   pub use ratatui_widgets::clear::Clear;
private val clear = Clear::class

// Rust:
//   pub use ratatui_widgets::gauge::{Gauge, LineGauge};
private val gauge = ratatui.widgets.gauge.Gauge::class
private val lineGauge = ratatui.widgets.gauge.LineGauge::class

// Rust:
//   pub use ratatui_widgets::list::{List, ListDirection, ListItem, ListState};
private val list = ratatui.widgets.list.List::class
private val listDirection = ratatui.widgets.list.ListDirection::class
private val listItem = ratatui.widgets.list.ListItem::class
private val listState = ratatui.widgets.list.ListState::class

// Rust:
//   pub use ratatui_widgets::logo::{RatatuiLogo, Size as RatatuiLogoSize};
private val ratatuiLogo = ratatui.widgets.logo.RatatuiLogo::class
private val ratatuiLogoSize = ratatui.widgets.logo.RatatuiLogoSize::class

// Rust:
//   pub use ratatui_widgets::mascot::{MascotEyeColor, RatatuiMascot};
private val mascotEyeColor = ratatui.widgets.mascot.MascotEyeColor::class
private val ratatuiMascot = ratatui.widgets.mascot.RatatuiMascot::class

// Rust:
//   pub use ratatui_widgets::paragraph::{Paragraph, Wrap};
private val paragraph = ratatui.widgets.paragraph.Paragraph::class
private val wrap = ratatui.widgets.paragraph.Wrap::class

// Rust:
//   pub use ratatui_widgets::scrollbar::{ScrollDirection, Scrollbar, ScrollbarOrientation, ScrollbarState};
private val scrollDirection = ratatui.widgets.scrollbar.ScrollDirection::class
private val scrollbar = ratatui.widgets.scrollbar.Scrollbar::class
private val scrollbarOrientation = ratatui.widgets.scrollbar.ScrollbarOrientation::class
private val scrollbarState = ratatui.widgets.scrollbar.ScrollbarState::class

// Rust:
//   pub use ratatui_widgets::sparkline::{RenderDirection, Sparkline, SparklineBar};
private val renderDirection = ratatui.widgets.sparkline.RenderDirection::class
private val sparkline = ratatui.widgets.sparkline.Sparkline::class
private val sparklineBar = ratatui.widgets.sparkline.SparklineBar::class

// Rust:
//   pub use ratatui_widgets::table::{Cell, HighlightSpacing, Row, Table, TableState};
private val tableCell = ratatui.widgets.table.Cell::class
private val highlightSpacing = HighlightSpacing::class
private val tableRow = ratatui.widgets.table.Row::class
private val table = ratatui.widgets.table.Table::class
private val tableState = ratatui.widgets.table.TableState::class

// Rust:
//   pub use ratatui_widgets::tabs::Tabs;
private val tabs = ratatui.widgets.tabs.Tabs::class

// Rust:
//   pub use {stateful_widget_ref::StatefulWidgetRef, widget_ref::WidgetRef};
private val widgetRef = WidgetRef::class
private val statefulWidgetRef = StatefulWidgetRef::class
