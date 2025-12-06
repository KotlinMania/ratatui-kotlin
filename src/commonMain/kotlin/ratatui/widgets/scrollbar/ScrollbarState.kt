package ratatui.widgets.scrollbar

/**
 * A struct representing the state of a Scrollbar widget.
 *
 * # Important
 *
 * It's essential to set the `contentLength` field when using this struct. This field
 * represents the total length of the scrollable content. The default value is zero
 * which will result in the Scrollbar not rendering.
 *
 * For example, in the following list, assume there are 4 bullet points:
 *
 * - the `contentLength` is 4
 * - the `position` is 0
 * - the `viewportContentLength` is 2
 *
 * ```
 * +---------------+
 * |1. this is a   #
 * |   single item #
 * |2. this is a   |
 * |   second item |
 * +---------------+
 * ```
 *
 * If you don't have multi-line content, you can leave the `viewportContentLength` set to the
 * default and it'll use the track size as a `viewportContentLength`.
 *
 * # Example
 *
 * ```kotlin
 * val state = ScrollbarState.new(100)
 *     .position(50)
 *     .viewportContentLength(10)
 * ```
 */
data class ScrollbarState(
    /** The total length of the scrollable content. */
    var contentLength: Int = 0,
    /** The current position within the scrollable content. */
    var position: Int = 0,
    /** The length of content in current viewport. */
    var viewportContentLength: Int = 0
) {
    /**
     * Sets the scroll position of the scrollbar.
     *
     * This represents the number of scrolled items.
     *
     * This is a fluent setter method which must be chained or used as it consumes self.
     */
    fun position(position: Int): ScrollbarState {
        this.position = position
        return this
    }

    /**
     * Sets the length of the scrollable content.
     *
     * This is the number of scrollable items. If items have a length of one, then this is the
     * same as the number of scrollable cells.
     *
     * This is a fluent setter method which must be chained or used as it consumes self.
     */
    fun contentLength(contentLength: Int): ScrollbarState {
        this.contentLength = contentLength
        return this
    }

    /**
     * Sets the items' size.
     *
     * This is a fluent setter method which must be chained or used as it consumes self.
     */
    fun viewportContentLength(viewportContentLength: Int): ScrollbarState {
        this.viewportContentLength = viewportContentLength
        return this
    }

    /**
     * Decrements the scroll position by one, ensuring it doesn't go below zero.
     */
    fun prev() {
        position = (position - 1).coerceAtLeast(0)
    }

    /**
     * Increments the scroll position by one, ensuring it doesn't exceed the length of the content.
     */
    fun next() {
        position = (position + 1).coerceAtMost((contentLength - 1).coerceAtLeast(0))
    }

    /**
     * Sets the scroll position to the start of the scrollable content.
     */
    fun first() {
        position = 0
    }

    /**
     * Sets the scroll position to the end of the scrollable content.
     */
    fun last() {
        position = (contentLength - 1).coerceAtLeast(0)
    }

    /**
     * Changes the scroll position based on the provided [ScrollDirection].
     */
    fun scroll(direction: ScrollDirection) {
        when (direction) {
            ScrollDirection.Forward -> next()
            ScrollDirection.Backward -> prev()
        }
    }

    /**
     * Returns the current position within the scrollable content.
     */
    fun getPosition(): Int = position

    companion object {
        /**
         * Constructs a new [ScrollbarState] with the specified content length.
         *
         * `contentLength` is the total number of elements that can be scrolled.
         */
        fun new(contentLength: Int): ScrollbarState = ScrollbarState(contentLength = contentLength)

        /**
         * Creates an empty default ScrollbarState.
         */
        fun default(): ScrollbarState = ScrollbarState()
    }
}
