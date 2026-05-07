// port-lint: source ratatui-macros/tests/ui/fails.rs
package ratatui_macros

import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Negative tests for macro-like helpers.
 *
 * Upstream uses `trybuild` compile-fail tests; Kotlin doesn't have an equivalent mechanism in
 * commonTest, so we assert that invalid uses throw at runtime.
 *
 * Transliteration target: `ratatui-macros/tests/ui/fails.rs`.
 */
class UiFailsTest {
    @Test
    fun spanShouldRejectExtraArgsWhenFormatHasNoPlaceholders() {
        assertFailsWith<IllegalArgumentException> {
            span(format = "hello", args = arrayOf("hello world"))
        }
    }
}
