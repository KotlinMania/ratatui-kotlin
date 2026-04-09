# Code Port - Progress Report

**Generated:** 2026-04-09
**Source:** tmp/ratatui/
**Target:** src/

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Total source files | 233 | 100% |
| Target units (paired) | 143 | - |
| Target files (total) | 143 | - |
| Porting progress | 133 | 57.1% (matched) |
| Missing files | 100 | 42.9% |

## Port Quality Analysis

**Average Similarity:** 0.34

**Quality Distribution:**
- Excellent (≥0.85): 6 files (4.5% of matched)
- Good (0.60-0.84): 23 files (17.3% of matched)
- Critical (<0.60): 104 files (78.2% of matched)

### Excellent Ports (Similarity ≥ 0.85)

These files are well-ported and likely complete:

- `layout.RectTest` (0.86, 54 deps)
- `ratatui.StylizeWidgetTest` (0.86, 9 deps)
- `demo.Ui` (0.86, 3 deps)
- `gauge.GaugeTest` (0.87, 0 deps)
- `tabs.TabsTest` (0.87, 0 deps)
- `canvas.CanvasTest` (0.85, 0 deps)

### Critical Ports (Similarity < 0.60)

These files need significant work:

- `ratatui-core.buffer` → `buffer.BufferModule` (0.14, 88 deps)
- `widgets.widget` → `widgets.Widget` (0.34, 44 deps)
- `ratatui-macros.line` → `ratatui_macros.Line` (0.52, 37 deps)
- `ratatui-core.text` → `text.TextModule` (0.15, 13 deps)
- `style.color` → `style.Color` (0.33, 15 deps)
- `commands.format` → `commands.Format` (0.59, 17 deps)
- `ratatui-core.symbols` → `symbols.SymbolsModule` (0.12, 6 deps)
- `ratatui-core.backend` → `backend.Backend` (0.36, 7 deps)
- `symbols.marker` → `symbols.Marker` (0.50, 9 deps)
- `widgets.stateful_widget` → `widgets.StatefulWidget` (0.14, 5 deps)
- `layout.alignment` → `layout.Alignment` (0.27, 5 deps)
- `layout.position` → `layout.Position` (0.14, 4 deps)
- `rect.iter` → `rect.Iter` (0.43, 6 deps)
- `ratatui-widgets.paragraph` → `paragraph.Paragraph` (0.21, 4 deps)
- `demo.app` → `demo.App` (0.51, 5 deps)
- `style.palette` → `palette.PaletteModule` (0.21, 3 deps)
- `ratatui-widgets.canvas` → `canvas.Canvas` (0.55, 5 deps)
- `ratatui-macros.span` → `ratatui_macros.Span` (0.47, 4 deps)
- `layout.constraint` → `layout.Constraint` (0.51, 4 deps)
- `table.highlight_spacing` → `list.HighlightSpacing` (0.41, 3 deps)
- `buffer.cell_width` → `buffer.CellWidth` (0.45, 3 deps)
- `ratatui-widgets.tabs` → `tabs.Tabs` (0.25, 2 deps)
- `ratatui-widgets.block` → `block.Block` (0.26, 2 deps)
- `symbols.border` → `symbols.Border` (0.54, 3 deps)
- `layout.size` → `layout.Size` (0.47, 2 deps)
- `terminal.cursor` → `terminal.CursorModuleTest` (0.00, 1 deps)
- `ratatui-widgets.borders` → `widgets.Borders` (0.00, 1 deps)
- `ratatui-widgets.scrollbar` → `scrollbar.Scrollbar` (0.05, 1 deps)
- `layout.direction` → `layout.Direction` (0.55, 2 deps)
- `ratatui-widgets.calendar` → `calendar.Monthly` (0.13, 1 deps)
- `widgets.stateful_widget_ref` → `widgets.StatefulWidgetRef` (0.15, 1 deps)
- `text.masked` → `text.Masked` (0.19, 1 deps)
- `ratatui-widgets.clear` → `widgets.Clear` (0.28, 1 deps)
- `layout.flex` → `layout.Flex` (0.29, 1 deps)
- `demo.crossterm` → `demo.Crossterm` (0.35, 1 deps)
- `layout.margin` → `layout.Margin` (0.36, 1 deps)
- `canvas.rectangle` → `canvas.Rectangle` (0.42, 1 deps)
- `symbols.braille` → `symbols.Braille` (0.49, 1 deps)
- `terminal.viewport` → `terminal.Viewport` (0.53, 1 deps)
- `ratatui-macros.row` → `ratatui_macros.Row` (0.54, 1 deps)
- `symbols.merge` → `symbols.Merge` (0.24)
- `style.anstyle` → `style.StylizeTest` (0.00)
- `tests.stateful_widget_ref_dyn` → `widgets.StatefulWidgetRefDynTest` (0.45)
- `symbols.pixel` → `symbols.Pixel` (0.30)
- `symbols.line` → `symbols.Line` (0.56)
- `backend.test` → `backend.TestBackend` (0.17)
- `canvas.map` → `canvas.Map` (0.43)
- `canvas.line` → `canvas.Line` (0.21)
- `ratatui.init` → `ratatui.Init` (0.00)
- `widgets.widget_ref` → `widgets.WidgetRef` (0.20)
- `ratatui-core.terminal` → `terminal.Terminal` (0.00)
- `symbols.half_block` → `symbols.HalfBlock` (0.53)
- `rect.ops` → `layout.RectOps` (0.00)
- `table.row` → `table.Row` (0.27)
- `tests.state_serde` → `ratatui.StateSerdeTest` (0.51)
- `ratatui-crossterm.lib` → `ratatui_crossterm.Lib` (0.28)
- `style.stylize` → `style.Stylize` (0.42)
- `text.grapheme` → `text.Grapheme` (0.33)
- `buffer.buffer` → `buffer.Buffer` (0.22)
- `barchart.bar` → `barchart.Bar` (0.36)
- `table.cell` → `table.Cell` (0.23)
- `layout.layout` → `layout.Layout` (0.10)
- `buffer.assert` → `buffer.Assert` (0.48)
- `main.list` → `list.List` (0.12)
- `examples.sparkline` → `sparkline.Sparkline` (0.22)
- `ratatui-widgets.chart` → `chart.Chart` (0.07)
- `examples.gauge` → `gauge.Gauge` (0.19)
- `ratatui-widgets.reflow` → `reflow.Reflow` (0.39)
- `examples.table` → `table.Table` (0.12)
- `layout.rect` → `layout.Rect` (0.37)
- `text.line` → `text.Line` (0.14)
- `ratatui-widgets.barchart` → `barchart.BarChart` (0.35)
- `text.span` → `text.Span` (0.20)
- `main.sparkline` → `sparkline.SparklineBar` (0.00)
- `ratatui-widgets.sparkline` → `sparkline.SparklineTest` (0.00)
- `examples.scrollbar` → `scrollbar.ScrollbarState` (0.00)
- `main.buffer` → `buffer.BufferTest` (0.00)
- `table.state` → `list.ListState` (0.00)
- `canvas.world` → `canvas.WorldData` (0.07)
- `main.gauge` → `gauge.LineGauge` (0.00)
- `ratatui-core.widgets` → `widgets.WidgetStylize` (0.06)
- `ratatui-widgets.list` → `list.ListItem` (0.00)
- `ratatui.widgets` → `widgets.WidgetTest` (0.00)
- `main.text` → `text.TextTest` (0.00)
- `main.table` → `table.TableState` (0.00)
- `ratatui-widgets.lib` → `widgets.StatefulWidgetTest` (0.08)
- `ratatui.lib` → `ratatui_macros.RowTest` (0.05)
- `ratatui-macros.layout` → `layout.ConstraintTest` (0.29)
- `ratatui-macros.lib` → `ratatui_macros.LineTest` (0.21)
- `ratatui-macros.text` → `text.UnicodeWidth` (0.14)
- `main.line` → `text.LineTest` (0.00)
- `ratatui-core.layout` → `layout.PositionTest` (0.21)
- `examples.calendar` → `calendar.CalendarEventStore` (0.00)
- `terminal.render` → `sparkline.RenderDirection` (0.00)
- `text.text` → `text.Text` (0.15)
- `examples.logo` → `logo.RatatuiLogo` (0.17)
- `ratatui-widgets.logo` → `logo.LogoMascotTest` (0.00)
- `main.rect` → `scrollbar.ScrollDirection` (0.00)
- `commands.backend` → `backend.ClearTypeTest` (0.00)
- `examples.chart` → `chart.LegendPosition` (0.00)
- `examples.barchart-grouped` → `chart.GraphType` (0.00)
- `examples.barchart` → `chart.Dataset` (0.00)
- `terminal.buffers` → `buffer.CellWidthTest` (0.00)
- `main.barchart` → `chart.Axis` (0.00)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `widgets.widget` | `widgets.Widget` | 1/2 | `Greeting` |
| `style.color` | `style.Color` | 1/5 | `Err` |
| `widgets.stateful_widget` | `widgets.StatefulWidget` | 3/4 | `PersonalGreeting`, `State`, `Bytes` |
| `layout.alignment` | `layout.Alignment` | 1/3 | `Alignment` |
| `layout.position` | `layout.Position` | 1/2 | `Output` |
| `rect.iter` | `rect.Iter` | 1/4 | `Item` |
| `ratatui-widgets.paragraph` | `paragraph.Paragraph` | 3/5 | `Horizontal`, `Vertical`, `Item` |
| `demo.app` | `demo.App` | 1/9 | `Item` |
| `ratatui-widgets.tabs` | `tabs.Tabs` | 1/2 | `Item` |
| `ratatui-widgets.block` | `block.Block` | 2/4 | `BlockExt`, `Item` |
| `ratatui-widgets.borders` | `widgets.Borders` | 1/1 | `BorderType` |
| `ratatui-widgets.scrollbar` | `scrollbar.Scrollbar` | 4/5 | `ScrollbarOrientation`, `ScrollbarState`, `ScrollDirection` … |
| `ratatui-widgets.calendar` | `calendar.Monthly` | 2/3 | `DateStyler`, `CalendarEventStore` |
| `widgets.stateful_widget_ref` | `widgets.StatefulWidgetRef` | 3/4 | `State`, `PersonalGreeting`, `Bytes` |
| `symbols.merge` | `symbols.Merge` | 1/4 | `BorderSymbolError` |
| `style.anstyle` | `style.StylizeTest` | 2/2 | `TryFromColorError`, `Error` |
| `tests.stateful_widget_ref_dyn` | `widgets.StatefulWidgetRefDynTest` | 3/8 | `State`, `BoxedWindow`, `BoxedState` |
| `backend.test` | `backend.TestBackend` | 2/3 | `Result`, `Error` |
| `ratatui.init` | `ratatui.Init` | 1/1 | `DefaultTerminal` |
| `widgets.widget_ref` | `widgets.WidgetRef` | 2/3 | `Greeting`, `Farewell` |
| `rect.ops` | `layout.RectOps` | 1/1 | `Output` |
| `table.row` | `table.Row` | 1/2 | `Item` |
| `ratatui-crossterm.lib` | `ratatui_crossterm.Lib` | 3/7 | `Error`, `IntoCrossterm`, `FromCrossterm` |
| `style.stylize` | `style.Stylize` | 1/5 | `Item` |
| `text.grapheme` | `text.Grapheme` | 1/2 | `Item` |

## High Priority Missing Files

| Rank | Source file | Deps | Path |
|------|------------|------|------|
| 1 | `demo.termion` | 2 | `examples/apps/demo/src/termion.rs` |
| 2 | `demo.termwiz` | 2 | `examples/apps/demo/src/termwiz.rs` |
| 3 | `commands.typos` | 1 | `xtask/src/commands/typos.rs` |
| 4 | `commands.docs` | 1 | `xtask/src/commands/docs.rs` |
| 5 | `commands.coverage` | 1 | `xtask/src/commands/coverage.rs` |
| 6 | `commands.clippy` | 1 | `xtask/src/commands/clippy.rs` |
| 7 | `commands.check` | 1 | `xtask/src/commands/check.rs` |
| 8 | `bin.refcell` | 1 | `examples/concepts/state/src/bin/refcell.rs` |
| 9 | `demo2.theme` | 1 | `examples/apps/demo2/src/theme.rs` |
| 10 | `advanced-widget-impl.main` | 0 | `examples/apps/advanced-widget-impl/src/main.rs` |
| 11 | `xtask.main` | 0 | `xtask/src/main.rs` |
| 12 | `custom-widget.main` | 0 | `examples/apps/custom-widget/src/main.rs` |
| 13 | `constraints.main` | 0 | `examples/apps/constraints/src/main.rs` |
| 14 | `demo2.app` | 0 | `examples/apps/demo2/src/app.rs` |
| 15 | `demo2.colors` | 0 | `examples/apps/demo2/src/colors.rs` |
| 16 | `demo2.destroy` | 0 | `examples/apps/demo2/src/destroy.rs` |
| 17 | `demo2.main` | 0 | `examples/apps/demo2/src/main.rs` |
| 18 | `demo2.tabs` | 0 | `examples/apps/demo2/src/tabs.rs` |
| 19 | `tabs.about` | 0 | `examples/apps/demo2/src/tabs/about.rs` |
| 20 | `tabs.email` | 0 | `examples/apps/demo2/src/tabs/email.rs` |

... and 80 more missing files.

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 7511 / 27912 lines (27%)

Top documentation gaps (>20%):

- `ratatui.widgets` - 100% gap (1430 → 0 lines)
- `layout.layout` - 91% gap (1212 → 110 lines)
- `ratatui-widgets.block` - 77% gap (1240 → 286 lines)
- `ratatui.init` - 100% gap (876 → 0 lines)
- `ratatui.lib` - 100% gap (826 → 0 lines)
- `ratatui-widgets.chart` - 82% gap (868 → 154 lines)
- `layout.rect` - 83% gap (816 → 140 lines)
- `table.state` - 88% gap (732 → 91 lines)
- `ratatui-core.layout` - 100% gap (620 → 0 lines)
- `symbols.merge` - 92% gap (640 → 52 lines)
- `palette.tailwind` - 97% gap (600 → 20 lines)
- `ratatui-core.terminal` - 81% gap (712 → 134 lines)
- `text.line` - 63% gap (914 → 338 lines)
- `buffer.buffer` - 88% gap (634 → 79 lines)
- `ratatui-widgets.list` - 88% gap (622 → 73 lines)

... and 62 more files with doc gaps.

