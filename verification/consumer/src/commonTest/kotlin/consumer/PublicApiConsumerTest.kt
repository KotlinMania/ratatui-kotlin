package consumer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import ratatui.style.ColorSerializer
import ratatui.style.ParseColorError

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
    fun publishedErrorRetainsItsStandardErrorType() {
        assertIs<ParseColorError>(publicParseError())
    }

    @Test
    fun publishedSerializerRetainsItsPublicInterface() {
        assertSame(ColorSerializer, publicColorSerializer())
    }
}
