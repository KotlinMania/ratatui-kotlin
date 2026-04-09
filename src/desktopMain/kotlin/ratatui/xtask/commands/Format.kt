// port-lint: source xtask/src/commands/format.rs
package ratatui.xtask.commands

import ratatui.xtask.cmd
import ratatui.xtask.runCargoNightly

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
