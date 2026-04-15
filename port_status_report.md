# Code Port - Progress Report

**Generated:** 2026-04-15
**Source:** tmp/ratatui
**Target:** src

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Total source files | 233 | 100% |
| Target units (paired) | 162 | - |
| Target files (total) | 162 | - |
| Porting progress | 143 | 61.4% (matched) |
| Missing files | 90 | 38.6% |

## Port Quality Analysis

**Average Similarity:** 0.38

**Quality Distribution:**
- Excellent (≥0.85): 5 files (3.5% of matched)
- Good (0.60-0.84): 32 files (22.4% of matched)
- Critical (<0.60): 106 files (74.1% of matched)

### Excellent Ports (Similarity ≥ 0.85)

These files are well-ported and likely complete:

- `layout.RectTest` (0.86, 54 deps)
- `ratatui.StylizeWidgetTest` (0.86, 9 deps)
- `demo.Ui` (0.86, 3 deps)
- `tabs.TabsTest` (0.86, 0 deps)
- `gauge.GaugeTest` (0.87, 0 deps)

### Critical Ports (Similarity < 0.60)

These files need significant work:

- `ratatui-core.buffer` → `buffer.BufferModule` (0.14, 88 deps)
- `widgets.widget` → `widgets.Widget` (0.34, 44 deps)
- `ratatui-macros.line` → `ratatui_macros.Line` (0.52, 37 deps)
- `ratatui-core.text` → `text.TextModule` (0.15, 13 deps)
- `style.color` → `style.Color` (0.39, 15 deps)
- `ratatui-core.symbols` → `symbols.SymbolsModule` (0.12, 6 deps)
- `ratatui-core.backend` → `backend.Backend` (0.36, 7 deps)
- `widgets.stateful_widget` → `widgets.StatefulWidget` (0.14, 5 deps)
- `symbols.marker` → `symbols.Marker` (0.58, 9 deps)
- `layout.position` → `layout.Position` (0.14, 4 deps)
- `rect.iter` → `rect.Iter` (0.43, 6 deps)
- `ratatui-widgets.paragraph` → `paragraph.Paragraph` (0.21, 4 deps)
- `layout.alignment` → `layout.Alignment` (0.38, 5 deps)
- `demo.app` → `demo.App` (0.51, 5 deps)
- `style.palette` → `palette.PaletteModule` (0.21, 3 deps)
- `buffer.cell_width` → `buffer.CellWidth` (0.21, 3 deps)
- `ratatui-widgets.canvas` → `canvas.Canvas` (0.55, 5 deps)
- `ratatui-macros.span` → `ratatui_macros.Span` (0.47, 4 deps)
- `demo.termion` → `demo.Termion` (0.00, 2 deps)
- `layout.constraint` → `layout.Constraint` (0.51, 4 deps)
- `table.highlight_spacing` → `widgets.HighlightSpacing` (0.46, 3 deps)
- `ratatui-widgets.tabs` → `tabs.Tabs` (0.25, 2 deps)
- `ratatui-widgets.block` → `commonMain.kotlin.ratatui.widgets.block.Block` (0.26, 2 deps)
- `symbols.border` → `border.Border` (0.54, 3 deps)
- `demo.termwiz` → `demo.Termwiz` (0.35, 2 deps)
- `layout.size` → `layout.Size` (0.47, 2 deps)
- `terminal.cursor` → `terminal.CursorTest` (0.00, 1 deps)
- `layout.direction` → `layout.Direction` (0.55, 2 deps)
- `widgets.stateful_widget_ref` → `widgets.StatefulWidgetRef` (0.15, 1 deps)
- `text.masked` → `text.Masked` (0.19, 1 deps)
- `ratatui-widgets.clear` → `widgets.Clear` (0.28, 1 deps)
- `layout.flex` → `layout.Flex` (0.29, 1 deps)
- `ratatui-widgets.scrollbar` → `commonMain.kotlin.ratatui.widgets.scrollbar.Scrollbar` (0.34, 1 deps)
- `layout.margin` → `layout.Margin` (0.37, 1 deps)
- `demo.crossterm` → `demo.Crossterm` (0.39, 1 deps)
- `canvas.rectangle` → `canvas.Rectangle` (0.42, 1 deps)
- `symbols.braille` → `symbols.Braille` (0.49, 1 deps)
- `terminal.viewport` → `terminal.Viewport` (0.53, 1 deps)
- `ratatui-macros.row` → `ratatui_macros.Row` (0.54, 1 deps)
- `ratatui-widgets.borders` → `widgets.Borders` (0.55, 1 deps)
- `commands.clippy` → `commands.Clippy` (0.59, 1 deps)
- `ratatui-core.layout` → `layout.LayoutModule` (0.14)
- `main.rect` → `rect.IterTest` (0.00)
- `table.cell` → `table.Cell` (0.23)
- `canvas.line` → `canvas.Line` (0.21)
- `backend.test` → `backend.TestBackend` (0.17)
- `symbols.merge` → `merge.Merge` (0.24)
- `ui.fails` → `ratatui_macros.UiFailsTest` (0.00)
- `canvas.map` → `canvas.Map` (0.43)
- `text.line` → `text.Line` (0.14)
- `barchart.bar` → `barchart.Bar` (0.36)
- `table.row` → `table.Row` (0.27)
- `list.item` → `list.ListItem` (0.40)
- `widgets.widget_ref` → `widgets.WidgetRef` (0.20)
- `ratatui-core.widgets` → `widgets.WidgetsModule` (0.17)
- `symbols.half_block` → `symbols.HalfBlock` (0.53)
- `ratatui-core.terminal` → `terminal.Terminal` (0.00)
- `ratatui-widgets.reflow` → `reflow.Reflow` (0.39)
- `ratatui-widgets.list` → `list.List` (0.25)
- `tests.macros` → `ratatui_macros.MacrosTest` (0.53)
- `symbols.line` → `line.Line` (0.56)
- `ratatui-crossterm.lib` → `ratatui_crossterm.Lib` (0.28)
- `list.state` → `list.ListState` (0.28)
- `rect.ops` → `layout.RectOps` (0.00)
- `tests.state_serde` → `ratatui.StateSerdeTest` (0.51)
- `layout.layout` → `layout.Layout` (0.10)
- `style.anstyle` → `style.Anstyle` (0.00)
- `table.state` → `table.TableState` (0.28)
- `tests.backend_termion` → `terminal.BackendDiffTest` (0.30)
- `ratatui-macros.layout` → `ratatui_macros.Layout` (0.47)
- `buffer.buffer` → `buffer.Buffer` (0.23)
- `ratatui-widgets.logo` → `logo.RatatuiLogo` (0.18)
- `canvas.world` → `canvas.WorldData` (0.07)
- `buffer.assert` → `buffer.Assert` (0.48)
- `text.grapheme` → `text.Grapheme` (0.33)
- `ratatui-widgets.barchart` → `barchart.BarChart` (0.35)
- `symbols.pixel` → `symbols.Pixel` (0.30)
- `style.stylize` → `style.Stylize` (0.42)
- `layout.rect` → `layout.Rect` (0.37)
- `text.text` → `text.Text` (0.15)
- `ratatui.widgets` → `widgets.RatatuiWidgetsModule` (0.00)
- `text.span` → `text.Span` (0.20)
- `tests.stateful_widget_ref_dyn` → `widgets.StatefulWidgetRefDynTest` (0.45)
- `main.line` → `ratatui_macros.LineTest` (0.00)
- `main.buffer` → `buffer.BufferTest` (0.00)
- `commands.backend` → `backend.ClearTypeTest` (0.00)
- `ratatui.init` → `ratatui.Init` (0.00)
- `examples.chart` → `chart.Chart` (0.11)
- `main.sparkline` → `sparkline.Sparkline` (0.20)
- `main.gauge` → `gauge.Gauge` (0.00)
- `ratatui-widgets.sparkline` → `sparkline.SparklineBar` (0.01)
- `examples.sparkline` → `sparkline.SparklineTest` (0.00)
- `ratatui-widgets.gauge` → `gauge.LineGauge` (0.10)
- `ratatui-widgets.chart` → `chart.GraphType` (0.00)
- `ratatui-widgets.lib` → `widgets.WidgetStylize` (0.07)
- `ratatui.lib` → `ratatui_macros.RowTest` (0.05)
- `main.text` → `text.TextTest` (0.00)
- `ratatui-macros.text` → `text.MaskedTest` (0.26)
- `terminal.render` → `sparkline.RenderDirection` (0.00)
- `examples.logo` → `logo.LogoMascotTest` (0.00)
- `examples.barchart-grouped` → `chart.Axis` (0.00)
- `bin.stateful-widget` → `widgets.StatefulWidgetTest` (0.00)
- `examples.barchart` → `chart.Dataset` (0.00)
- `main.barchart` → `chart.LegendPosition` (0.00)
- `terminal.buffers` → `buffer.CellWidthTest` (0.00)
- `ratatui-widgets.table` → `table.Table` (0.14)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `widgets.widget` | `widgets.Widget` | 1/2 | `Greeting` |
| `style.color` | `style.Color` | 1/5 | `Err` |
| `widgets.stateful_widget` | `widgets.StatefulWidget` | 3/4 | `PersonalGreeting`, `State`, `Bytes` |
| `layout.position` | `layout.Position` | 1/2 | `Output` |
| `rect.iter` | `rect.Iter` | 1/4 | `Item` |
| `ratatui-widgets.paragraph` | `paragraph.Paragraph` | 3/5 | `Horizontal`, `Vertical`, `Item` |
| `demo.app` | `demo.App` | 1/9 | `Item` |
| `demo.termion` | `demo.Termion` | 1/1 | `Event` |
| `ratatui-widgets.tabs` | `tabs.Tabs` | 1/2 | `Item` |
| `ratatui-widgets.block` | `commonMain.kotlin.ratatui.widgets.block.Block` | 2/4 | `BlockExt`, `Item` |
| `widgets.stateful_widget_ref` | `widgets.StatefulWidgetRef` | 3/4 | `State`, `PersonalGreeting`, `Bytes` |
| `ratatui-widgets.scrollbar` | `commonMain.kotlin.ratatui.widgets.scrollbar.Scrollbar` | 1/5 | `State` |
| `table.cell` | `table.Cell` | 1/2 | `Item` |
| `backend.test` | `backend.TestBackend` | 2/3 | `Result`, `Error` |
| `symbols.merge` | `merge.Merge` | 1/4 | `BorderSymbolError` |
| `text.line` | `text.Line` | 3/5 | `Item`, `IntoIter`, `Output` |
| `barchart.bar` | `barchart.Bar` | 1/2 | `Item` |
| `table.row` | `table.Row` | 1/2 | `Item` |
| `widgets.widget_ref` | `widgets.WidgetRef` | 2/3 | `Greeting`, `Farewell` |
| `ratatui-widgets.reflow` | `reflow.Reflow` | 1/5 | `Composer` |
| `ratatui-widgets.list` | `list.List` | 1/3 | `Item` |
| `ratatui-crossterm.lib` | `ratatui_crossterm.Lib` | 3/7 | `Error`, `IntoCrossterm`, `FromCrossterm` |
| `rect.ops` | `layout.RectOps` | 1/1 | `Output` |
| `layout.layout` | `layout.Layout` | 4/7 | `Rects`, `Segments`, `Spacers` … |
| `style.anstyle` | `style.Anstyle` | 1/2 | `Error` |

## High Priority Missing Files

| Rank | Source file | Deps | Path |
|------|------------|------|------|
| 1 | `bin.refcell` | 1 | `examples/concepts/state/src/bin/refcell.rs` |
| 2 | `advanced-widget-impl.main` | 0 | `examples/apps/advanced-widget-impl/src/main.rs` |
| 3 | `xtask.main` | 0 | `xtask/src/main.rs` |
| 4 | `canvas.main` | 0 | `examples/apps/canvas/src/main.rs` |
| 5 | `chart.main` | 0 | `examples/apps/chart/src/main.rs` |
| 6 | `color-explorer.main` | 0 | `examples/apps/color-explorer/src/main.rs` |
| 7 | `colors-rgb.main` | 0 | `examples/apps/colors-rgb/src/main.rs` |
| 8 | `constraint-explorer.main` | 0 | `examples/apps/constraint-explorer/src/main.rs` |
| 9 | `constraints.main` | 0 | `examples/apps/constraints/src/main.rs` |
| 10 | `custom-widget.main` | 0 | `examples/apps/custom-widget/src/main.rs` |
| 11 | `demo.main` | 0 | `examples/apps/demo/src/main.rs` |
| 12 | `demo2.app` | 0 | `examples/apps/demo2/src/app.rs` |
| 13 | `demo2.colors` | 0 | `examples/apps/demo2/src/colors.rs` |
| 14 | `demo2.destroy` | 0 | `examples/apps/demo2/src/destroy.rs` |
| 15 | `demo2.main` | 0 | `examples/apps/demo2/src/main.rs` |
| 16 | `demo2.tabs` | 0 | `examples/apps/demo2/src/tabs.rs` |
| 17 | `tabs.about` | 0 | `examples/apps/demo2/src/tabs/about.rs` |
| 18 | `tabs.email` | 0 | `examples/apps/demo2/src/tabs/email.rs` |
| 19 | `tabs.recipe` | 0 | `examples/apps/demo2/src/tabs/recipe.rs` |
| 20 | `tabs.traceroute` | 0 | `examples/apps/demo2/src/tabs/traceroute.rs` |

... and 70 more missing files.

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 7984 / 30632 lines (26%)

Top documentation gaps (>20%):

- `ratatui.widgets` - 97% gap (1430 → 44 lines)
- `layout.layout` - 91% gap (1212 → 110 lines)
- `ratatui-widgets.table` - 89% gap (1188 → 136 lines)
- `ratatui-widgets.block` - 77% gap (1240 → 286 lines)
- `ratatui.init` - 100% gap (876 → 0 lines)
- `ratatui-widgets.chart` - 96% gap (868 → 33 lines)
- `ratatui.lib` - 100% gap (826 → 0 lines)
- `palette.material` - 94% gap (854 → 49 lines)
- `layout.rect` - 83% gap (816 → 140 lines)
- `table.state` - 87% gap (732 → 96 lines)
- `symbols.merge` - 92% gap (640 → 52 lines)
- `palette.tailwind` - 97% gap (600 → 20 lines)
- `ratatui-core.terminal` - 81% gap (712 → 136 lines)
- `text.line` - 63% gap (914 → 338 lines)
- `buffer.buffer` - 83% gap (634 → 105 lines)

... and 66 more files with doc gaps.

