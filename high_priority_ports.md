# High Priority Ports - Action Plan

## Top 20 Files by Impact (Priority Score = Deps × (1 - Similarity))

| Rank | Source | Target | Similarity | Deps | Priority |
|------|--------|--------|------------|------|----------|
| 1 | `ratatui-core.buffer` | `buffer.BufferModule` | 0.14 | 88 | 75.4 |
| 2 | `widgets.widget` | `widgets.Widget` | 0.34 | 44 | 29.2 |
| 3 | `ratatui-macros.line` | `ratatui_macros.Line` | 0.52 | 37 | 17.7 |
| 4 | `ratatui-core.text` | `text.TextModule` | 0.15 | 13 | 11.1 |
| 5 | `style.color` | `style.Color` | 0.33 | 15 | 10.1 |
| 6 | `tests.rect` | `layout.RectTest` | 0.86 | 54 | 7.4 |
| 7 | `commands.format` | `commands.Format` | 0.59 | 17 | 6.9 |
| 8 | `terminal.frame` | `terminal.Frame` | 0.69 | 22 | 6.9 |
| 9 | `ratatui-core.symbols` | `symbols.SymbolsModule` | 0.12 | 6 | 5.3 |
| 10 | `ratatui-core.style` | `style.Style` | 0.63 | 14 | 5.1 |
| 11 | `ratatui-core.backend` | `backend.Backend` | 0.36 | 7 | 4.5 |
| 12 | `symbols.marker` | `symbols.Marker` | 0.50 | 9 | 4.5 |
| 13 | `widgets.stateful_widget` | `widgets.StatefulWidget` | 0.14 | 5 | 4.3 |
| 14 | `layout.alignment` | `layout.Alignment` | 0.27 | 5 | 3.7 |
| 15 | `layout.position` | `layout.Position` | 0.14 | 4 | 3.4 |
| 16 | `rect.iter` | `rect.Iter` | 0.43 | 6 | 3.4 |
| 17 | `buffer.cell` | `buffer.Cell` | 0.67 | 10 | 3.3 |
| 18 | `ratatui-widgets.paragraph` | `paragraph.Paragraph` | 0.21 | 4 | 3.2 |
| 19 | `tests.terminal` | `terminal.TerminalTest` | 0.80 | 13 | 2.6 |
| 20 | `demo.app` | `demo.App` | 0.51 | 5 | 2.5 |

## Critical Issues (Similarity < 0.60 with Dependencies)

These files need immediate attention:

- **ratatui-core.buffer** → `buffer.BufferModule`
  - Similarity: 0.14
  - Dependencies: 88

- **widgets.widget** → `widgets.Widget`
  - Similarity: 0.34
  - Dependencies: 44

- **ratatui-macros.line** → `ratatui_macros.Line`
  - Similarity: 0.52
  - Dependencies: 37

- **ratatui-core.text** → `text.TextModule`
  - Similarity: 0.15
  - Dependencies: 13

- **style.color** → `style.Color`
  - Similarity: 0.33
  - Dependencies: 15

- **commands.format** → `commands.Format`
  - Similarity: 0.59
  - Dependencies: 17

- **ratatui-core.symbols** → `symbols.SymbolsModule`
  - Similarity: 0.12
  - Dependencies: 6

- **ratatui-core.backend** → `backend.Backend`
  - Similarity: 0.36
  - Dependencies: 7
  - Lint issues: 1

- **symbols.marker** → `symbols.Marker`
  - Similarity: 0.50
  - Dependencies: 9

- **widgets.stateful_widget** → `widgets.StatefulWidget`
  - Similarity: 0.14
  - Dependencies: 5

- **layout.alignment** → `layout.Alignment`
  - Similarity: 0.27
  - Dependencies: 5

- **layout.position** → `layout.Position`
  - Similarity: 0.14
  - Dependencies: 4

- **rect.iter** → `rect.Iter`
  - Similarity: 0.43
  - Dependencies: 6

- **ratatui-widgets.paragraph** → `paragraph.Paragraph`
  - Similarity: 0.21
  - Dependencies: 4

- **demo.app** → `demo.App`
  - Similarity: 0.51
  - Dependencies: 5

- **style.palette** → `palette.PaletteModule`
  - Similarity: 0.21
  - Dependencies: 3

- **ratatui-widgets.canvas** → `canvas.Canvas`
  - Similarity: 0.55
  - Dependencies: 5

- **ratatui-macros.span** → `ratatui_macros.Span`
  - Similarity: 0.47
  - Dependencies: 4

- **layout.constraint** → `layout.Constraint`
  - Similarity: 0.51
  - Dependencies: 4

- **table.highlight_spacing** → `list.HighlightSpacing`
  - Similarity: 0.41
  - Dependencies: 3

- **buffer.cell_width** → `buffer.CellWidth`
  - Similarity: 0.45
  - Dependencies: 3

- **ratatui-widgets.tabs** → `tabs.Tabs`
  - Similarity: 0.25
  - Dependencies: 2

- **ratatui-widgets.block** → `block.Block`
  - Similarity: 0.26
  - Dependencies: 2

- **symbols.border** → `symbols.Border`
  - Similarity: 0.54
  - Dependencies: 3

- **layout.size** → `layout.Size`
  - Similarity: 0.47
  - Dependencies: 2

- **terminal.cursor** → `terminal.CursorModuleTest`
  - Similarity: 0.00
  - Dependencies: 1

- **ratatui-widgets.borders** → `widgets.Borders`
  - Similarity: 0.00
  - Dependencies: 1

- **ratatui-widgets.scrollbar** → `scrollbar.Scrollbar`
  - Similarity: 0.05
  - Dependencies: 1

- **layout.direction** → `layout.Direction`
  - Similarity: 0.55
  - Dependencies: 2

- **ratatui-widgets.calendar** → `calendar.Monthly`
  - Similarity: 0.13
  - Dependencies: 1

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

- **demo.crossterm** → `demo.Crossterm`
  - Similarity: 0.35
  - Dependencies: 1

- **layout.margin** → `layout.Margin`
  - Similarity: 0.36
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

## Missing Files (Top by Dependents)

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

