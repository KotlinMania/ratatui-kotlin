package ratatui.widgets.reflow

import ratatui.layout.Alignment
import ratatui.text.StyledGrapheme

/**
 * A line that has been wrapped to a certain width.
 */
data class WrappedLine(
    /** One line reflowed to the correct width */
    val graphemes: List<StyledGrapheme>,
    /** The width of the line */
    val width: Int,
    /** The alignment of the line */
    val alignment: Alignment
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
    private val inputLines: Iterator<Pair<Iterator<StyledGrapheme>, Alignment>>,
    private val maxLineWidth: Int,
    private val trim: Boolean
) : LineComposer {

    private val wrappedLines = ArrayDeque<MutableList<StyledGrapheme>>()
    private var currentAlignment = Alignment.Left
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
            val symbolWidth = grapheme.width()

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
                    val wsWidth = ws.width()

                    if (wsWidth > remainingWidth) {
                        break
                    }

                    whitespaceWidth -= wsWidth
                    remainingWidth -= wsWidth
                    pendingWhitespace.removeFirst()
                }

                // don't count first whitespace toward next word
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
                val lineWidth = cachedLine.sumOf { it.width() }
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
            lines: Iterator<Pair<Iterator<StyledGrapheme>, Alignment>>,
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
    private val inputLines: Iterator<Pair<Iterator<StyledGrapheme>, Alignment>>,
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
            val symbolWidth = grapheme.width()

            // Ignore characters wider than the total max width
            if (symbolWidth > maxLineWidth) {
                continue
            }

            if (currentLineWidth + symbolWidth > maxLineWidth) {
                // Truncate line
                break
            }

            val symbol = if (localHorizontalOffset == 0 || alignment != Alignment.Left) {
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
                currentLineWidth += unicodeWidth(symbol)
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
            lines: Iterator<Pair<Iterator<StyledGrapheme>, Alignment>>,
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
        val w = unicodeWidth(grapheme)
        if (w <= remainingOffset) {
            remainingOffset -= w
            startIndex += grapheme.length
        } else {
            break
        }
    }
    return src.substring(startIndex)
}

/**
 * Split a string into grapheme clusters.
 * This is a simplified implementation that treats each character as a grapheme.
 * For proper grapheme cluster support, a full Unicode segmentation library would be needed.
 */
private fun graphemes(s: String): Sequence<String> = sequence {
    // Simple implementation: each char is a "grapheme"
    // For proper support, need unicode segmentation library
    val chars = s.toList()
    var i = 0
    while (i < chars.size) {
        val char = chars[i]
        // Check for combining characters and zero-width joiners
        val builder = StringBuilder()
        builder.append(char)
        i++
        // Collect any following combining characters
        while (i < chars.size) {
            val next = chars[i]
            if (isCombining(next) || next == '\u200D') {
                builder.append(next)
                i++
            } else {
                break
            }
        }
        yield(builder.toString())
    }
}

/**
 * Check if a character is a combining character.
 */
private fun isCombining(c: Char): Boolean {
    val code = c.code
    return code in 0x0300..0x036F ||  // Combining Diacritical Marks
            code in 0x1AB0..0x1AFF ||  // Combining Diacritical Marks Extended
            code in 0x1DC0..0x1DFF ||  // Combining Diacritical Marks Supplement
            code in 0x20D0..0x20FF ||  // Combining Diacritical Marks for Symbols
            code in 0xFE20..0xFE2F     // Combining Half Marks
}

/**
 * Calculate the unicode display width of a string.
 */
private fun unicodeWidth(s: String): Int {
    var width = 0
    for (char in s) {
        width += when {
            char.isISOControl() -> 0
            // CJK characters are typically 2 columns wide
            char.code in 0x4E00..0x9FFF -> 2  // CJK Unified Ideographs
            char.code in 0x3400..0x4DBF -> 2  // CJK Unified Ideographs Extension A
            char.code in 0x20000..0x2A6DF -> 2  // CJK Unified Ideographs Extension B
            char.code in 0xF900..0xFAFF -> 2  // CJK Compatibility Ideographs
            char.code in 0xFF00..0xFF60 -> 2  // Fullwidth Forms
            char.code in 0xFFE0..0xFFE6 -> 2  // Fullwidth Forms
            // Zero-width characters
            char.code == 0x200B -> 0  // Zero Width Space
            char.code == 0x200C -> 0  // Zero Width Non-Joiner
            char.code == 0x200D -> 0  // Zero Width Joiner
            char.code == 0x200E -> 0  // Left-to-Right Mark
            char.code == 0x200F -> 0  // Right-to-Left Mark
            char.code == 0xFEFF -> 0  // Zero Width No-Break Space
            // Combining characters
            isCombining(char) -> 0
            else -> 1
        }
    }
    return width
}
