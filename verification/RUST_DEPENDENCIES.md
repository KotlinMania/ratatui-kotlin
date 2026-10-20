# Ratatui Cargo dependency alignment

This audit uses the checked-in upstream workspace under `tmp/ratatui` and the
published Maven Central artifacts, not sibling checkout versions. It describes
source changes awaiting local/full CI validation.

## Serde repair

`ratatui-core/Cargo.toml` makes `serde` optional through its `serde` feature,
including `bitflags/serde` and `compact_str/serde`. `ratatui-widgets` propagates
that feature. `serde_json` is a core dev-dependency. The Kotlin port previously
implemented this feature directly with kotlinx.serialization: commonMain bundle
core+JSON, public `ColorSerializer : KSerializer<Color>`, compiler-generated
widget serializers, and a Kotlin serialization Gradle plugin. That direct
runtime/API path explains Swift's `OrgJetbrainsKotlinxKotlinxSerializationCore`.

The candidate uses the published Serde runtime module `serde-core:0.1.11` as an
API dependency. All `Serialize`, `Deserialize`, `Serializer`, `Deserializer` and
`SerdeResult` public types used here are owned by this module. The umbrella
`serde-kotlin:0.1.11` also API-depends on `serde-derive`, which pulls compiler ports;
handwritten trait implementations need neither the derive compiler nor its Syn
runtime graph. This is the module split of the Kotlin Serde port, not a new
serialization implementation inside Ratatui. JSON `serde-json-kotlin:0.1.1`
is a commonTest dependency only, matching Rust's dev dependency.

This changes the Kotlin-specific public serialization API: values implement
`Serialize`, companions implement `Deserialize<T>`, and `ColorSerializer`
implements `Deserialize<Color>`. For example:

```kotlin
val encoded = toStr(Color.Red).getOrThrow() // serdejson, application/test format
val decoded = fromStr(encoded, Color).getOrThrow()
val state = fromStr(json, TableState).getOrThrow()
```

The original state/Color golden tests are retained. New tests exercise the
format-independent Serde contract, all named Color variants, legacy RGB/indexed
maps, Rust field names, nullable sequences, duplicate/missing/unknown fields,
unsigned index checks and enum variants. Index storage remains Kotlin Int as in
the existing port; the accepted nonnegative range is consequently bounded by
Int.MAX_VALUE rather than Rust's host usize maximum. No alternate implementation
runs on a failure. Backward Color/table formats are upstream Rust behavior.

The independent root-only consumer now imports `Deserialize<Color>` and invokes
a Serde string deserializer; its four named tests remain required on JVM and
Android host. The fourth Android publication API edge is
`io.github.kotlinmania:serde-core:serde-core-android`, verified against the actual
published POM/AAR/module. Staging and actual consumer execution must be repeated
for these changed bytes; historical eight-test success is not new proof.

## Remaining graph and Cargo mapping

Published serde-core0.1.11 and serde-json0.1.1 source JARs contain no kotlinx
imports, but their publication metadata still includes inherited coroutines,
serialization-core/JSON, datetime and immutable-collections bundles. Therefore
serialization-core can remain transitively resolved after this repair. No
exclusions are used to conceal those producer metadata edges. A separately owned
upstream cleanup/publication is needed before claiming that graph is removed.
Serde JSON's own published dependency on serde-kotlin0.1.10 is also retained,
so derive/compiler ports can enter the test graph until producer metadata changes.

Ratatui's unused direct coroutines and immutable-collections implementation
bundle entries are removed; their existing catalog aliases/pins are retained.
Calendar code genuinely imports kotlinx.datetime and is kept pending a separate
published time-kotlin API/behavior audit (`time` is the Rust calendar feature).

| Rust dependency | Existing Kotlin mapping / disposition |
| --- | --- |
| anstyle, bitflags, itertools, kasuari, lru, strum, thiserror | Existing published KotlinMania catalog dependencies retained |
| unicode-segmentation, unicode-width, crossterm | Existing published KotlinMania dependencies retained |
| serde / serde_json | Published serde-core runtime API / serde-json test-only; repaired here |
| time (optional calendar) | datetime implementation currently retained; sibling migration pending evidence |
| compact_str, hashbrown | Kotlin String/collections in existing port; no matching checkout found in bounded sibling inventory |
| unicode-truncate, line-clipping, palette | Existing local algorithms/types; no matching checkout found in bounded sibling inventory; parity not established by this Serde repair |
| indoc, document-features, instability | Rust literal/documentation/attribute tooling; not Kotlin runtime libraries |
| critical-section (optional layout cache), portable-atomic forwarding | Rust feature-specific synchronization; unchanged by this Serde repair |

This audit is not a claim that every Cargo feature has complete Kotlin parity.
In particular, Rust Style/Modifier serialization beyond the preexisting Kotlin
serialization surface requires its own explicit parity review.
