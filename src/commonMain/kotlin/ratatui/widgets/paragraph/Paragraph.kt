package ratatui.widgets.paragraph

import ratatui.buffer.Buffer
import ratatui.layout.Alignment
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Line
import ratatui.text.StyledGrapheme
import ratatui.text.Text
import ratatui.widgets.Widget
import ratatui.widgets.block.Block
import ratatui.widgets.block.innerIfSome
import ratatui.widgets.reflow.LineComposer
import ratatui.widgets.reflow.LineTruncator
import ratatui.widgets.reflow.WordWrapper
import ratatui.widgets.reflow.WrappedLine

/**
 * Describes how to wrap text across lines.
 *
 * ## Examples
 *
 * ```kotlin
 * val bulletPoints = Text.from("""Some indented points:
 *     - First thing goes here and is long so that it wraps
 *     - Here is another point that is long enough to wrap""")
 *
 * // With leading spaces trimmed (window width of 30 chars):
 * Paragraph.new(bulletPoints).wrap(Wrap(trim = true))
 * // Some indented points:
 * // - First thing goes here and is
 * // long so that it wraps
 * // - Here is another point that
 * // is long enough to wrap
 *
 * // But without trimming, indentation is preserved:
 * Paragraph.new(bulletPoints).wrap(Wrap(trim = false))
 * // Some indented points:
 * //     - First thing goes here
 * // and is long so that it wraps
 * //     - Here is another point
 * // that is long enough to wrap
 * ```
 */
data class Wrap(
    /** Should leading whitespace be trimmed */
    val trim: Boolean = false
)

/**
 * A widget to display some text.
 *
 * It is used to display a block of text. The text can be styled and aligned. It can also be
 * wrapped to the next line if it is too long to fit in the given area.
 *
 * The text can be any type that can be converted into a [Text]. By default, the text is styled
 * with [Style.default], not wrapped, and aligned to the left.
 *
 * The text can be wrapped to the next line if it is too long to fit in the given area. The
 * wrapping can be configured with the [wrap] method.
 *
 * The text can be aligned to the left, right, or center. The alignment can be configured with the
 * [alignment] method or with the [leftAligned], [rightAligned], and [centered] methods.
 *
 * The text can be scrolled to show a specific part of the text. The scroll offset can be set with
 * the [scroll] method.
 *
 * The text can be surrounded by a [Block] with a title and borders. The block can be configured
 * with the [block] method.
 *
 * The style of the text can be set with the [style] method. This style will be applied to the
 * entire widget, including the block if one is present.
 *
 * Note: If neither wrapping nor a block is needed, consider rendering the [Text], [Line], or
 * [ratatui.text.Span] widgets directly.
 *
 * # Example
 *
 * ```kotlin
 * val text = listOf(
 *     Line.from(listOf(
 *         Span.raw("First"),
 *         Span.styled("line", Style.new().green().italic()),
 *         ".".toSpan(),
 *     )),
 *     Line.from("Second line".red()),
 *     "Third line".toLine(),
 * )
 * Paragraph.new(text)
 *     .block(Block.bordered().title("Paragraph"))
 *     .style(Style.new().white().onBlack())
 *     .alignment(Alignment.Center)
 *     .wrap(Wrap(trim = true))
 * ```
 */
data class Paragraph(
    /** A block to wrap the widget in */
    private val block: Block? = null,
    /** Widget style */
    private val paragraphStyle: Style = Style.default(),
    /** How to wrap the text */
    private val wrap: Wrap? = null,
    /** The text to display */
    private val text: Text = Text.default(),
    /** Scroll offset (y, x) */
    private val scroll: Position = Position.ORIGIN,
    /** Alignment of the text */
    private val paragraphAlignment: Alignment = Alignment.Left
) : Widget, Styled<Paragraph> {

    /**
     * Surrounds the Paragraph widget with a [Block].
     *
     * # Example
     *
     * ```kotlin
     * val paragraph = Paragraph.new("Hello, world!").block(Block.bordered().title("Paragraph"))
     * ```
     */
    fun block(block: Block): Paragraph = copy(block = block)

    /**
     * Sets the style of the entire widget.
     *
     * This applies to the entire widget, including the block if one is present. Any style set on
     * the block or text will be added to this style.
     *
     * # Example
     *
     * ```kotlin
     * val paragraph = Paragraph.new("Hello, world!").style(Style.new().red().onWhite())
     * ```
     */
    fun style(style: Style): Paragraph = copy(paragraphStyle = style)

    /**
     * Sets the wrapping configuration for the widget.
     *
     * See [Wrap] for more information on the different options.
     *
     * # Example
     *
     * ```kotlin
     * val paragraph = Paragraph.new("Hello, world!").wrap(Wrap(trim = true))
     * ```
     */
    fun wrap(wrap: Wrap): Paragraph = copy(wrap = wrap)

    /**
     * Set the scroll offset for the given paragraph.
     *
     * The scroll offset is a pair of (y, x) offset. The y offset is the number of lines to
     * scroll, and the x offset is the number of characters to scroll. The scroll offset is applied
     * after the text is wrapped and aligned.
     *
     * Note: the order of the pair is (y, x) instead of (x, y), which is different from general
     * convention across the crate.
     */
    fun scroll(offset: Pair<Int, Int>): Paragraph = copy(
        scroll = Position(x = offset.second, y = offset.first)
    )

    /**
     * Set the text alignment for the given paragraph.
     *
     * The alignment is a variant of the [Alignment] enum which can be one of Left, Right, or
     * Center. If no alignment is specified, the text in a paragraph will be left-aligned.
     *
     * # Example
     *
     * ```kotlin
     * val paragraph = Paragraph.new("Hello World").alignment(Alignment.Center)
     * ```
     */
    fun alignment(alignment: Alignment): Paragraph = copy(paragraphAlignment = alignment)

    /**
     * Left-aligns the text in the given paragraph.
     *
     * Convenience shortcut for `Paragraph.alignment(Alignment.Left)`.
     */
    fun leftAligned(): Paragraph = alignment(Alignment.Left)

    /**
     * Center-aligns the text in the given paragraph.
     *
     * Convenience shortcut for `Paragraph.alignment(Alignment.Center)`.
     */
    fun centered(): Paragraph = alignment(Alignment.Center)

    /**
     * Right-aligns the text in the given paragraph.
     *
     * Convenience shortcut for `Paragraph.alignment(Alignment.Right)`.
     */
    fun rightAligned(): Paragraph = alignment(Alignment.Right)

    /**
     * Calculates the number of lines needed to fully render.
     *
     * Given a max line width, this method calculates the number of lines that a paragraph will
     * need in order to be fully rendered. For paragraphs that do not use wrapping, this count is
     * simply the number of lines present in the paragraph.
     *
     * This method will also account for the [Block] if one is set through [block].
     *
     * # Example
     *
     * ```kotlin
     * val paragraph = Paragraph.new("Hello World").wrap(Wrap(trim = false))
     * assertEquals(paragraph.lineCount(20), 1)
     * assertEquals(paragraph.lineCount(10), 2)
     * ```
     */
    fun lineCount(width: Int): Int {
        if (width < 1) {
            return 0
        }

        val (top, bottom) = block?.verticalSpace() ?: (0 to 0)

        val count = if (wrap != null) {
            val styled = text.lines.asSequence().map { line ->
                val graphemes = line.spans.flatMap { span ->
                    span.styledGraphemes(paragraphStyle)
                }.iterator()
                val alignment = line.alignment ?: paragraphAlignment
                graphemes to alignment
            }.iterator()
            val lineComposer = WordWrapper(styled, width, wrap.trim)
            var count = 0
            while (lineComposer.nextLine() != null) {
                count++
            }
            count
        } else {
            text.height()
        }

        return count + top + bottom
    }

    /**
     * Calculates the shortest line width needed to avoid any word being wrapped or truncated.
     *
     * Accounts for the [Block] if a block is set through [block].
     *
     * # Example
     *
     * ```kotlin
     * val paragraph = Paragraph.new("Hello World")
     * assertEquals(paragraph.lineWidth(), 11)
     *
     * val paragraph = Paragraph.new("Hello World\nhi\nHello World!!!")
     * assertEquals(paragraph.lineWidth(), 14)
     * ```
     */
    fun lineWidth(): Int {
        val width = text.lines.maxOfOrNull { it.width() } ?: 0
        val (left, right) = block?.horizontalSpace() ?: (0 to 0)
        return width + left + right
    }

    // Widget implementation
    override fun render(area: Rect, buf: Buffer) {
        val clippedArea = area.intersection(buf.area)
        buf.setStyle(clippedArea, paragraphStyle)
        block?.render(clippedArea, buf)
        val inner = block.innerIfSome(clippedArea)
        renderParagraph(inner, buf)
    }

    private fun renderParagraph(textArea: Rect, buf: Buffer) {
        if (textArea.isEmpty()) {
            return
        }

        buf.setStyle(textArea, paragraphStyle)
        val styled = text.lines.asSequence().map { line ->
            val graphemes = line.styledGraphemes(text.style).iterator()
            val alignment = line.alignment ?: paragraphAlignment
            graphemes to alignment
        }.iterator()

        if (wrap != null) {
            val lineComposer = WordWrapper(styled, textArea.width, wrap.trim)
            // compute the lines iteratively until we reach the desired scroll offset
            repeat(scroll.y) {
                if (lineComposer.nextLine() == null) {
                    return
                }
            }
            renderLines(lineComposer, textArea, buf)
        } else {
            // avoid unnecessary work by skipping directly to the relevant line before rendering
            val lines = styled.asSequence().drop(scroll.y).iterator()
            val lineComposer = LineTruncator(lines, textArea.width)
            lineComposer.setHorizontalOffset(scroll.x)
            renderLines(lineComposer, textArea, buf)
        }
    }

    private fun renderLines(composer: LineComposer, area: Rect, buf: Buffer) {
        var y = 0
        while (true) {
            val wrapped = composer.nextLine() ?: break
            renderLine(wrapped, area, buf, y)
            y++
            if (y >= area.height) {
                break
            }
        }
    }

    private fun renderLine(wrapped: WrappedLine, area: Rect, buf: Buffer, y: Int) {
        var x = getLineOffset(wrapped.width, area.width, wrapped.alignment)
        for (grapheme in wrapped.graphemes) {
            val width = grapheme.width()
            if (width == 0) {
                continue
            }
            // Make sure to overwrite any previous character with a space (rather than a zero-width)
            val symbol = if (grapheme.symbol.isEmpty()) " " else grapheme.symbol
            val position = Position(area.left() + x, area.top() + y)
            buf.cell(position)?.setSymbol(symbol)?.setStyle(grapheme.style)
            x += width
        }
    }

    private fun getLineOffset(lineWidth: Int, textAreaWidth: Int, alignment: Alignment): Int {
        return when (alignment) {
            Alignment.Center -> ((textAreaWidth / 2) - (lineWidth / 2)).coerceAtLeast(0)
            Alignment.Right -> (textAreaWidth - lineWidth).coerceAtLeast(0)
            Alignment.Left -> 0
        }
    }

    // Styled implementation
    override fun getStyle(): Style = paragraphStyle

    override fun setStyle(style: Style): Paragraph = style(style)

    companion object {
        /**
         * Creates a new Paragraph widget with the given text.
         *
         * The text parameter can be a [Text] or any type that can be converted into a [Text]. By
         * default, the text is styled with [Style.default], not wrapped, and aligned to the left.
         *
         * # Examples
         *
         * ```kotlin
         * val paragraph = Paragraph.new("Hello, world!")
         * val paragraph = Paragraph.new(Text.raw("Hello, world!"))
         * val paragraph = Paragraph.new(Text.styled("Hello, world!", Style.default()))
         * val paragraph = Paragraph.new(Line.from(listOf("Hello, ".toSpan(), "world!".red())))
         * ```
         */
        fun new(text: String): Paragraph = Paragraph(text = Text.from(text))

        /**
         * Creates a new Paragraph widget with the given Text.
         */
        fun new(text: Text): Paragraph = Paragraph(text = text)

        /**
         * Creates a new Paragraph widget with the given Line.
         */
        fun new(line: Line): Paragraph = Paragraph(text = Text.from(line))

        /**
         * Creates a new Paragraph widget with the given lines.
         */
        fun new(lines: List<Line>): Paragraph = Paragraph(text = Text.from(lines))

        /**
         * Creates a default empty Paragraph.
         */
        fun default(): Paragraph = Paragraph()
    }
}
