// port-lint: source ratatui/tests/widgetsCalendar.rs
package ratatui.widgets.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ratatui.backend.TestBackend
import ratatui.buffer.Buffer
import ratatui.style.Style
import ratatui.terminal.Terminal
import ratatui.widgets.Widget
import kotlin.test.Test

private fun <W : Widget> testRender(widget: W, width: Int, height: Int, expected: Buffer) {
    val backend = TestBackend.new(width, height)
    val terminal = Terminal(backend)
    terminal.draw { f ->
        f.renderWidget(widget, f.area())
    }
    terminal.backend().assertBuffer(expected)
}

class MonthlyTest {
    @Test
    fun daysLayout() {
        val c = Monthly.new(
            LocalDate(2023, Month.JANUARY, 1),
            CalendarEventStore.default()
        )
        val expected = Buffer.withLines(
            "  1  2  3  4  5  6  7",
            "  8  9 10 11 12 13 14",
            " 15 16 17 18 19 20 21",
            " 22 23 24 25 26 27 28",
            " 29 30 31",
        )
        testRender(c, 21, 5, expected)
    }

    @Test
    fun daysLayoutShowSurrounding() {
        val c = Monthly.new(
            LocalDate(2023, Month.DECEMBER, 1),
            CalendarEventStore.default()
        ).showSurrounding(Style.default())
        val expected = Buffer.withLines(
            " 26 27 28 29 30  1  2",
            "  3  4  5  6  7  8  9",
            " 10 11 12 13 14 15 16",
            " 17 18 19 20 21 22 23",
            " 24 25 26 27 28 29 30",
            " 31  1  2  3  4  5  6",
        )
        testRender(c, 21, 6, expected)
    }

    @Test
    fun showMonthHeader() {
        val c = Monthly.new(
            LocalDate(2023, Month.JANUARY, 1),
            CalendarEventStore.default()
        ).showMonthHeader(Style.default())
        val expected = Buffer.withLines(
            "    January 2023     ",
            "  1  2  3  4  5  6  7",
            "  8  9 10 11 12 13 14",
            " 15 16 17 18 19 20 21",
            " 22 23 24 25 26 27 28",
            " 29 30 31",
        )
        testRender(c, 21, 6, expected)
    }

    @Test
    fun showWeekdaysHeader() {
        val c = Monthly.new(
            LocalDate(2023, Month.JANUARY, 1),
            CalendarEventStore.default()
        ).showWeekdaysHeader(Style.default())
        val expected = Buffer.withLines(
            " Su Mo Tu We Th Fr Sa",
            "  1  2  3  4  5  6  7",
            "  8  9 10 11 12 13 14",
            " 15 16 17 18 19 20 21",
            " 22 23 24 25 26 27 28",
            " 29 30 31",
        )
        testRender(c, 21, 6, expected)
    }

    @Test
    fun showCombo() {
        val c = Monthly.new(
            LocalDate(2023, Month.JANUARY, 1),
            CalendarEventStore.default()
        )
            .showWeekdaysHeader(Style.default())
            .showMonthHeader(Style.default())
            .showSurrounding(Style.default())
        val expected = Buffer.withLines(
            "    January 2023     ",
            " Su Mo Tu We Th Fr Sa",
            "  1  2  3  4  5  6  7",
            "  8  9 10 11 12 13 14",
            " 15 16 17 18 19 20 21",
            " 22 23 24 25 26 27 28",
            " 29 30 31  1  2  3  4",
        )
        testRender(c, 21, 7, expected)
    }
}

