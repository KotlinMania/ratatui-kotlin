// port-lint: source xtask/src/commands/clippy.rs
package ratatui.xtask.commands

import ratatui.xtask.CROSSTERM_COMMON_FEATURES
import ratatui.xtask.CROSSTERM_VERSION_FEATURES
import ratatui.xtask.runCargo

/**
 * Run clippy on the project.
 *
 * Transliteration of `xtask/src/commands/clippy.rs`.
 */
data class Clippy(
    /** Fix clippy warnings. */
    val fix: Boolean = false
) {
    fun run() {
        // Arguments that go before feature flags (e.g., "clippy", "--fix").
        val clippyCommand = buildList {
            add("clippy")
            if (fix) add("--fix")
        }

        // Define common non-version-specific features for ratatui-crossterm.
        val commonFeatures = CROSSTERM_COMMON_FEATURES.joinToString(",")
        val clippyOptions = listOf("--", "-D", "warnings")

        // Run Clippy on `ratatui-crossterm` with `crossterm_0_28` and `crossterm_0_29`.
        for (crosstermFeature in CROSSTERM_VERSION_FEATURES) {
            val features = "$commonFeatures,$crosstermFeature"
            val command = buildList {
                addAll(clippyCommand)
                addAll(
                    listOf(
                        "--package",
                        "ratatui-crossterm",
                        "--no-default-features",
                        "--features",
                        features
                    )
                )
                addAll(clippyOptions)
            }
            runCargo(command)
        }

        // Run Clippy on all other workspace packages with --all-features.
        val command = buildList {
            addAll(clippyCommand)
            addAll(
                listOf(
                    "--all-features",
                    "--all-targets",
                    "--tests",
                    "--benches",
                    "--workspace",
                    "--exclude",
                    "ratatui-crossterm"
                )
            )
            addAll(clippyOptions)
        }
        runCargo(command)
    }
}

