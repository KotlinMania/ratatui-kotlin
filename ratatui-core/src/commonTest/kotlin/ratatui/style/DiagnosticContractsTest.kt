package ratatui.style

import ai.solace.tui.anstyle.Ansi256Color
import ai.solace.tui.anstyle.AnsiColor
import ai.solace.tui.anstyle.RgbColor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DiagnosticContractsTest {
    @Test
    fun parseColorErrorDisplayAndSource() {
        val error = ParseColorError()
        assertEquals("Failed to parse Colors", error.toString())
        assertEquals(error.toString(), error.message)
        assertNull(error.source())
        assertEquals(ParseColorError(), error)
        assertEquals(ParseColorError().hashCode(), error.hashCode())
    }

    @Test
    fun parsingReturnsTheOriginalDiagnostic() {
        val error = ParseColorError()
        val result = ColorParseResult.err(error)
        assertTrue(result.isErr)
        assertNull(result.value)
        assertSame(error, result.error)
        val thrown = assertFailsWith<ColorParseException> { result.unwrap() }
        assertSame(error, thrown.error)
        assertEquals(error.message, thrown.message)
    }

    @Test
    fun conversionErrorsDisplayAndSource() {
        val expected = listOf(
            TryFromColorError.Ansi256 to "cannot convert Ratatui Color to an Ansi256Color as it is not an indexed color",
            TryFromColorError.Ansi to "cannot convert Ratatui Color to AnsiColor as it is not a 4-bit color",
            TryFromColorError.RgbColor to "cannot convert Ratatui Color to RgbColor as it is not an RGB color",
        )
        for ((error, message) in expected) {
            assertEquals(message, error.message)
            assertEquals(message, error.toString())
            assertNull(error.source())
        }
    }

    @Test
    fun conversionResultsRetainOriginalDiagnostics() {
        val ansi256 = Ansi256Color.tryFrom(Color.Rgb(0u, 0u, 0u))
        val ansi = AnsiColor.tryFrom(Color.Rgb(0u, 0u, 0u))
        val rgb = RgbColor.tryFrom(Color.Indexed(42u))
        assertNull(ansi256.value)
        assertSame(TryFromColorError.Ansi256, ansi256.error)
        assertNull(ansi.value)
        assertSame(TryFromColorError.Ansi, ansi.error)
        assertNull(rgb.value)
        assertSame(TryFromColorError.RgbColor, rgb.error)
        val thrown = assertFailsWith<TryFromColorException> { ansi256.unwrap() }
        assertSame(ansi256.error, thrown.error)
    }

    @Test
    fun successfulResultsContainOnlyTheExpectedValue() {
        val color = Color.Rgb(255u, 0u, 0u)
        val parsed = ColorParseResult.ok(color)
        assertTrue(parsed.isOk)
        assertSame(color, parsed.value)
        assertNull(parsed.error)
        assertSame(color, parsed.unwrap())
        val rgb = RgbColor.tryFrom(color)
        assertTrue(rgb.isOk)
        assertEquals(RgbColor(255u, 0u, 0u), rgb.value)
        assertNull(rgb.error)
        val indexed = Ansi256Color.tryFrom(Color.Indexed(42u))
        assertEquals(Ansi256Color(42u), indexed.value)
        assertNull(indexed.error)
    }
}
