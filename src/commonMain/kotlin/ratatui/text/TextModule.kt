// port-lint: source ratatui-core/src/text.rs
package ratatui.text

import ratatui.text.Line
import ratatui.text.Masked
import ratatui.text.Span
import ratatui.text.StyledGrapheme
import ratatui.text.Text
import ratatui.text.ToLine
import ratatui.text.ToSpan
import ratatui.text.ToText

/**
 * Primitives for styled text.
 *
 * A terminal UI is, at its root, a lot of strings. In order to make it accessible and stylish,
 * those strings may be associated with a set of styles. Ratatui has three ways to represent them:
 *
 * - A single-line string where all graphemes have the same style is represented by a [Span].
 * - A single-line string where each grapheme may have its own style is represented by a [Line].
 * - A multi-line string where each grapheme may have its own style is represented by a [Text].
 *
 * These types form a hierarchy: [Line] is a collection of [Span], and each line of [Text] is a
 * [Line].
 *
 * Keep in mind that many widgets will use these types to advertise what kind of string is
 * supported for their properties. Moreover, Ratatui provides convenient conversion helpers so that
 * you can start by using simple `String` values and then promote them to these primitives when you
 * need additional styling capabilities.
 *
 * For example, for the `Block` widget, all the following calls are valid to set its title
 * property (which is a [Line] under the hood):
 *
 * ```kotlin
 * import ratatui.style.Color
 * import ratatui.style.Style
 * import ratatui.text.Line
 * import ratatui.text.Span
 * import ratatui.widgets.block.Block
 *
 * // A simple string with no styling.
 * val block1 = Block.new().title("My title")
 *
 * // A simple string with a unique style.
 * val block2 = Block.new().title(Span.styled("My title", Style.default().fg(Color.Yellow)))
 *
 * // A string with multiple styles.
 * val block3 = Block.new().title(
 *     Line.from(
 *         listOf(
 *             Span.styled("My", Style.default().fg(Color.Yellow)),
 *             Span.raw(" title"),
 *         )
 *     )
 * )
 * ```
 */

@Suppress("unused")
private val exports = listOf(
    StyledGrapheme::class,
    Line::class,
    ToLine::class,
    Masked::class,
    Span::class,
    ToSpan::class,
    Text::class,
    ToText::class,
)
