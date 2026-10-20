// port-lint: source ratatui-core/src/backend.rs
package ratatui.backend

/**
 * Backend operations corresponding to the upstream scrolling-regions feature.
 * Regions use Kotlin inclusive ranges; use start until end for a half-open region.
 * Operation failures propagate to the caller without retrying another rendering algorithm.
 */
interface ScrollingRegionsBackend : Backend {
    /** Scroll rows upwards; a region starting at row zero contributes to scrollback. */
    fun scrollRegionUp(region: IntRange, scrollBy: UShort)

    /** Scroll rows downwards without adding rows to scrollback. */
    fun scrollRegionDown(region: IntRange, scrollBy: UShort)
}
