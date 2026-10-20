package ratatui.serde

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeException
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serde.serdeCatching
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.DeserializeSeed
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.EnumAccess
import io.github.kotlinmania.serdecore.de.I32Deserialize
import io.github.kotlinmania.serdecore.de.IgnoredAny
import io.github.kotlinmania.serdecore.de.MapAccess
import io.github.kotlinmania.serdecore.de.SeqAccess
import io.github.kotlinmania.serdecore.de.StringDeserialize
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.de.nullableDeserialize
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import ratatui.widgets.list.ListState
import ratatui.widgets.table.TableState
import ratatui.widgets.scrollbar.ScrollbarState

internal fun invalidSerde(message: String): Nothing = throw SerdeException(SerdeError.custom(message))

/** Kotlin's signed index storage represents the nonnegative subset of Rust usize. */
internal object IndexDeserialize : Deserialize<Int> {
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Int> =
        I32Deserialize.deserialize(deserializer).flatMap {
            if (it >= 0) SerdeResult.success(it) else SerdeResult.failure(SerdeError.custom("expected usize"))
        }
}

internal fun indexValue(value: Int): Serialize = object : Serialize {
    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> = when {
        value < 0 -> SerdeResult.failure(SerdeError.custom("expected usize"))
        else -> serializer.serializeU64(value.toULong())
    }
}

internal fun optionalIndexValue(value: Int?): Serialize = object : Serialize {
    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
        if (value == null) serializer.serializeNone() else serializer.serializeSome(indexValue(value))
}

internal fun <Ok> serializeFields(
    serializer: Serializer<Ok>, name: String, vararg fields: Pair<String, Serialize>,
): SerdeResult<Ok> = serdeCatching {
    val state = serializer.serializeStruct(name, fields.size).getOrThrow()
    for ((key, value) in fields) state.serializeField(key, value).getOrThrow()
    state.end().getOrThrow()
}

// A box distinguishes a present null Option from the end of a sequence.
private class Element<T>(val value: T)
internal fun <T> requiredElement(access: SeqAccess, value: Deserialize<T>): T {
    val seed = object : DeserializeSeed<Element<T>> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<Element<T>> =
            value.deserialize(deserializer).map { Element(it) }
    }
    val element = access.nextElementSeed(seed).getOrThrow() ?: invalidSerde("missing sequence field")
    return element.value
}

/** Handwritten equivalent of serde's struct derive: duplicate/missing/unknown fields and sequences. */
internal abstract class StateDeserialize<T>(
    private val name: String,
    private val fields: List<String>,
    private val optional: Set<String> = emptySet(),
) : Deserialize<T> {
    protected abstract fun create(values: Map<String, Int?>): T
    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<T> =
        deserializer.deserializeStruct(name, fields, object : Visitor<T> {
            override fun expecting(): String = "struct $name"
            override fun <A : MapAccess> visitMap(access: A): SerdeResult<T> = serdeCatching {
                val values = mutableMapOf<String, Int?>()
                while (true) {
                    val field = access.nextKey(StringDeserialize).getOrThrow() ?: break
                    if (field !in fields) { access.nextValue(IgnoredAny).getOrThrow(); continue }
                    if (values.containsKey(field)) invalidSerde("duplicate field `$field`")
                    values[field] = if (field in optional)
                        access.nextValue(nullableDeserialize(IndexDeserialize)).getOrThrow()
                    else access.nextValue(IndexDeserialize).getOrThrow()
                }
                for (field in fields) if (field !in optional && field !in values) invalidSerde("missing field `$field`")
                create(values)
            }
            override fun <A : SeqAccess> visitSeq(access: A): SerdeResult<T> = serdeCatching {
                val values = fields.associateWith { field ->
                    if (field in optional) requiredElement(access, nullableDeserialize(IndexDeserialize))
                    else requiredElement(access, IndexDeserialize)
                }
                // The format owns rejection of excess sequence members (serde's derive contract).
                create(values)
            }
        })
}

internal object ListStateDeserialize : StateDeserialize<ListState>(
    "ListState", listOf("offset", "selected"), setOf("selected"),
) {
    override fun create(values: Map<String, Int?>): ListState = ListState(values.getValue("offset")!!, values["selected"])
}
internal object TableStateDeserialize : StateDeserialize<TableState>(
    "TableState", listOf("offset", "selected", "selected_column"), setOf("selected", "selected_column"),
) {
    override fun create(values: Map<String, Int?>): TableState =
        TableState(values.getValue("offset")!!, values["selected"], values["selected_column"])
}
internal object ScrollbarStateDeserialize : StateDeserialize<ScrollbarState>(
    "ScrollbarState", listOf("content_length", "position", "viewport_content_length"),
) {
    override fun create(values: Map<String, Int?>): ScrollbarState = ScrollbarState(
        values.getValue("content_length")!!, values.getValue("position")!!, values.getValue("viewport_content_length")!!,
    )
}

internal fun <T : Enum<T>> deserializeUnitEnum(
    deserializer: Deserializer, name: String, entries: List<T>,
): SerdeResult<T> = deserializer.deserializeEnum(name, entries.map { it.name }, object : Visitor<T> {
    override fun expecting(): String = "enum $name"
    override fun <A : EnumAccess> visitEnum(access: A): SerdeResult<T> = serdeCatching {
        val (variant, payload) = access.variantSeed(object : DeserializeSeed<String> {
            override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<String> =
                StringDeserialize.deserialize(deserializer)
        }).getOrThrow()
        val value = entries.firstOrNull { it.name == variant } ?: invalidSerde("unknown variant `$variant`")
        payload.unitVariant().getOrThrow()
        value
    }
})
