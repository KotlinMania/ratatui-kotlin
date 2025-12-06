package ratatui.symbols.scrollbar

import ratatui.symbols.block.Block
import ratatui.symbols.line.Line

/**
 * Scrollbar symbol set.
 *
 * ```
 * <--▮------->
 * ^  ^   ^   ^
 * │  │   │   └ end
 * │  │   └──── track
 * │  └──────── thumb
 * └─────────── begin
 * ```
 */
data class Set(
    val track: String,
    val thumb: String,
    val begin: String,
    val end: String
) {
    companion object {
        /**
         * Default scrollbar set with vertical orientation.
         */
        fun default(): Set = VERTICAL
    }
}

/** Double-line vertical scrollbar */
val DOUBLE_VERTICAL = Set(
    track = Line.DOUBLE_VERTICAL,
    thumb = Block.FULL,
    begin = "▲",
    end = "▼"
)

/** Double-line horizontal scrollbar */
val DOUBLE_HORIZONTAL = Set(
    track = Line.DOUBLE_HORIZONTAL,
    thumb = Block.FULL,
    begin = "◄",
    end = "►"
)

/** Single-line vertical scrollbar */
val VERTICAL = Set(
    track = Line.VERTICAL,
    thumb = Block.FULL,
    begin = "↑",
    end = "↓"
)

/** Single-line horizontal scrollbar */
val HORIZONTAL = Set(
    track = Line.HORIZONTAL,
    thumb = Block.FULL,
    begin = "←",
    end = "→"
)
