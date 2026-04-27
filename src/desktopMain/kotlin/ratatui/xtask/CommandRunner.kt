package ratatui.xtask

import platform.posix.system

/**
 * Shared command execution helpers for the `xtask` transliterations.
 *
 * The upstream Rust project uses `duct` and structured error handling. This Kotlin port keeps the
 * same high-level behavior (log the command, run it, fail on non-zero exit status) using
 * `platform.posix.system`.
 */
internal data class Expression(
    val program: String,
    val args: List<String>
) {
    fun runWithTrace() {
        val command = (listOf(program) + args).joinToString(" ") { shellEscape(it) }
        println("running command: $command")
        val code = system(command)
        if (code != 0) {
            println("failed to run command: $command")
            error("command failed with exit code $code: $command")
        }
    }
}

internal fun cmd(program: String, args: List<String> = emptyList()): Expression = Expression(program, args)

internal fun runCargo(args: List<String>) {
    cmd("cargo", args).runWithTrace()
}

internal fun runCargoNightly(args: List<String>) {
    // Rust uses `envRemove("CARGO")` and sets `RUSTUP_TOOLCHAIN=nightly`.
    // Here we invoke through `env` to scope the variable to this command.
    cmd("env", listOf("RUSTUP_TOOLCHAIN=nightly", "cargo") + args).runWithTrace()
}

internal fun shellEscape(arg: String): String {
    if (arg.isEmpty()) return "''"
    if (arg.none { it.isWhitespace() || it == '\'' || it == '"' || it == '\\' }) return arg
    val escaped = arg.replace("'", "'\"'\"'")
    return "'$escaped'"
}

/**
 * The available feature flags for ratatui-crossterm.
 *
 * Transliteration of Rust `xtask/src/main.rs` `CROSSTERM_COMMON_FEATURES`.
 */
internal val CROSSTERM_COMMON_FEATURES: List<String> = listOf(
    "serde",
    "underline-color",
    "scrolling-regions",
    "unstable",
    "unstable-backend-writer"
)

/**
 * The available feature flags for crossterm versions.
 *
 * Transliteration of Rust `xtask/src/main.rs` `CROSSTERM_VERSION_FEATURES`.
 */
internal val CROSSTERM_VERSION_FEATURES: List<String> = listOf("crossterm_0_28", "crossterm_0_29")

