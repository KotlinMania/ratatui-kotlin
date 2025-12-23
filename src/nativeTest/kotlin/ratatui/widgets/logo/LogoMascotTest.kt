package ratatui.widgets.logo

import ratatui.buffer.Buffer
import ratatui.layout.Rect
import ratatui.widgets.mascot.MascotEyeColor
import ratatui.widgets.mascot.RatatuiMascot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for the RatatuiLogo widget.
 */
class RatatuiLogoTest {

    @Test
    fun logoDefault() {
        val logo = RatatuiLogo.default()
        assertNotNull(logo)
    }

    @Test
    fun logoTiny() {
        val logo = RatatuiLogo.tiny()
        assertNotNull(logo)
    }

    @Test
    fun logoSmall() {
        val logo = RatatuiLogo.small()
        assertNotNull(logo)
    }

    @Test
    fun logoWithSize() {
        val logo = RatatuiLogo.default()
            .size(RatatuiLogoSize.Small)
        assertNotNull(logo)
    }

    @Test
    fun logoSizeAsString() {
        assertEquals(2, RatatuiLogoSize.Tiny.asString().lines().size)
        assertEquals(2, RatatuiLogoSize.Small.asString().lines().size)
    }

    @Test
    fun logoTinyDimensions() {
        // Tiny logo is 2x15 characters
        val lines = RatatuiLogoSize.Tiny.asString().lines()
        assertEquals(2, lines.size)
        assertEquals(15, lines[0].length)
    }

    @Test
    fun logoSmallDimensions() {
        // Small logo is 2x27 characters
        val lines = RatatuiLogoSize.Small.asString().lines()
        assertEquals(2, lines.size)
        assertEquals(27, lines[0].length)
    }

    @Test
    fun logoRenderInMinimalBuffer() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val logo = RatatuiLogo.tiny()
        // This should not panic
        logo.render(buffer.area, buffer)
    }

    @Test
    fun logoRenderInZeroSizeBuffer() {
        val buffer = Buffer.empty(Rect.ZERO)
        val logo = RatatuiLogo.tiny()
        // This should not panic
        logo.render(buffer.area, buffer)
    }

    @Test
    fun logoRenderTiny() {
        val buffer = Buffer.empty(Rect.new(0, 0, 20, 3))
        val logo = RatatuiLogo.tiny()
        logo.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }

    @Test
    fun logoRenderSmall() {
        val buffer = Buffer.empty(Rect.new(0, 0, 30, 3))
        val logo = RatatuiLogo.small()
        logo.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }
}

/**
 * Tests for the RatatuiMascot widget.
 */
class RatatuiMascotTest {

    @Test
    fun mascotDefault() {
        val mascot = RatatuiMascot.default()
        assertNotNull(mascot)
    }

    @Test
    fun mascotNew() {
        val mascot = RatatuiMascot.new()
        assertNotNull(mascot)
    }

    @Test
    fun mascotWithEyeState() {
        val mascot = RatatuiMascot.default()
            .setEye(MascotEyeColor.Red)
        assertNotNull(mascot)
    }

    @Test
    fun mascotEyeColorDefault() {
        val mascot = RatatuiMascot.default()
            .setEye(MascotEyeColor.Default)
        assertNotNull(mascot)
    }

    @Test
    fun mascotRenderInMinimalBuffer() {
        val buffer = Buffer.empty(Rect.new(0, 0, 1, 1))
        val mascot = RatatuiMascot.default()
        // This should not panic
        mascot.render(buffer.area, buffer)
    }

    @Test
    fun mascotRenderInZeroSizeBuffer() {
        val buffer = Buffer.empty(Rect.ZERO)
        val mascot = RatatuiMascot.default()
        // This should not panic
        mascot.render(buffer.area, buffer)
    }

    @Test
    fun mascotRenderFull() {
        // Mascot takes 32x16 cells
        val buffer = Buffer.empty(Rect.new(0, 0, 35, 18))
        val mascot = RatatuiMascot.default()
        mascot.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }

    @Test
    fun mascotRenderWithRedEye() {
        val buffer = Buffer.empty(Rect.new(0, 0, 35, 18))
        val mascot = RatatuiMascot.default()
            .setEye(MascotEyeColor.Red)
        mascot.render(buffer.area, buffer)
        // Just verify it doesn't panic
    }
}

/**
 * Tests for MascotEyeColor enum.
 */
class MascotEyeColorTest {

    @Test
    fun mascotEyeColorDefault() {
        assertEquals(MascotEyeColor.Default, MascotEyeColor.Default)
    }

    @Test
    fun mascotEyeColorRed() {
        assertEquals(MascotEyeColor.Red, MascotEyeColor.Red)
    }

    @Test
    fun mascotEyeColorValues() {
        val values = MascotEyeColor.entries
        assertEquals(2, values.size)
    }
}
