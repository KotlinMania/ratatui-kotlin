# High Priority Ports - Action Plan

## Top 20 Files by Impact (Priority Score = Deps × (1 - Similarity))

| Rank | Source | Target | Similarity | Deps | Priority |
|------|--------|--------|------------|------|----------|
| 1 | `ratatui-core.buffer` | `buffer.BufferTest` | 0.00 | 88 | 88.0 |
| 2 | `widgets.widget` | `widgets.Widget` | 0.34 | 44 | 29.2 |
| 3 | `ratatui-macros.line` | `ratatui_macros.Line` | 0.52 | 37 | 17.7 |
| 4 | `style.color` | `style.Color` | 0.17 | 15 | 12.5 |
| 5 | `ratatui-core.text` | `text.UnicodeWidth` | 0.13 | 13 | 11.3 |
| 6 | `tests.rect` | `layout.RectTest` | 0.86 | 54 | 7.4 |
| 7 | `commands.format` | `commands.Format` | 0.59 | 17 | 6.9 |
| 8 | `terminal.frame` | `terminal.Frame` | 0.69 | 22 | 6.9 |
| 9 | `ratatui-core.style` | `style.Style` | 0.62 | 14 | 5.4 |
| 10 | `symbols.marker` | `symbols.Marker` | 0.50 | 9 | 4.5 |
| 11 | `ratatui-core.backend` | `backend.Backend` | 0.36 | 7 | 4.5 |
| 12 | `widgets.stateful_widget` | `widgets.StatefulWidget` | 0.14 | 5 | 4.3 |
| 13 | `layout.alignment` | `layout.Alignment` | 0.27 | 5 | 3.7 |
| 14 | `layout.position` | `layout.Position` | 0.14 | 4 | 3.4 |
| 15 | `rect.iter` | `rect.Iter` | 0.43 | 6 | 3.4 |
| 16 | `buffer.cell` | `buffer.Cell` | 0.67 | 10 | 3.3 |
| 17 | `ratatui-widgets.paragraph` | `paragraph.Paragraph` | 0.21 | 4 | 3.2 |
| 18 | `tests.terminal` | `terminal.TerminalTest` | 0.80 | 13 | 2.6 |
| 19 | `demo.app` | `demo.App` | 0.51 | 5 | 2.5 |
| 20 | `ratatui-widgets.canvas` | `canvas.Canvas` | 0.55 | 5 | 2.2 |

## Critical Issues (Similarity < 0.60 with Dependencies)

These files need immediate attention:

- **ratatui-core.buffer** → `buffer.BufferTest`
  - Similarity: 0.00
  - Dependencies: 88

- **widgets.widget** → `widgets.Widget`
  - Similarity: 0.34
  - Dependencies: 44

- **ratatui-macros.line** → `ratatui_macros.Line`
  - Similarity: 0.52
  - Dependencies: 37

- **style.color** → `style.Color`
  - Similarity: 0.17
  - Dependencies: 15

- **ratatui-core.text** → `text.UnicodeWidth`
  - Similarity: 0.13
  - Dependencies: 13

- **commands.format** → `commands.Format`
  - Similarity: 0.59
  - Dependencies: 17

- **symbols.marker** → `symbols.Marker`
  - Similarity: 0.50
  - Dependencies: 9

- **ratatui-core.backend** → `backend.Backend`
  - Similarity: 0.36
  - Dependencies: 7

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

- **ratatui-widgets.borders** → `widgets.Borders`
  - Similarity: 0.00
  - Dependencies: 1

- **ratatui-widgets.scrollbar** → `scrollbar.ScrollbarState`
  - Similarity: 0.02
  - Dependencies: 1

- **layout.direction** → `layout.Direction`
  - Similarity: 0.55
  - Dependencies: 2

- **ratatui-widgets.calendar** → `calendar.Monthly`
  - Similarity: 0.13
  - Dependencies: 1

- **terminal.cursor** → `terminal.Cursor`
  - Similarity: 0.14
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
| 1 | `ratatui-core.symbols` | 6 | `ratatui-core/src/symbols.rs` |
| 2 | `style.palette` | 3 | `ratatui-core/src/style/palette.rs` |
| 3 | `demo.termion` | 2 | `examples/apps/demo/src/termion.rs` |
| 4 | `demo.termwiz` | 2 | `examples/apps/demo/src/termwiz.rs` |
| 5 | `commands.typos` | 1 | `xtask/src/commands/typos.rs` |
| 6 | `commands.docs` | 1 | `xtask/src/commands/docs.rs` |
| 7 | `commands.coverage` | 1 | `xtask/src/commands/coverage.rs` |
| 8 | `commands.clippy` | 1 | `xtask/src/commands/clippy.rs` |
| 9 | `commands.check` | 1 | `xtask/src/commands/check.rs` |
| 10 | `bin.refcell` | 1 | `examples/concepts/state/src/bin/refcell.rs` |
| 11 | `demo2.theme` | 1 | `examples/apps/demo2/src/theme.rs` |
| 12 | `demo.crossterm` | 1 | `examples/apps/demo/src/crossterm.rs` |
| 13 | `advanced-widget-impl.main` | 0 | `examples/apps/advanced-widget-impl/src/main.rs` |
| 14 | `xtask.main` | 0 | `xtask/src/main.rs` |
| 15 | `demo2.app` | 0 | `examples/apps/demo2/src/app.rs` |
| 16 | `demo2.colors` | 0 | `examples/apps/demo2/src/colors.rs` |
| 17 | `demo2.destroy` | 0 | `examples/apps/demo2/src/destroy.rs` |
| 18 | `demo2.main` | 0 | `examples/apps/demo2/src/main.rs` |
| 19 | `demo2.tabs` | 0 | `examples/apps/demo2/src/tabs.rs` |
| 20 | `tabs.about` | 0 | `examples/apps/demo2/src/tabs/about.rs` |

... and 83 more missing files.

