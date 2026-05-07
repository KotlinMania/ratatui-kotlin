// port-lint: source xtask/src/commands/docs.rs
package ratatui.xtask.commands

import ratatui.xtask.runCargoNightly

/**
 * Check documentation for errors and warnings.
 *
 * Transliteration of `xtask/src/commands/docs.rs`.
 */
data class Docs(
    /** Open the documentation in the browser. */
    val open: Boolean = false
) {
    fun run() {
        // cargo +nightly hack --all --ignore-private docs-rs
        val args = mutableListOf("hack", "--all", "--ignore-private", "docs-rs")
        if (open) {
            args.add("--open")
        }
        runCargoNightly(args)
    }
}

