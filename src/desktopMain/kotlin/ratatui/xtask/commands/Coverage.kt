// port-lint: source xtask/src/commands/coverage.rs
package ratatui.xtask.commands

import ratatui.xtask.runCargo

/**
 * Generate code coverage report.
 *
 * Transliteration of `xtask/src/commands/coverage.rs`.
 */
data class Coverage(
    /** Only generate coverage for unit tests. */
    val lib: Boolean = false
) {
    fun run() {
        run {
            val args = mutableListOf(
                "llvm-cov",
                "--workspace",
                "--exclude",
                "ratatui-crossterm",
                "--all-features",
                "--no-report"
            )
            if (lib) args.add("--lib")
            runCargo(args)
        }

        run {
            val args = mutableListOf("llvm-cov", "--package", "ratatui-crossterm", "--no-report")
            if (lib) args.add("--lib")
            runCargo(args)
        }

        runCargo(listOf("llvm-cov", "report", "--lcov", "--output-path", "target/lcov.info"))
    }
}

