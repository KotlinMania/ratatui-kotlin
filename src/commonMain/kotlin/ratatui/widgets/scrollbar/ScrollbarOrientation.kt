package ratatui.widgets.scrollbar

/**
 * The position of the scrollbar around a given area.
 *
 * ```
 *           HorizontalTop
 *             +-------+
 * VerticalLeft|       |VerticalRight
 *             +-------+
 *          HorizontalBottom
 * ```
 */
enum class ScrollbarOrientation {
    /** Positions the scrollbar on the right, scrolling vertically */
    VerticalRight,

    /** Positions the scrollbar on the left, scrolling vertically */
    VerticalLeft,

    /** Positions the scrollbar on the bottom, scrolling horizontally */
    HorizontalBottom,

    /** Positions the scrollbar on the top, scrolling horizontally */
    HorizontalTop;

    /** Returns `true` if the scrollbar is vertical. */
    fun isVertical(): Boolean = this == VerticalRight || this == VerticalLeft

    /** Returns `true` if the scrollbar is horizontal. */
    fun isHorizontal(): Boolean = this == HorizontalBottom || this == HorizontalTop

    companion object {
        /** The default orientation (VerticalRight) */
        fun default(): ScrollbarOrientation = VerticalRight
    }
}
