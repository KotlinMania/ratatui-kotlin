package ratatui.text

import ratatui.style.Style
import ratatui.style.Styled

/** Non-breaking space character */
private const val NBSP = "\u00a0"

/** Zero-width space character */
private const val ZWSP = "\u200b"

/**
 * A grapheme associated with a style.
 *
 * Note that, although [StyledGrapheme] is the smallest divisible unit of text,
 * it actually is not a member of the text type hierarchy ([Text] -> [Line] -> [Span]).
 * It is a separate type used mostly for rendering purposes. A [Span] consists of components that
 * can be split into [StyledGrapheme]s, but it does not contain a collection of [StyledGrapheme]s.
 */
data class StyledGrapheme(
    /** The grapheme symbol */
    val symbol: String,
    /** The style to apply to the grapheme */
    override val style: Style = Style.default()
) : Styled<StyledGrapheme> {

    /**
     * Returns true if this grapheme represents whitespace.
     *
     * Zero-width space is considered whitespace, but non-breaking space is not.
     */
    fun isWhitespace(): Boolean {
        return symbol == ZWSP || (symbol.all { it.isWhitespace() } && symbol != NBSP)
    }

    override fun setStyle(style: Style): StyledGrapheme = copy(style = style)

    companion object {
        /**
         * Creates a new [StyledGrapheme] with the given symbol and style.
         */
        fun new(symbol: String, style: Style = Style.default()): StyledGrapheme {
            return StyledGrapheme(symbol = symbol, style = style)
        }
    }
}
