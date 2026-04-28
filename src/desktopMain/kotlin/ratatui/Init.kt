// port-lint: source ratatui/src/init.rs
package ratatui

import io.github.kotlinmania.crossterm.terminal.EnterAlternateScreen
import io.github.kotlinmania.crossterm.terminal.LeaveAlternateScreen
import io.github.kotlinmania.crossterm.terminal.sys.disableRawMode
import io.github.kotlinmania.crossterm.terminal.sys.enableRawMode
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.getUnhandledExceptionHook
import kotlin.native.setUnhandledExceptionHook
import ratatui.terminal.Terminal
import ratatui.terminal.TerminalOptions
import ratatui_crossterm.CrosstermBackend

/**
 * Terminal initialization and restoration functions.
 *
 * This module provides a set of convenience functions for initializing and restoring terminal
 * state when creating Ratatui applications. These functions handle the common setup and teardown
 * tasks required for terminal user interfaces.
 *
 * All functions in this module use the [CrosstermBackend] by default, which provides excellent
 * cross-platform compatibility and is the recommended backend for most applications. The
 * initialization functions return `Terminal<CrosstermBackend>` directly — the upstream
 * `pub type DefaultTerminal = Terminal<CrosstermBackend<Stdout>>` alias is inlined here rather
 * than reproduced as a Kotlin `typealias`, per the project rule against re-export shims.
 *
 * Once you have initialized a terminal using the functions in this module, you can use it to
 * draw the UI and handle events.
 *
 * # Available Types and Functions
 *
 * ## Functions
 *
 * The module provides several related functions that handle different initialization scenarios:
 *
 * - [run] - Initializes a terminal, runs a closure, and automatically restores the terminal
 *   state. This is the simplest way to run a Ratatui application and handles all setup and cleanup
 *   automatically.
 * - [init] - Creates a terminal with reasonable defaults including alternate screen and raw
 *   mode. Throws on failure.
 * - [tryInit] - Same as [init] but allows callers to handle thrown errors.
 * - [initWithOptions] - Creates a terminal with custom [TerminalOptions], enabling raw mode
 *   but not alternate screen. Throws on failure.
 * - [tryInitWithOptions] - Same as [initWithOptions] but allows callers to handle thrown errors.
 * - [restore] - Restores the terminal to its original state. Prints errors to stderr but does
 *   not throw.
 * - [tryRestore] - Same as [restore] but allows the caller to handle thrown errors.
 *
 * # Key Differences
 *
 * | Function | Alternate Screen | Raw Mode | Error Handling | Use Case |
 * |----------|------------------|----------|----------------|----------|
 * | [run] | yes | yes | Auto-cleanup | Simple apps |
 * | [init] | yes | yes | Throws | Standard full-screen apps |
 * | [tryInit] | yes | yes | Throws | Standard apps with error handling |
 * | [initWithOptions] | no | yes | Throws | Custom viewport apps |
 * | [tryInitWithOptions] | no | yes | Throws | Custom viewport with error handling |
 *
 * # Panic Hook
 *
 * On Kotlin/Native desktop targets, all initialization functions install an unhandled-exception
 * hook that automatically restores the terminal state before propagating the exception. This
 * ensures that even if your application throws, the terminal will be left in a usable state.
 */

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

/**
 * Run a closure with a terminal initialized with reasonable defaults for most applications.
 *
 * This function creates a new `Terminal<CrosstermBackend>` with [init] and then runs the given closure
 * with a mutable reference to the terminal. After the closure completes, the terminal is restored
 * to its original state with [restore].
 *
 * This function is a convenience wrapper around [init] and [restore], and is useful for simple
 * applications that need a terminal with reasonable defaults for the entire lifetime of the
 * application.
 */
fun <R> run(block: (Terminal<CrosstermBackend>) -> R): R {
    val terminal = init()
    try {
        return block(terminal)
    } finally {
        restore()
    }
}

/**
 * Initialize a terminal with reasonable defaults for most applications.
 *
 * This will create a new `Terminal<CrosstermBackend>` and initialize it with the following defaults:
 *
 * - Backend: [CrosstermBackend] writing to standard output
 * - Raw mode is enabled
 * - Alternate screen buffer enabled
 * - A panic hook is installed that restores the terminal before panicking. Ensure that this method
 *   is called after any other panic hooks that may be installed to ensure that the terminal is
 *   restored before those hooks are called.
 *
 * For more control over the terminal initialization, use [Terminal.new] or
 * [Terminal.withOptions].
 *
 * @throws IllegalStateException if any of the following steps fail: enabling raw mode, entering
 *   the alternate screen buffer, or creating the terminal.
 */
fun init(): Terminal<CrosstermBackend> {
    return tryInit()
}

/**
 * Try to initialize a terminal using reasonable defaults for most applications.
 *
 * This function will attempt to create a `Terminal<CrosstermBackend>` and initialize it with the following
 * defaults:
 *
 * - Raw mode is enabled
 * - Alternate screen buffer enabled
 * - A panic hook is installed that restores the terminal before panicking.
 * - A [Terminal] is created using [CrosstermBackend] writing to standard output
 *
 * Generally, you should use [init] instead of this function, as the panic hook installed by this
 * function will ensure that any failures during initialization will restore the terminal before
 * panicking. This function is provided for cases where you need to handle the error yourself.
 */
fun tryInit(): Terminal<CrosstermBackend> {
    setPanicHook()
    enableRawMode()

    val stdout = StdoutBuffer()
    EnterAlternateScreen.writeAnsi(stdout)
    stdout.flush()

    val backend = CrosstermBackend(stdout, stdout::flush)
    return Terminal.new(backend)
}

/**
 * Initialize a terminal with the given options and reasonable defaults.
 *
 * This function allows the caller to specify a custom viewport via the [TerminalOptions]. It
 * will create a new `Terminal<CrosstermBackend>` and initialize it with the given options and the following
 * defaults:
 *
 * - Raw mode is enabled
 * - A panic hook is installed that restores the terminal before panicking.
 *
 * Unlike [init], this function does not enter the alternate screen buffer as this may not be
 * desired in all cases. If you need the alternate screen buffer, you should enable it manually
 * after calling this function.
 *
 * @throws IllegalStateException if enabling raw mode or creating the terminal fails.
 */
fun initWithOptions(options: TerminalOptions): Terminal<CrosstermBackend> {
    return tryInitWithOptions(options)
}

/**
 * Try to initialize a terminal with the given options and reasonable defaults.
 *
 * This function allows the caller to specify a custom viewport via the [TerminalOptions]. It
 * will attempt to create a `Terminal<CrosstermBackend>` and initialize it with the given options and the
 * following defaults:
 *
 * - Raw mode is enabled
 * - A panic hook is installed that restores the terminal before panicking.
 *
 * Unlike [tryInit], this function does not enter the alternate screen buffer as this may not be
 * desired in all cases.
 */
fun tryInitWithOptions(options: TerminalOptions): Terminal<CrosstermBackend> {
    setPanicHook()
    enableRawMode()
    val stdout = StdoutBuffer()
    val backend = CrosstermBackend(stdout, stdout::flush)
    return Terminal.withOptions(backend, options)
}

/**
 * Restores the terminal to its original state.
 *
 * This function should be called before the program exits to ensure that the terminal is
 * restored to its original state.
 *
 * This function will attempt to restore the terminal to its original state by performing the
 * following steps:
 *
 * 1. Raw mode is disabled.
 * 2. The alternate screen buffer is left.
 *
 * If either of these steps fail, the error is printed to stderr and ignored.
 *
 * Use this function over [tryRestore] when you do not need to handle the error yourself.
 */
fun restore() {
    try {
        tryRestore()
    } catch (t: Throwable) {
        println("Failed to restore terminal: $t")
    }
}

/**
 * Restore the terminal to its original state.
 *
 * This function will attempt to restore the terminal to its original state by performing the
 * following steps:
 *
 * 1. Raw mode is disabled.
 * 2. The alternate screen buffer is left.
 *
 * If either of these steps fail, the error is thrown.
 */
fun tryRestore() {
    // disabling raw mode first is important as it has more side effects than leaving the alternate
    // screen buffer
    disableRawMode()
    val stdout = StdoutBuffer()
    LeaveAlternateScreen.writeAnsi(stdout)
    stdout.flush()
}

/**
 * Sets a panic hook that restores the terminal before panicking.
 *
 * Replaces the panic hook with one that will restore the terminal state before calling the
 * original handler. This ensures that the terminal is left in a good state when an exception
 * propagates out of the application main thread.
 */
@OptIn(ExperimentalNativeApi::class)
private fun setPanicHook() {
    val previous = getUnhandledExceptionHook()
    setUnhandledExceptionHook { throwable ->
        restore()
        previous?.invoke(throwable)
    }
}
