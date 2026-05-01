// port-lint: source ratatui-core/src/buffer/assert.rs
package ratatui.buffer

/**
 * Assert that two buffers are equal by comparing their areas and content.
 *
 * # Panics
 * When the buffers differ this method panics and displays the differences similar to
 * `assertEq!()`.
 */
fun assertBufferEq(actual: Buffer, expected: Buffer) {
    if (actual.area != expected.area) {
        throw AssertionError(
            "buffer areas not equal\nexpected: $expected\nactual:   $actual"
        )
    }

    val niceDiff = expected
        .diff(actual)
        .mapIndexed { i, (x, y, cell) ->
            val expectedCell = expected[x, y]
            "$i: at ($x, $y)\n  expected: $expectedCell\n  actual:   $cell"
        }
        .joinToString("\n")

    if (niceDiff.isNotEmpty()) {
        throw AssertionError(
            "buffer contents not equal\nexpected: $expected\nactual:   $actual\ndiff:\n$niceDiff"
        )
    }

    if (actual != expected) {
        throw AssertionError(
            "buffers are not equal in an unexpected way. Please open an issue about this."
        )
    }
}
