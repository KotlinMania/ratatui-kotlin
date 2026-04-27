// port-lint: source ratatui/tests/stateSerde.rs
package ratatui

import kotlinx.serialization.Serializable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ratatui.backend.TestBackend
import ratatui.layout.Constraint
import ratatui.layout.Direction
import ratatui.layout.Layout
import ratatui.terminal.Terminal
import ratatui.widgets.Borders
import ratatui.widgets.block.Block
import ratatui.widgets.list.List as ListWidget
import ratatui.widgets.list.ListState
import ratatui.widgets.scrollbar.Scrollbar
import ratatui.widgets.scrollbar.ScrollbarOrientation
import ratatui.widgets.scrollbar.ScrollbarState
import ratatui.widgets.table.Row
import ratatui.widgets.table.Table
import ratatui.widgets.table.TableState
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration tests for serializing/deserializing widget state.
 *
 * Transliteration of `ratatui/tests/stateSerde.rs`.
 */
class StateSerdeTest {
    @Serializable
    private data class AppState(
        var list: ListState = ListState.default(),
        var table: TableState = TableState.default(),
        var scrollbar: ScrollbarState = ScrollbarState.new(10)
    ) {
        fun select(index: Int) {
            list.select(index)
            table.selectCell(index to index)
            scrollbar = scrollbar.position(index)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        explicitNulls = true
        encodeDefaults = true
    }

    private fun assertBuffer(state: AppState, expected: List<String>) {
        val backend = TestBackend.new(21, 5)
        val terminal = Terminal.new(backend)
        terminal.draw { f ->
            val items = arrayOf(
                "awa",
                "banana",
                "Cats!!",
                "d20",
                "Echo",
                "Foxtrot",
                "Golf",
                "Hotel",
                "IwI",
                "Juliett"
            )

            val layout = Layout.default()
                .direction(Direction.Horizontal)
                .constraints(
                    listOf(
                        Constraint.Length(10),
                        Constraint.Length(10),
                        Constraint.Length(1)
                    )
                )
                .split(f.area())

            val list = ListWidget.new(*items)
                .highlightSymbol(">>")
                .block(Block.new().borders(Borders.RIGHT))
            f.renderStatefulWidget(list, layout[0], state.list)

            val table = Table.new(
                items.map { Row.new(it) },
                listOf(Constraint.Length(10))
            ).highlightSymbol(">>")
            f.renderStatefulWidget(table, layout[1], state.table)

            val scrollbar = Scrollbar.new(ScrollbarOrientation.VerticalRight)
            f.renderStatefulWidget(scrollbar, layout[2], state.scrollbar)
        }
        terminal.backend().assertBufferLines(expected)
    }

    private val defaultStateBuffer =
        listOf(
            "awa      │awa       ▲",
            "banana   │banana    █",
            "Cats!!   │Cats!!    ║",
            "d20      │d20       ║",
            "Echo     │Echo      ▼",
        )

    private val defaultStateRepr =
        """
        {
          "list": {
            "offset": 0,
            "selected": null
          },
          "table": {
            "offset": 0,
            "selected": null,
            "selected_column": null
          },
          "scrollbar": {
            "content_length": 10,
            "position": 0,
            "viewport_content_length": 0
          }
        }
        """.trimIndent()

    @Test
    fun defaultStateSerialize() {
        val state = AppState()
        assertBuffer(state, defaultStateBuffer)
        val serialized = json.encodeToString(state)
        assertEquals(defaultStateRepr, serialized)
    }

    @Test
    fun defaultStateDeserialize() {
        val state = json.decodeFromString<AppState>(defaultStateRepr)
        assertBuffer(state, defaultStateBuffer)
    }

    private val selectedStateBuffer =
        listOf(
            "  awa    │  awa     ▲",
            ">>banana │>>banana  █",
            "  Cats!! │  Cats!!  ║",
            "  d20    │  d20     ║",
            "  Echo   │  Echo    ▼",
        )

    private val selectedStateRepr =
        """
        {
          "list": {
            "offset": 0,
            "selected": 1
          },
          "table": {
            "offset": 0,
            "selected": 1,
            "selected_column": 0
          },
          "scrollbar": {
            "content_length": 10,
            "position": 1,
            "viewport_content_length": 0
          }
        }
        """.trimIndent()

    @Test
    fun selectedStateSerialize() {
        val state = AppState()
        state.select(1)
        assertBuffer(state, selectedStateBuffer)
        val serialized = json.encodeToString(state)
        assertEquals(selectedStateRepr, serialized)
    }

    @Test
    fun selectedStateDeserialize() {
        val state = json.decodeFromString<AppState>(selectedStateRepr)
        assertBuffer(state, selectedStateBuffer)
    }

    private val scrolledStateBuffer =
        listOf(
            "  Echo   │  Echo    ▲",
            "  Foxtrot│  Foxtrot ║",
            "  Golf   │  Golf    ║",
            "  Hotel  │  Hotel   █",
            ">>IwI    │>>IwI     ▼",
        )

    private val scrolledStateRepr =
        """
        {
          "list": {
            "offset": 4,
            "selected": 8
          },
          "table": {
            "offset": 4,
            "selected": 8,
            "selected_column": 0
          },
          "scrollbar": {
            "content_length": 10,
            "position": 8,
            "viewport_content_length": 0
          }
        }
        """.trimIndent()

    @Test
    fun scrolledStateSerialize() {
        val state = AppState()
        state.select(8)
        assertBuffer(state, scrolledStateBuffer)
        val serialized = json.encodeToString(state)
        assertEquals(scrolledStateRepr, serialized)
    }

    @Test
    fun scrolledStateDeserialize() {
        val state = json.decodeFromString<AppState>(scrolledStateRepr)
        assertBuffer(state, scrolledStateBuffer)
    }

    private val oldTableDeserialize =
        """
        {
            "offset": 0,
            "selected": 1
        }
        """.trimIndent()

    private val newTableDeserialize =
        """
        {
            "offset": 0,
            "selected": 1,
            "selected_column": null
        }
        """.trimIndent()

    @Test
    fun tableStateBackwardsCompatibility() {
        val oldState = json.decodeFromString<TableState>(oldTableDeserialize)
        val newState = json.decodeFromString<TableState>(newTableDeserialize)
        assertEquals(oldState, newState)
    }
}
