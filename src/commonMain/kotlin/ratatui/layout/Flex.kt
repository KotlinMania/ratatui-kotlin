// port-lint: source ratatui-core/src/layout/flex.rs
/**
 * Defines the options for layout flex justify content in a container.
 *
 * This enumeration controls the distribution of space when layout constraints are met and there
 * is excess space available. The [Flex] option is used with [Layout] to
 * control how extra space is distributed among layout segments, which is particularly useful for
 * creating responsive layouts that adapt to different terminal sizes.
 *
 * Available options:
 *
 * - [Legacy]: Fills the available space within the container, putting excess space into the last element.
 * - [Start]: Aligns items to the start of the container.
 * - [End]: Aligns items to the end of the container.
 * - [Center]: Centers items within the container.
 * - [SpaceBetween]: Adds excess space between each element.
 * - [SpaceAround]: Adds excess space around each element.
 *
 * For comprehensive layout documentation and examples, see the layout module.
 */
package ratatui.layout

/**
 * Defines the options for layout flex justify content in a container.
 */
enum class Flex {
    /**
     * Fills the available space within the container, putting excess space into the last
     * constraint of the lowest priority. This matches the default behavior of ratatui and tui
     * applications without [Flex].
     *
     * The following examples illustrate the allocation of excess in various combinations of
     * constraints. As a refresher, the priorities of constraints are as follows:
     *
     * 1. [Constraint.Min]
     * 2. [Constraint.Max]
     * 3. [Constraint.Length]
     * 4. [Constraint.Percentage]
     * 5. [Constraint.Ratio]
     * 6. [Constraint.Fill]
     *
     * When every constraint is `Length`, the last element gets the excess.
     *
     * ```
     * <----------------------------------- 80 px ------------------------------------>
     * ┌──────20 px───────┐┌──────20 px───────┐┌────────────────40 px─────────────────┐
     * │    Length(20)    ││    Length(20)    ││              Length(20)              │
     * └──────────────────┘└──────────────────┘└──────────────────────────────────────┘
     *                                         ^^^^^^^^^^^^^^^^ EXCESS ^^^^^^^^^^^^^^^^
     * ```
     *
     * Fill constraints have the lowest priority amongst all the constraints and hence
     * will always take up any excess space available.
     *
     * ```
     * <----------------------------------- 80 px ------------------------------------>
     * ┌──────20 px───────┐┌──────20 px───────┐┌──────20 px───────┐┌──────20 px───────┐
     * │      Fill(0)     ││      Max(20)     ││    Length(20)    ││     Length(20)   │
     * └──────────────────┘└──────────────────┘└──────────────────┘└──────────────────┘
     * ^^^^^^ EXCESS ^^^^^^
     * ```
     */
    Legacy,

    /**
     * Aligns items to the start of the container.
     *
     * ```
     * <------------------------------------80 px------------------------------------->
     * ┌────16 px─────┐┌──────20 px───────┐┌──────20 px───────┐
     * │Percentage(20)││    Length(20)    ││     Fixed(20)    │
     * └──────────────┘└──────────────────┘└──────────────────┘
     *
     * <------------------------------------80 px------------------------------------->
     * ┌──────20 px───────┐┌──────20 px───────┐
     * │      Max(20)     ││      Max(20)     │
     * └──────────────────┘└──────────────────┘
     * ```
     */
    Start,

    /**
     * Aligns items to the end of the container.
     *
     * ```
     * <------------------------------------80 px------------------------------------->
     *                         ┌────16 px─────┐┌──────20 px───────┐┌──────20 px───────┐
     *                         │Percentage(20)││    Length(20)    ││     Length(20)   │
     *                         └──────────────┘└──────────────────┘└──────────────────┘
     * ```
     */
    End,

    /**
     * Centers items within the container.
     *
     * ```
     * <------------------------------------80 px------------------------------------->
     *             ┌────16 px─────┐┌──────20 px───────┐┌──────20 px───────┐
     *             │Percentage(20)││    Length(20)    ││     Length(20)   │
     *             └──────────────┘└──────────────────┘└──────────────────┘
     * ```
     */
    Center,

    /**
     * Adds excess space between each element.
     *
     * ```
     * <------------------------------------80 px------------------------------------->
     * ┌────16 px─────┐            ┌──────20 px───────┐            ┌──────20 px───────┐
     * │Percentage(20)│            │    Length(20)    │            │     Length(20)   │
     * └──────────────┘            └──────────────────┘            └──────────────────┘
     * ```
     */
    SpaceBetween,

    /**
     * Evenly distributes excess space between all elements, including before the first and after
     * the last.
     *
     * ```
     * <------------------------------------80 px------------------------------------->
     *       ┌────16 px─────┐      ┌──────20 px───────┐      ┌──────20 px───────┐
     *       │Percentage(20)│      │    Length(20)    │      │     Length(20)   │
     *       └──────────────┘      └──────────────────┘      └──────────────────┘
     * ```
     */
    SpaceEvenly,

    /**
     * Adds excess space around each element.
     *
     * ```
     * <------------------------------------80 px------------------------------------->
     *     ┌────16 px─────┐       ┌──────20 px───────┐       ┌──────20 px───────┐
     *     │Percentage(20)│       │    Length(20)    │       │     Length(20)   │
     *     └──────────────┘       └──────────────────┘       └──────────────────┘
     * ```
     */
    SpaceAround;

    companion object {
        /** The default flex mode (Start) */
        val default: Flex = Start
    }

    /** Check if this is [Legacy] */
    fun isLegacy(): Boolean = this == Legacy

    /** Check if this is [Start] */
    fun isStart(): Boolean = this == Start

    /** Check if this is [End] */
    fun isEnd(): Boolean = this == End

    /** Check if this is [Center] */
    fun isCenter(): Boolean = this == Center

    /** Check if this is [SpaceBetween] */
    fun isSpaceBetween(): Boolean = this == SpaceBetween

    /** Check if this is [SpaceEvenly] */
    fun isSpaceEvenly(): Boolean = this == SpaceEvenly

    /** Check if this is [SpaceAround] */
    fun isSpaceAround(): Boolean = this == SpaceAround
}
