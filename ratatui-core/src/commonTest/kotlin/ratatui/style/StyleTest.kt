// port-lint: tests ratatui-core/src/style.rs
package ratatui.style

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.de.value.StrDeserializer
import io.github.kotlinmania.serdecore.de.value.U16Deserializer
import io.github.kotlinmania.serdecore.de.value.U64Deserializer
import io.github.kotlinmania.serdecore.ser.FormatterSerializer
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.SerializeStruct
import io.github.kotlinmania.serdecore.ser.Serializer
import io.github.kotlinmania.serdejson.fromStr
import io.github.kotlinmania.serdejson.toStr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class StyleTest {
    @Test
    fun debug() {
        assertEquals("Style.new()", Style.new().toString())
        assertEquals("Style.new()", Style.default().toString())
        assertEquals("Style.new().red()", Style.new().red().toString())
        assertEquals("Style.new().onBlue()", Style.new().onBlue().toString())
        assertEquals("Style.new().bold()", Style.new().bold().toString())
        assertEquals("Style.new().notItalic()", Style.new().notItalic().toString())
        assertEquals(
            "Style.new().red().onBlue().bold().italic().notDim().notHidden()",
            Style.new().red().onBlue().bold().italic().notDim().notHidden().toString()
        )
    }

    @Test
    fun combinedPatchGivesSameResultAsIndividualPatch() {
        val styles = listOf(
            Style.new(),
            Style.new().fg(Color.Yellow),
            Style.new().bg(Color.Yellow),
            Style.new().addModifier(Modifier.BOLD),
            Style.new().removeModifier(Modifier.BOLD),
            Style.new().addModifier(Modifier.ITALIC),
            Style.new().removeModifier(Modifier.ITALIC),
            Style.new().addModifier(Modifier.ITALIC or Modifier.BOLD),
            Style.new().removeModifier(Modifier.ITALIC or Modifier.BOLD),
        )

        for (a in styles) {
            for (b in styles) {
                for (c in styles) {
                    for (d in styles) {
                        assertEquals(
                            Style.new().patch(a).patch(b).patch(c).patch(d),
                            Style.new().patch(a.patch(b.patch(c.patch(d))))
                        )
                    }
                }
            }
        }
    }

    @Test
    fun modifierDebug() {
        assertEquals("NONE", Modifier.empty().toString())
        assertEquals("BOLD", Modifier.BOLD.toString())
        assertEquals("DIM", Modifier.DIM.toString())
        assertEquals("ITALIC", Modifier.ITALIC.toString())
        assertEquals("UNDERLINED", Modifier.UNDERLINED.toString())
        assertEquals("SLOW_BLINK", Modifier.SLOW_BLINK.toString())
        assertEquals("RAPID_BLINK", Modifier.RAPID_BLINK.toString())
        assertEquals("REVERSED", Modifier.REVERSED.toString())
        assertEquals("HIDDEN", Modifier.HIDDEN.toString())
        assertEquals("CROSSED_OUT", Modifier.CROSSED_OUT.toString())
        assertEquals("BOLD | DIM", (Modifier.BOLD or Modifier.DIM).toString())
        assertEquals(
            "BOLD | DIM | ITALIC | UNDERLINED | SLOW_BLINK | RAPID_BLINK | REVERSED | HIDDEN | CROSSED_OUT",
            Modifier.all().toString()
        )
    }

    @Test
    fun hasModifierChecks() {
        val style = Style.default().addModifier(Modifier.BOLD or Modifier.ITALIC)
        assertTrue(style.hasModifier(Modifier.BOLD))
        assertTrue(style.hasModifier(Modifier.ITALIC))
        assertFalse(style.hasModifier(Modifier.UNDERLINED))

        val removed = Style.default().addModifier(Modifier.BOLD).removeModifier(Modifier.BOLD)
        assertFalse(removed.hasModifier(Modifier.BOLD))
    }

    @Test
    fun fgCanBeStylized() {
        assertEquals(Style.new().fg(Color.Black), Style.new().black())
        assertEquals(Style.new().fg(Color.Red), Style.new().red())
        assertEquals(Style.new().fg(Color.Green), Style.new().green())
        assertEquals(Style.new().fg(Color.Yellow), Style.new().yellow())
        assertEquals(Style.new().fg(Color.Blue), Style.new().blue())
        assertEquals(Style.new().fg(Color.Magenta), Style.new().magenta())
        assertEquals(Style.new().fg(Color.Cyan), Style.new().cyan())
        assertEquals(Style.new().fg(Color.White), Style.new().white())
        assertEquals(Style.new().fg(Color.Gray), Style.new().gray())
        assertEquals(Style.new().fg(Color.DarkGray), Style.new().darkGray())
        assertEquals(Style.new().fg(Color.LightRed), Style.new().lightRed())
        assertEquals(Style.new().fg(Color.LightGreen), Style.new().lightGreen())
        assertEquals(Style.new().fg(Color.LightYellow), Style.new().lightYellow())
        assertEquals(Style.new().fg(Color.LightBlue), Style.new().lightBlue())
        assertEquals(Style.new().fg(Color.LightMagenta), Style.new().lightMagenta())
        assertEquals(Style.new().fg(Color.LightCyan), Style.new().lightCyan())
    }

    @Test
    fun bgCanBeStylized() {
        assertEquals(Style.new().bg(Color.Black), Style.new().onBlack())
        assertEquals(Style.new().bg(Color.Red), Style.new().onRed())
        assertEquals(Style.new().bg(Color.Green), Style.new().onGreen())
        assertEquals(Style.new().bg(Color.Yellow), Style.new().onYellow())
        assertEquals(Style.new().bg(Color.Blue), Style.new().onBlue())
        assertEquals(Style.new().bg(Color.Magenta), Style.new().onMagenta())
        assertEquals(Style.new().bg(Color.Cyan), Style.new().onCyan())
        assertEquals(Style.new().bg(Color.White), Style.new().onWhite())
        assertEquals(Style.new().bg(Color.Gray), Style.new().onGray())
        assertEquals(Style.new().bg(Color.DarkGray), Style.new().onDarkGray())
        assertEquals(Style.new().bg(Color.LightRed), Style.new().onLightRed())
        assertEquals(Style.new().bg(Color.LightGreen), Style.new().onLightGreen())
        assertEquals(Style.new().bg(Color.LightYellow), Style.new().onLightYellow())
        assertEquals(Style.new().bg(Color.LightBlue), Style.new().onLightBlue())
        assertEquals(Style.new().bg(Color.LightMagenta), Style.new().onLightMagenta())
        assertEquals(Style.new().bg(Color.LightCyan), Style.new().onLightCyan())
    }

    @Test
    fun addModifierCanBeStylized() {
        assertEquals(Style.new().addModifier(Modifier.BOLD), Style.new().bold())
        assertEquals(Style.new().addModifier(Modifier.DIM), Style.new().dim())
        assertEquals(Style.new().addModifier(Modifier.ITALIC), Style.new().italic())
        assertEquals(Style.new().addModifier(Modifier.UNDERLINED), Style.new().underlined())
        assertEquals(Style.new().addModifier(Modifier.SLOW_BLINK), Style.new().slowBlink())
        assertEquals(Style.new().addModifier(Modifier.RAPID_BLINK), Style.new().rapidBlink())
        assertEquals(Style.new().addModifier(Modifier.REVERSED), Style.new().reversed())
        assertEquals(Style.new().addModifier(Modifier.HIDDEN), Style.new().hidden())
        assertEquals(Style.new().addModifier(Modifier.CROSSED_OUT), Style.new().crossedOut())
    }

    @Test
    fun removeModifierCanBeStylized() {
        assertEquals(Style.new().removeModifier(Modifier.BOLD), Style.new().notBold())
        assertEquals(Style.new().removeModifier(Modifier.DIM), Style.new().notDim())
        assertEquals(Style.new().removeModifier(Modifier.ITALIC), Style.new().notItalic())
        assertEquals(Style.new().removeModifier(Modifier.UNDERLINED), Style.new().notUnderlined())
        assertEquals(Style.new().removeModifier(Modifier.SLOW_BLINK), Style.new().notSlowBlink())
        assertEquals(Style.new().removeModifier(Modifier.RAPID_BLINK), Style.new().notRapidBlink())
        assertEquals(Style.new().removeModifier(Modifier.REVERSED), Style.new().notReversed())
        assertEquals(Style.new().removeModifier(Modifier.HIDDEN), Style.new().notHidden())
        assertEquals(Style.new().removeModifier(Modifier.CROSSED_OUT), Style.new().notCrossedOut())
    }

    @Test
    fun fromColor() {
        assertEquals(Style.new().fg(Color.Red), Style.from(Color.Red))
    }

    @Test
    fun fromColorColor() {
        assertEquals(Style.new().fg(Color.Red).bg(Color.Blue), Style.from(Color.Red, Color.Blue))
    }

    @Test
    fun fromModifier() {
        assertEquals(
            Style.new().addModifier(Modifier.BOLD).addModifier(Modifier.ITALIC),
            Style.from(Modifier.BOLD or Modifier.ITALIC)
        )
    }

    @Test
    fun fromModifierModifier() {
        assertEquals(
            Style.new()
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC)
                .removeModifier(Modifier.DIM),
            Style.from(Modifier.BOLD or Modifier.ITALIC, Modifier.DIM)
        )
    }

    @Test
    fun fromColorModifier() {
        assertEquals(
            Style.new()
                .fg(Color.Red)
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC),
            Style.from(Color.Red, Modifier.BOLD or Modifier.ITALIC)
        )
    }

    @Test
    fun fromColorColorModifier() {
        assertEquals(
            Style.new()
                .fg(Color.Red)
                .bg(Color.Blue)
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC),
            Style.from(Color.Red, Color.Blue, Modifier.BOLD or Modifier.ITALIC)
        )
    }

    @Test
    fun fromColorColorModifierModifier() {
        assertEquals(
            Style.new()
                .fg(Color.Red)
                .bg(Color.Blue)
                .addModifier(Modifier.BOLD)
                .addModifier(Modifier.ITALIC)
                .removeModifier(Modifier.DIM),
            Style.from(Color.Red, Color.Blue, Modifier.BOLD or Modifier.ITALIC, Modifier.DIM)
        )
    }

    @Test
    fun modifierWidth() {
        val flags = Modifier.fromBitsRetain(0xFFFFuL)
        assertEquals(0xFFFFuL, flags.bits())
        assertEquals(0xFE00uL, flags.unknownBits())
        assertEquals(0x1FFuL, flags.truncate().bits())
        assertEquals(0xFFFFuL, flags.union(Modifier.BOLD).bits())
        assertEquals(0xFFFEuL, flags.difference(Modifier.BOLD).bits())
        assertFailsWith<IllegalArgumentException> { Modifier.fromBitsRetain(0x10000uL) }
        assertFailsWith<IllegalArgumentException> { Modifier.BOLD.fromBitsRetain(ULong.MAX_VALUE) }
    }

    @Test
    fun modifierSerializeThenDeserialize() {
        for ((flags, json) in listOf(
            Modifier.empty() to "\"\"",
            (Modifier.BOLD or Modifier.ITALIC) to "\"BOLD | ITALIC\"",
            Modifier.fromBitsRetain(0x8001uL) to "\"BOLD | 0x8000\"",
            Modifier.fromBitsRetain(0xFFFFuL) to
                "\"BOLD | DIM | ITALIC | UNDERLINED | SLOW_BLINK | RAPID_BLINK | REVERSED | HIDDEN | CROSSED_OUT | 0xfe00\"",
        )) {
            assertEquals(json, toStr(flags).getOrThrow())
            assertEquals(flags, fromStr(json, Modifier).getOrThrow())
        }
        assertEquals(Modifier.BOLD, fromStr("\"0x+1\"", Modifier).getOrThrow())
        assertEquals(Modifier.BOLD, fromStr("\"BOLD | BOLD\"", Modifier).getOrThrow())
        assertEquals(Modifier.BOLD, fromStr("\"\u0085BOLD\u0085\"", Modifier).getOrThrow())
    }

    @Test
    fun modifierDeserializeErrors() {
        for ((input, message) in listOf(
            "NONE" to "unrecognized named flag `NONE`",
            "BOLD || DIM" to "encountered empty flag",
            "0x10000" to "invalid hex flag `10000`",
            "0xＦＦ" to "invalid hex flag `ＦＦ`",
            "bad | 0x10000" to "unrecognized named flag `bad`",
            "\u001CBOLD" to "unrecognized named flag `\u001CBOLD`",
        )) {
            val json = toStr(object : Serialize {
                override fun <S> serialize(serializer: Serializer<S>): SerdeResult<S> =
                    serializer.serializeStr(input)
            }).getOrThrow()
            val result = fromStr(json, Modifier)
            assertTrue(result.isFailure, input)
            assertTrue(result.exceptionOrNull()?.message?.startsWith(message) == true, input)
        }
        for (json in listOf("null", "12", "true", "[]", "{}")) {
            assertTrue(fromStr(json, Modifier).isFailure, json)
        }
    }

    @Test
    fun modifierCompactUsesU16() {
        var value: UShort? = null
        var text: String? = null
        val serializer = object : Serializer<Unit> by FormatterSerializer(StringBuilder()) {
            override fun isHumanReadable(): Boolean = false
            override fun <T : Serialize> serializeNewtypeStruct(name: String, value: T): SerdeResult<Unit> {
                assertEquals("Modifier", name)
                return value.serialize(this)
            }
            override fun serializeU16(v: UShort): SerdeResult<Unit> {
                value = v
                return SerdeResult.success(Unit)
            }
            override fun serializeStr(v: String): SerdeResult<Unit> {
                text = v
                return SerdeResult.success(Unit)
            }
        }
        val flags = Modifier.fromBitsRetain(0xFFFFuL)
        flags.serialize(serializer).getOrThrow()
        assertEquals(UShort.MAX_VALUE, value)
        assertEquals(null, text)
        val deserializer = object : Deserializer by U16Deserializer.new(UShort.MAX_VALUE) {
            override fun isHumanReadable(): Boolean = false
            override fun <V> deserializeNewtypeStruct(name: String, visitor: Visitor<V>): SerdeResult<V> {
                assertEquals("Modifier", name)
                return visitor.visitNewtypeStruct(this)
            }
        }
        assertEquals(flags, Modifier.deserialize(deserializer).getOrThrow())
        val overflow = object : Deserializer by U64Deserializer.new(0x10000uL) {
            override fun isHumanReadable(): Boolean = false
            override fun <V> deserializeNewtypeStruct(name: String, visitor: Visitor<V>): SerdeResult<V> {
                assertEquals("Modifier", name)
                return visitor.visitNewtypeStruct(this)
            }
        }
        assertTrue(Modifier.deserialize(overflow).isFailure)
    }

    @Test
    fun modifierDeserializePreservesFailure() {
        val error = SerdeError.custom("input failed")
        val deserializer = object : Deserializer by StrDeserializer.new("") {
            override fun <V> deserializeNewtypeStruct(name: String, visitor: Visitor<V>): SerdeResult<V> =
                SerdeResult.failure(error)
        }
        assertSame(error, Modifier.deserialize(deserializer).exceptionOrNull())
    }

    @Test
    fun serializeThenDeserialize() {
        val style = Style(
            fg = Color.Rgb(255u, 0u, 255u), bg = Color.White, underlineColor = Color.Indexed(3u),
            addModifier = Modifier.UNDERLINED, subModifier = Modifier.CROSSED_OUT,
        )
        val json = "{\"fg\":\"#FF00FF\",\"bg\":\"White\",\"underline_color\":\"3\",\"add_modifier\":\"UNDERLINED\",\"sub_modifier\":\"CROSSED_OUT\"}"
        assertEquals(json, toStr(style).getOrThrow())
        assertEquals(style, fromStr(json, Style).getOrThrow())
    }

    @Test
    fun deserializeDefaults() {
        assertEquals("{}", toStr(Style.default()).getOrThrow())
        assertEquals(Style.default(), fromStr("{}", Style).getOrThrow())
    }

    @Test
    fun deserializeNullModifiers() {
        val json = "{\"add_modifier\":null,\"sub_modifier\":null}"
        assertEquals(Style.default(), fromStr(json, Style).getOrThrow())
    }

    @Test
    fun styleDeserializeErrorsAndUnknownFields() {
        assertEquals(Style.default(), fromStr("{\"unknown\":{\"nested\":[1,null,true]}}", Style).getOrThrow())
        for (json in listOf(
            "{\"fg\":null,\"fg\":\"Red\"}",
            "{\"add_modifier\":null,\"add_modifier\":\"BOLD\"}",
            "{\"add_modifier\":\"bad\"}", "{\"fg\":\"invalid_color\"}",
        )) {
            assertTrue(fromStr(json, Style).isFailure, json)
        }
        for (json in listOf("[null,null,null]", "[null,null,null,null]", "[null,null,null,null,null]")) {
            assertEquals(Style.default(), fromStr(json, Style).getOrThrow())
        }
        assertTrue(fromStr("[null,null]", Style).isFailure)
        assertTrue(fromStr("[null,null,null,null,null,null]", Style).isFailure)
    }

    @Test
    fun styleSerializeKeepsOptionalColorTags() {
        val present = mutableListOf<String>()
        val serializer = object : Serializer<Unit> by FormatterSerializer(StringBuilder()) {
            override fun serializeStruct(name: String, len: Int): SerdeResult<SerializeStruct<Unit>> {
                assertEquals("Style", name)
                assertEquals(3, len)
                return SerdeResult.success(object : SerializeStruct<Unit> {
                    override fun <T : Serialize> serializeField(key: String, value: T): SerdeResult<Unit> {
                        val fieldSerializer = object : Serializer<Unit> by FormatterSerializer(StringBuilder()) {
                            override fun <T : Serialize> serializeSome(value: T): SerdeResult<Unit> {
                                present.add(key)
                                return SerdeResult.success(Unit)
                            }
                        }
                        return value.serialize(fieldSerializer)
                    }
                    override fun end(): SerdeResult<Unit> = SerdeResult.success(Unit)
                })
            }
        }
        Style(fg = Color.Red, bg = Color.Blue, underlineColor = Color.Green).serialize(serializer).getOrThrow()
        assertEquals(listOf("fg", "bg", "underline_color"), present)
    }

    @Test
    fun styleSerializePreservesFailure() {
        val error = SerdeError.custom("field failed")
        var finished = false
        val serializer = object : Serializer<Unit> by FormatterSerializer(StringBuilder()) {
            override fun serializeStruct(name: String, len: Int): SerdeResult<SerializeStruct<Unit>> =
                SerdeResult.success(object : SerializeStruct<Unit> {
                    override fun <T : Serialize> serializeField(key: String, value: T): SerdeResult<Unit> =
                        SerdeResult.failure(error)
                    override fun end(): SerdeResult<Unit> {
                        finished = true
                        return SerdeResult.success(Unit)
                    }
                })
        }
        assertSame(error, Style(fg = Color.Red).serialize(serializer).exceptionOrNull())
        assertFalse(finished)
    }

}
