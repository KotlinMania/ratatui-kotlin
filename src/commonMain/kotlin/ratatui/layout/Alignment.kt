// port-lint: source ratatui-core/src/layout/alignment.rs
/**
 * Alignment types for horizontal and vertical content positioning.
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
package ratatui.layout

import io.github.kotlinmania.strum.fromStringOrNull

/**
 * Horizontal content alignment within a layout area.
 *
 * This type is used throughout Ratatui to control how content is positioned horizontally within
 * available space. It commonly used with widgets to control text alignment, but can also be
 * used in layout calculations.
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
enum class HorizontalAlignment {
    Left,
    Center,
    Right,
    ;

    companion object {
        /** The default alignment (Left) */
        val default: HorizontalAlignment = Left

        /** Parse an alignment from a string (mirrors upstream `strum::EnumString`) */
        fun fromString(value: String): HorizontalAlignment =
            fromStringOrNull<HorizontalAlignment>(value) ?: throw IllegalArgumentException("Unknown alignment: $value")
    }
}

/**
 * Vertical content alignment within a layout area.
 *
 * This type is used to control how content is positioned vertically within available space.
 * It complements [HorizontalAlignment] to provide full 2D positioning control.
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
enum class VerticalAlignment {
    Top,
    Center,
    Bottom,
    ;

    companion object {
        /** The default alignment (Top) */
        val default: VerticalAlignment = Top

        /** Parse an alignment from a string (mirrors upstream `strum::EnumString`) */
        fun fromString(value: String): VerticalAlignment =
            fromStringOrNull<VerticalAlignment>(value) ?: throw IllegalArgumentException("Unknown alignment: $value")
    }
}
