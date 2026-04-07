// port-lint: source ratatui-core/src/terminal/cursor.rs
package ratatui.terminal

import ratatui.backend.Backend
import ratatui.layout.Position

/**
 * Cursor control APIs on [Terminal].
 *
 * Transliteration of `ratatui-core/src/terminal/cursor.rs`.
 */

/**
 * Gets the current cursor position.
 *
 * This is the position of the cursor after the last draw call and is returned as a tuple of
 * `(x, y)` coordinates.
 */
@Deprecated("use getCursorPosition() instead which returns Position")
fun <B : Backend> Terminal<B>.getCursor(): Pair<UShort, UShort> {
    val (x, y) = getCursorPosition()
    return Pair(x.toUShort(), y.toUShort())
}

/**
 * Sets the cursor position.
 */
@Deprecated("use setCursorPosition((x, y)) instead which takes Position")
fun <B : Backend> Terminal<B>.setCursor(x: UShort, y: UShort) {
    setCursorPosition(Position(x.toInt(), y.toInt()))
}

