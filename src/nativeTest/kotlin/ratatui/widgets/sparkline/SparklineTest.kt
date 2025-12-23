package ratatui.widgets.sparkline

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import ratatui.symbols.bar.NINE_LEVELS
import ratatui.symbols.bar.THREE_LEVELS
import ratatui.widgets.block.Block
import ratatui.widgets.red
import ratatui.widgets.onWhite
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for the Sparkline widget.
 */
class SparklineTest {

    @Test
    fun sparklineDefault() {
        val sparkline = Sparkline.default()
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithData() {
        val sparkline = Sparkline.default()
            .data(1uL, 2uL, 3uL, 4uL)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithIntData() {
        val sparkline = Sparkline.default()
            .dataInts(1, 2, 3, 4)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithNullableData() {
        val sparkline = Sparkline.default()
            .dataFromNullable(listOf(1uL, null, 3uL, null, 5uL))
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithNullableIntData() {
        val sparkline = Sparkline.default()
            .dataFromNullableInts(listOf(1, null, 3, null, 5))
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithBlock() {
        val sparkline = Sparkline.default()
            .block(Block.bordered().title("Sparkline"))
            .data(1uL, 2uL, 3uL)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithStyle() {
        val sparkline = Sparkline.default()
            .style(Style.default().fg(Color.Red))
            .data(1uL, 2uL, 3uL)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithStylize() {
        val sparkline = Sparkline.default()
            .data(1uL, 2uL, 3uL)
            .red()
            .onWhite()
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithMax() {
        val sparkline = Sparkline.default()
            .data(1uL, 2uL, 3uL)
            .max(10uL)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithBarSet() {
        val sparkline = Sparkline.default()
            .data(1uL, 2uL, 3uL)
            .barSet(THREE_LEVELS)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineWithDirection() {
        val sparkline = Sparkline.default()
            .data(1uL, 2uL, 3uL)
            .direction(RenderDirection.RightToLeft)
        assertNotNull(sparkline)
    }

    @Test
    fun sparklineRenderInMinimalBuffer() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val sparkline = Sparkline.default().data(1uL, 2uL, 3uL)
        // This should not panic, even if the buffer is too small
        sparkline.render(buffer.area, buffer)
    }

    @Test
    fun sparklineRenderInZeroSizeBuffer() {
        val buffer = Buffer.empty(Rect.ZERO)
        val sparkline = Sparkline.default().data(1uL, 2uL, 3uL)
        // This should not panic, even if the buffer has zero size
        sparkline.render(buffer.area, buffer)
    }

    @Test
    fun sparklineRenderWithAbsentValues() {
        val buffer = Buffer.empty(Rect.new(0, 0, 10, 3))
        val sparkline = Sparkline.default()
            .dataFromNullable(listOf(1uL, null, 3uL))
            .absentValueStyle(Style.default().fg(Color.Red))
        sparkline.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }
}

/**
 * Tests for RenderDirection enum.
 */
class RenderDirectionTest {

    @Test
    fun renderDirectionToString() {
        assertEquals("LeftToRight", RenderDirection.LeftToRight.toString())
        assertEquals("RightToLeft", RenderDirection.RightToLeft.toString())
    }

    @Test
    fun renderDirectionFromString() {
        assertEquals(RenderDirection.LeftToRight, RenderDirection.fromString("LeftToRight"))
        assertEquals(RenderDirection.LeftToRight, RenderDirection.fromString("left-to-right"))
        assertEquals(RenderDirection.LeftToRight, RenderDirection.fromString("left_to_right"))
        assertEquals(RenderDirection.LeftToRight, RenderDirection.fromString("ltr"))

        assertEquals(RenderDirection.RightToLeft, RenderDirection.fromString("RightToLeft"))
        assertEquals(RenderDirection.RightToLeft, RenderDirection.fromString("right-to-left"))
        assertEquals(RenderDirection.RightToLeft, RenderDirection.fromString("rtl"))
    }

    @Test
    fun renderDirectionFromStringInvalid() {
        assertNull(RenderDirection.fromString("invalid"))
        assertNull(RenderDirection.fromString(""))
    }

    @Test
    fun renderDirectionParse() {
        assertEquals(RenderDirection.LeftToRight, RenderDirection.parse("ltr"))
        assertEquals(RenderDirection.RightToLeft, RenderDirection.parse("rtl"))
    }
}

/**
 * Tests for SparklineBar.
 */
class SparklineBarTest {

    @Test
    fun sparklineBarFromULong() {
        val bar = SparklineBar.from(5uL)
        assertEquals(5uL, bar.value)
        assertNull(bar.barStyle)
    }

    @Test
    fun sparklineBarFromInt() {
        val bar = SparklineBar.from(5)
        assertEquals(5uL, bar.value)
    }

    @Test
    fun sparklineBarAbsent() {
        val bar = SparklineBar.absent()
        assertNull(bar.value)
    }

    @Test
    fun sparklineBarWithStyle() {
        val bar = SparklineBar.from(5uL)
            .style(Style.default().fg(Color.Red))
        assertEquals(5uL, bar.value)
        assertNotNull(bar.barStyle)
    }
}
