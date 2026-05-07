// port-lint: source xtask/src/commands/check.rs
package ratatui.xtask.commands

import ratatui.xtask.CROSSTERM_COMMON_FEATURES
import ratatui.xtask.CROSSTERM_VERSION_FEATURES
import ratatui.xtask.runCargo

/**
 * Run cargo check.
 *
 * Transliteration of `xtask/src/commands/check.rs`.
 */
data class Check(
    /** Check all features. */
    val allFeatures: Boolean = false
) {
    fun run() {
        if (allFeatures) {
            val commonFeatures = CROSSTERM_COMMON_FEATURES.joinToString(",")

            // Run `cargo check` on `ratatui-crossterm` with specific crossterm versions.
            for (crosstermFeature in CROSSTERM_VERSION_FEATURES) {
                val features = "$commonFeatures,$crosstermFeature"
                val command = listOf(
                    "check",
                    "--all-targets",
                    "--package",
                    "ratatui-crossterm",
                    "--no-default-features",
                    "--features",
                    features
                )
                runCargo(command)
            }

            // Run `cargo hack check` across the rest of the workspace.
            runCargo(
                listOf(
                    "hack",
                    "--exclude",
                    "ratatui-crossterm",
                    // Rust excludes ratatui-termion on Windows. We keep the command portable and
                    // allow the underlying tools to decide.
                    "check",
                    "--all-targets",
                    "--all-features"
                )
            )
        } else {
            runCargo(listOf("check", "--all-targets"))
        }
    }
}

