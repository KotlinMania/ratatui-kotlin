package ratatui.widgets.logo

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.text.Text
import ratatui.widgets.Widget

/**
 * A widget that renders the Ratatui logo
 *
 * The Ratatui logo takes up two lines of text and comes in two sizes: `Tiny` and `Small`. This may
 * be used in an application's help or about screen to show that it is powered by Ratatui.
 *
 * ## Tiny (default, 2x15 characters)
 *
 * ```kotlin
 * frame.renderWidget(RatatuiLogo.tiny(), frame.area())
 * ```
 *
 * Renders:
 *
 * ```text
 * ▛▚▗▀▖▜▘▞▚▝▛▐ ▌▌
 * ▛▚▐▀▌▐ ▛▜ ▌▝▄▘▌
 * ```
 *
 * ## Small (2x27 characters)
 *
 * ```kotlin
 * frame.renderWidget(RatatuiLogo.small(), frame.area())
 * ```
 *
 * Renders:
 *
 * ```text
 * █▀▀▄ ▄▀▀▄▝▜▛▘▄▀▀▄▝▜▛▘█  █ █
 * █▀▀▄ █▀▀█ ▐▌ █▀▀█ ▐▌ ▀▄▄▀ █
 * ```
 */
data class RatatuiLogo(
    private val size: RatatuiLogoSize = RatatuiLogoSize.Tiny
) : Widget {

    /**
     * Set the size of the logo
     *
     * @param size The size of the logo
     * @return A new RatatuiLogo with the size set
     */
    fun size(size: RatatuiLogoSize): RatatuiLogo = copy(size = size)

    override fun render(area: Rect, buf: Buffer) {
        val logo = size.asString()
        Text.raw(logo).render(area, buf)
    }

    companion object {
        /**
         * Create a new Ratatui logo widget
         *
         * @param size The size of the logo
         * @return A new RatatuiLogo
         */
        fun new(size: RatatuiLogoSize): RatatuiLogo = RatatuiLogo(size = size)

        /**
         * Create a new Ratatui logo widget with a tiny size
         *
         * @return A new RatatuiLogo with tiny size
         */
        fun tiny(): RatatuiLogo = RatatuiLogo(size = RatatuiLogoSize.Tiny)

        /**
         * Create a new Ratatui logo widget with a small size
         *
         * @return A new RatatuiLogo with small size
         */
        fun small(): RatatuiLogo = RatatuiLogo(size = RatatuiLogoSize.Small)

        /**
         * Create a default Ratatui logo widget (tiny size)
         *
         * @return A new RatatuiLogo with default (tiny) size
         */
        fun default(): RatatuiLogo = RatatuiLogo()
    }
}

/**
 * The size of the logo
 */
enum class RatatuiLogoSize {
    /**
     * A tiny logo
     *
     * The default size of the logo (2x15 characters)
     *
     * ```text
     * ▛▚▗▀▖▜▘▞▚▝▛▐ ▌▌
     * ▛▚▐▀▌▐ ▛▜ ▌▝▄▘▌
     * ```
     */
    Tiny,

    /**
     * A small logo
     *
     * A slightly larger version of the logo (2x27 characters)
     *
     * ```text
     * █▀▀▄ ▄▀▀▄▝▜▛▘▄▀▀▄▝▜▛▘█  █ █
     * █▀▀▄ █▀▀█ ▐▌ █▀▀█ ▐▌ ▀▄▄▀ █
     * ```
     */
    Small;

    /**
     * Get the logo as a string
     */
    fun asString(): String = when (this) {
        Tiny -> TINY_LOGO
        Small -> SMALL_LOGO
    }

    companion object {
        private val TINY_LOGO = """
            |▛▚▗▀▖▜▘▞▚▝▛▐ ▌▌
            |▛▚▐▀▌▐ ▛▜ ▌▝▄▘▌
        """.trimMargin()

        private val SMALL_LOGO = """
            |█▀▀▄ ▄▀▀▄▝▜▛▘▄▀▀▄▝▜▛▘█  █ █
            |█▀▀▄ █▀▀█ ▐▌ █▀▀█ ▐▌ ▀▄▄▀ █
        """.trimMargin()
    }
}
