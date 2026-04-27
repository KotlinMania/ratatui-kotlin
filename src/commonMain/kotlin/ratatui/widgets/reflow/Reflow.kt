// port-lint: source ratatui-widgets/src/reflow.rs
package ratatui.widgets.reflow

import ratatui.buffer.cellWidth
import ratatui.layout.HorizontalAlignment
import ratatui.text.StyledGrapheme
import ratatui.text.graphemes

/**
 * A line that has been wrapped to a certain width.
 */
data class WrappedLine(
    /** One line reflowed to the correct width */
    val graphemes: List<StyledGrapheme>,
    /** The width of the line */
    val width: Int,
    /** The alignment of the line */
    val alignment: HorizontalAlignment
)

/**
 * A state machine to pack styled symbols into lines.
 */
interface LineComposer {
    /**
     * Get the next wrapped line, or null if there are no more lines.
     */
    fun nextLine(): WrappedLine?
}

/**
 * A state machine that wraps lines on word boundaries.
 *
 * @param inputLines Iterator providing (line graphemes, alignment) pairs
 * @param maxLineWidth Maximum width of each wrapped line
 * @param trim Whether to remove leading whitespace from lines
 */
class WordWrapper(
    private val inputLines: Iterator<Pair<Iterator<StyledGrapheme>, HorizontalAlignment>>,
    private val maxLineWidth: Int,
    private val trim: Boolean
) : LineComposer {

    private val wrappedLines = ArrayDeque<MutableList<StyledGrapheme>>()
    private var currentAlignment = HorizontalAlignment.Left
    private var currentLine = mutableListOf<StyledGrapheme>()

    // Cached allocations
    private val pendingWord = mutableListOf<StyledGrapheme>()
    private val pendingWhitespace = ArrayDeque<StyledGrapheme>()
    private val pendingLinePool = mutableListOf<MutableList<StyledGrapheme>>()

    /**
     * Split an input line into wrapped lines and cache them to be emitted later.
     */
    private fun processInput(lineSymbols: Iterator<StyledGrapheme>) {
        var pendingLine = pendingLinePool.removeLastOrNull() ?: mutableListOf()
        var lineWidth = 0
        var wordWidth = 0
        var whitespaceWidth = 0
        var nonWhitespacePrevious = false

        pendingWord.clear()
        pendingWhitespace.clear()
        pendingLine.clear()

        for (grapheme in lineSymbols) {
            val isWhitespace = grapheme.isWhitespace()
            val symbolWidth = grapheme.symbol.cellWidth().toInt()

            // ignore symbols wider than line limit
            if (symbolWidth > maxLineWidth) {
                continue
            }

            val wordFound = nonWhitespacePrevious && isWhitespace
            // current word would overflow after removing whitespace
            val trimmedOverflow = pendingLine.isEmpty() &&
                    trim &&
                    wordWidth + symbolWidth > maxLineWidth
            // separated whitespace would overflow on its own
            val whitespaceOverflow = pendingLine.isEmpty() &&
                    trim &&
                    whitespaceWidth + symbolWidth > maxLineWidth
            // current full word (including whitespace) would overflow
            val untrimmedOverflow = pendingLine.isEmpty() &&
                    !trim &&
                    wordWidth + whitespaceWidth + symbolWidth > maxLineWidth

            // append finished segment to current line
            if (wordFound || trimmedOverflow || whitespaceOverflow || untrimmedOverflow) {
                if (pendingLine.isNotEmpty() || !trim) {
                    pendingLine.addAll(pendingWhitespace)
                    pendingWhitespace.clear()
                    lineWidth += whitespaceWidth
                }

                pendingLine.addAll(pendingWord)
                pendingWord.clear()
                lineWidth += wordWidth

                pendingWhitespace.clear()
                whitespaceWidth = 0
                wordWidth = 0
            }

            // pending line fills up limit
            val lineFull = lineWidth >= maxLineWidth
            // pending word would overflow line limit
            val pendingWordOverflow = symbolWidth > 0 &&
                    lineWidth + whitespaceWidth + wordWidth >= maxLineWidth

            // add finished wrapped line to remaining lines
            if (lineFull || pendingWordOverflow) {
                var remainingWidth = (maxLineWidth - lineWidth).coerceAtLeast(0)

                val finishedLine = pendingLine
                pendingLine = pendingLinePool.removeLastOrNull() ?: mutableListOf()
                wrappedLines.addLast(finishedLine)
                lineWidth = 0

                // remove whitespace up to the end of line
                while (pendingWhitespace.isNotEmpty()) {
                    val ws = pendingWhitespace.first()
                    val wsWidth = ws.symbol.cellWidth().toInt()

                    if (wsWidth > remainingWidth) {
                        break
                    }

                    whitespaceWidth -= wsWidth
                    remainingWidth -= wsWidth
                    pendingWhitespace.removeFirst()
                }

                // do not count first whitespace toward next word
                if (isWhitespace && pendingWhitespace.isEmpty()) {
                    continue
                }
            }

            // append symbol to a pending buffer
            if (isWhitespace) {
                whitespaceWidth += symbolWidth
                pendingWhitespace.addLast(grapheme)
            } else {
                wordWidth += symbolWidth
                pendingWord.add(grapheme)
            }

            nonWhitespacePrevious = !isWhitespace
        }

        // append remaining text parts
        if (pendingLine.isEmpty() &&
            pendingWord.isEmpty() &&
            pendingWhitespace.isNotEmpty() &&
            trim
        ) {
            wrappedLines.addLast(mutableListOf())
        }
        if (pendingLine.isNotEmpty() || !trim) {
            pendingLine.addAll(pendingWhitespace)
            pendingWhitespace.clear()
        }
        pendingLine.addAll(pendingWord)
        pendingWord.clear()

        if (pendingLine.isNotEmpty()) {
            wrappedLines.addLast(pendingLine)
        } else if (pendingLine.isNotEmpty()) {
            pendingLinePool.add(pendingLine)
        }
        if (wrappedLines.isEmpty()) {
            wrappedLines.addLast(mutableListOf())
        }
    }

    private fun replaceCurrentLine(line: MutableList<StyledGrapheme>) {
        val cache = currentLine
        currentLine = line
        if (cache.isNotEmpty()) {
            cache.clear()
            pendingLinePool.add(cache)
        }
    }

    override fun nextLine(): WrappedLine? {
        if (maxLineWidth == 0) {
            return null
        }

        while (true) {
            // emit next cached line if present
            val cachedLine = wrappedLines.removeFirstOrNull()
            if (cachedLine != null) {
                val lineWidth = cachedLine.sumOf { it.symbol.cellWidth().toInt() }
                replaceCurrentLine(cachedLine)
                return WrappedLine(
                    graphemes = currentLine,
                    width = lineWidth,
                    alignment = currentAlignment
                )
            }

            // otherwise, process pending wrapped lines from input
            if (!inputLines.hasNext()) {
                return null
            }
            val (lineSymbols, lineAlignment) = inputLines.next()
            currentAlignment = lineAlignment
            processInput(lineSymbols)
        }
    }

    companion object {
        /**
         * Create a new WordWrapper with the given lines and maximum line width.
         */
        fun new(
            lines: Iterator<Pair<Iterator<StyledGrapheme>, HorizontalAlignment>>,
            maxLineWidth: Int,
            trim: Boolean
        ): WordWrapper = WordWrapper(lines, maxLineWidth, trim)
    }
}

/**
 * A state machine that truncates overhanging lines.
 *
 * @param inputLines Iterator providing (line graphemes, alignment) pairs
 * @param maxLineWidth Maximum width of each line
 */
class LineTruncator(
    private val inputLines: Iterator<Pair<Iterator<StyledGrapheme>, HorizontalAlignment>>,
    private val maxLineWidth: Int
) : LineComposer {

    private var currentLine = mutableListOf<StyledGrapheme>()
    private var horizontalOffset = 0

    /**
     * Set the horizontal offset to skip render.
     */
    fun setHorizontalOffset(offset: Int) {
        horizontalOffset = offset
    }

    override fun nextLine(): WrappedLine? {
        if (maxLineWidth == 0) {
            return null
        }

        currentLine.clear()
        var currentLineWidth = 0

        if (!inputLines.hasNext()) {
            return null
        }

        val (lineSymbols, alignment) = inputLines.next()
        var localHorizontalOffset = horizontalOffset

        for (grapheme in lineSymbols) {
            val symbolWidth = grapheme.symbol.cellWidth().toInt()

            // Ignore characters wider than the total max width
            if (symbolWidth > maxLineWidth) {
                continue
            }

            if (currentLineWidth + symbolWidth > maxLineWidth) {
                // Truncate line
                break
            }

            val symbol = if (localHorizontalOffset == 0 || alignment != HorizontalAlignment.Left) {
                grapheme.symbol
            } else {
                val w = symbolWidth
                if (w > localHorizontalOffset) {
                    val t = trimOffset(grapheme.symbol, localHorizontalOffset)
                    localHorizontalOffset = 0
                    t
                } else {
                    localHorizontalOffset -= w
                    ""
                }
            }

            if (symbol.isNotEmpty()) {
                currentLineWidth += symbol.cellWidth().toInt()
                currentLine.add(StyledGrapheme(symbol, grapheme.style))
            }
        }

        return WrappedLine(
            graphemes = currentLine,
            width = currentLineWidth,
            alignment = alignment
        )
    }

    companion object {
        /**
         * Create a new LineTruncator with the given lines and maximum line width.
         */
        fun new(
            lines: Iterator<Pair<Iterator<StyledGrapheme>, HorizontalAlignment>>,
            maxLineWidth: Int
        ): LineTruncator = LineTruncator(lines, maxLineWidth)
    }
}

/**
 * Trim a string from the start by the given offset (in display width).
 * Returns the remaining substring.
 */
private fun trimOffset(src: String, offset: Int): String {
    var remainingOffset = offset
    var startIndex = 0
    for (grapheme in graphemes(src)) {
        val w = grapheme.cellWidth().toInt()
        if (w <= remainingOffset) {
            remainingOffset -= w
            startIndex += grapheme.length
        } else {
            break
        }
    }
    return src.substring(startIndex)
}
