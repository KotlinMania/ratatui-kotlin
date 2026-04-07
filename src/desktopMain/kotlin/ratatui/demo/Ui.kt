// port-lint: source examples/apps/demo/src/ui.rs
package ratatui.demo

import ratatui.layout.Constraint
import ratatui.layout.Layout
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.symbols.Marker
import ratatui.symbols.bar.NINE_LEVELS
import ratatui.symbols.bar.THREE_LEVELS
import ratatui.symbols.line.Line as LineSymbols
import ratatui.terminal.Frame
import ratatui.text.Line
import ratatui.text.Span
import ratatui.widgets.barchart.BarChart
import ratatui.widgets.barchart.BarGroup
import ratatui.widgets.block.Block
import ratatui.widgets.canvas.Canvas
import ratatui.widgets.canvas.Circle
import ratatui.widgets.canvas.Line as CanvasLine
import ratatui.widgets.canvas.Map
import ratatui.widgets.canvas.MapResolution
import ratatui.widgets.canvas.Rectangle
import ratatui.widgets.chart.Axis
import ratatui.widgets.chart.Chart
import ratatui.widgets.chart.Dataset
import ratatui.widgets.gauge.Gauge
import ratatui.widgets.gauge.LineGauge
import ratatui.widgets.list.List
import ratatui.widgets.list.ListItem
import ratatui.widgets.paragraph.Paragraph
import ratatui.widgets.paragraph.Wrap
import ratatui.widgets.sparkline.Sparkline
import ratatui.widgets.table.Cell
import ratatui.widgets.table.Row
import ratatui.widgets.table.Table
import ratatui.widgets.tabs.Tabs
import kotlin.math.roundToInt

fun render(frame: Frame, app: App) {
    val chunks = Layout.vertical(listOf(Constraint.Length(3), Constraint.Min(0))).split(frame.area())

    val titles = app.tabs.titles.map { t ->
        Line.from(Span.styled(t, Style.default().fg(Color.Green)))
    }

    val tabs = Tabs.new(titles)
        .block(Block.bordered().title(app.title))
        .highlightStyle(Style.default().fg(Color.Yellow))
        .select(app.tabs.index)

    frame.renderWidget(tabs, chunks[0])

    when (app.tabs.index) {
        0 -> drawFirstTab(frame, app, chunks[1])
        1 -> drawSecondTab(frame, app, chunks[1])
        2 -> drawThirdTab(frame, app, chunks[1])
        else -> {}
    }
}

private fun drawFirstTab(frame: Frame, app: App, area: Rect) {
    val chunks = Layout.vertical(
        listOf(
            Constraint.Length(9),
            Constraint.Min(8),
            Constraint.Length(7)
        )
    ).split(area)

    drawGauges(frame, app, chunks[0])
    drawCharts(frame, app, chunks[1])
    drawText(frame, chunks[2])
}

private fun drawGauges(frame: Frame, app: App, area: Rect) {
    val chunks = Layout.vertical(
        listOf(
            Constraint.Length(2),
            Constraint.Length(3),
            Constraint.Length(2)
        )
    ).margin(1).split(area)

    val block = Block.bordered().title("Graphs")
    frame.renderWidget(block, area)

    val percent = app.progress * 100.0
    val percentRounded = (percent * 100.0).roundToInt()
    val label = run {
        val whole = percentRounded / 100
        val frac = (percentRounded % 100).toString().padStart(2, '0')
        "$whole.$frac%"
    }

    val gauge = Gauge.default()
        .block(Block.new().title("Gauge:"))
        .gaugeStyle(
            Style.default()
                .fg(Color.Magenta)
                .bg(Color.Black)
                .addModifier(Modifier.ITALIC or Modifier.BOLD)
        )
        .useUnicode(app.enhancedGraphics)
        .label(label)
        .ratio(app.progress)
    frame.renderWidget(gauge, chunks[0])

    val sparkline = Sparkline.default()
        .block(Block.new().title("Sparkline:"))
        .style(Style.default().fg(Color.Green))
        .dataFromValues(app.sparkline.points)
        .barSet(if (app.enhancedGraphics) NINE_LEVELS else THREE_LEVELS)
    frame.renderWidget(sparkline, chunks[1])

    val lineGauge = LineGauge.default()
        .block(Block.new().title("LineGauge:"))
        .filledStyle(Style.default().fg(Color.Magenta))
        .filledSymbol(if (app.enhancedGraphics) LineSymbols.THICK_HORIZONTAL else LineSymbols.HORIZONTAL)
        .unfilledSymbol(if (app.enhancedGraphics) LineSymbols.THICK_HORIZONTAL else LineSymbols.HORIZONTAL)
        .ratio(app.progress)
    frame.renderWidget(lineGauge, chunks[2])
}

@Suppress("CyclomaticComplexMethod", "LongMethod")
private fun drawCharts(frame: Frame, app: App, area: Rect) {
    val constraints = if (app.showChart) {
        listOf(Constraint.Percentage(50), Constraint.Percentage(50))
    } else {
        listOf(Constraint.Percentage(100))
    }
    val chunks = Layout.horizontal(constraints).split(area)

    run {
        val chunks = Layout.vertical(listOf(Constraint.Percentage(50), Constraint.Percentage(50))).split(chunks[0])
        run {
            val chunks = Layout.horizontal(listOf(Constraint.Percentage(50), Constraint.Percentage(50))).split(chunks[0])

            // Draw tasks
            val tasks = app.tasks.items.map { i ->
                ListItem.new(listOf(Line.from(Span.raw(i))))
            }
            val tasksList = List.new(tasks)
                .block(Block.bordered().title("List"))
                .highlightStyle(Style.default().addModifier(Modifier.BOLD))
                .highlightSymbol("> ")
            frame.renderStatefulWidget(tasksList, chunks[0], app.tasks.state)

            // Draw logs
            val infoStyle = Style.default().fg(Color.Blue)
            val warningStyle = Style.default().fg(Color.Yellow)
            val errorStyle = Style.default().fg(Color.Magenta)
            val criticalStyle = Style.default().fg(Color.Red)

            val logs = app.logs.items.map { (evt, level) ->
                val style = when (level) {
                    "ERROR" -> errorStyle
                    "CRITICAL" -> criticalStyle
                    "WARNING" -> warningStyle
                    else -> infoStyle
                }
                val content = listOf(
                    Line.from(
                        listOf(
                            Span.styled(level.padEnd(9, ' '), style),
                            Span.raw(evt)
                        )
                    )
                )
                ListItem.new(content)
            }
            val logsList = List.new(logs).block(Block.bordered().title("List"))
            frame.renderStatefulWidget(logsList, chunks[1], app.logs.state)
        }

        val barchart = BarChart.default()
            .block(Block.bordered().title("Bar chart"))
            .data(BarGroup.from(app.barchart))
            .barWidth(3)
            .barGap(2)
            .barSet(if (app.enhancedGraphics) NINE_LEVELS else THREE_LEVELS)
            .valueStyle(
                Style.default()
                    .fg(Color.Black)
                    .bg(Color.Green)
                    .addModifier(Modifier.ITALIC)
            )
            .labelStyle(Style.default().fg(Color.Yellow))
            .barStyle(Style.default().fg(Color.Green))
        frame.renderWidget(barchart, chunks[1])
    }

    if (app.showChart) {
        val xLabels = listOf(
            Line.from(Span.styled(app.signals.window[0].toString(), Style.default().addModifier(Modifier.BOLD))),
            Line.from(((app.signals.window[0] + app.signals.window[1]) / 2.0).toString()),
            Line.from(Span.styled(app.signals.window[1].toString(), Style.default().addModifier(Modifier.BOLD)))
        )

        val datasets = listOf(
            Dataset.default()
                .name("data2")
                .marker(Marker.Dot)
                .style(Style.default().fg(Color.Cyan))
                .data(app.signals.sin1.points),
            Dataset.default()
                .name("data3")
                .marker(if (app.enhancedGraphics) Marker.Braille else Marker.Dot)
                .style(Style.default().fg(Color.Yellow))
                .data(app.signals.sin2.points)
        )

        val chart = Chart.new(datasets)
            .block(
                Block.bordered().title(
                    Line.from(
                        Span.styled(
                            "Chart",
                            Style.default().fg(Color.Cyan).addModifier(Modifier.BOLD)
                        )
                    )
                )
            )
            .xAxis(
                Axis.default()
                    .title("X Axis")
                    .style(Style.default().fg(Color.Gray))
                    .bounds(app.signals.window)
                    .labels(xLabels)
            )
            .yAxis(
                Axis.default()
                    .title("Y Axis")
                    .style(Style.default().fg(Color.Gray))
                    .bounds(-20.0, 20.0)
                    .labels(
                        listOf(
                            Line.from(Span.styled("-20", Style.default().addModifier(Modifier.BOLD))),
                            Line.from("0"),
                            Line.from(Span.styled("20", Style.default().addModifier(Modifier.BOLD)))
                        )
                    )
            )
        frame.renderWidget(chart, chunks[1])
    }
}

private fun drawText(frame: Frame, area: Rect) {
    val text = listOf(
        Line.from(
            "This is a paragraph with several lines. You can change style your text the way you want"
        ),
        Line.from(""),
        Line.from(
            listOf(
                Span.from("For example: "),
                Span.styled("under", Style.default().fg(Color.Red)),
                Span.raw(" "),
                Span.styled("the", Style.default().fg(Color.Green)),
                Span.raw(" "),
                Span.styled("rainbow", Style.default().fg(Color.Blue)),
                Span.raw(".")
            )
        ),
        Line.from(
            listOf(
                Span.raw("Oh and if you didn't "),
                Span.styled("notice", Style.default().addModifier(Modifier.ITALIC)),
                Span.raw(" you can "),
                Span.styled("automatically", Style.default().addModifier(Modifier.BOLD)),
                Span.raw(" "),
                Span.styled("wrap", Style.default().addModifier(Modifier.REVERSED)),
                Span.raw(" your "),
                Span.styled("text", Style.default().addModifier(Modifier.UNDERLINED)),
                Span.raw(".")
            )
        ),
        Line.from("One more thing is that it should display unicode characters: 10€")
    )

    val block = Block.bordered().title(
        Line.from(
            Span.styled(
                "Footer",
                Style.default()
                    .fg(Color.Magenta)
                    .addModifier(Modifier.BOLD)
            )
        )
    )

    val paragraph = Paragraph.new(text).block(block).wrap(Wrap(trim = true))
    frame.renderWidget(paragraph, area)
}

private fun drawSecondTab(frame: Frame, app: App, area: Rect) {
    val chunks = Layout.horizontal(listOf(Constraint.Percentage(30), Constraint.Percentage(70))).split(area)

    val upStyle = Style.default().fg(Color.Green)
    val failureStyle = Style.default()
        .fg(Color.Red)
        .addModifier(Modifier.RAPID_BLINK or Modifier.CROSSED_OUT)

    val rows = app.servers.map { s ->
        val style = if (s.status == "Up") upStyle else failureStyle
        Row.new(s.name, s.location, s.status).style(style)
    }

    val table = Table.new(
        rows,
        listOf(
            Constraint.Length(15),
            Constraint.Length(15),
            Constraint.Length(10)
        )
    ).header(
        Row.new("Server", "Location", "Status")
            .style(Style.default().fg(Color.Yellow))
            .bottomMargin(1)
    ).block(Block.bordered().title("Servers"))

    frame.renderWidget(table, chunks[0])

    val map = Canvas.default()
        .block(Block.bordered().title("World"))
        .paint { ctx ->
            ctx.draw(Map(color = Color.White, resolution = MapResolution.High))
            ctx.layer()
            ctx.draw(
                Rectangle(
                    x = 0.0,
                    y = 30.0,
                    width = 10.0,
                    height = 10.0,
                    color = Color.Yellow
                )
            )
            ctx.draw(
                Circle(
                    x = app.servers[2].coords.second,
                    y = app.servers[2].coords.first,
                    radius = 10.0,
                    color = Color.Green
                )
            )

            for ((i, s1) in app.servers.withIndex()) {
                for (s2 in app.servers.drop(i + 1)) {
                    ctx.draw(
                        CanvasLine(
                            x1 = s1.coords.second,
                            y1 = s1.coords.first,
                            x2 = s2.coords.second,
                            y2 = s2.coords.first,
                            color = Color.Yellow
                        )
                    )
                }
            }

            for (server in app.servers) {
                val color = if (server.status == "Up") Color.Green else Color.Red
                ctx.print(
                    server.coords.second,
                    server.coords.first,
                    Line.from(Span.styled("X", Style.default().fg(color)))
                )
            }
        }
        .marker(if (app.enhancedGraphics) Marker.Braille else Marker.Dot)
        .xBounds(doubleArrayOf(-180.0, 180.0))
        .yBounds(doubleArrayOf(-90.0, 90.0))

    frame.renderWidget(map, chunks[1])
}

private fun drawThirdTab(frame: Frame, app: App, area: Rect) {
    val chunks = Layout.horizontal(listOf(Constraint.Ratio(1u, 2u), Constraint.Ratio(1u, 2u))).split(area)

    val colors = listOf(
        Color.Reset,
        Color.Black,
        Color.Red,
        Color.Green,
        Color.Yellow,
        Color.Blue,
        Color.Magenta,
        Color.Cyan,
        Color.Gray,
        Color.DarkGray,
        Color.LightRed,
        Color.LightGreen,
        Color.LightYellow,
        Color.LightBlue,
        Color.LightMagenta,
        Color.LightCyan,
        Color.White
    )

    val items = colors.map { c ->
        Row.new(
            listOf(
                Cell.new(Span.raw("${c}: ")),
                Cell.new(Span.styled("Foreground", Style.default().fg(c))),
                Cell.new(Span.styled("Background", Style.default().bg(c)))
            )
        )
    }

    val table = Table.new(
        items,
        listOf(
            Constraint.Ratio(1u, 3u),
            Constraint.Ratio(1u, 3u),
            Constraint.Ratio(1u, 3u)
        )
    ).block(Block.bordered().title("Colors"))

    frame.renderWidget(table, chunks[0])
}
