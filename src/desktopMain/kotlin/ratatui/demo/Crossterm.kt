// port-lint: source examples/apps/demo/src/crossterm.rs
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

private class StdoutBuffer : Appendable {
    private val buffer = StringBuilder()

    override fun append(value: CharSequence?): StdoutBuffer {
        buffer.append(value)
        return this
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): StdoutBuffer {
        buffer.append(value, startIndex, endIndex)
        return this
    }

    override fun append(value: Char): StdoutBuffer {
        buffer.append(value)
        return this
    }

    fun flush() {
        if (buffer.isNotEmpty()) {
            print(buffer.toString())
            buffer.setLength(0)
        }
    }
}

fun runCrossterm(tickRate: Duration, enhancedGraphics: Boolean) {
    enableRawMode()
    val stdout = StdoutBuffer()
    EnterAlternateScreen.writeAnsi(stdout)
    EnableMouseCapture.writeAnsi(stdout)
    stdout.flush()

    val backend = CrosstermBackend(stdout, stdout::flush)
    val terminal = Terminal.new(backend)

    val app = App.new("Crossterm Demo", enhancedGraphics)
    val result = runApp(terminal, app, tickRate)

    disableRawMode()
    LeaveAlternateScreen.writeAnsi(stdout)
    DisableMouseCapture.writeAnsi(stdout)
    stdout.flush()
    terminal.showCursor()

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
            if (!poll(timeout)) {
                app.onTick()
                lastTick = TimeSource.Monotonic.markNow()
                continue
            }

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

            if (app.shouldQuit) {
                return null
            }
        }
    } catch (t: Throwable) {
        return t
    }
}
