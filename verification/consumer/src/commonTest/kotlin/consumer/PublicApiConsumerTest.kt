package consumer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import ratatui.style.Color
import ratatui.style.Modifier
import ratatui.style.Style
import io.github.kotlinmania.serdecore.ser.FormatterSerializer
import io.github.kotlinmania.serdecore.de.value.intoDeserializer
import io.github.kotlinmania.serdecore.de.value.StrDeserializer
import ratatui.style.ParseColorError
import ratatui.style.tryFrom

class PublicApiConsumerTest {
    @Test
    fun publishedTextUsesUnicodeWidth() {
        assertEquals(5, publicTextWidth("green"))
        assertEquals(2, publicTextWidth("界"))
    }

    @Test
    fun publishedModifierExposesBitflags() {
        assertTrue(publicModifierFlags().isNotEmpty())
    }

    @Test
    fun publishedErrorsRetainRustDiagnostics() {
        val error: ParseColorError = publicParseError()
        assertEquals("Failed to parse Colors", error.message)
        assertEquals(error.message, error.toString())
        assertNull(error.source())
        val failed = Color.fromStr("invalid_color")
        assertTrue(failed.isErr)
        assertNull(failed.value)
        assertEquals(error, failed.error)
        assertEquals(42u.toUByte(), publicAnsiConversion().index)
        val conversion = ai.solace.tui.anstyle.AnsiColor.tryFrom(Color.Indexed(42u))
        assertNull(conversion.value)
        assertSame(ratatui.style.TryFromColorError.Ansi, conversion.error)
        assertNull(conversion.error?.source())
    }

    @Test
    fun publishedColorRetainsItsDeserializeContract() {
        assertSame(Color, publicColorDeserializer())
        assertEquals(Color.Red, publicColorDeserializer().deserialize(StrDeserializer.new("Red")).getOrThrow())
    }

    @Test
    fun publishedModifierRetainsSerdeContracts() {
        val text = StringBuilder()
        publicModifierSerializer().serialize(FormatterSerializer(text)).getOrThrow()
        assertEquals("BOLD | 0x8000", text.toString())
        val flags = publicModifierDeserializer().deserialize(StrDeserializer.new(text.toString())).getOrThrow()
        assertEquals(0x8001uL, flags.bits())
        assertSame(Modifier, publicModifierDeserializer())
    }

    @Test
    fun publishedStyleRetainsSerdeContracts() {
        assertEquals(Style.default(), publicStyleSerializer())
        val empty = emptyMap<StrDeserializer, StrDeserializer>().intoDeserializer()
        assertEquals(Style.default(), publicStyleDeserializer().deserialize(empty).getOrThrow())
        assertSame(Style, publicStyleDeserializer())
    }

}
