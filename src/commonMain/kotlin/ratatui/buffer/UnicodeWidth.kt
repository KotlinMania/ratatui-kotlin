package ratatui.buffer

import ratatui.text.graphemes

/**
 * Kotlin implementation of terminal cell-width measurement.
 *
 * This exists to replace Rust's `unicode_width::UnicodeWidthStr::width()` on Kotlin/Native.
 */
internal fun String.width(): Int {
    if (isEmpty()) return 0

    var width = 0
    for (grapheme in graphemes(this)) {
        width += graphemeCellWidth(grapheme)
    }
    return width
}

private fun graphemeCellWidth(grapheme: String): Int {
    // Treat emoji ZWJ sequences as a single grapheme with width 2.
    if (grapheme.indexOf('\u200D') >= 0) return 2

    var width = 0
    var i = 0
    while (i < grapheme.length) {
        val ch = grapheme[i]
        val codePoint =
            if (ch.isHighSurrogate() && i + 1 < grapheme.length && grapheme[i + 1].isLowSurrogate()) {
                val high = ch.code - 0xD800
                val low = grapheme[i + 1].code - 0xDC00
                i += 2
                0x10000 + ((high shl 10) or low)
            } else {
                i += 1
                ch.code
            }

        width += when {
            codePoint < 0x20 || codePoint in 0x7F..0x9F -> 0
            isZeroWidth(codePoint) -> 0
            isWide(codePoint) -> 2
            else -> 1
        }
    }

    // VS16 (emoji presentation selector) promotes many otherwise-narrow symbols to emoji width.
    if (grapheme.indexOf('\uFE0F') >= 0 && width == 1) {
        width = 2
    }

    return width
}

private fun isZeroWidth(codePoint: Int): Boolean {
    return when {
        // Explicit zero-width codepoints
        codePoint == 0x200B -> true // ZERO WIDTH SPACE
        codePoint == 0x200C -> true // ZERO WIDTH NON-JOINER
        codePoint == 0x200D -> true // ZERO WIDTH JOINER
        codePoint == 0x200E -> true // LEFT-TO-RIGHT MARK
        codePoint == 0x200F -> true // RIGHT-TO-LEFT MARK
        codePoint == 0xFEFF -> true // ZERO WIDTH NO-BREAK SPACE

        // Combining marks
        codePoint in 0x0300..0x036F -> true // Combining Diacritical Marks
        codePoint in 0x1AB0..0x1AFF -> true // Combining Diacritical Marks Extended
        codePoint in 0x1DC0..0x1DFF -> true // Combining Diacritical Marks Supplement
        codePoint in 0x20D0..0x20FF -> true // Combining Diacritical Marks for Symbols
        codePoint in 0xFE20..0xFE2F -> true // Combining Half Marks

        // Variation selectors (including VS16 emoji presentation).
        codePoint in 0xFE00..0xFE0F -> true
        codePoint in 0xE0100..0xE01EF -> true

        // Emoji modifiers (Fitzpatrick skin tones).
        codePoint in 0x1F3FB..0x1F3FF -> true
        else -> false
    }
}

private fun isWide(codePoint: Int): Boolean {
    return when {
        // Hangul Jamo init. consonants
        codePoint in 0x1100..0x115F -> true
        // CJK / Japanese / Korean blocks (includes Hiragana/Katakana)
        codePoint in 0x2E80..0xA4CF -> true
        // Hangul syllables
        codePoint in 0xAC00..0xD7A3 -> true
        // CJK compatibility ideographs
        codePoint in 0xF900..0xFAFF -> true
        // Fullwidth forms
        codePoint in 0xFF00..0xFF60 -> true
        codePoint in 0xFFE0..0xFFE6 -> true
        // Common emoji/pictographs ranges (many terminals render these as double-width)
        codePoint in 0x1F300..0x1F64F -> true
        codePoint in 0x1F900..0x1F9FF -> true
        // CJK Unified Ideographs Extensions (non-BMP)
        codePoint in 0x20000..0x2FFFD -> true
        codePoint in 0x30000..0x3FFFD -> true
        else -> false
    }
}

