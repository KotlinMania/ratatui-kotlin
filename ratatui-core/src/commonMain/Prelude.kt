// port-lint: source ratatui/src/prelude.rs
/**
 * A prelude for conveniently using the Ratatui library.
 *
 * The prelude module is available for convenience and backwards compatibility.
 * It re-exports commonly used types, making it easy to get started with Ratatui
 * applications without needing to import each type individually.
 *
 * Note: While convenient, importing wildcards can make it harder to distinguish
 * between library and application types. Consider explicit imports for production code.
 *
 * # Examples
 *
 * ```kotlin
 * import ratatui.prelude.*
 *
 * fun main() {
 *     val terminal = Terminal(TestBackend(80, 24))
 *     terminal.draw { frame ->
 *         val block = Block.bordered().title("Example")
 *         frame.renderWidget(block, frame.area())
 *     }
 * }
 * ```
 *
 * The prelude also includes type aliases and objects that help avoid naming
 * conflicts when writing applications:
 *
 * ```kotlin
 * import ratatui.prelude.*
 * import ratatui.widgets.*
 *
 * // Custom Line class won't conflict with ratatui.text.Line
 * data class Line(val content: String)
 *
 * val customLine = Line("my content")
 * val textLine = ratatui.text.Line.from("text content")
 * ```
 */
package ratatui.prelude

// Backend types
import ratatui.backend.Backend

// Buffer types
import ratatui.buffer.Buffer

// Layout types
import ratatui.layout.Alignment
import ratatui.layout.Constraint
import ratatui.layout.Direction
import ratatui.layout.Layout
import ratatui.layout.Margin
import ratatui.layout.Position
import ratatui.layout.Rect
import ratatui.layout.Size

// Style types
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import ratatui.style.Stylize

// Text types
import ratatui.text.Line
import ratatui.text.Masked
import ratatui.text.Span
import ratatui.text.Text

// Widget types
import ratatui.widgets.StatefulWidget
import ratatui.widgets.Widget

// Terminal types
import ratatui.terminal.Frame
import ratatui.terminal.Terminal

// Symbols
import ratatui.symbols

/**
 * Common type re-exports for convenience.
 *
 * This object provides direct access to frequently used types without
 * requiring individual imports.
 */
object Prelude {
    // Note: In Kotlin, we use object instead of re-exporting modules
    // Users can import specific types or use qualified names
}
