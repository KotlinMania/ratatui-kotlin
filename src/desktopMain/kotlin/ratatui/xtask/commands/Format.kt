// port-lint: source xtask/src/commands/format.rs
package ratatui.xtask.commands

import platform.posix.system

/**
 * Check for formatting issues in the project.
 *
 * Transliteration of `xtask/src/commands/format.rs`.
 */
data class Format(
    /** Check formatting issues */
    val check: Boolean = false
) {
    fun run() {
        runRustfmt()
        runTaplo()
    }

    private fun runRustfmt() {
        val args = mutableListOf("fmt", "--all")
        if (check) {
            args.add("--check")
        }
        runCargoNightly(args)
    }

    private fun runTaplo() {
        val args = mutableListOf("format", "--colors", "always")
        if (check) {
            args.add("--check")
        }
        cmd("taplo", args).runWithTrace()
    }
}

private fun runCargoNightly(args: List<String>) {
    // Rust uses duct + env("RUSTUP_TOOLCHAIN", "nightly"). Here we run cargo directly; callers are
    // expected to have a working nightly toolchain available.
    cmd("cargo", args).runWithTrace()
}

private data class Expression(
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

private fun cmd(program: String, args: List<String>): Expression = Expression(program, args)

private fun shellEscape(arg: String): String {
    if (arg.isEmpty()) return "''"
    if (arg.none { it.isWhitespace() || it == '\'' || it == '"' || it == '\\' }) return arg
    val escaped = arg.replace("'", "'\"'\"'")
    return "'$escaped'"
}

