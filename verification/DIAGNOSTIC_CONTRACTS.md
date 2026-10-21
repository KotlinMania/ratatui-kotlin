# Color diagnostic and conversion contracts

The oracle is upstream Ratatui commit `c78fa2169401527cebe30bee2cbf305d916fb238`.
The full `ratatui-core/src/style/color.rs` (788 lines),
`ratatui-core/src/style/anstyle.rs` (361 lines), and
`ratatui-core/src/symbols/merge.rs` (748 lines) were inspected.
The workspace and repository AGENTS.md files are byte-identical SHA-256
`45b6e09a5fb99114327212bf8fa68c6c688312ee202fd2d4efb17963c8db3e53`;
there are no deeper applicable source/verification/harness guides.

## Actual error ownership

Rust `ParseColorError` is a unit struct with equality/hash and explicit Display.
It manually implements `core::error::Error`; it does not derive thiserror.
Its default `source()` is None. Kotlin preserves the name, unit-value equality,
exact display/message `Failed to parse Colors`, and null source. It is an actual
non-Throwable diagnostic, not an alias, wrapper around a foreign diagnostic, or
implementation of a locally invented shared standard-error interface.

Rust `TryFromColorError` has the three existing Ansi256/Ansi/RgbColor variants.
Deriving thiserror generates standard error/display behavior; the resulting Rust
public contract does not inherit thiserror runtime helper types. Kotlin retains
those variants, exact display messages and null source directly. Internal
`BorderSymbolError` retains its message variants without the same helper trait.
No error here supplies an underlying source, so null is the upstream default
behavior, not an unimplemented error chain.

No published core/std Error sibling was found in the bounded existing workspace
inventory. This repair does not claim to implement the entire Rust standard
library Error/FromStr/TryFrom generic trait system or arbitrary dynamic-error
substitutability. It preserves these concrete Ratatui implementations and their
actual success/error behavior without exporting derive tooling or Throwable.

## Public operation and caller mapping

These are intentional source and binary signature changes. Recompiled consumers
are required; a metadata-only dependency adjustment cannot preserve the old ABI.
No compatibility aliases or renamed wrapper operations retain the wrong behavior.

| Previous Kotlin declaration | Current canonical declaration | Pinned Rust operation |
| --- | --- | --- |
| `Color.fromStr(String): Color`, throws public ParseColorError | `Color.fromStr(String): ColorParseResult` | Color FromStr `from_str`, returning `Result<Color, ParseColorError>` |
| `Color.toAnsi256Color()` | `Ansi256Color.tryFrom(Color): Ansi256ColorResult` | `TryFrom<Color> for Ansi256Color::try_from` |
| `Color.toAnsiColor()` | `AnsiColor.tryFrom(Color): AnsiColorResult` | `TryFrom<Color> for AnsiColor::try_from` |
| `Color.toRgbColor()` | `RgbColor.tryFrom(Color): RgbColorResult` | `TryFrom<Color> for RgbColor::try_from` |
| `Ansi256Color/AnsiColor/RgbColor/AnstyleColor.toRatatuiColor()` | `Color.from(source)` overloads | Corresponding `From<source> for Color::from` |
| `Color.toAnstyleColor()` | `AnstyleColor.from(Color)` | `From<Color> for anstyle::Color::from`; Reset still panics |
| `Effects.toModifier()` | `Modifier.from(Effects)` | `From<Effects> for Modifier::from` |
| `Modifier.toEffects()` | `Effects.from(Modifier)` | `From<Modifier> for Effects::from` |
| `AnstyleStyle.toRatatuiStyle()` | `Style.from(AnstyleStyle)` | `From<anstyle::Style> for Style::from` |
| `Style.toAnstyleStyle()` | `AnstyleStyle.from(Style)` | `From<Style> for anstyle::Style::from` |
| public `ColorSerializer` object | `Color.Companion.deserialize` owns the implementation | Actual `Deserialize for Color::deserialize`, with no public serializer object |
| public `TryFromColorException` | internal unwrapping carrier only | No corresponding upstream public exception type |

`ColorParseResult`, `Ansi256ColorResult`, `AnsiColorResult`, and `RgbColorResult`
are explicit concrete Swift boundary specializations of the four listed Rust
Result instantiations. They are **not upstream type names** and are not claimed
as a port of a generic Result implementation. They retain a concrete nullable
value and original concrete diagnostic, with exactly one populated field enforced
by private constructors and non-null `ok`/`err` factories. `isOk`, `isErr` and
`unwrap` retain those Rust operations. They neither alias a generic result nor
substitute Any for a value. Unwrap carries the original diagnostic in an internal
exception; ordinary fromStr/tryFrom failure returns a value and does not throw.

All owned ColorTest/AnstyleTest callers are migrated: Rust success unwraps map to
actual result unwraps, and Rust error assertions inspect actual returned errors.
Original colors, conversion inputs, exact error variants, Reset panic, screen
and rendering assertions are retained. Additional tests check identity through
result/explicit unwrap, unit diagnostic equality/hash, exact display and null
sources. Color's Serde string visitor maps the returned ParseColorError message
into the existing Serde custom-error contract, matching upstream map_err.

RUST_CALLERS.md contains no explicit ParseColorError/TryFromColorError/from_str
inbound symbol entries. Its broader Ratatui callers include Codex and ANSI.
The existing ANSI `src` (6 Kotlin files) and Codex `src` (245 Kotlin files),
followed by their complete Kotlin-file inventories excluding vendored/generated
build trees, were searched for the old error and conversion declarations; neither
contains a call matching these changed operations. This bounded scan is not a claim of a full
Codex build or exhaustive downstream-repository validation. The independent
published-coordinate Android/JVM consumer is explicitly migrated and must rebuild.

## Dependency and publication mapping

UnicodeWidthStr is an actual public Rust Text trait implementation. Modifier is
actually generated by bitflags; their existing API edges are retained. This
repair does not certify every Modifier width/generic/export adaptation.

Anstyle is an optional normal core Cargo dependency inherited from workspace
dependencies and enabled by the `anstyle` feature. Kotlin currently compiles
these conversion declarations unconditionally; that is the enabled feature
surface, not a claim of equivalent Cargo feature-toggle selection.
Public conversion signatures directly use its color/style/effect types. Its
existing published Kotlin `anstyle-kotlin:0.1.5` is therefore API, not private
implementation. Actual Central root/Android/JVM module metadata and Android POM
HTTP200 receipts are in workspace automation-artifacts/2026-10-20-ansi-pr99/
diagnostic-contracts/published-anstyle-evidence.json. The selected published JVM bytecode confirms only Ansi256Color and Effects
companions. AnsiColor, RgbColor, Color and Style companions are absent from
0.1.5, so the current conversion declarations cannot compile against that
publication. All six companions exist in Anstyle 0.1.6 source and private staged
Android/JVM artifacts, which is distinct from immutable Central availability.
No sibling source or unpublished consumer pin is substituted.

Thiserror remains recorded at its unchanged catalog pin as upstream derive
provenance; handwritten concrete implementations do not depend on its runtime
helpers. The core public artifact guard now checks bitflags/anstyle/unicode-width/
serde-core API edges, all real diagnostic/result classes, and Android/JVM
superclasses/interfaces so neither public Throwable nor derive-helper inheritance
can be silently reintroduced. Indirect published dependencies are not excluded.

The prior eight-consumer pass at b4ceecab verifies the former signature, not this
source change. Current Kotlin runtime tests, regenerated Swift declarations,
strict dependency/harness compilation, exact artifact/source checks and rebuilt
independent consumers must provide separate evidence for the new ABI. The known
published JSON and Swift-runtime failures remain visible release gates.


## Numeric grammar and public Modifier

Color.fromStr retains the pinned Rust operation, named-color normalization and
indexed-before-hex parsing order. Its numeric paths now reject non-ASCII input:
Kotlin JVM unsigned parsing accepts fullwidth and Arabic-Indic digits that Rust
u8 parsing rejects. ASCII checks retain Rust's accepted leading plus sign,
including each two-byte hexadecimal component. Named Unicode lowercasing remains
independent of numeric validation. Common tests assert rejection through the real
ParseColorError result and preserve that diagnostic's identity through unwrap.
The JVM-stdlib/Rust witness records the grammar defect; it is not execution of
the repaired Kotlin common source on Native, JS, Wasm, Android or JVM.

Modifier is public in the pinned Rust style module. The inherited
HiddenFromObjC annotation and its otherwise-unused opt-in/import are removed;
no alternative type replaces its public contract. The existing BitFlags base
still uses ULong while upstream Modifier stores u16, so this does not certify
width, generic export, unknown-bit behavior or optional Serde parity. Real
Native export and strict Swift compilation must assess the visible contract.
These changes introduce no public operation/type renames or dependency pin changes.
