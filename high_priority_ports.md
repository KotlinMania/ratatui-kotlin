# High Priority Ports - Action Plan

## Top 20 Files by Impact (Priority Score = Deps × (1 - Similarity))

| Rank | Source | Target | Similarity | Deps | Priority |
|------|--------|--------|------------|------|----------|
| 1 | `ratatui-core.buffer` | `buffer.BufferModule` | 0.57 | 88 | 38.0 |
| 2 | `widgets.widget` | `widgets.Widget` | 0.34 | 44 | 29.2 |
| 3 | `ratatui-macros.line` | `ratatui_macros.Line` | 0.52 | 37 | 17.7 |
| 4 | `style.color` | `style.Color` | 0.39 | 15 | 9.2 |
| 5 | `tests.rect` | `layout.RectTest` | 0.86 | 54 | 7.4 |
| 6 | `terminal.frame` | `terminal.Frame` | 0.69 | 22 | 6.9 |
| 7 | `ratatui-core.text` | `text.TextModule` | 0.55 | 13 | 5.8 |
| 8 | `commands.format` | `commands.Format` | 0.68 | 17 | 5.4 |
| 9 | `ratatui-core.style` | `style.Style` | 0.63 | 14 | 5.1 |
| 10 | `ratatui-core.backend` | `backend.Backend` | 0.36 | 7 | 4.5 |
| 11 | `widgets.stateful_widget` | `widgets.StatefulWidget` | 0.14 | 5 | 4.3 |
| 12 | `symbols.marker` | `symbols.Marker` | 0.58 | 9 | 3.8 |
| 13 | `layout.position` | `layout.Position` | 0.14 | 4 | 3.4 |
| 14 | `rect.iter` | `rect.Iter` | 0.43 | 6 | 3.4 |
| 15 | `buffer.cell` | `buffer.Cell` | 0.67 | 10 | 3.3 |
| 16 | `ratatui-widgets.paragraph` | `paragraph.Paragraph` | 0.21 | 4 | 3.2 |
| 17 | `layout.alignment` | `layout.Alignment` | 0.38 | 5 | 3.1 |
| 18 | `ratatui-core.symbols` | `symbols.SymbolsModule` | 0.51 | 6 | 2.9 |
| 19 | `tests.terminal` | `terminal.TerminalTest` | 0.80 | 13 | 2.6 |
| 20 | `demo.app` | `demo.App` | 0.51 | 5 | 2.5 |

## Critical Issues (Similarity < 0.60 with Dependencies)

These files need immediate attention:

- **ratatui-core.buffer** → `buffer.BufferModule`
  - Similarity: 0.57
  - Dependencies: 88

- **widgets.widget** → `widgets.Widget`
  - Similarity: 0.34
  - Dependencies: 44

- **ratatui-macros.line** → `ratatui_macros.Line`
  - Similarity: 0.52
  - Dependencies: 37

- **style.color** → `style.Color`
  - Similarity: 0.39
  - Dependencies: 15

- **ratatui-core.text** → `text.TextModule`
  - Similarity: 0.55
  - Dependencies: 13

- **ratatui-core.backend** → `backend.Backend`
  - Similarity: 0.36
  - Dependencies: 7

- **widgets.stateful_widget** → `widgets.StatefulWidget`
  - Similarity: 0.14
  - Dependencies: 5

- **symbols.marker** → `symbols.Marker`
  - Similarity: 0.58
  - Dependencies: 9

- **layout.position** → `layout.Position`
  - Similarity: 0.14
  - Dependencies: 4

- **rect.iter** → `rect.Iter`
  - Similarity: 0.43
  - Dependencies: 6

- **ratatui-widgets.paragraph** → `paragraph.Paragraph`
  - Similarity: 0.21
  - Dependencies: 4

- **layout.alignment** → `layout.Alignment`
  - Similarity: 0.38
  - Dependencies: 5

- **ratatui-core.symbols** → `symbols.SymbolsModule`
  - Similarity: 0.51
  - Dependencies: 6

- **demo.app** → `demo.App`
  - Similarity: 0.51
  - Dependencies: 5

- **buffer.cell_width** → `buffer.CellWidth`
  - Similarity: 0.21
  - Dependencies: 3

- **ratatui-widgets.canvas** → `canvas.Canvas`
  - Similarity: 0.55
  - Dependencies: 5

- **ratatui-macros.span** → `ratatui_macros.Span`
  - Similarity: 0.47
  - Dependencies: 4

- **demo.termion** → `demo.Termion`
  - Similarity: 0.00
  - Dependencies: 2

- **style.palette** → `palette.PaletteModule`
  - Similarity: 0.34
  - Dependencies: 3

- **layout.constraint** → `layout.Constraint`
  - Similarity: 0.51
  - Dependencies: 4

- **table.highlight_spacing** → `widgets.HighlightSpacing`
  - Similarity: 0.46
  - Dependencies: 3

- **ratatui-widgets.tabs** → `tabs.Tabs`
  - Similarity: 0.25
  - Dependencies: 2

- **ratatui-widgets.block** → `commonMain.kotlin.ratatui.widgets.block.Block`
  - Similarity: 0.26
  - Dependencies: 2

- **symbols.border** → `border.Border`
  - Similarity: 0.54
  - Dependencies: 3

- **demo.termwiz** → `demo.Termwiz`
  - Similarity: 0.35
  - Dependencies: 2

- **layout.size** → `layout.Size`
  - Similarity: 0.47
  - Dependencies: 2

- **terminal.cursor** → `terminal.CursorTest`
  - Similarity: 0.00
  - Dependencies: 1

- **layout.direction** → `layout.Direction`
  - Similarity: 0.55
  - Dependencies: 2

- **widgets.stateful_widget_ref** → `widgets.StatefulWidgetRef`
  - Similarity: 0.15
  - Dependencies: 1

- **text.masked** → `text.Masked`
  - Similarity: 0.19
  - Dependencies: 1

- **ratatui-widgets.clear** → `widgets.Clear`
  - Similarity: 0.28
  - Dependencies: 1

- **layout.flex** → `layout.Flex`
  - Similarity: 0.29
  - Dependencies: 1

- **ratatui-widgets.scrollbar** → `commonMain.kotlin.ratatui.widgets.scrollbar.Scrollbar`
  - Similarity: 0.33
  - Dependencies: 1

- **layout.margin** → `layout.Margin`
  - Similarity: 0.37
  - Dependencies: 1

- **demo.crossterm** → `demo.Crossterm`
  - Similarity: 0.39
  - Dependencies: 1

- **canvas.rectangle** → `canvas.Rectangle`
  - Similarity: 0.42
  - Dependencies: 1

- **symbols.braille** → `symbols.Braille`
  - Similarity: 0.49
  - Dependencies: 1

- **terminal.viewport** → `terminal.Viewport`
  - Similarity: 0.53
  - Dependencies: 1

- **ratatui-macros.row** → `ratatui_macros.Row`
  - Similarity: 0.54
  - Dependencies: 1

- **ratatui-widgets.borders** → `widgets.Borders`
  - Similarity: 0.55
  - Dependencies: 1

- **ratatui-widgets.calendar** → `calendar.Monthly`
  - Similarity: 0.59
  - Dependencies: 1

- **commands.clippy** → `commands.Clippy`
  - Similarity: 0.59
  - Dependencies: 1

## Missing Files (Top by Dependents)

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

