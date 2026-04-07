package ratatui

import io.github.kotlinmania.crossterm.terminal.EnterAlternateScreen
import io.github.kotlinmania.crossterm.terminal.LeaveAlternateScreen
import io.github.kotlinmania.crossterm.terminal.sys.disableRawMode
import io.github.kotlinmania.crossterm.terminal.sys.enableRawMode
import ratatui.terminal.Terminal
import ratatui.terminal.TerminalOptions
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

fun <R> run(block: (Terminal<CrosstermBackend>) -> R): R {
    val terminal = init()
    try {
        return block(terminal)
    } finally {
        restore()
    }
}

fun init(): Terminal<CrosstermBackend> {
    return tryInit()
}

fun tryInit(): Terminal<CrosstermBackend> {
    enableRawMode()

    val stdout = StdoutBuffer()
    EnterAlternateScreen.writeAnsi(stdout)
    stdout.flush()

    val backend = CrosstermBackend(stdout, stdout::flush)
    return Terminal.new(backend)
}

fun initWithOptions(options: TerminalOptions): Terminal<CrosstermBackend> {
    return tryInitWithOptions(options)
}

fun tryInitWithOptions(options: TerminalOptions): Terminal<CrosstermBackend> {
    enableRawMode()
    val stdout = StdoutBuffer()
    val backend = CrosstermBackend(stdout, stdout::flush)
    return Terminal.withOptions(backend, options)
}

fun restore() {
    try {
        tryRestore()
    } catch (t: Throwable) {
        println("Failed to restore terminal: $t")
    }
}

fun tryRestore() {
    disableRawMode()
    val stdout = StdoutBuffer()
    LeaveAlternateScreen.writeAnsi(stdout)
    stdout.flush()
}
