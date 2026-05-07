// port-lint: source xtask/src/commands/typos.rs
package ratatui.xtask.commands

import ratatui.xtask.cmd

/**
 * Check for typos in the project.
 *
 * Transliteration of `xtask/src/commands/typos.rs`.
 */
data class Typos(
    /** Fix typos. */
    val fix: Boolean = false
) {
    fun run() {
        if (fix) {
            cmd("typos", listOf("--write-changes")).runWithTrace()
        } else {
            cmd("typos").runWithTrace()
        }
    }
}

