package ratatui.widgets.barchart

import ratatui.buffer.Buffer
import ratatui.layout.Alignment
import ratatui.layout.Direction
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.symbols.bar.NINE_LEVELS
import ratatui.symbols.bar.THREE_LEVELS
import ratatui.text.Line
import ratatui.widgets.BorderType
import ratatui.widgets.block.Block
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for the BarChart widget.
 *
 * These tests are transliterated from the Rust ratatui-widgets tests.
 */
class BarChartTest {

    @Test
    fun defaultBarChart() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val widget = BarChart.default()
        widget.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("          ", "          ", "          "), buffer)
    }

    @Test
    fun dataBarChart() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val widget = BarChart.default()
            .data(BarGroup.from("foo" to 1L, "bar" to 2L))
        widget.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "  █       ",
            "1 2       ",
            "f b       "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun blockBarChart() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 5))
        val block = Block.bordered()
            .borderType(BorderType.Double)
            .title("Block")
        val widget = BarChart.default()
            .data(BarGroup.from("foo" to 1L, "bar" to 2L))
            .block(block)
        widget.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "╔Block═══╗",
            "║  █     ║",
            "║1 2     ║",
            "║f b     ║",
            "╚════════╝"
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun maxBarChart() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val withoutMax = BarChart.default()
            .data(BarGroup.from("foo" to 1L, "bar" to 2L, "baz" to 100L))
        withoutMax.render(buffer.area, buffer)
        val expectedWithoutMax = Buffer.withLines(
            "    █     ",
            "    █     ",
            "f b b     "
        )
        assertEquals(expectedWithoutMax, buffer)

        val buffer2 = Buffer.empty(Rect.new(0, 0, 10, 3))
        val withMax = BarChart.default()
            .data(BarGroup.from("foo" to 1L, "bar" to 2L, "baz" to 100L))
            .max(2)
        withMax.render(buffer2.area, buffer2)
        val expectedWithMax = Buffer.withLines(
            "  █ █     ",
            "1 2 █     ",
            "f b b     "
        )
        assertEquals(expectedWithMax, buffer2)
    }

    @Test
    fun barWidth() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val widget = BarChart.default()
            .data(BarGroup.from("foo" to 1L, "bar" to 2L))
            .barWidth(3)
        widget.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "    ███   ",
            "█1█ █2█   ",
            "foo bar   "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun barGap() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val widget = BarChart.default()
            .data(BarGroup.from("foo" to 1L, "bar" to 2L))
            .barGap(2)
        widget.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "   █      ",
            "1  2      ",
            "f  b      "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun barSetThreeLevels() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val widget = BarChart.default()
            .data(BarGroup.from("foo" to 0L, "bar" to 1L, "baz" to 3L))
            .barSet(THREE_LEVELS)
        widget.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "    █     ",
            "  ▄ 3     ",
            "f b b     "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun barSetNineLevels() {
        val buffer = Buffer.empty(Rect.new(0, 0, 18, 3))
        val widget = BarChart.default()
            .data(BarGroup.from(
                "a" to 0L,
                "b" to 1L,
                "c" to 2L,
                "d" to 3L,
                "e" to 4L,
                "f" to 5L,
                "g" to 6L,
                "h" to 7L,
                "i" to 8L
            ))
            .barSet(NINE_LEVELS)
        widget.render(Rect.new(0, 1, 18, 2), buffer)
        val expected = Buffer.withLines(
            "                  ",
            "  ▁ ▂ ▃ ▄ ▅ ▆ ▇ 8 ",
            "a b c d e f g h i "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun testEmptyGroup() {
        val chart = BarChart.default()
            .data(BarGroup.default().label("invisible"))
            .data(
                BarGroup.default()
                    .label("G")
                    .bars(Bar.default().value(1), Bar.default().value(2))
            )

        val buffer = Buffer.empty(Rect.new(0, 0, 3, 3))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "  █",
            "1 2",
            "G  "
        )
        assertEquals(expected, buffer)
    }

    private fun buildTestBarchart(): BarChart {
        return BarChart.default()
            .data(BarGroup.default().label("G1").bars(
                Bar.default().value(2),
                Bar.default().value(3),
                Bar.default().value(4)
            ))
            .data(BarGroup.default().label("G2").bars(
                Bar.default().value(3),
                Bar.default().value(4),
                Bar.default().value(5)
            ))
            .groupGap(1)
            .direction(Direction.Horizontal)
            .barGap(0)
    }

    @Test
    fun testHorizontalBars() {
        val chart = buildTestBarchart()

        val buffer = Buffer.empty(Rect.new(0, 0, 5, 8))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "2█   ",
            "3██  ",
            "4███ ",
            "G1   ",
            "3██  ",
            "4███ ",
            "5████",
            "G2   "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun testHorizontalBarsNoSpaceForGroupLabel() {
        val chart = buildTestBarchart()

        val buffer = Buffer.empty(Rect.new(0, 0, 5, 7))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "2█   ",
            "3██  ",
            "4███ ",
            "G1   ",
            "3██  ",
            "4███ ",
            "5████"
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun testHorizontalBarsNoSpaceForAllBars() {
        val chart = buildTestBarchart()

        val buffer = Buffer.empty(Rect.new(0, 0, 5, 5))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "2█   ",
            "3██  ",
            "4███ ",
            "G1   ",
            "3██  "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun testHorizontalLabel() {
        val chart = BarChart.default()
            .direction(Direction.Horizontal)
            .barGap(0)
            .data(BarGroup.from("Jan" to 10L, "Feb" to 20L, "Mar" to 5L))

        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "Jan 10█   ",
            "Feb 20████",
            "Mar 5     "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun testGroupLabelCenter() {
        // test the centered group position when one bar is outside the group
        val group = BarGroup.from("a" to 1L, "b" to 2L, "c" to 3L, "c" to 4L)
        val chart = BarChart.default()
            .data(group.label(Line.from("G1").alignment(Alignment.Center)))
            .data(BarGroup.from("a" to 1L, "b" to 2L, "c" to 3L, "c" to 4L)
                .label(Line.from("G2").alignment(Alignment.Center)))

        val buffer = Buffer.empty(Rect.new(0, 0, 13, 5))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "    ▂ █     ▂",
            "  ▄ █ █   ▄ █",
            "▆ 2 3 4 ▆ 2 3",
            "a b c c a b c",
            "  G1     G2  "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun testGroupLabelRight() {
        val chart = BarChart.default().data(
            BarGroup.default()
                .label(Line.from("G").alignment(Alignment.Right))
                .bars(Bar.default().value(2), Bar.default().value(5))
        )

        val buffer = Buffer.empty(Rect.new(0, 0, 3, 3))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "  █",
            "▆ 5",
            "  G"
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun handlesZeroWidth() {
        // this test is to ensure that a BarChart with zero bar / gap width does not panic
        val chart = BarChart.default()
            .data(BarGroup.from("A" to 1L))
            .barWidth(0)
            .barGap(0)
        val buffer = Buffer.empty(Rect.new(0, 0, 0, 10))
        chart.render(buffer.area, buffer)
        assertEquals(Buffer.empty(Rect.new(0, 0, 0, 10)), buffer)
    }

    @Test
    fun singleLine() {
        var group = BarGroup.from(
            "a" to 0L,
            "b" to 1L,
            "c" to 2L,
            "d" to 3L,
            "e" to 4L,
            "f" to 5L,
            "g" to 6L,
            "h" to 7L,
            "i" to 8L
        )
        group = group.label("Group")

        val chart = BarChart.default()
            .data(group)
            .barSet(NINE_LEVELS)

        val buffer = Buffer.empty(Rect.new(0, 0, 17, 1))
        chart.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("  ▁ ▂ ▃ ▄ ▅ ▆ ▇ 8"), buffer)
    }

    @Test
    fun twoLines() {
        var group = BarGroup.from(
            "a" to 0L,
            "b" to 1L,
            "c" to 2L,
            "d" to 3L,
            "e" to 4L,
            "f" to 5L,
            "g" to 6L,
            "h" to 7L,
            "i" to 8L
        )
        group = group.label("Group")

        val chart = BarChart.default()
            .data(group)
            .barSet(NINE_LEVELS)

        val buffer = Buffer.empty(Rect.new(0, 0, 17, 3))
        chart.render(Rect.new(0, 1, buffer.area.width, 2), buffer)
        val expected = Buffer.withLines(
            "                 ",
            "  ▁ ▂ ▃ ▄ ▅ ▆ ▇ 8",
            "a b c d e f g h i"
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun threeLines() {
        var group = BarGroup.from(
            "a" to 0L,
            "b" to 1L,
            "c" to 2L,
            "d" to 3L,
            "e" to 4L,
            "f" to 5L,
            "g" to 6L,
            "h" to 7L,
            "i" to 8L
        )
        group = group.label(Line.from("Group").alignment(Alignment.Center))

        val chart = BarChart.default()
            .data(group)
            .barSet(NINE_LEVELS)

        val buffer = Buffer.empty(Rect.new(0, 0, 17, 3))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "  ▁ ▂ ▃ ▄ ▅ ▆ ▇ 8",
            "a b c d e f g h i",
            "      Group      "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun fourLines() {
        var group = BarGroup.from(
            "a" to 0L,
            "b" to 1L,
            "c" to 2L,
            "d" to 3L,
            "e" to 4L,
            "f" to 5L,
            "g" to 6L,
            "h" to 7L,
            "i" to 8L
        )
        group = group.label(Line.from("Group").alignment(Alignment.Center))

        val chart = BarChart.default()
            .data(group)
            .barSet(NINE_LEVELS)

        val buffer = Buffer.empty(Rect.new(0, 0, 17, 4))
        chart.render(buffer.area, buffer)
        val expected = Buffer.withLines(
            "          ▂ ▄ ▆ █",
            "  ▂ ▄ ▆ 4 5 6 7 8",
            "a b c d e f g h i",
            "      Group      "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun twoLinesWithoutBarLabels() {
        val group = BarGroup.default()
            .label(Line.from("Group").alignment(Alignment.Center))
            .bars(
                Bar.default().value(0),
                Bar.default().value(1),
                Bar.default().value(2),
                Bar.default().value(3),
                Bar.default().value(4),
                Bar.default().value(5),
                Bar.default().value(6),
                Bar.default().value(7),
                Bar.default().value(8)
            )

        val chart = BarChart.default().data(group)

        val buffer = Buffer.empty(Rect.new(0, 0, 17, 3))
        chart.render(Rect.new(0, 1, buffer.area.width, 2), buffer)
        val expected = Buffer.withLines(
            "                 ",
            "  ▁ ▂ ▃ ▄ ▅ ▆ ▇ 8",
            "      Group      "
        )
        assertEquals(expected, buffer)
    }

    @Test
    fun renderInMinimalBuffer() {
        val chart = BarChart.default()
            .data(BarGroup.from("A" to 1L, "B" to 2L))
            .barWidth(3)
            .barGap(1)
            .direction(Direction.Vertical)

        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        // This should not panic, even if the buffer is too small to render the chart.
        chart.render(buffer.area, buffer)
        assertEquals(Buffer.withLines(" "), buffer)
    }

    @Test
    fun renderInZeroSizeBuffer() {
        val chart = BarChart.default()
            .data(BarGroup.from("A" to 1L, "B" to 2L))
            .barWidth(3)
            .barGap(1)
            .direction(Direction.Vertical)

        val buffer = Buffer.empty(Rect.ZERO)
        // This should not panic, even if the buffer has zero size.
        chart.render(buffer.area, buffer)
    }
}
