# Rust function and feature parity evidence

Audited source head `8bb97a8c28b89316eae8343753540eb2e88a2d5a`, pinned upstream `c78fa2169401527cebe30bee2cbf305d916fb238`.
The established `tools/ast_distance` binary SHA-256 is
`f050b0933c74318037bcf5d8b7d91e6f19e12cabf64055ba7856e55333ee08c6`.

Actual `--compare-functions <Rust file> rust <Kotlin file> kotlin` commands ran
sequentially for 156 source pairs, using current real module paths. All exited
zero, but this is **not a parity pass**. Six comparisons report Kotlin parser
errors. Missing function matches remain unresolved. A Rust file can own several
Kotlin production and test files, so per-row missing counts overlap and must
not be summed into a project completeness score. Properties, overload owners,
split files and test naming also need semantic review.

Commands, all input/report hashes, full per-function bodies and missing function
names are retained under workspace
`automation-artifacts/2026-10-20-ansi-pr99/serde-alignment/`:
`ast-distance-current-inputs.json`, `ast-distance-execution.json`,
`ast-distance-supplemental.json`, `ast-function-index.json`, and
`ast-function-reports/`. These are source comparisons, not generated snapshots.

## Function comparison inventory

Matched/source and unmatched counts describe this file pair only.

| Kotlin source | Rust source | Matched/source | Unmatched source/target | Parser limited |
| --- | --- | --- | --- | --- |
| `src/commonTest/kotlin/ratatui/StateSerdeTest.kt` | `ratatui/tests/state_serde.rs` | 9/10 | 1/6 | no |
| `src/commonTest/kotlin/ratatui/StylizeWidgetTest.kt` | `ratatui/tests/stylize.rs` | 3/3 | 0/0 | no |
| `src/commonTest/kotlin/ratatui/terminal/BackendDiffTest.kt` | `ratatui/tests/backend_termion.rs` | 0/1 | 1/12 | no |
| `src/commonTest/kotlin/ratatui/terminal/TerminalTest.kt` | `ratatui/tests/terminal.rs` | 7/11 | 4/2 | yes |
| `src/desktopExamples/kotlin/ratatui/demo/App.kt` | `examples/apps/demo/src/app.rs` | 19/19 | 0/0 | no |
| `src/desktopExamples/kotlin/ratatui/demo/Crossterm.kt` | `examples/apps/demo/src/crossterm.rs` | 1/2 | 1/1 | no |
| `src/desktopExamples/kotlin/ratatui/demo/Termion.kt` | `examples/apps/demo/src/termion.rs` | 1/3 | 2/1 | no |
| `src/desktopExamples/kotlin/ratatui/demo/Termwiz.kt` | `examples/apps/demo/src/termwiz.rs` | 1/2 | 1/1 | no |
| `src/desktopExamples/kotlin/ratatui/demo/Ui.kt` | `examples/apps/demo/src/ui.rs` | 7/7 | 0/0 | no |
| `src/desktopExamples/kotlin/ratatui/demo2/Theme.kt` | `examples/apps/demo2/src/theme.rs` | 0/0 | 0/0 | no |
| `src/desktopMain/kotlin/ratatui/Init.kt` | `ratatui/src/init.rs` | 8/8 | 0/4 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/backend/Backend.kt` | `ratatui-core/src/backend.rs` | 3/5 | 2/1 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/backend/ScrollingRegionsBackend.kt` | `ratatui-core/src/backend.rs` | 0/5 | 5/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/backend/TestBackend.kt` | `ratatui-core/src/backend/test.rs` | 28/62 | 34/7 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/buffer/Assert.kt` | `ratatui-core/src/buffer/assert.rs` | 0/3 | 3/1 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/buffer/BufferDiff.kt` | `ratatui-core/src/buffer/diff.rs` | 4/14 | 10/1 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/buffer/Cell.kt` | `ratatui-core/src/buffer/cell.rs` | 17/31 | 14/6 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/buffer/CellWidth.kt` | `ratatui-core/src/buffer/cell_width.rs` | 1/4 | 3/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Alignment.kt` | `ratatui-core/src/layout/alignment.rs` | 0/4 | 4/2 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Constraint.kt` | `ratatui-core/src/layout/constraint.rs` | 12/21 | 9/11 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Direction.kt` | `ratatui-core/src/layout/direction.rs` | 1/4 | 3/1 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Flex.kt` | `ratatui-core/src/layout/flex.rs` | 0/0 | 0/8 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Layout.kt` | `ratatui-core/src/layout/layout.rs` | 32/93 | 61/10 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Offset.kt` | `ratatui-core/src/layout/offset.rs` | 2/5 | 3/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Position.kt` | `ratatui-core/src/layout/position.rs` | 5/20 | 15/7 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/RectOps.kt` | `ratatui-core/src/layout/rect/ops.rs` | 0/10 | 10/6 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Size.kt` | `ratatui-core/src/layout/size.rs` | 4/12 | 8/2 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/style/Anstyle.kt` | `ratatui-core/src/style/anstyle.rs` | 0/26 | 26/13 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/style/Color.kt` | `ratatui-core/src/style/color.rs` | 13/27 | 14/23 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/style/Style.kt` | `ratatui-core/src/style.rs` | 16/40 | 24/69 | yes |
| `ratatui-core/src/commonMain/kotlin/ratatui/style/Stylize.kt` | `ratatui-core/src/style/stylize.rs` | 9/27 | 18/377 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/style/palette/material/Material.kt` | `ratatui-core/src/style/palette/material.rs` | 2/2 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/style/palette/tailwind/Tailwind.kt` | `ratatui-core/src/style/palette/tailwind.rs` | 0/0 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/Braille.kt` | `ratatui-core/src/symbols/braille.rs` | 0/0 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/HalfBlock.kt` | `ratatui-core/src/symbols/half_block.rs` | 0/0 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/Marker.kt` | `ratatui-core/src/symbols/marker.rs` | 0/2 | 2/4 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/Pixel.kt` | `ratatui-core/src/symbols/pixel.rs` | 0/0 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/bar/Bar.kt` | `ratatui-core/src/symbols/bar.rs` | 1/1 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/block/Block.kt` | `ratatui-core/src/symbols/block.rs` | 1/1 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/border/Border.kt` | `ratatui-core/src/symbols/border.rs` | 2/23 | 21/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/line/Line.kt` | `ratatui-core/src/symbols/line.rs` | 1/7 | 6/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/merge/Merge.kt` | `ratatui-core/src/symbols/merge.rs` | 8/12 | 4/4 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/scrollbar/Scrollbar.kt` | `ratatui-core/src/symbols/scrollbar.rs` | 0/0 | 0/1 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/symbols/shade/Shade.kt` | `ratatui-core/src/symbols/shade.rs` | 0/0 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/terminal/Frame.kt` | `ratatui-core/src/terminal/frame.rs` | 8/8 | 0/2 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/terminal/Terminal.kt` | `ratatui-core/src/terminal.rs` | 0/1 | 1/29 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/terminal/Viewport.kt` | `ratatui-core/src/terminal/viewport.rs` | 0/2 | 2/7 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/text/Grapheme.kt` | `ratatui-core/src/text/grapheme.rs` | 3/8 | 5/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/text/Line.kt` | `ratatui-core/src/text/line.rs` | 25/111 | 86/10 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/text/Masked.kt` | `ratatui-core/src/text/masked.rs` | 3/16 | 13/5 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/text/Span.kt` | `ratatui-core/src/text/span.rs` | 17/63 | 46/3 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/text/Text.kt` | `ratatui-core/src/text/text.rs` | 27/102 | 75/9 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/widgets/StatefulWidget.kt` | `ratatui-core/src/widgets/stateful_widget.rs` | 0/6 | 6/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/widgets/Widget.kt` | `ratatui-core/src/widgets/widget.rs` | 2/12 | 10/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/backend/ClearTypeTest.kt` | `ratatui-core/src/backend.rs` | 0/5 | 5/2 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/backend/TestBackendTest.kt` | `ratatui-core/src/backend/test.rs` | 1/62 | 61/37 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/buffer/AssertTest.kt` | `ratatui-core/src/buffer/assert.rs` | 0/3 | 3/3 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/buffer/BufferDiffTest.kt` | `ratatui-core/src/buffer/diff.rs` | 10/14 | 4/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/buffer/BufferTest.kt` | `ratatui-core/src/buffer/buffer.rs` | 40/69 | 29/5 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/buffer/CellTest.kt` | `ratatui-core/src/buffer/cell.rs` | 14/31 | 17/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/buffer/CellWidthTest.kt` | `ratatui-core/src/buffer/cell_width.rs` | 3/4 | 1/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/AlignmentTest.kt` | `ratatui-core/src/layout/alignment.rs` | 1/4 | 3/3 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/ConstraintTest.kt` | `ratatui-core/src/layout/constraint.rs` | 8/21 | 13/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/OffsetTest.kt` | `ratatui-core/src/layout/offset.rs` | 3/5 | 2/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/PositionTest.kt` | `ratatui-core/src/layout/position.rs` | 8/20 | 12/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/RectOpsTest.kt` | `ratatui-core/src/layout/rect/ops.rs` | 4/10 | 6/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/style/AnstyleTest.kt` | `ratatui-core/src/style/anstyle.rs` | 8/26 | 18/6 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/style/ColorTest.kt` | `ratatui-core/src/style/color.rs` | 10/27 | 17/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/style/StyleTest.kt` | `ratatui-core/src/style.rs` | 15/40 | 25/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/symbols/MarkerTest.kt` | `ratatui-core/src/symbols/marker.rs` | 1/2 | 1/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/terminal/CursorTest.kt` | `ratatui-core/src/terminal/cursor.rs` | 5/11 | 6/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/text/GraphemeTest.kt` | `ratatui-core/src/text/grapheme.rs` | 4/8 | 4/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/text/MaskedTest.kt` | `ratatui-core/src/text/masked.rs` | 3/16 | 13/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/widgets/StatefulWidgetRefDynTest.kt` | `ratatui/tests/stateful_widget_ref_dyn.rs` | 4/4 | 0/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/widgets/StatefulWidgetTest.kt` | `ratatui-core/src/widgets/stateful_widget.rs` | 6/6 | 0/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/widgets/WidgetTest.kt` | `ratatui-core/src/widgets/widget.rs` | 9/12 | 3/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/Borders.kt` | `ratatui-widgets/src/borders.rs` | 2/8 | 6/14 | yes |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/Clear.kt` | `ratatui-widgets/src/clear.rs` | 1/5 | 4/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/HighlightSpacing.kt` | `ratatui-widgets/src/table/highlight_spacing.rs` | 2/3 | 1/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/barchart/Bar.kt` | `ratatui-widgets/src/barchart/bar.rs` | 11/15 | 4/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/barchart/BarChart.kt` | `ratatui-widgets/src/barchart.rs` | 26/67 | 41/1 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/barchart/BarGroup.kt` | `ratatui-widgets/src/barchart/bar_group.rs` | 8/10 | 2/5 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/block/Block.kt` | `ratatui-widgets/src/block.rs` | 34/92 | 58/7 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/block/Padding.kt` | `ratatui-widgets/src/block/padding.rs` | 11/16 | 5/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/calendar/Monthly.kt` | `ratatui-widgets/src/calendar.rs` | 18/28 | 10/5 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/Canvas.kt` | `ratatui-widgets/src/canvas.rs` | 30/42 | 12/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/Circle.kt` | `ratatui-widgets/src/canvas/circle.rs` | 2/3 | 1/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/Line.kt` | `ratatui-widgets/src/canvas/line.rs` | 2/6 | 4/0 | yes |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/Map.kt` | `ratatui-widgets/src/canvas/map.rs` | 3/7 | 4/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/Points.kt` | `ratatui-widgets/src/canvas/points.rs` | 2/2 | 0/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/Rectangle.kt` | `ratatui-widgets/src/canvas/rectangle.rs` | 2/8 | 6/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/canvas/WorldData.kt` | `ratatui-widgets/src/canvas/world.rs` | 0/0 | 0/1 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/list/List.kt` | `ratatui-widgets/src/list.rs` | 13/27 | 14/8 | yes |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/list/ListItem.kt` | `ratatui-widgets/src/list/item.rs` | 4/19 | 15/6 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/list/ListState.kt` | `ratatui-widgets/src/list/state.rs` | 9/16 | 7/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/logo/RatatuiLogo.kt` | `ratatui-widgets/src/logo.rs` | 5/17 | 12/2 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/mascot/RatatuiMascot.kt` | `ratatui-widgets/src/mascot.rs` | 5/10 | 5/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/paragraph/Paragraph.kt` | `ratatui-widgets/src/paragraph.rs` | 17/56 | 39/4 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/reflow/Reflow.kt` | `ratatui-widgets/src/reflow.rs` | 8/29 | 21/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/scrollbar/Scrollbar.kt` | `ratatui-widgets/src/scrollbar.rs` | 32/59 | 27/10 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/table/Cell.kt` | `ratatui-widgets/src/table/cell.rs` | 7/12 | 5/4 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/table/Row.kt` | `ratatui-widgets/src/table/row.rs` | 10/18 | 8/2 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/table/Table.kt` | `ratatui-widgets/src/table.rs` | 28/99 | 71/0 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/table/TableState.kt` | `ratatui-widgets/src/table/state.rs` | 20/46 | 26/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/tabs/Tabs.kt` | `ratatui-widgets/src/tabs.rs` | 17/45 | 28/7 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/BordersTest.kt` | `ratatui-widgets/src/borders.rs` | 3/8 | 5/1 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/barchart/BarChartTest.kt` | `ratatui/tests/widgets_barchart.rs` | 2/2 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/block/BlockTest.kt` | `ratatui/tests/widgets_block.rs` | 8/8 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/calendar/MonthlyTest.kt` | `ratatui/tests/widgets_calendar.rs` | 6/6 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/canvas/CanvasTest.kt` | `ratatui/tests/widgets_canvas.rs` | 1/1 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/chart/ChartTest.kt` | `ratatui/tests/widgets_chart.rs` | 9/10 | 1/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/gauge/GaugeTest.kt` | `ratatui/tests/widgets_gauge.rs` | 5/5 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/list/ListTest.kt` | `ratatui/tests/widgets_list.rs` | 9/9 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/paragraph/ParagraphTest.kt` | `ratatui/tests/widgets_paragraph.rs` | 8/8 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/table/TableTest.kt` | `ratatui/tests/widgets_table.rs` | 11/11 | 0/0 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/tabs/TabsTest.kt` | `ratatui/tests/widgets_tabs.rs` | 2/2 | 0/0 | no |
| `ratatui-macros/src/commonMain/kotlin/ratatui_macros/Layout.kt` | `ratatui-macros/src/layout.rs` | 0/0 | 0/10 | no |
| `ratatui-macros/src/commonMain/kotlin/ratatui_macros/Line.kt` | `ratatui-macros/src/line.rs` | 0/7 | 7/4 | no |
| `ratatui-macros/src/commonMain/kotlin/ratatui_macros/Row.kt` | `ratatui-macros/src/row.rs` | 0/7 | 7/4 | no |
| `ratatui-macros/src/commonMain/kotlin/ratatui_macros/Span.kt` | `ratatui-macros/src/span.rs` | 0/2 | 2/14 | no |
| `ratatui-macros/src/commonTest/kotlin/ratatui_macros/LineTest.kt` | `ratatui-macros/src/line.rs` | 7/7 | 0/0 | no |
| `ratatui-macros/src/commonTest/kotlin/ratatui_macros/MacrosTest.kt` | `ratatui-macros/tests/macros.rs` | 2/2 | 0/0 | no |
| `ratatui-macros/src/commonTest/kotlin/ratatui_macros/RowTest.kt` | `ratatui-macros/src/row.rs` | 7/7 | 0/0 | no |
| `ratatui-macros/src/commonTest/kotlin/ratatui_macros/UiFailsTest.kt` | `ratatui-macros/tests/ui/fails.rs` | 0/1 | 1/1 | no |
| `xtask/src/commonMain/kotlin/ratatui/xtask/commands/Check.kt` | `xtask/src/commands/check.rs` | 1/1 | 0/0 | no |
| `xtask/src/commonMain/kotlin/ratatui/xtask/commands/Clippy.kt` | `xtask/src/commands/clippy.rs` | 1/1 | 0/0 | no |
| `xtask/src/commonMain/kotlin/ratatui/xtask/commands/Coverage.kt` | `xtask/src/commands/coverage.rs` | 1/1 | 0/0 | no |
| `xtask/src/commonMain/kotlin/ratatui/xtask/commands/Docs.kt` | `xtask/src/commands/docs.rs` | 1/1 | 0/0 | no |
| `xtask/src/commonMain/kotlin/ratatui/xtask/commands/Format.kt` | `xtask/src/commands/format.rs` | 3/3 | 0/0 | no |
| `xtask/src/commonMain/kotlin/ratatui/xtask/commands/Typos.kt` | `xtask/src/commands/typos.rs` | 1/1 | 0/0 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/buffer/Buffer.kt` | `ratatui-core/src/buffer/buffer.rs` | 25/69 | 44/13 | yes |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Margin.kt` | `ratatui-core/src/layout/margin.rs` | 2/6 | 4/2 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/Rect.kt` | `ratatui-core/src/layout/rect.rs` | 31/73 | 42/2 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/rect/Iter.kt` | `ratatui-core/src/layout/rect/iter.rs` | 11/23 | 12/2 | no |
| `ratatui-core/src/commonMain/kotlin/ratatui/layout/rect/Ops.kt` | `ratatui-core/src/layout/rect/ops.rs` | 0/10 | 10/4 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/MarginTest.kt` | `ratatui-core/src/layout/margin.rs` | 3/6 | 3/0 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/RectTest.kt` | `ratatui-core/src/layout/rect.rs` | 5/73 | 68/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/RectUnitTest.kt` | `ratatui-core/src/layout/rect.rs` | 41/73 | 32/1 | no |
| `ratatui-core/src/commonTest/kotlin/ratatui/layout/rect/IterTest.kt` | `ratatui-core/src/layout/rect/iter.rs` | 12/23 | 11/0 | no |
| `ratatui-core/src/nativeTest/kotlin/ratatui/style/StylizeTest.kt` | `ratatui-core/src/style/stylize.rs` | 14/27 | 13/2 | no |
| `ratatui-core/src/nativeTest/kotlin/ratatui/text/LineTest.kt` | `ratatui-core/src/text/line.rs` | 28/111 | 83/0 | no |
| `ratatui-core/src/nativeTest/kotlin/ratatui/text/TextTest.kt` | `ratatui-core/src/text/text.rs` | 29/102 | 73/7 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/chart/Axis.kt` | `ratatui-widgets/src/chart.rs` | 7/51 | 44/8 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/chart/Chart.kt` | `ratatui-widgets/src/chart.rs` | 15/51 | 36/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/chart/Dataset.kt` | `ratatui-widgets/src/chart.rs` | 7/51 | 44/6 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/chart/GraphType.kt` | `ratatui-widgets/src/chart.rs` | 0/51 | 51/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/chart/LegendPosition.kt` | `ratatui-widgets/src/chart.rs` | 1/51 | 50/1 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/gauge/Gauge.kt` | `ratatui-widgets/src/gauge.rs` | 13/42 | 29/1 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/gauge/LineGauge.kt` | `ratatui-widgets/src/gauge.rs` | 12/42 | 30/1 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/sparkline/RenderDirection.kt` | `ratatui-widgets/src/sparkline.rs` | 0/42 | 42/3 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/sparkline/Sparkline.kt` | `ratatui-widgets/src/sparkline.rs` | 13/42 | 29/7 | no |
| `ratatui-widgets/src/commonMain/kotlin/ratatui/widgets/sparkline/SparklineBar.kt` | `ratatui-widgets/src/sparkline.rs` | 4/42 | 38/2 | no |
| `ratatui-widgets/src/commonTest/kotlin/ratatui/widgets/HighlightSpacingTest.kt` | `ratatui-widgets/src/table/highlight_spacing.rs` | 0/3 | 3/2 | no |
| `ratatui-widgets/src/nativeTest/kotlin/ratatui/widgets/sparkline/SparklineTest.kt` | `ratatui-widgets/src/sparkline.rs` | 1/42 | 41/21 | no |
| `ratatui-crossterm/src/commonMain/kotlin/ratatui_crossterm/CrosstermBackend.kt` | `ratatui-crossterm/src/lib.rs` | 21/37 | 16/4 | no |
| `ratatui-crossterm/src/commonTest/kotlin/ratatui_crossterm/CrosstermBackendTest.kt` | `ratatui-crossterm/src/lib.rs` | 0/37 | 37/3 | no |

## Remaining ownership and behavior review

Eight Kotlin files have no direct single Rust-file pairing: the new Serde
contract tests, StdoutBuffer, WidgetRef, StatefulWidgetRef, StateSerde,
WidgetStylize, combined LogoMascotTest and CommandRunner. They remain explicit
glue/split-ownership review items, not omitted successes. WidgetRef traits
must additionally be checked against the pinned upstream supported trait
surface. The Crossterm implementation is compared to real crate `lib.rs`
without adding a Kotlin reexport wrapper or a fabricated provenance header.

For Buffer, the tool marks Rust `content()` and `area()` unmatched while Kotlin
exposes properties. This requires contract review instead of new methods merely
to raise a score. Rust production files also contain tests; their matches are
split between production and test comparison rows. A missing strict-name match
alone cannot establish that behavior is absent.

The latest full root run at `ab24b0ee` generated 3,788 fresh passing cases across
20 test tasks, with zero failures/errors/skips in those XML files. Root integration
tests and several widgets target compilations were blocked before execution;
strict published Swift runtime compilation failed before Swift tests. Repairs
to module dependencies/test construction after that run remain unvalidated.
Historical test receipts do not prove current exact-head or all-target success.

## Cargo feature coverage

All 42 pinned workspace manifests, 226 dependency declarations, inherited
features, default-feature/optional values, target predicates and path edges are
recorded in `rust-workspace.json` and explained in `RUST_DEPENDENCIES.md`.

| Feature/member | Current source and behavior evidence | Open gap |
| --- | --- | --- |
| core/widgets serde | Real published Serde traits; nine golden tests plus eleven contract tests retained | Existing JSON producer has two confirmed error-propagation/unit-variant bugs; concrete Swift outcomes require published producer repair |
| calendar/time | Existing datetime-backed implementation and Monthly tests retained | Matching published time sibling behavior has not been established |
| crossterm | Actual backend module; three macOS backend tests passed | Backend currently supports only three desktop Native targets |
| termion/termwiz | Upstream optional manifests inventoried | No real backend modules; named demos currently use Crossterm, not member parity |
| feature toggles/std/cache/atomics | Raw feature closures/edges recorded | Enabled Kotlin surface is not Cargo compile-feature parity; synchronization/layout-cache behavior needs specific review |
| chart/gauge/sparkline/layout/text | Real sources and per-function reports retained | Split-file matches and divergent/missing functions need behavior-by-behavior review |
| examples/xtask | Existing examples compilation separated from publications; actual tooling source preserved | No claim of full 33-example or tooling behavior parity |

No missing feature is waived by a similarity score, Kotlin compilation, or a
passing subset of tests. Per-coordinate staged metadata, consumer execution,
strict Swift and complete platform CI remain release gates.
