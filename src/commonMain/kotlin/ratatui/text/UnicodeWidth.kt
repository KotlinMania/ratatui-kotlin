package ratatui.text

/**
 * Calculate the unicode display width of a string.
 *
 * This currently implements the same simplified heuristic as the earlier implementation that lived in `Span.kt`.
 * It is factored out so width calculations are consistent across the codebase (e.g. buffer diffs and spans).
 *
 * For full Unicode support, consider using a proper Unicode width implementation (e.g. ICU) across all targets.
 */
fun unicodeWidth(s: String): Int {
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
            char.code in 0x0300..0x036F -> 0  // Combining Diacritical Marks
            char.code in 0x1AB0..0x1AFF -> 0  // Combining Diacritical Marks Extended
            char.code in 0x1DC0..0x1DFF -> 0  // Combining Diacritical Marks Supplement
            char.code in 0x20D0..0x20FF -> 0  // Combining Diacritical Marks for Symbols
            char.code in 0xFE20..0xFE2F -> 0  // Combining Half Marks
            else -> 1
        }
    }
    return width
}

