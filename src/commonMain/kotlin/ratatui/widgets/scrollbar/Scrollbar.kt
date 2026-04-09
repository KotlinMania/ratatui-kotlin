// port-lint: source ratatui-widgets/src/scrollbar.rs
package ratatui.widgets.scrollbar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.symbols.scrollbar.DOUBLE_HORIZONTAL
import ratatui.symbols.scrollbar.DOUBLE_VERTICAL
import ratatui.symbols.scrollbar.Set as ScrollbarSet
import ratatui.widgets.StatefulWidget
import kotlin.math.roundToInt

/**
 * A struct representing the state of a Scrollbar widget.
 *
 * # Important
 *
 * It's essential to set the [contentLength] field when using this struct. This field
 * represents the total length of the scrollable content. The default value is zero
 * which will result in the Scrollbar not rendering.
 *
 * For example, in the following list, assume there are 4 bullet points:
 *
 * - the `contentLength` is 4
 * - the `position` is 0
 * - the `viewportContentLength` is 2
 *
 * ```text
 * ┌───────────────┐
 * │1. this is a   █
 * │   single item █
 * │2. this is a   ║
 * │   second item ║
 * └───────────────┘
 * ```
 *
 * If you don't have multi-line content, you can leave the `viewportContentLength` set to the
 * default and it'll use the track size as a `viewportContentLength`.
 */
@Serializable
data class ScrollbarState(
    /** The total length of the scrollable content. */
    @SerialName("content_length")
    var contentLength: Int = 0,
    /** The current position within the scrollable content. */
    var position: Int = 0,
    /**
     * The length of content in current viewport.
     *
     * Note: in the Rust implementation this is documented as `FIXME: this should be Option<usize>`,
     * but it will break serialization to change it.
     */
    @SerialName("viewport_content_length")
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

    /** Decrements the scroll position by one, ensuring it doesn't go below zero. */
    fun prev() {
        position = (position - 1).coerceAtLeast(0)
    }

    /** Increments the scroll position by one, ensuring it doesn't exceed the length of the content. */
    fun next() {
        position = (position + 1).coerceAtMost((contentLength - 1).coerceAtLeast(0))
    }

    /** Sets the scroll position to the start of the scrollable content. */
    fun first() {
        position = 0
    }

    /** Sets the scroll position to the end of the scrollable content. */
    fun last() {
        position = (contentLength - 1).coerceAtLeast(0)
    }

    /** Changes the scroll position based on the provided [ScrollDirection]. */
    fun scroll(direction: ScrollDirection) {
        when (direction) {
            ScrollDirection.Forward -> next()
            ScrollDirection.Backward -> prev()
        }
    }

    /** Returns the current position within the scrollable content. */
    fun position(): Int = position

    companion object {
        /**
         * Constructs a new [ScrollbarState] with the specified content length.
         *
         * `contentLength` is the total number of elements that can be scrolled.
         */
        fun new(contentLength: Int): ScrollbarState = ScrollbarState(contentLength = contentLength)

        /** Creates an empty default ScrollbarState. */
        fun default(): ScrollbarState = ScrollbarState()
    }
}

/**
 * An enum representing a scrolling direction.
 *
 * This is used with [ScrollbarState.scroll].
 *
 * It is useful for example when you want to store in which direction to scroll.
 */
@Serializable
enum class ScrollDirection {
    /** Forward scroll direction, usually corresponds to scrolling downwards or rightwards. */
    Forward,

    /** Backward scroll direction, usually corresponds to scrolling upwards or leftwards. */
    Backward;

    companion object {
        /** The default scroll direction (Forward). */
        fun default(): ScrollDirection = Forward

        /** Mirrors Rust `EnumString` derive on the enum. */
        fun fromString(value: String): ScrollDirection? {
            return when (value) {
                "Forward" -> Forward
                "Backward" -> Backward
                else -> null
            }
        }
    }
}

/**
 * The position of the scrollbar around a given area.
 *
 * ```text
 *           HorizontalTop
 *             +-------+
 * VerticalLeft|       |VerticalRight
 *             +-------+
 *          HorizontalBottom
 * ```
 */
@Serializable
enum class ScrollbarOrientation {
    /** Positions the scrollbar on the right, scrolling vertically. */
    VerticalRight,

    /** Positions the scrollbar on the left, scrolling vertically. */
    VerticalLeft,

    /** Positions the scrollbar on the bottom, scrolling horizontally. */
    HorizontalBottom,

    /** Positions the scrollbar on the top, scrolling horizontally. */
    HorizontalTop;

    /** Returns true if the scrollbar is vertical. */
    fun isVertical(): Boolean = this == VerticalRight || this == VerticalLeft

    /** Returns true if the scrollbar is horizontal. */
    fun isHorizontal(): Boolean = this == HorizontalBottom || this == HorizontalTop

    companion object {
        /** The default scrollbar orientation (VerticalRight). */
        fun default(): ScrollbarOrientation = VerticalRight
    }
}

/**
 * A widget to display a scrollbar.
 *
 * The following components of the scrollbar are customizable in symbol and style. Note the
 * scrollbar is represented horizontally but it can also be set vertically (which is actually the
 * default).
 *
 * ```
 * <--#------->
 * ^  ^   ^   ^
 * |  |   |   +- end
 * |  |   +---- track
 * |  +-------- thumb
 * +----------- begin
 * ```
 *
 * # Important
 *
 * You must specify the [ScrollbarState.contentLength] before rendering the `Scrollbar`, or
 * else the `Scrollbar` will render blank.
 *
 * # Example
 *
 * ```kotlin
 * val scrollbar = Scrollbar.new(ScrollbarOrientation.VerticalRight)
 *     .beginSymbol("^")
 *     .endSymbol("v")
 *
 * val state = ScrollbarState.new(items.size).position(verticalScroll)
 *
 * scrollbar.render(area, buffer, state)
 * ```
 */
data class Scrollbar(
    private val orientation: ScrollbarOrientation = ScrollbarOrientation.VerticalRight,
    private val thumbStyle: Style = Style.default(),
    private val thumbSymbol: String = DOUBLE_VERTICAL.thumb,
    private val trackStyle: Style = Style.default(),
    private val trackSymbol: String? = DOUBLE_VERTICAL.track,
    private val beginSymbol: String? = DOUBLE_VERTICAL.begin,
    private val beginStyle: Style = Style.default(),
    private val endSymbol: String? = DOUBLE_VERTICAL.end,
    private val endStyle: Style = Style.default()
) : StatefulWidget<ScrollbarState> {

    /**
     * Sets the position of the scrollbar.
     *
     * The orientation of the scrollbar is the position it will take around a [Rect]. See
     * [ScrollbarOrientation] for more details.
     *
     * Resets the symbols to [DOUBLE_VERTICAL] or [DOUBLE_HORIZONTAL] based on orientation.
     */
    fun orientation(orientation: ScrollbarOrientation): Scrollbar {
        val symbols = if (orientation.isVertical()) DOUBLE_VERTICAL else DOUBLE_HORIZONTAL
        return copy(
            orientation = orientation,
            thumbSymbol = symbols.thumb,
            trackSymbol = symbols.track,
            beginSymbol = symbols.begin,
            endSymbol = symbols.end
        )
    }

    /**
     * Sets the orientation and symbols for the scrollbar from a [ScrollbarSet].
     *
     * This has the same effect as calling [orientation] and then [symbols].
     */
    fun orientationAndSymbol(orientation: ScrollbarOrientation, symbols: ScrollbarSet): Scrollbar {
        return copy(orientation = orientation).symbols(symbols)
    }

    /**
     * Sets the symbol that represents the thumb of the scrollbar.
     *
     * The thumb is the handle representing the progression on the scrollbar.
     */
    fun thumbSymbol(thumbSymbol: String): Scrollbar = copy(thumbSymbol = thumbSymbol)

    /**
     * Sets the style on the scrollbar thumb.
     *
     * The thumb is the handle representing the progression on the scrollbar.
     */
    fun thumbStyle(thumbStyle: Style): Scrollbar = copy(thumbStyle = thumbStyle)

    /**
     * Sets the symbol that represents the track of the scrollbar.
     */
    fun trackSymbol(trackSymbol: String?): Scrollbar = copy(trackSymbol = trackSymbol)

    /**
     * Sets the style that is used for the track of the scrollbar.
     */
    fun trackStyle(trackStyle: Style): Scrollbar = copy(trackStyle = trackStyle)

    /**
     * Sets the symbol that represents the beginning of the scrollbar.
     */
    fun beginSymbol(beginSymbol: String?): Scrollbar = copy(beginSymbol = beginSymbol)

    /**
     * Sets the style that is used for the beginning of the scrollbar.
     */
    fun beginStyle(beginStyle: Style): Scrollbar = copy(beginStyle = beginStyle)

    /**
     * Sets the symbol that represents the end of the scrollbar.
     */
    fun endSymbol(endSymbol: String?): Scrollbar = copy(endSymbol = endSymbol)

    /**
     * Sets the style that is used for the end of the scrollbar.
     */
    fun endStyle(endStyle: Style): Scrollbar = copy(endStyle = endStyle)

    /**
     * Sets the symbols used for the various parts of the scrollbar from a [ScrollbarSet].
     *
     * Only sets `beginSymbol`, `endSymbol` and `trackSymbol` if they already contain a value.
     * If they were set to `null` explicitly, this function will respect that choice.
     */
    fun symbols(symbols: ScrollbarSet): Scrollbar {
        return copy(
            thumbSymbol = symbols.thumb,
            trackSymbol = if (trackSymbol != null) symbols.track else null,
            beginSymbol = if (beginSymbol != null) symbols.begin else null,
            endSymbol = if (endSymbol != null) symbols.end else null
        )
    }

    /**
     * Sets the style used for the various parts of the scrollbar.
     */
    fun style(style: Style): Scrollbar = copy(
        trackStyle = style,
        thumbStyle = style,
        beginStyle = style,
        endStyle = style
    )

    // StatefulWidget implementation
    override fun render(area: Rect, buf: Buffer, state: ScrollbarState) {
        if (state.contentLength == 0 || trackLengthExcludingArrowHeads(area) == 0) {
            return
        }

        val scrollbarArea = scrollbarArea(area) ?: return
        val barSymbols = barSymbols(scrollbarArea, state)

        // Iterate over the scrollbar area cells
        for ((cellArea, bar) in scrollbarArea.columns().asSequence()
            .flatMap { col -> col.rows().asSequence() }
            .zip(barSymbols)) {
            if (bar != null) {
                val (symbol, style) = bar
                buf.setString(cellArea.x, cellArea.y, symbol, style)
            }
        }
    }

    /**
     * Returns a sequence of the symbols and styles of the scrollbar.
     */
    private fun barSymbols(
        area: Rect,
        state: ScrollbarState
    ): Sequence<Pair<String, Style>?> {
        val (trackStartLen, thumbLen, trackEndLen) = partLengths(area, state)

        val begin = beginSymbol?.let { it to beginStyle }
        val track = trackSymbol?.let { it to trackStyle }
        val thumb = thumbSymbol to thumbStyle
        val end = endSymbol?.let { it to endStyle }

        return sequence {
            // `<`
            yield(begin)
            // `<===`
            repeat(trackStartLen) { yield(track) }
            // `<===#####`
            repeat(thumbLen) { yield(thumb) }
            // `<===#####=======`
            repeat(trackEndLen) { yield(track) }
            // `<===#####=======>`
            yield(end)
        }
    }

    /**
     * Returns the lengths of the parts of a scrollbar.
     *
     * The scrollbar has 3 parts of note:
     * - `<===#####=======>`: full scrollbar
     * - ` ===             `: track start
     * - `    #####        `: thumb
     * - `         ======= `: track end
     *
     * This method returns the length of the start, thumb, and end as a triple.
     */
    private fun partLengths(area: Rect, state: ScrollbarState): Triple<Int, Int, Int> {
        val trackLength = trackLengthExcludingArrowHeads(area).toDouble()
        val viewportLength = viewportLength(state, area).toDouble()

        // Ensure that the position of the thumb is within the bounds of the content taking into
        // account the content and viewport length. When the last line of the content is at the top
        // of the viewport, the thumb should be at the bottom of the track.
        val maxPosition = (state.contentLength - 1).coerceAtLeast(0).toDouble()
        val startPosition = state.position.toDouble().coerceIn(0.0, maxPosition)
        val maxViewportPosition = maxPosition + viewportLength
        val endPosition = startPosition + viewportLength

        // Calculate the start and end positions of the thumb. The size will be proportional to the
        // viewport length compared to the total amount of possible visible rows.
        val thumbStart = startPosition * trackLength / maxViewportPosition
        val thumbEnd = endPosition * trackLength / maxViewportPosition

        // Make sure that the thumb is at least 1 cell long by ensuring that the start of the thumb
        // is less than the track_len. We use the positions instead of the sizes and use nearest
        // integer instead of floor / ceil to avoid problems caused by rounding errors.
        val thumbStartClamped = thumbStart.roundToInt().coerceIn(0, (trackLength - 1).toInt())
        val thumbEndClamped = thumbEnd.roundToInt().coerceIn(0, trackLength.toInt())

        val thumbLength = (thumbEndClamped - thumbStartClamped).coerceAtLeast(1)
        val trackEndLength = (trackLength.toInt() - thumbStartClamped - thumbLength).coerceAtLeast(0)

        return Triple(thumbStartClamped, thumbLength, trackEndLength)
    }

    private fun scrollbarArea(area: Rect): Rect? {
        return when (orientation) {
            ScrollbarOrientation.VerticalLeft -> area.columns().asSequence().firstOrNull()
            ScrollbarOrientation.VerticalRight -> area.columns().asSequence().lastOrNull()
            ScrollbarOrientation.HorizontalTop -> area.rows().asSequence().firstOrNull()
            ScrollbarOrientation.HorizontalBottom -> area.rows().asSequence().lastOrNull()
        }
    }

    /**
     * Calculates length of the track excluding the arrow heads.
     *
     * ```
     *        +---------- track_length
     *  vvvvvvvvvvvvvvv
     * <===#####=======>
     * ```
     */
    private fun trackLengthExcludingArrowHeads(area: Rect): Int {
        val startLen = beginSymbol?.length ?: 0
        val endLen = endSymbol?.length ?: 0
        val arrowsLen = startLen + endLen
        return if (orientation.isVertical()) {
            (area.height - arrowsLen).coerceAtLeast(0)
        } else {
            (area.width - arrowsLen).coerceAtLeast(0)
        }
    }

    private fun viewportLength(state: ScrollbarState, area: Rect): Int {
        return if (state.viewportContentLength != 0) {
            state.viewportContentLength
        } else if (orientation.isVertical()) {
            area.height
        } else {
            area.width
        }
    }

    companion object {
        /**
         * Creates a new scrollbar with the given orientation.
         *
         * Most of the time you'll want [ScrollbarOrientation.VerticalRight] or
         * [ScrollbarOrientation.HorizontalBottom]. See [ScrollbarOrientation] for more options.
         */
        fun new(orientation: ScrollbarOrientation): Scrollbar {
            val symbols = if (orientation.isVertical()) DOUBLE_VERTICAL else DOUBLE_HORIZONTAL
            return Scrollbar(
                orientation = orientation,
                thumbSymbol = symbols.thumb,
                trackSymbol = symbols.track,
                beginSymbol = symbols.begin,
                endSymbol = symbols.end
            )
        }

        /**
         * Creates a default scrollbar (VerticalRight).
         */
        fun default(): Scrollbar = new(ScrollbarOrientation.VerticalRight)
    }
}
