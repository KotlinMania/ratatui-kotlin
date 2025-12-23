package ratatui.widgets.calendar

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import ratatui.buffer.Buffer
import ratatui.layout.Alignment
import ratatui.layout.Constraint
import ratatui.layout.Layout
import ratatui.layout.Direction
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.text.Line
import ratatui.text.Span
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome

/**
 * Display a month calendar for the month containing [displayDate].
 *
 * The [Monthly] widget will display a calendar for the month provided in [displayDate]. Days
 * are styled using the default style unless:
 * - [showSurrounding] is set, then days not in the [displayDate] month will use that style.
 * - a style is returned by the [DateStyler] for the day
 *
 * [Monthly] has several controls for what should be displayed:
 * - [showSurrounding] - show days from adjacent months
 * - [showWeekdaysHeader] - show a header with weekday abbreviations
 * - [showMonthHeader] - show a header with month name and year
 *
 * # Example
 *
 * ```kotlin
 * val date = LocalDate(2024, 1, 15)
 * val events = CalendarEventStore.today(Style.default().bold())
 * val calendar = Monthly(date, events)
 *     .showMonthHeader(Style.default().bold())
 *     .showWeekdaysHeader(Style.default())
 *     .showSurrounding(Style.default().dim())
 *     .block(Block.bordered().title("Calendar"))
 * ```
 */
data class Monthly<DS : DateStyler>(
    /** The date to display (any day in the target month) */
    val displayDate: LocalDate,
    /** The event styler */
    val events: DS,
    /** Style for days not in the current month (if shown) */
    private val showSurroundingStyle: Style? = null,
    /** Style for the weekday header row */
    private val showWeekdayStyle: Style? = null,
    /** Style for the month/year header */
    private val showMonthStyle: Style? = null,
    /** Default style for calendar days */
    private val defaultStyle: Style = Style.default(),
    /** Optional block to wrap the calendar */
    private val block: Block? = null
) : Widget {

    /**
     * Fill the calendar slots for days not in the current month also, this causes each line to be
     * completely filled. If there is an event style for a date, this style will be patched with
     * the event's style.
     *
     * @param style The style for surrounding days
     * @return A new Monthly with surrounding days shown
     */
    fun showSurrounding(style: Style): Monthly<DS> = copy(showSurroundingStyle = style)

    /**
     * Display a header containing weekday abbreviations.
     *
     * @param style The style for the weekday header
     * @return A new Monthly with weekday header shown
     */
    fun showWeekdaysHeader(style: Style): Monthly<DS> = copy(showWeekdayStyle = style)

    /**
     * Display a header containing the month and year.
     *
     * @param style The style for the month header
     * @return A new Monthly with month header shown
     */
    fun showMonthHeader(style: Style): Monthly<DS> = copy(showMonthStyle = style)

    /**
     * How to render otherwise unstyled dates.
     *
     * @param style The default style
     * @return A new Monthly with the default style set
     */
    fun defaultStyle(style: Style): Monthly<DS> = copy(defaultStyle = style)

    /**
     * Render the calendar within a [Block].
     *
     * @param block The block to wrap the calendar in
     * @return A new Monthly wrapped in the block
     */
    fun block(block: Block): Monthly<DS> = copy(block = block)

    /**
     * Return the width required to render the calendar.
     */
    fun width(): Int {
        val daysPerWeek = 7
        val gutterWidth = 1
        val dayWidth = 2

        var width = daysPerWeek * (gutterWidth + dayWidth)
        block?.let { b ->
            val (left, right) = b.horizontalSpace()
            width += left + right
        }
        return width
    }

    /**
     * Return the height required to render the calendar.
     */
    fun height(): Int {
        var height = sundayBasedWeeks(displayDate) +
            (if (showMonthStyle != null) 1 else 0) +
            (if (showWeekdayStyle != null) 1 else 0)

        block?.let { b ->
            val (top, bottom) = b.verticalSpace()
            height += top + bottom
        }
        return height
    }

    /**
     * Return a style with only the background from the default style.
     */
    private fun defaultBg(): Style {
        return defaultStyle.bg?.let { Style.default().bg(it) } ?: Style.default()
    }

    /**
     * All logic to style a date goes here.
     */
    private fun formatDate(date: LocalDate): Span {
        return if (date.month == displayDate.month) {
            Span.styled(
                date.dayOfMonth.toString().padStart(2),
                defaultStyle.patch(events.getStyle(date))
            )
        } else {
            when (showSurroundingStyle) {
                null -> Span.styled("  ", defaultBg())
                else -> {
                    val style = defaultStyle
                        .patch(showSurroundingStyle)
                        .patch(events.getStyle(date))
                    Span.styled(date.dayOfMonth.toString().padStart(2), style)
                }
            }
        }
    }

    override fun render(area: Rect, buf: Buffer) {
        block?.render(area, buf)
        val inner = block.innerIfSome(area)
        renderMonthly(inner, buf)
    }

    private fun renderMonthly(area: Rect, buf: Buffer) {
        val layout = Layout.default()
            .direction(Direction.Vertical)
            .constraints(listOf(
                Constraint.Length(if (showMonthStyle != null) 1 else 0),
                Constraint.Length(if (showWeekdayStyle != null) 1 else 0),
                Constraint.Fill(1)
            ))
        val areas = layout.split(area)
        val monthHeader = areas[0]
        val daysHeader = areas[1]
        val daysArea = areas[2]

        // Draw the month name and year
        showMonthStyle?.let { style ->
            Line.styled(
                "${displayDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${displayDate.year}",
                style
            ).centered().render(monthHeader, buf)
        }

        // Draw days of week
        showWeekdayStyle?.let { style ->
            Span.styled(" Su Mo Tu We Th Fr Sa", style).render(daysHeader, buf)
        }

        // Set the start of the calendar to the Sunday before the 1st (or the sunday of the first)
        val firstOfMonth = LocalDate(displayDate.year, displayDate.month, 1)
        val offset = numberDaysFromSunday(firstOfMonth.dayOfWeek)
        var currDay = firstOfMonth.minus(DatePeriod(days = offset))

        var y = daysArea.y
        // go through all the weeks containing a day in the target month.
        val nextMonth = displayDate.month.nextMonth()
        while (currDay.month != nextMonth || (currDay.month == nextMonth && currDay.dayOfMonth == 1 && numberDaysFromSunday(currDay.dayOfWeek) != 0)) {
            // Safety check: stop if we've gone too far past the target month
            if (currDay.month == nextMonth && currDay.dayOfMonth > 7) break

            val spans = mutableListOf<Span>()
            for (i in 0 until 7) {
                // Draw the gutter. Do it here so we can avoid worrying about
                // styling the ' ' in the format_date method
                if (i == 0) {
                    spans.add(Span.styled(" ", Style.default()))
                } else {
                    spans.add(Span.styled(" ", defaultBg()))
                }
                spans.add(formatDate(currDay))
                currDay = currDay.plus(DatePeriod(days = 1))
            }

            if (buf.area.height > y) {
                buf.setLine(daysArea.x, y, Line.from(spans), area.width)
            }
            y += 1
        }
    }

    companion object {
        /**
         * Construct a calendar for the [displayDate] and highlight the [events].
         *
         * @param displayDate Any date in the month to display
         * @param events The event styler
         * @return A new Monthly calendar widget
         */
        fun <DS : DateStyler> new(displayDate: LocalDate, events: DS): Monthly<DS> =
            Monthly(displayDate, events)
    }
}

/**
 * Get the number of days from Sunday (0 = Sunday, 1 = Monday, ..., 6 = Saturday)
 */
private fun numberDaysFromSunday(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        else -> 0
    }
}

/**
 * Get the next month
 */
private fun Month.nextMonth(): Month {
    return Month.entries[(this.ordinal + 1) % 12]
}

/**
 * Get the number of days in a month
 */
private fun Month.length(year: Int): Int {
    return when (this) {
        Month.JANUARY -> 31
        Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
        Month.MARCH -> 31
        Month.APRIL -> 30
        Month.MAY -> 31
        Month.JUNE -> 30
        Month.JULY -> 31
        Month.AUGUST -> 31
        Month.SEPTEMBER -> 30
        Month.OCTOBER -> 31
        Month.NOVEMBER -> 30
        Month.DECEMBER -> 31
        else -> 30
    }
}

/**
 * Check if a year is a leap year
 */
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

/**
 * Compute how many Sunday-based week rows are needed to render [displayDate].
 *
 * Mirrors the rendering logic by taking the difference between the first and last day
 * Sunday-based week numbers (inclusive).
 */
private fun sundayBasedWeeks(displayDate: LocalDate): Int {
    val firstOfMonth = LocalDate(displayDate.year, displayDate.month, 1)
    val lastOfMonth = LocalDate(displayDate.year, displayDate.month, displayDate.month.length(displayDate.year))

    val firstWeek = sundayBasedWeek(firstOfMonth)
    val lastWeek = sundayBasedWeek(lastOfMonth)

    return (lastWeek - firstWeek).coerceAtLeast(0) + 1
}

/**
 * Get the Sunday-based week number of the year for a date.
 */
private fun sundayBasedWeek(date: LocalDate): Int {
    val dayOfYear = date.dayOfYear
    val dayOfWeek = numberDaysFromSunday(date.dayOfWeek)

    // Week number is (day of year + days to previous sunday) / 7
    val startOfYear = LocalDate(date.year, Month.JANUARY, 1)
    val firstSundayOffset = (7 - numberDaysFromSunday(startOfYear.dayOfWeek)) % 7

    return if (dayOfYear <= firstSundayOffset) {
        0
    } else {
        (dayOfYear - firstSundayOffset - 1) / 7 + 1
    }
}
