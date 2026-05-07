// port-lint: source examples/apps/demo/src/termwiz.rs
package ratatui.demo

import io.github.kotlinmania.crossterm.event.DisableMouseCapture
import io.github.kotlinmania.crossterm.event.EnableMouseCapture
import io.github.kotlinmania.crossterm.event.KeyCode
import io.github.kotlinmania.crossterm.event.poll
import io.github.kotlinmania.crossterm.event.read
import io.github.kotlinmania.crossterm.terminal.EnterAlternateScreen
import io.github.kotlinmania.crossterm.terminal.LeaveAlternateScreen
import io.github.kotlinmania.crossterm.terminal.sys.disableRawMode
import io.github.kotlinmania.crossterm.terminal.sys.enableRawMode
import kotlin.time.Duration
import kotlin.time.TimeSource
import ratatui.backend.Backend
import ratatui.terminal.Terminal
import ratatui_crossterm.CrosstermBackend

/**
 * Termwiz demo runner.
 *
 * Rust uses the `termwiz` crate and polls input through the backend. This Kotlin port targets
 * Kotlin/Native and uses the published `crossterm-kotlin` dependency for input handling.
 *
 * Transliteration target: `examples/apps/demo/src/termwiz.rs`.
 */
fun runTermwiz(tickRate: Duration, enhancedGraphics: Boolean) {
    enableRawMode()
    val stdout = StdoutBuffer()
    EnterAlternateScreen.writeAnsi(stdout)
    EnableMouseCapture.writeAnsi(stdout)
    stdout.flush()

    val backend = CrosstermBackend(stdout, stdout::flush)
    val terminal = Terminal.new(backend)
    terminal.hideCursor()

    val app = App.new("Termwiz Demo", enhancedGraphics)
    val result = runApp(terminal, app, tickRate)

    disableRawMode()
    LeaveAlternateScreen.writeAnsi(stdout)
    DisableMouseCapture.writeAnsi(stdout)
    stdout.flush()
    terminal.showCursor()
    terminal.flush()

    if (result != null) {
        println(result)
    }
}

private fun <B : Backend> runApp(
    terminal: Terminal<B>,
    app: App,
    tickRate: Duration
): Throwable? {
    var lastTick = TimeSource.Monotonic.markNow()

    try {
        while (true) {
            terminal.draw { frame -> render(frame, app) }

            val timeout = (tickRate - lastTick.elapsedNow()).coerceAtLeast(Duration.ZERO)
            if (poll(timeout)) {
                val event = read()
                val key = event.asKeyPressEvent()
                if (key != null) {
                    when (val code = key.code) {
                        is KeyCode.Char -> {
                            when (val c = code.char) {
                                'h' -> app.onLeft()
                                'j' -> app.onDown()
                                'k' -> app.onUp()
                                'l' -> app.onRight()
                                else -> app.onKey(c)
                            }
                        }
                        KeyCode.Left -> app.onLeft()
                        KeyCode.Down -> app.onDown()
                        KeyCode.Up -> app.onUp()
                        KeyCode.Right -> app.onRight()
                        else -> Unit
                    }
                }
            }

            if (lastTick.elapsedNow() >= tickRate) {
                app.onTick()
                lastTick = TimeSource.Monotonic.markNow()
            }

            if (app.shouldQuit) {
                return null
            }
        }
    } catch (t: Throwable) {
        return t
    }
}
