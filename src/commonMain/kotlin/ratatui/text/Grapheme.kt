// port-lint: source ratatui-core/src/text/grapheme.rs
package ratatui.text

import ratatui.style.Style
import ratatui.style.Styled

private const val NBSP: String = "\u00A0"
private const val ZWSP: String = "\u200B"

/**
 * A grapheme associated to a style.
 *
 * Note that, although [StyledGrapheme] is the smallest divisible unit of text, it actually is not
 * a member of the text type hierarchy ([Text] -> [Line] -> [Span]). It is a separate type used
 * mostly for rendering purposes. A [Span] consists of components that can be split into
 * [StyledGrapheme]s, but it does not contain a collection of [StyledGrapheme]s.
 *
 * Transliteration of `ratatui_core::text::grapheme::StyledGrapheme`.
 */
data class StyledGrapheme(
    val symbol: String,
    val style: Style,
) : Styled<StyledGrapheme> {
    /**
     * Creates a new [StyledGrapheme] with the given symbol and style.
     *
     * `style` accepts any type that is convertible to [Style] (e.g. [Style], [ratatui.style.Color],
     * or your own type that can be converted to [Style]).
     */
    companion object {
        fun new(symbol: String, style: Style): StyledGrapheme = StyledGrapheme(symbol, style)
    }

    fun isWhitespace(): Boolean {
        val symbol = this.symbol
        return symbol == ZWSP || (symbol.all { it.isWhitespace() } && symbol != NBSP)
    }

    override fun style(): Style = style

    override fun setStyle(style: Style): StyledGrapheme = copy(style = style)
}

