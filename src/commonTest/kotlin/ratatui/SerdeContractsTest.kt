package ratatui

import io.github.kotlinmania.serdecore.de.value.StrDeserializer
import io.github.kotlinmania.serdejson.fromStr
import io.github.kotlinmania.serdejson.toStr
import ratatui.style.Color
import ratatui.widgets.list.ListState
import ratatui.widgets.table.TableState
import ratatui.widgets.scrollbar.ScrollbarState
import ratatui.widgets.scrollbar.ScrollDirection
import ratatui.widgets.scrollbar.ScrollbarOrientation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SerdeContractsTest {
    @Test fun colorUsesSerdeWithoutJson() {
        assertEquals(Color.Red, Color.deserialize(StrDeserializer.new("Red")).getOrThrow())
    }

    @Test fun colorsRoundTripThroughPublishedSerdeJson() {
        val colors = listOf(Color.Reset, Color.Black, Color.Red, Color.Green, Color.Yellow, Color.Blue,
            Color.Magenta, Color.Cyan, Color.Gray, Color.DarkGray, Color.LightRed, Color.LightGreen,
            Color.LightYellow, Color.LightBlue, Color.LightMagenta, Color.LightCyan, Color.White,
            Color.Rgb(0u, 255u, 42u), Color.Indexed(0u), Color.Indexed(255u))
        for (color in colors) assertEquals(color, fromStr(toStr(color).getOrThrow(), Color).getOrThrow())
    }

    @Test fun malformedLegacyColorsFail() {
        for (json in listOf("true", "10", "null", "[]", "\"invalid\"", "{\"Rgb\":[0,0]}",
            "{\"Rgb\":[0,0,0,0]}", "{\"Rgb\":[256,0,0]}", "{\"Indexed\":-1}",
            "{\"Indexed\":256}", "{\"Indexed\":1,\"Rgb\":[0,0,0]}")) {
            assertTrue(fromStr(json, Color).isFailure, json)
        }
    }

    @Test fun listStateSerdeRoundTrip() {
        val state = ListState(4, 8)
        assertEquals("{\"offset\":4,\"selected\":8}", toStr(state).getOrThrow())
        assertEquals(state, fromStr(toStr(state).getOrThrow(), ListState).getOrThrow())
        assertEquals(ListState(), fromStr("{\"offset\":0}", ListState).getOrThrow())
    }

    @Test fun tableStateKeepsRustFieldNamesAndOptionalColumnCompatibility() {
        val state = TableState(4, 8, 2)
        assertEquals("{\"offset\":4,\"selected\":8,\"selected_column\":2}", toStr(state).getOrThrow())
        assertEquals(state, fromStr(toStr(state).getOrThrow(), TableState).getOrThrow())
        assertEquals(TableState(0, 1, null), fromStr("{\"offset\":0,\"selected\":1}", TableState).getOrThrow())
    }

    @Test fun scrollbarKeepsRustFieldNames() {
        val state = ScrollbarState(10, 2, 4)
        assertEquals("{\"content_length\":10,\"position\":2,\"viewport_content_length\":4}", toStr(state).getOrThrow())
        assertEquals(state, fromStr(toStr(state).getOrThrow(), ScrollbarState).getOrThrow())
    }

    @Test fun stateSequencesPreservePresentNullFields() {
        assertEquals(ListState(4, null), fromStr("[4,null]", ListState).getOrThrow())
        assertEquals(TableState(4, null, 2), fromStr("[4,null,2]", TableState).getOrThrow())
        assertEquals(ScrollbarState(10, 2, 4), fromStr("[10,2,4]", ScrollbarState).getOrThrow())
        assertTrue(fromStr("[4]", ListState).isFailure)
        assertTrue(fromStr("[4,null,2,3]", TableState).isFailure)
    }

    @Test fun duplicateAndMissingRequiredFieldsFail() {
        assertTrue(fromStr("{}", ListState).isFailure)
        assertTrue(fromStr("{\"offset\":0,\"offset\":1}", ListState).isFailure)
        assertTrue(fromStr("{\"offset\":0,\"selected\":null,\"selected\":null}", TableState).isFailure)
        assertTrue(fromStr("{\"content_length\":10,\"position\":0}", ScrollbarState).isFailure)
    }

    @Test fun unknownFieldsAreIgnoredAsInSerdeDerive() {
        assertEquals(ListState(), fromStr("{\"offset\":0,\"future\":{\"nested\":[1,null]}}", ListState).getOrThrow())
    }

    @Test fun negativeUsizeFailsWithoutAlternateEncoding() {
        assertTrue(fromStr("{\"offset\":-1}", ListState).isFailure)
        assertTrue(toStr(ListState(-1)).isFailure)
        assertTrue(toStr(TableState(selectedColumn = -1)).isFailure)
        assertTrue(toStr(ScrollbarState(position = -1)).isFailure)
    }

    @Test fun enumVariantsUseSerdeUnitContracts() {
        for (value in ScrollDirection.entries) {
            assertEquals("\"${value.name}\"", toStr(value).getOrThrow())
            assertEquals(value, fromStr(toStr(value).getOrThrow(), ScrollDirection).getOrThrow())
        }
        for (value in ScrollbarOrientation.entries) {
            assertEquals(value, fromStr(toStr(value).getOrThrow(), ScrollbarOrientation).getOrThrow())
        }
        assertTrue(fromStr("\"Sideways\"", ScrollDirection).isFailure)
        assertTrue(fromStr("{\"Forward\":1}", ScrollDirection).isFailure)
    }
}
