// port-lint: source ratatui-core/src/buffer/diff.rs
package ratatui.buffer

import ratatui.layout.Rect
import ratatui.style.Color
import ratatui.style.Style
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class BufferDiffTest {
    private fun collect(diff: BufferDiff): List<BufferDiff.Item> {
        val items = mutableListOf<BufferDiff.Item>()
        while (diff.hasNext()) {
            items.add(diff.next())
        }
        return items
    }

    @Test
    fun emptyBuffersYieldNoDiffs() {
        val rect = Rect.new(0, 0, 5, 1)
        val buf = Buffer.empty(rect)
        val diff = collect(BufferDiff.new(buf, buf))
        assertTrue(diff.isEmpty())
    }

    @Test
    fun identicalBuffersYieldNoDiffs() {
        val buf = Buffer.withLines("hello")
        val diff = collect(BufferDiff.new(buf, buf))
        assertTrue(diff.isEmpty())
    }

    @Test
    fun singleCellChange() {
        val prev = Buffer.withLines("hello")
        val next = Buffer.withLines("hallo")
        val diff = collect(BufferDiff.new(prev, next))
        assertEquals(1, diff.size)
        assertEquals(1, diff[0].x)
        assertEquals(0, diff[0].y)
        assertEquals("a", diff[0].cell.symbol())
    }

    @Test
    fun allCellsChanged() {
        val prev = Buffer.withLines("aaa")
        val next = Buffer.withLines("bbb")
        val diff = collect(BufferDiff.new(prev, next))
        assertEquals(3, diff.size)
    }

    @Test
    fun skipCellsAreSkipped() {
        val prev = Buffer.withLines("abc")
        val next = Buffer.withLines("xyz")
        next.content[1].setDiffOption(CellDiffOption.Skip)

        val diff = collect(BufferDiff.new(prev, next))
        assertEquals(2, diff.size)
        assertEquals("x", diff[0].cell.symbol())
        assertEquals("z", diff[1].cell.symbol())
    }

    @Test
    fun forcedWidthSkipsTrailing() {
        val prev = Buffer.withLines("abcd")
        val next = Buffer.withLines("xbcd")
        val width = requireNotNull(NonZeroUShort.new(2.toUShort()))
        next.content[0].setDiffOption(CellDiffOption.ForcedWidth(width))

        val diff = collect(BufferDiff.new(prev, next))
        assertEquals(1, diff.size)
        assertEquals("x", diff[0].cell.symbol())
    }

    @Test
    fun vs16TrailingCellUnchanged() {
        val rect = Rect.new(0, 0, 4, 1)

        val prev = Buffer.empty(rect)
        prev.setString(0, 0, "⌨️", Style.new())
        prev.setString(2, 0, "ab", Style.new())

        val next = Buffer.empty(rect)
        next.setString(0, 0, "⌨️", Style.new().fg(Color.Red))
        next.setString(2, 0, "ab", Style.new())

        val diff = collect(BufferDiff.new(prev, next))
        assertEquals(1, diff.size)
        assertEquals(0, diff[0].x)
        assertEquals(0, diff[0].y)
    }

    @Test
    @Suppress("DEPRECATION")
    fun deprecatedSkipFieldIsRespected() {
        val prev = Buffer.withLines("abc")
        val next = Buffer.withLines("xyz")
        next.content[1].skip = true

        val diffSymbols = collect(BufferDiff.new(prev, next)).joinToString(separator = "") { it.cell.symbol() }
        assertEquals("xz", diffSymbols)
    }

    @Test
    @Suppress("DEPRECATION")
    fun forcedWidthTakesPrecedenceOverDeprecatedSkip() {
        val prev = Buffer.withLines("abcd")
        val next = Buffer.withLines("xbcd")
        next.content[0].skip = true
        next.content[0].setDiffOption(CellDiffOption.ForcedWidth(requireNotNull(NonZeroUShort.new(2.toUShort()))))

        val diffSymbols = collect(BufferDiff.new(prev, next)).joinToString(separator = "") { it.cell.symbol() }
        assertEquals("x", diffSymbols)
    }

    @Test
    fun mismatchedWidthsPanics() {
        val prev = Buffer.empty(Rect.new(0, 0, 5, 1))
        val next = Buffer.empty(Rect.new(0, 0, 10, 1))
        assertFailsWith<IllegalStateException> {
            BufferDiff.new(prev, next)
        }
    }
}

