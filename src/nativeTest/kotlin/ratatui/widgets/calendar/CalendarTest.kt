package ratatui.widgets.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.widgets.block.Block
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for the Monthly calendar widget.
 */
class MonthlyTest {

    @Test
    fun monthlyDefault() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
        assertNotNull(calendar)
    }

    @Test
    fun monthlyWithMonthHeader() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .showMonthHeader(Style.default().bold())
        assertNotNull(calendar)
    }

    @Test
    fun monthlyWithWeekdaysHeader() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .showWeekdaysHeader(Style.default())
        assertNotNull(calendar)
    }

    @Test
    fun monthlyWithSurrounding() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .showSurrounding(Style.default().dim())
        assertNotNull(calendar)
    }

    @Test
    fun monthlyWithBlock() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .block(Block.bordered().title("Calendar"))
        assertNotNull(calendar)
    }

    @Test
    fun monthlyWithDefaultStyle() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .defaultStyle(Style.default().fg(Color.White))
        assertNotNull(calendar)
    }

    @Test
    fun monthlyWidth() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
        assertEquals(21, calendar.width())
    }

    @Test
    fun monthlyWidthWithBlock() {
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .block(Block.bordered())
        assertEquals(23, calendar.width())
    }

    @Test
    fun monthlyHeight() {
        // February 2015 starts on Sunday and spans 4 rows
        val date = LocalDate(2015, Month.FEBRUARY, 1)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
        assertEquals(4, calendar.height())
    }

    @Test
    fun monthlyHeightWithHeaders() {
        val date = LocalDate(2015, Month.FEBRUARY, 1)
        val events = CalendarEventStore.default()
        val calendar = Monthly.new(date, events)
            .showMonthHeader(Style.default())
            .showWeekdaysHeader(Style.default())
        assertEquals(6, calendar.height())
    }

    @Test
    fun monthlyRenderInMinimalBuffer() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val date = LocalDate(2024, Month.JANUARY, 15)
        val calendar = Monthly.new(date, CalendarEventStore.default())
        // This should not panic
        calendar.render(buffer.area, buffer)
    }

    @Test
    fun monthlyRenderInZeroSizeBuffer() {
        val buffer = Buffer.empty(Rect.ZERO)
        val date = LocalDate(2024, Month.JANUARY, 15)
        val calendar = Monthly.new(date, CalendarEventStore.default())
        // This should not panic
        calendar.render(buffer.area, buffer)
    }

    @Test
    fun monthlyRenderFull() {
        val buffer = Buffer.empty(Rect.new(0, 0, 25, 10))
        val date = LocalDate(2024, Month.JANUARY, 15)
        val events = CalendarEventStore.default()
        events.add(LocalDate(2024, Month.JANUARY, 15), Style.default().bg(Color.Red))

        val calendar = Monthly.new(date, events)
            .showMonthHeader(Style.default().bold())
            .showWeekdaysHeader(Style.default())
            .showSurrounding(Style.default().dim())
            .block(Block.bordered())

        calendar.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }
}

/**
 * Tests for CalendarEventStore.
 */
class CalendarEventStoreTest {

    @Test
    fun eventStoreDefault() {
        val store = CalendarEventStore.default()
        assertNotNull(store)
    }

    @Test
    fun eventStoreToday() {
        val store = CalendarEventStore.today(Style.default().bold())
        assertNotNull(store)
    }

    @Test
    fun eventStoreAdd() {
        val store = CalendarEventStore.default()
        val date = LocalDate(2024, Month.JANUARY, 15)
        store.add(date, Style.default().fg(Color.Red))

        val style = store.getStyle(date)
        assertEquals(Color.Red, style.fg)
    }

    @Test
    fun eventStoreGetStyleMissing() {
        val store = CalendarEventStore.default()
        val date = LocalDate(2024, Month.JANUARY, 15)
        val style = store.getStyle(date)
        // Should return default style
        assertEquals(Style.default(), style)
    }

    @Test
    fun eventStoreOverwrite() {
        val store = CalendarEventStore.default()
        val date = LocalDate(2024, Month.JANUARY, 15)

        store.add(date, Style.default().fg(Color.Red))
        store.add(date, Style.default().fg(Color.Blue))

        val style = store.getStyle(date)
        // Last write wins
        assertEquals(Color.Blue, style.fg)
    }
}

/**
 * Tests for DateStyler interface.
 */
class DateStylerTest {

    @Test
    fun customDateStyler() {
        // Custom implementation
        val styler = object : DateStyler {
            override fun getStyle(date: LocalDate): Style {
                return if (date.dayOfMonth == 15) {
                    Style.default().fg(Color.Green)
                } else {
                    Style.default()
                }
            }
        }

        val date15 = LocalDate(2024, Month.JANUARY, 15)
        val date16 = LocalDate(2024, Month.JANUARY, 16)

        assertEquals(Color.Green, styler.getStyle(date15).fg)
        assertEquals(null, styler.getStyle(date16).fg)
    }
}
