package ratatui.widgets.scrollbar

/**
 * An enum representing a scrolling direction.
 *
 * This is used with [ScrollbarState.scroll].
 *
 * It is useful for example when you want to store in which direction to scroll.
 */
enum class ScrollDirection {
    /** Forward scroll direction, usually corresponds to scrolling downwards or rightwards. */
    Forward,

    /** Backward scroll direction, usually corresponds to scrolling upwards or leftwards. */
    Backward;

    companion object {
        /** The default scroll direction (Forward) */
        fun default(): ScrollDirection = Forward
    }
}
