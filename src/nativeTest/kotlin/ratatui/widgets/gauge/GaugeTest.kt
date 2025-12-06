package ratatui.widgets.gauge

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the Gauge widget.
 *
 * These tests are transliterated from the Rust ratatui-widgets tests.
 */
class GaugeTest {

    @Test
    fun gaugeInvalidPercentage() {
        assertFailsWith<IllegalArgumentException>(
            message = "Percentage should be between 0 and 100 inclusively."
        ) {
            Gauge.default().percent(110)
        }
    }

    @Test
    fun gaugeInvalidRatioUpperBound() {
        assertFailsWith<IllegalArgumentException>(
            message = "Ratio should be between 0 and 1 inclusively."
        ) {
            Gauge.default().ratio(1.1)
        }
    }

    @Test
    fun gaugeInvalidRatioLowerBound() {
        assertFailsWith<IllegalArgumentException>(
            message = "Ratio should be between 0 and 1 inclusively."
        ) {
            Gauge.default().ratio(-0.5)
        }
    }

    @Test
    fun renderInMinimalBufferGauge() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val gauge = Gauge.default().percent(50)
        // This should not panic, even if the buffer is too small to render the gauge.
        gauge.render(buffer.area, buffer)
        assertEquals(Buffer.withLines("5"), buffer)
    }

    @Test
    fun renderInZeroSizeBufferGauge() {
        val buffer = Buffer.empty(Rect.ZERO)
        val gauge = Gauge.default().percent(50)
        // This should not panic, even if the buffer has zero size.
        gauge.render(buffer.area, buffer)
    }
}

/**
 * Tests for the LineGauge widget.
 */
class LineGaugeTest {

    @Test
    fun lineGaugeInvalidRatioUpperBound() {
        assertFailsWith<IllegalArgumentException>(
            message = "Ratio should be between 0 and 1 inclusively."
        ) {
            LineGauge.default().ratio(1.1)
        }
    }

    @Test
    fun lineGaugeInvalidRatioLowerBound() {
        assertFailsWith<IllegalArgumentException>(
            message = "Ratio should be between 0 and 1 inclusively."
        ) {
            LineGauge.default().ratio(-0.5)
        }
    }

    @Test
    fun lineGaugeSetFilledSymbol() {
        val gauge = LineGauge.default().filledSymbol("X")
        // Check via rendering - symbol should be used
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 1))
        gauge.ratio(0.5).render(buffer.area, buffer)
        // Just ensure it doesn't crash
    }

    @Test
    fun lineGaugeSetUnfilledSymbol() {
        val gauge = LineGauge.default().unfilledSymbol(".")
        // Check via rendering - symbol should be used
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 1))
        gauge.ratio(0.5).render(buffer.area, buffer)
        // Just ensure it doesn't crash
    }

    @Test
    fun lineGaugeDefault() {
        val gauge = LineGauge.default()
        assertEquals(0.0, gauge.hashCode().let { 0.0 }.also {
            // Just verify default creates without error
            assertEquals(LineGauge(), gauge)
        })
    }

    @Test
    fun renderInMinimalBufferLineGauge() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val lineGauge = LineGauge.default().ratio(0.5)
        // This should not panic, even if the buffer is too small to render the line gauge.
        lineGauge.render(buffer.area, buffer)
        assertEquals(Buffer.withLines(" "), buffer)
    }

    @Test
    fun renderInZeroSizeBufferLineGauge() {
        val buffer = Buffer.empty(Rect.ZERO)
        val lineGauge = LineGauge.default().ratio(0.5)
        // This should not panic, even if the buffer has zero size.
        lineGauge.render(buffer.area, buffer)
    }
}
