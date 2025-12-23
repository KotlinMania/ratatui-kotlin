package ratatui.widgets.calendar

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import ratatui.style.Style

/**
 * A simple [DateStyler] based on a [Map].
 */
class CalendarEventStore(
    internal val events: MutableMap<LocalDate, Style> = mutableMapOf()
) : DateStyler {

    /**
     * Add a date and style to the store.
     *
     * @param date The date to style
     * @param style The style to apply to that date
     */
    fun add(date: LocalDate, style: Style) {
        events[date] = style
    }

    override fun getStyle(date: LocalDate): Style =
        events[date] ?: Style.default()

    companion object {
        /**
         * Construct a store that has the current date styled.
         *
         * @param style The style to apply to today's date
         * @return A new CalendarEventStore with today styled
         */
        fun today(style: Style): CalendarEventStore {
            val store = CalendarEventStore()
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            store.add(today, style)
            return store
        }

        /**
         * Create an empty event store.
         *
         * @return A new empty CalendarEventStore
         */
        fun default(): CalendarEventStore = CalendarEventStore()
    }
}
