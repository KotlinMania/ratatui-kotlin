// port-lint: source ratatui/tests/widgetsTable.rs
@file:Suppress("DEPRECATION")

package ratatui.widgets.table

import kotlin.test.Test
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.layout.Constraint
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.text.Line
import ratatui.text.Span
import ratatui.terminal.Terminal
import ratatui.widgets.Borders
import ratatui.widgets.HighlightSpacing
import ratatui.widgets.block.Block

class TableTest {
    @Test
    fun widgetsTableColumnSpacingCanBeChanged() {
        data class Case(val columnSpacing: Int, val expected: List<String>)

        val cases = listOf(
            Case(0, listOf(
                "┌────────────────────────────┐",
                "│Head1Head2Head3             │",
                "│                            │",
                "│Row11Row12Row13             │",
                "│Row21Row22Row23             │",
                "│Row31Row32Row33             │",
                "│Row41Row42Row43             │",
                "│                            │",
                "│                            │",
                "└────────────────────────────┘",
            )),
            Case(1, listOf(
                "┌────────────────────────────┐",
                "│Head1 Head2 Head3           │",
                "│                            │",
                "│Row11 Row12 Row13           │",
                "│Row21 Row22 Row23           │",
                "│Row31 Row32 Row33           │",
                "│Row41 Row42 Row43           │",
                "│                            │",
                "│                            │",
                "└────────────────────────────┘",
            )),
            Case(6, listOf(
                "┌────────────────────────────┐",
                "│Head1      Head2      Head3 │",
                "│                            │",
                "│Row11      Row12      Row13 │",
                "│Row21      Row22      Row23 │",
                "│Row31      Row32      Row33 │",
                "│Row41      Row42      Row43 │",
                "│                            │",
                "│                            │",
                "└────────────────────────────┘",
            )),
            Case(7, listOf(
                "┌────────────────────────────┐",
                "│Head1       Head       Head3│",
                "│                            │",
                "│Row11       Row1       Row13│",
                "│Row21       Row2       Row23│",
                "│Row31       Row3       Row33│",
                "│Row41       Row4       Row43│",
                "│                            │",
                "│                            │",
                "└────────────────────────────┘",
            )),
        )

        val backend = TestBackend.new(30, 10)
        val terminal = Terminal(backend)

        for (case in cases) {
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23"),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43"),
                    ),
                    listOf(
                        Constraint.Length(5),
                        Constraint.Length(5),
                        Constraint.Length(5),
                    ),
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                    .columnSpacing(case.columnSpacing)
                f.renderWidget(table, f.area())
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableColumnsWidthsCanUseFixedLengthConstraints() {
        data class Case(val widths: List<Constraint>, val expected: List<String>)

        val cases = listOf(
            Case(
                listOf(Constraint.Length(0), Constraint.Length(0), Constraint.Length(0)),
                listOf(
                    "┌────────────────────────────┐",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Length(1), Constraint.Length(1), Constraint.Length(1)),
                listOf(
                    "┌────────────────────────────┐",
                    "│H H H                       │",
                    "│                            │",
                    "│R R R                       │",
                    "│R R R                       │",
                    "│R R R                       │",
                    "│R R R                       │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Length(8), Constraint.Length(8), Constraint.Length(8)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1    Head2    Head3     │",
                    "│                            │",
                    "│Row11    Row12    Row13     │",
                    "│Row21    Row22    Row23     │",
                    "│Row31    Row32    Row33     │",
                    "│Row41    Row42    Row43     │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
        )

        val backend = TestBackend.new(30, 10)
        val terminal = Terminal(backend)

        for (case in cases) {
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23"),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43"),
                    ),
                    case.widths,
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                f.renderWidget(table, f.area())
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableColumnsWidthsCanUsePercentageConstraints() {
        data class Case(val widths: List<Constraint>, val expected: List<String>)

        val cases = listOf(
            Case(
                listOf(Constraint.Percentage(0), Constraint.Percentage(0), Constraint.Percentage(0)),
                listOf(
                    "┌────────────────────────────┐",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Percentage(11), Constraint.Percentage(11), Constraint.Percentage(11)),
                listOf(
                    "┌────────────────────────────┐",
                    "│HeaHeaHea                   │",
                    "│                            │",
                    "│RowRowRow                   │",
                    "│RowRowRow                   │",
                    "│RowRowRow                   │",
                    "│RowRowRow                   │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Percentage(33), Constraint.Percentage(33), Constraint.Percentage(33)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1    Head2    Head3     │",
                    "│                            │",
                    "│Row11    Row12    Row13     │",
                    "│Row21    Row22    Row23     │",
                    "│Row31    Row32    Row33     │",
                    "│Row41    Row42    Row43     │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Percentage(50), Constraint.Percentage(50)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1         Head2         │",
                    "│                            │",
                    "│Row11         Row12         │",
                    "│Row21         Row22         │",
                    "│Row31         Row32         │",
                    "│Row41         Row42         │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
        )

        val backend = TestBackend.new(30, 10)
        val terminal = Terminal(backend)

        for (case in cases) {
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23"),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43"),
                    ),
                    case.widths,
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                    .columnSpacing(0)
                f.renderWidget(table, f.area())
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableColumnsWidthsCanUseMixedConstraints() {
        data class Case(val widths: List<Constraint>, val expected: List<String>)

        val cases = listOf(
            Case(
                listOf(Constraint.Percentage(0), Constraint.Length(0), Constraint.Percentage(0)),
                listOf(
                    "┌────────────────────────────┐",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Percentage(11), Constraint.Length(20), Constraint.Percentage(11)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Hea Head2                Hea│",
                    "│                            │",
                    "│Row Row12                Row│",
                    "│Row Row22                Row│",
                    "│Row Row32                Row│",
                    "│Row Row42                Row│",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Percentage(33), Constraint.Length(10), Constraint.Percentage(33)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1     Head2      Head3  │",
                    "│                            │",
                    "│Row11     Row12      Row13  │",
                    "│Row21     Row22      Row23  │",
                    "│Row31     Row32      Row33  │",
                    "│Row41     Row42      Row43  │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Percentage(60), Constraint.Length(10), Constraint.Percentage(60)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1      Head2      Head3 │",
                    "│                            │",
                    "│Row11      Row12      Row13 │",
                    "│Row21      Row22      Row23 │",
                    "│Row31      Row32      Row33 │",
                    "│Row41      Row42      Row43 │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
        )

        val backend = TestBackend.new(30, 10)
        val terminal = Terminal(backend)

        for (case in cases) {
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23"),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43"),
                    ),
                    case.widths,
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                f.renderWidget(table, f.area())
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableColumnsWidthsCanUseRatioConstraints() {
        data class Case(val widths: List<Constraint>, val expected: List<String>)

        val cases = listOf(
            Case(
                listOf(Constraint.Ratio(0u, 1u), Constraint.Ratio(0u, 1u), Constraint.Ratio(0u, 1u)),
                listOf(
                    "┌────────────────────────────┐",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Ratio(1u, 9u), Constraint.Ratio(1u, 9u), Constraint.Ratio(1u, 9u)),
                listOf(
                    "┌────────────────────────────┐",
                    "│HeaHeaHea                   │",
                    "│                            │",
                    "│RowRowRow                   │",
                    "│RowRowRow                   │",
                    "│RowRowRow                   │",
                    "│RowRowRow                   │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Ratio(1u, 3u), Constraint.Ratio(1u, 3u), Constraint.Ratio(1u, 3u)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1    Head2     Head3    │",
                    "│                            │",
                    "│Row11    Row12     Row13    │",
                    "│Row21    Row22     Row23    │",
                    "│Row31    Row32     Row33    │",
                    "│Row41    Row42     Row43    │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                listOf(Constraint.Ratio(1u, 2u), Constraint.Ratio(1u, 2u)),
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1         Head2         │",
                    "│                            │",
                    "│Row11         Row12         │",
                    "│Row21         Row22         │",
                    "│Row31         Row32         │",
                    "│Row41         Row42         │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
        )

        val backend = TestBackend.new(30, 10)
        val terminal = Terminal(backend)

        for (case in cases) {
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23"),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43"),
                    ),
                    case.widths,
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                    .columnSpacing(0)
                f.renderWidget(table, f.area())
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableCanHaveRowsWithMultiLines() {
        data class Case(val selected: Int?, val expected: List<String>)

        val cases = listOf(
            Case(
                null,
                listOf(
                    "┌────────────────────────────┐",
                    "│Head1 Head2 Head3           │",
                    "│                            │",
                    "│Row11 Row12 Row13           │",
                    "│Row21 Row22 Row23           │",
                    "│                            │",
                    "│Row31 Row32 Row33           │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                0,
                listOf(
                    "┌────────────────────────────┐",
                    "│   Head1 Head2 Head3        │",
                    "│                            │",
                    "│>> Row11 Row12 Row13        │",
                    "│   Row21 Row22 Row23        │",
                    "│                            │",
                    "│   Row31 Row32 Row33        │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                1,
                listOf(
                    "┌────────────────────────────┐",
                    "│   Head1 Head2 Head3        │",
                    "│                            │",
                    "│   Row11 Row12 Row13        │",
                    "│>> Row21 Row22 Row23        │",
                    "│                            │",
                    "│   Row31 Row32 Row33        │",
                    "└────────────────────────────┘",
                ),
            ),
            Case(
                3,
                listOf(
                    "┌────────────────────────────┐",
                    "│   Head1 Head2 Head3        │",
                    "│                            │",
                    "│   Row31 Row32 Row33        │",
                    "│>> Row41 Row42 Row43        │",
                    "│                            │",
                    "│                            │",
                    "└────────────────────────────┘",
                ),
            ),
        )

        for (case in cases) {
            val state = TableState.default().withSelected(case.selected)
            val backend = TestBackend.new(30, 8)
            val terminal = Terminal(backend)
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23").height(2),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43").height(2),
                    ),
                    listOf(
                        Constraint.Length(5),
                        Constraint.Length(5),
                        Constraint.Length(5),
                    ),
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                    .highlightSymbol(">> ")
                    .columnSpacing(1)
                f.renderStatefulWidget(table, f.area(), state)
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableEnableAlwaysHighlightSpacing() {
        data class Case(val selected: Int?, val space: HighlightSpacing, val expected: List<String>)

        val cases = listOf(
            Case(null, HighlightSpacing.WhenSelected, listOf(
                "┌────────────────────────────┐",
                "│Head1 Head2 Head3           │",
                "│                            │",
                "│Row11 Row12 Row13           │",
                "│Row21 Row22 Row23           │",
                "│                            │",
                "│Row31 Row32 Row33           │",
                "└────────────────────────────┘",
            )),
            Case(null, HighlightSpacing.Always, listOf(
                "┌────────────────────────────┐",
                "│   Head1 Head2 Head3        │",
                "│                            │",
                "│   Row11 Row12 Row13        │",
                "│   Row21 Row22 Row23        │",
                "│                            │",
                "│   Row31 Row32 Row33        │",
                "└────────────────────────────┘",
            )),
            Case(null, HighlightSpacing.Never, listOf(
                "┌────────────────────────────┐",
                "│Head1 Head2 Head3           │",
                "│                            │",
                "│Row11 Row12 Row13           │",
                "│Row21 Row22 Row23           │",
                "│                            │",
                "│Row31 Row32 Row33           │",
                "└────────────────────────────┘",
            )),
            Case(0, HighlightSpacing.WhenSelected, listOf(
                "┌────────────────────────────┐",
                "│   Head1 Head2 Head3        │",
                "│                            │",
                "│>> Row11 Row12 Row13        │",
                "│   Row21 Row22 Row23        │",
                "│                            │",
                "│   Row31 Row32 Row33        │",
                "└────────────────────────────┘",
            )),
            Case(0, HighlightSpacing.Always, listOf(
                "┌────────────────────────────┐",
                "│   Head1 Head2 Head3        │",
                "│                            │",
                "│>> Row11 Row12 Row13        │",
                "│   Row21 Row22 Row23        │",
                "│                            │",
                "│   Row31 Row32 Row33        │",
                "└────────────────────────────┘",
            )),
            Case(0, HighlightSpacing.Never, listOf(
                "┌────────────────────────────┐",
                "│Head1 Head2 Head3           │",
                "│                            │",
                "│Row11 Row12 Row13           │",
                "│Row21 Row22 Row23           │",
                "│                            │",
                "│Row31 Row32 Row33           │",
                "└────────────────────────────┘",
            )),
        )

        for (case in cases) {
            val state = TableState.default().withSelected(case.selected)
            val backend = TestBackend.new(30, 8)
            val terminal = Terminal(backend)
            terminal.draw { f ->
                val table = Table.new(
                    listOf(
                        Row.new("Row11", "Row12", "Row13"),
                        Row.new("Row21", "Row22", "Row23").height(2),
                        Row.new("Row31", "Row32", "Row33"),
                        Row.new("Row41", "Row42", "Row43").height(2),
                    ),
                    listOf(
                        Constraint.Length(5),
                        Constraint.Length(5),
                        Constraint.Length(5),
                    ),
                )
                    .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                    .block(Block.bordered())
                    .highlightSymbol(">> ")
                    .highlightSpacing(case.space)
                    .columnSpacing(1)
                f.renderStatefulWidget(table, f.area(), state)
            }
            terminal.backend().assertBufferLines(case.expected)
        }
    }

    @Test
    fun widgetsTableCanHaveElementsStyledIndividually() {
        val backend = TestBackend.new(30, 4)
        val terminal = Terminal(backend)
        val state = TableState.default().apply {
            select(0)
            selectColumn(1)
        }

        terminal.draw { f ->
            val table = Table.new(
                listOf(
                    Row.new("Row11", "Row12", "Row13").style(Style.default().fg(Color.Green)),
                    Row.new(
                        listOf(
                            Cell.new("Row21"),
                            Cell.new("Row22").style(Style.default().fg(Color.Yellow)),
                            Cell.new(
                                Line.from(
                                    listOf(
                                        Span.raw("Row"),
                                        Span.styled("23", Style.default().fg(Color.Blue)),
                                    )
                                )
                            ).style(Style.default().fg(Color.Red)),
                        )
                    ).style(Style.default().fg(Color.LightGreen)),
                ),
                listOf(
                    Constraint.Length(6),
                    Constraint.Length(6),
                    Constraint.Length(6),
                ),
            )
                .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                .block(Block.new().borders(Borders.LEFT or Borders.RIGHT))
                .highlightSymbol(">> ")
                .rowHighlightStyle(Style.default().addModifier(Modifier.BOLD))
                .columnHighlightStyle(Style.default().addModifier(Modifier.ITALIC))
                .cellHighlightStyle(Style.default().addModifier(Modifier.DIM))
                .columnSpacing(1)
            f.renderStatefulWidget(table, f.area(), state)
        }

        val expected = Buffer.withLines(
            "│   Head1  Head2  Head3      │",
            "│                            │",
            "│>> Row11  Row12  Row13      │",
            "│   Row21  Row22  Row23      │",
        )

        // First row = row color + highlight style.
        for (col in 1..28) {
            expected[col, 2].setStyle(Style.default().fg(Color.Green).addModifier(Modifier.BOLD))
        }

        // Second column highlight style.
        for (row in 2..3) {
            for (col in 11..16) {
                expected[col, row].setStyle(Style.default().addModifier(Modifier.ITALIC))
            }
        }

        // First row, second column highlight style (cell highlight).
        for (col in 11..16) {
            expected[col, 2].setStyle(Style.default().addModifier(Modifier.DIM))
        }

        // Second row:
        // 1. row color
        for (col in 1..28) {
            expected[col, 3].setStyle(Style.default().fg(Color.LightGreen))
        }
        // 2. cell color
        for (col in 11..16) {
            expected[col, 3].setStyle(Style.default().fg(Color.Yellow))
        }
        for (col in 18..23) {
            expected[col, 3].setStyle(Style.default().fg(Color.Red))
        }
        // 3. text color
        for (col in 21..22) {
            expected[col, 3].setStyle(Style.default().fg(Color.Blue))
        }

        terminal.backend().assertBuffer(expected)
    }

    @Test
    fun widgetsTableShouldRenderEvenIfEmpty() {
        val backend = TestBackend.new(30, 4)
        val terminal = Terminal(backend)

        terminal.draw { f ->
            val table = Table.new(
                emptyList(),
                listOf(
                    Constraint.Length(6),
                    Constraint.Length(6),
                    Constraint.Length(6),
                ),
            )
                .header(Row.new("Head1", "Head2", "Head3"))
                .block(Block.new().borders(Borders.LEFT or Borders.RIGHT))
                .columnSpacing(1)
            f.renderWidget(table, f.area())
        }

        terminal.backend().assertBufferLines(
            "│Head1  Head2  Head3         │",
            "│                            │",
            "│                            │",
            "│                            │",
        )
    }

    @Test
    fun widgetsTableColumnsDontPanic() {
        val tableWidth = 98

        val table = Table.new(
            listOf(Row.new("r1", "r2", "r3", "r4")),
            listOf(
                Constraint.Percentage(15),
                Constraint.Percentage(15),
                Constraint.Percentage(25),
                Constraint.Percentage(45),
            ),
        )
            .header(Row.new("h1", "h2", "h3", "h4"))
            .block(Block.bordered())
            .highlightSymbol(">> ")
            .columnSpacing(1)

        val state = TableState.default().apply {
            // Select first, which would cause a panic before fix.
            select(0)
        }

        val backend = TestBackend.new(tableWidth, 8)
        val terminal = Terminal(backend)
        terminal.draw { f ->
            f.renderStatefulWidget(table, f.area(), state)
        }
    }

    @Test
    fun widgetsTableShouldClampOffsetIfRowsAreRemoved() {
        val backend = TestBackend.new(30, 8)
        val terminal = Terminal(backend)
        val state = TableState.default()

        // Render with 6 items => offset will be at 2.
        state.select(5)
        terminal.draw { f ->
            val table = Table.new(
                listOf(
                    Row.new("Row01", "Row02", "Row03"),
                    Row.new("Row11", "Row12", "Row13"),
                    Row.new("Row21", "Row22", "Row23"),
                    Row.new("Row31", "Row32", "Row33"),
                    Row.new("Row41", "Row42", "Row43"),
                    Row.new("Row51", "Row52", "Row53"),
                ),
                listOf(
                    Constraint.Length(5),
                    Constraint.Length(5),
                    Constraint.Length(5),
                ),
            )
                .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                .block(Block.bordered())
                .columnSpacing(1)
            f.renderStatefulWidget(table, f.area(), state)
        }
        terminal.backend().assertBufferLines(
            "┌────────────────────────────┐",
            "│Head1 Head2 Head3           │",
            "│                            │",
            "│Row21 Row22 Row23           │",
            "│Row31 Row32 Row33           │",
            "│Row41 Row42 Row43           │",
            "│Row51 Row52 Row53           │",
            "└────────────────────────────┘",
        )

        // Render with 1 item => offset will be at 1.
        state.select(1)
        terminal.draw { f ->
            val table = Table.new(
                listOf(Row.new("Row31", "Row32", "Row33")),
                listOf(
                    Constraint.Length(5),
                    Constraint.Length(5),
                    Constraint.Length(5),
                ),
            )
                .header(Row.new("Head1", "Head2", "Head3").bottomMargin(1))
                .block(Block.bordered())
                .columnSpacing(1)
            f.renderStatefulWidget(table, f.area(), state)
        }
        terminal.backend().assertBufferLines(
            "┌────────────────────────────┐",
            "│Head1 Head2 Head3           │",
            "│                            │",
            "│Row31 Row32 Row33           │",
            "│                            │",
            "│                            │",
            "│                            │",
            "└────────────────────────────┘",
        )
    }
}
