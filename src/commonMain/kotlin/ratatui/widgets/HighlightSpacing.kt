// port-lint: source ratatui-widgets/src/table/highlight_spacing.rs
package ratatui.widgets

/**
 * This option allows the user to configure the "highlight symbol" column width spacing.
 *
 * Transliteration of `ratatui_widgets::table::highlight_spacing::HighlightSpacing`.
 */
enum class HighlightSpacing {
    /**
     * Always add spacing for the selection symbol column.
     *
     * With this variant, the column for the selection symbol will always be allocated, and so the
     * table will never change size, regardless of if a row is selected or not.
     */
    Always,

    /**
     * Only add spacing for the selection symbol column if a row is selected.
     *
     * With this variant, the column for the selection symbol will only be allocated if there is a
     * selection, causing the table to shift if selected / unselected.
     */
    WhenSelected,

    /**
     * Never add spacing to the selection symbol column, regardless of whether something is
     * selected or not.
     *
     * This means that the highlight symbol will never be drawn.
     */
    Never;

    /**
     * Determine if a selection column should be displayed.
     *
     * @param hasSelection true if a row is selected.
     * @return true if a selection column should be displayed.
     */
    internal fun shouldAdd(hasSelection: Boolean): Boolean = when (this) {
        Always -> true
        WhenSelected -> hasSelection
        Never -> false
    }

    companion object {
        /**
         * Parse a [HighlightSpacing] from its string representation.
         *
         * Mirrors Rust's `EnumString` derive for `HighlightSpacing`.
         *
         * @throws IllegalArgumentException if [s] does not match any variant.
         */
        fun fromStr(s: String): HighlightSpacing {
            return try {
                valueOf(s)
            } catch (_: IllegalArgumentException) {
                throw IllegalArgumentException("VariantNotFound")
            }
        }
    }
}

