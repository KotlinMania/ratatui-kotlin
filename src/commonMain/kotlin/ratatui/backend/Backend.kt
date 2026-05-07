// port-lint: source ratatui-core/src/backend.rs
/**
 * This module provides the backend implementations for different terminal libraries.
 *
 * It defines the [Backend] interface which is used to abstract over the specific terminal library
 * being used.
 *
 * Supported terminal backends (Rust workspace):
 * - Crossterm: enable the `crossterm` feature (enabled by default) and use `CrosstermBackend`
 * - Termion: enable the `termion` feature and use `TermionBackend`
 * - Termwiz: enable the `termwiz` feature and use `TermwizBackend`
 *
 * Additionally, a [TestBackend] is provided for testing purposes.
 *
 * See the “Backend Comparison” section of the Ratatui website for more details on the different
 * backends.
 *
 * Each backend supports a number of features, such as raw mode, alternate screen, and mouse
 * capture. These features are generally not enabled by default, and must be enabled by the
 * application before they can be used. See the documentation for each backend for more details.
 *
 * Note: most applications should use [ratatui.terminal.Terminal] instead of directly calling
 * methods on the backend.
 *
 * # Example
 *
 * ```kotlin
 * // Pseudocode: choose a concrete backend and pass it to Terminal.
 * val backend: Backend = TestBackend.new(width = 80, height = 25)
 * val terminal = ratatui.terminal.Terminal(backend)
 * terminal.clear()
 * terminal.draw { frame ->
 *     // ...
 * }
 * ```
 *
 * See the examples directory in the upstream Ratatui repository for more examples.
 *
 * # Raw Mode
 *
 * Raw mode is a mode where the terminal does not perform any processing or handling of the input
 * and output. This means that features such as echoing input characters, line buffering, and
 * special character processing (e.g., CTRL-C for SIGINT) are disabled. This is useful for
 * applications that want to have complete control over the terminal input and output, processing
 * each keystroke themselves.
 *
 * For example, in raw mode, the terminal will not perform line buffering on the input, so the
 * application will receive each key press as it is typed, instead of waiting for the user to
 * press enter. This makes it suitable for real-time applications like text editors,
 * terminal-based games, and more.
 *
 * Each backend handles raw mode differently, so the behavior may vary depending on the backend
 * being used. Be sure to consult the backend's specific documentation for exact details on how it
 * implements raw mode.
 *
 * # Alternate Screen
 *
 * The alternate screen is a separate buffer that some terminals provide, distinct from the main
 * screen. When activated, the terminal will display the alternate screen, hiding the current
 * content of the main screen. Applications can write to this screen as if it were the regular
 * terminal display, but when the application exits, the terminal will switch back to the main
 * screen, and the contents of the alternate screen will be cleared. This is useful for
 * applications like text editors or terminal games that want to use the full terminal window
 * without disrupting the command line or other terminal content.
 *
 * This creates a seamless transition between the application and the regular terminal session, as
 * the content displayed before launching the application will reappear after the application
 * exits.
 *
 * Note that not all terminal emulators support the alternate screen, and even those that do may
 * handle it differently. As a result, the behavior may vary depending on the backend being used.
 * Always consult the specific backend's documentation to understand how it implements the
 * alternate screen.
 *
 * # Mouse Capture
 *
 * Mouse capture is a mode where the terminal captures mouse events such as clicks, scrolls, and
 * movement, and sends them to the application as special sequences or events. This enables the
 * application to handle and respond to mouse actions, providing a more interactive and graphical
 * user experience within the terminal. It's particularly useful for applications like
 * terminal-based games, text editors, or other programs that require more direct interaction from
 * the user.
 *
 * Each backend handles mouse capture differently, with variations in the types of events that can
 * be captured and how they are represented. As such, the behavior may vary depending on the
 * backend being used, and developers should consult the specific backend's documentation to
 * understand how it implements mouse capture.
 */
package ratatui.backend

import ratatui.buffer.BufferDiff
import ratatui.layout.Position
import ratatui.layout.Size

/**
 * Defines which region of the terminal's visible display area is cleared.
 *
 * Transliteration of `ratatui_core::backend::ClearType`.
 *
 * Clearing operates on character cells in the active display surface. It does not move, hide, or
 * reset the cursor position. If the cursor lies inside the cleared region, the character cell at
 * the cursor position is cleared as well.
 *
 * Clearing applies to the terminal's visible display area, not just content previously drawn by
 * Ratatui. No guarantees are made about scrollback, history, or off-screen buffers.
 */
enum class ClearType {
    /** Clears all character cells in the visible display area. */
    All,

    /** Clears from the cursor position (inclusive) through the end of the display area. */
    AfterCursor,

    /** Clears from the start of the display area through the cursor position (inclusive). */
    BeforeCursor,

    /** Clears all character cells in the cursor's current line. */
    CurrentLine,

    /** Clears from the cursor position (inclusive) to the end of the current line. */
    UntilNewLine;

    companion object {
        fun fromString(value: String): ClearType? {
            return when (value) {
                "All" -> All
                "AfterCursor" -> AfterCursor
                "BeforeCursor" -> BeforeCursor
                "CurrentLine" -> CurrentLine
                "UntilNewLine" -> UntilNewLine
                else -> null
            }
        }
    }
}

/**
 * The window size in characters (columns / rows) as well as pixels.
 *
 * Transliteration of `ratatui_core::backend::WindowSize`.
 */
data class WindowSize(
    /** Size of the window in characters (columns / rows). */
    val columnsRows: Size,
    /**
     * Size of the window in pixels.
     *
     * The `pixels` fields may not be implemented by all terminals and return `0,0`. See
     * <https://man7.org/linux/man-pages/man4/tty_ioctl.4.html> under section "Get and set window
     * size" / TIOCGWINSZ where the fields are commented as "unused".
     */
    val pixels: Size
)

/**
 * The `Backend` interface provides an abstraction over different terminal libraries.
 *
 * Transliteration of `ratatui_core::backend::Backend`.
 *
 * Most applications should not need to interact with [Backend] directly as the
 * [ratatui.terminal.Terminal] class provides a higher level interface for interacting with the
 * terminal.
 */
interface Backend {
    /**
     * Draw the given content to the terminal screen.
     *
     * The content is provided as an iterator over `(x, y, cell)` tuples, where the first two
     * elements represent the x and y coordinates, and the third element is the [ratatui.buffer.Cell]
     * to be drawn.
     */
    fun draw(content: Iterator<BufferDiff.Item>)

    /**
     * Insert `n` line breaks to the terminal screen.
     *
     * This method is optional and may not be implemented by all backends.
     */
    fun appendLines(n: UShort) {
        @Suppress("UNUSED_EXPRESSION")
        n
        // default: no-op
    }

    /**
     * Hide the cursor on the terminal screen.
     *
     * See also [showCursor].
     */
    fun hideCursor()

    /**
     * Show the cursor on the terminal screen.
     *
     * See [hideCursor] for an example.
     */
    fun showCursor()

    /**
     * Get the current cursor position on the terminal screen.
     *
     * The origin (0, 0) is at the top left corner of the screen.
     */
    fun getCursorPosition(): Position

    /**
     * Set the cursor position on the terminal screen to the given x and y coordinates.
     *
     * The origin (0, 0) is at the top left corner of the screen.
     */
    fun setCursorPosition(position: Position)

    /**
     * Get the current cursor position on the terminal screen.
     *
     * The returned tuple contains the x and y coordinates of the cursor. The origin
     * (0, 0) is at the top left corner of the screen.
     */
    @Deprecated("use getCursorPosition() instead which returns Position")
    fun getCursor(): Pair<UShort, UShort> {
        val pos = getCursorPosition()
        return Pair(pos.x.toUShort(), pos.y.toUShort())
    }

    /**
     * Set the cursor position on the terminal screen to the given x and y coordinates.
     *
     * The origin (0, 0) is at the top left corner of the screen.
     */
    @Deprecated("use setCursorPosition(Position(x, y)) instead which takes Position")
    fun setCursor(x: UShort, y: UShort) {
        setCursorPosition(Position(x.toInt(), y.toInt()))
    }

    /**
     * Clears all character cells in the terminal's visible display area.
     *
     * This operation preserves the cursor position.
     *
     * This is equivalent to calling [clearRegion] with [ClearType.All].
     */
    fun clear()

    /**
     * Clears a specific region of the terminal's visible display area, as defined by [ClearType].
     *
     * This operation preserves the cursor position. If the cursor lies within the cleared region,
     * the character cell at the cursor position is cleared. Clearing applies to the active display
     * surface only and does not make guarantees about scrollback, history, or off-screen buffers.
     */
    fun clearRegion(clearType: ClearType)

    /**
     * Get the size of the terminal screen in columns/rows as a [Size].
     *
     * The returned [Size] contains the width and height of the terminal screen.
     */
    fun size(): Size

    /**
     * Get the size of the terminal screen in columns/rows and pixels.
     *
     * The reason for this not returning only the pixel size, given the redundancy with [size], is
     * that the underlying backends most likely get both values with one syscall, and the user is
     * also most likely to need columns and rows along with pixel size.
     */
    fun windowSize(): WindowSize

    /** Flush any buffered content to the terminal screen. */
    fun flush()
}
