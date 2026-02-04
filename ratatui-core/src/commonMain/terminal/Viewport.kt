package ratatui.terminal

import ratatui.layout.Rect

/**
 * Represents the viewport of the terminal. The viewport is the area of the terminal that is
 * currently visible to the user. It can be either fullscreen, inline or fixed.
 *
 * When the viewport is [Fullscreen], the whole terminal is used to draw the application.
 *
 * When the viewport is [Inline], it is drawn inline with the rest of the terminal. The height of
 * the viewport is fixed, but the width is the same as the terminal width.
 *
 * When the viewport is [Fixed], it is drawn in a fixed area of the terminal. The area is specified
 * by a [Rect].
 *
 * See [Terminal.withOptions] for more information.
 */
sealed class Viewport {
    /**
     * The viewport is fullscreen - the whole terminal is used to draw the application.
     */
    data object Fullscreen : Viewport() {
        override fun toString(): String = "Fullscreen"
    }

    /**
     * The viewport is inline with the rest of the terminal.
     *
     * The viewport's height is fixed and specified in number of lines. The width is the same as
     * the terminal's width. The viewport is drawn below the cursor position.
     *
     * @param height The height of the inline viewport in lines
     */
    data class Inline(val height: UShort) : Viewport() {
        override fun toString(): String = "Inline($height)"
    }

    /**
     * The viewport is drawn in a fixed area of the terminal.
     *
     * @param area The fixed area to use for the viewport
     */
    data class Fixed(val area: Rect) : Viewport() {
        override fun toString(): String = "Fixed($area)"
    }

    companion object {
        /** Creates a fullscreen viewport (default) */
        fun fullscreen(): Viewport = Fullscreen

        /** Creates an inline viewport with the specified height */
        fun inline(height: UShort): Viewport = Inline(height)

        /** Creates a fixed viewport in the specified area */
        fun fixed(area: Rect): Viewport = Fixed(area)

        /** Default viewport is fullscreen */
        fun default(): Viewport = Fullscreen
    }
}
