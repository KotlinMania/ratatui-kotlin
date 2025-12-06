package ratatui.widgets.barchart

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Style
import ratatui.style.Styled
import ratatui.text.Line

/**
 * A bar to be shown by the [BarChart] widget.
 *
 * Here is an explanation of a `Bar`'s components:
 * ```
 * ███                          ┐
 * █2█  <- textValue or value   │ bar
 * foo  <- label                ┘
 * ```
 * Note that every element can be styled individually.
 *
 * # Example
 *
 * The following example creates a bar with the label "Bar 1", a value "10",
 * red background and a white value foreground.
 * ```kotlin
 * Bar.withLabel("Bar 1", 10)
 *     .style(Style.new().red())
 *     .valueStyle(Style.new().red().onWhite())
 *     .textValue("10C")
 * ```
 */
data class Bar(
    /** Value to display on the bar (computed when the data is passed to the widget) */
    internal val value: Long = 0,
    /** Optional label to be printed under the bar */
    internal val label: Line? = null,
    /** Style for the bar */
    internal val barStyle: Style = Style.default(),
    /** Style of the value printed at the bottom of the bar */
    internal val valueStyle: Style = Style.default(),
    /** Optional textValue to be shown on the bar instead of the actual value */
    internal val textValue: String? = null
) : Styled<Bar> {

    /**
     * Set the value of this bar.
     *
     * The value will be displayed inside the bar.
     */
    fun value(value: Long): Bar = copy(value = value)

    /**
     * Set the label of the bar.
     *
     * For Vertical bars, display the label **under** the bar.
     * For Horizontal bars, display the label **in** the bar.
     */
    fun label(label: String): Bar = copy(label = Line.from(label))

    /**
     * Set the label of the bar.
     */
    fun label(label: Line): Bar = copy(label = label)

    /**
     * Set the style of the bar.
     *
     * This will apply to every non-styled element. It can be seen and used as a default value.
     */
    fun style(style: Style): Bar = copy(barStyle = style)

    /**
     * Set the style of the value.
     */
    fun valueStyle(style: Style): Bar = copy(valueStyle = style)

    /**
     * Set the text value printed in the bar.
     *
     * If textValue is not set, then the string representation of value will be shown on the bar.
     */
    fun textValue(textValue: String): Bar = copy(textValue = textValue)

    /**
     * Render the value of the bar.
     *
     * [textValue] is used if set, otherwise the value is converted to string.
     * The value is rendered using valueStyle. If the value width is greater than the
     * bar width, then the value is split into 2 parts. The first part is rendered in the bar
     * using valueStyle. The second part is rendered outside the bar using barStyle.
     */
    internal fun renderValueWithDifferentStyles(
        buf: Buffer,
        area: Rect,
        barLength: Int,
        defaultValueStyle: Style,
        defaultBarStyle: Style
    ) {
        val text = textValue ?: value.toString()

        if (text.isNotEmpty()) {
            val style = defaultValueStyle.patch(valueStyle)
            // Since the value may be longer than the bar itself, we need to use 2 different styles
            // while rendering. Render the first part with the default value style
            buf.setStringn(area.x, area.y, text, barLength, style)
            // render the second part with the bar_style
            if (text.length > barLength) {
                val first = text.take(barLength)
                val second = text.drop(barLength)

                val patchedStyle = defaultBarStyle.patch(barStyle)
                buf.setStringn(
                    area.x + first.length,
                    area.y,
                    second,
                    area.width - first.length,
                    patchedStyle
                )
            }
        }
    }

    internal fun renderValue(
        buf: Buffer,
        maxWidth: Int,
        x: Int,
        y: Int,
        defaultValueStyle: Style,
        ticks: Long
    ) {
        if (value != 0L) {
            val ticksPerLine = 8L
            val valueLabel = textValue ?: value.toString()
            val width = valueLabel.length
            // if we have enough space or the ticks are greater equal than 1 cell (8)
            // then print the value
            if (width < maxWidth || (width == maxWidth && ticks >= ticksPerLine)) {
                buf.setString(
                    x + (maxWidth - valueLabel.length).coerceAtLeast(0) / 2,
                    y,
                    valueLabel,
                    defaultValueStyle.patch(valueStyle)
                )
            }
        }
    }

    internal fun renderLabel(
        buf: Buffer,
        maxWidth: Int,
        x: Int,
        y: Int,
        defaultLabelStyle: Style
    ) {
        // center the label. Necessary to do it this way as we don't want to set the style
        // of the whole area, just the label area
        val labelLine = label ?: return
        val width = labelLine.width().coerceAtMost(maxWidth)
        val area = Rect(
            x = x + (maxWidth - width).coerceAtLeast(0) / 2,
            y = y,
            width = width,
            height = 1
        )
        buf.setStyle(area, defaultLabelStyle)
        labelLine.render(area, buf)
    }

    // Styled implementation
    override fun getStyle(): Style = barStyle

    override fun setStyle(style: Style): Bar = style(style)

    companion object {
        /**
         * Creates a new Bar with the given value.
         */
        fun new(value: Long): Bar = Bar(value = value)

        /**
         * Creates a new Bar with the given label and value.
         */
        fun withLabel(label: String, value: Long): Bar = Bar(
            value = value,
            label = Line.from(label)
        )

        /**
         * Creates a new Bar with the given label and value.
         */
        fun withLabel(label: Line, value: Long): Bar = Bar(
            value = value,
            label = label
        )

        /**
         * Creates a default empty Bar.
         */
        fun default(): Bar = Bar()
    }
}
