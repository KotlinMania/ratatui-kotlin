package ratatui.widgets.calendar

import kotlinx.datetime.LocalDate
import ratatui.style.Style

/**
 * Provides a method for styling a given date. [Monthly] is generic on this trait, so any type
 * that implements this interface can be used.
 */
interface DateStyler {
    /**
     * Given a date, return a style for that date
     */
    fun getStyle(date: LocalDate): Style
}
