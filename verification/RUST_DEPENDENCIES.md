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

The core crate uses the published Serde runtime module `serde-core:0.1.11` as an
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
Android host. The fourth core Android publication API edge is
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

## Complete workspace inventory and structured build

The pinned upstream Git revision is
`c78fa2169401527cebe30bee2cbf305d916fb238`. The machine-readable
[`rust-workspace.json`](rust-workspace.json) records **all 42 Cargo.toml files**,
individual SHA-256 hashes, all 226 dependency declarations, workspace inheritance,
additive features, default-feature and optional settings, path/member edges and
raw target predicates. Counts are 196 normal and 30 dev declarations, two target
specific edges, and zero build-dependency declarations at this revision. The root
manifest SHA-256 and inherited package/dependency tables are included. This is
source evidence, not execution of Cargo features on every platform.

| Rust member | Kotlin ownership and publication boundary |
| --- | --- |
| ratatui | Root `ratatui-kotlin` facade: API project edges to actual core/widgets/macros; native desktop backend/init remains target-specific |
| ratatui-core | `:ratatui-core`: backend contracts/TestBackend, buffer/layout/style/text/symbols/terminal, base Widget traits; own KMP coordinates |
| ratatui-widgets | `:ratatui-widgets`: actual widgets and state Serde, API dependency on core; own KMP coordinates |
| ratatui-macros | `:ratatui-macros`: existing DSL functions, API core/widgets edges; own KMP coordinates |
| ratatui-crossterm | `:ratatui-crossterm`: actual backend source and tests; API core and published Crossterm implementation; macOS arm64/Linux x64/Windows x64 only |
| ratatui-termion / ratatui-termwiz | **Unimplemented optional backends.** No fake module or Crossterm alias substitutes for their behavior |
| xtask | `:xtask`: existing native command tooling, separately compiled, never published |
| 33 example members | Every manifest inventoried individually. Existing Kotlin demos are in the root's separate associated `examples` compilations and excluded from published main sources; full member/example parity is incomplete |

Core/widgets/macros retain every existing root KMP target, including Android host
and device compile tasks, Native targets, JS browser/Node and both Wasm runtimes.
The root's target selectors and `test`/`allTests`/`build` explicitly depend on the
owning crate tasks. Root strict Swift export includes the actual modules. No
Kotlin wrapper/typealias reexports and no duplicate source roots implement the
facade. The backend's narrower availability is a concrete port/publication gap;
it does not remove any root/core/widgets/macros target or count compilation as
missing device/native runtime coverage.

Current `km-io` checkout `8881f949230444d00a0e7db5cc2ea899b99172d8` is flattened:
its settings has no module includes and the residual core/bytestring directories
contain build output only. Its full strict target/publication mechanics were
inspected, not misreported as an active multi-module build. Serde's verified
included `build-logic` convention plugin, thin crate builds and independent KMP
coordinate pattern inform this structure; weaker source filters/settings were
not copied. Ratatui's strict warning settings and root real Swift harness remain.

The root deployment bundle aggregates the four published crate owners. Consumer
staging builds root/Android/JVM coordinates for facade, core, widgets and macros;
Android guards check public bytecode in the owning artifact and the facade API
edges before a separate root-only JVM/Android consumer runs. Cross-crate source
moves preserve Kotlin packages and public declarations; the artifact boundary
change requires a new version and rebuilt consumers after green initial gates.

Rust's optional backend/serde/calendar/scrolling/unstable feature propagation is
recorded from every member, rather than inferred from root Cargo.toml. The
current Kotlin build still exposes a chosen enabled feature surface rather than
Cargo's full feature-toggle matrix. Unresolved runtime, optional, example and
tooling mappings are explicitly marked in the JSON inventory and mean incomplete
porting. The inventory does not convert them to completed dependency parity.
