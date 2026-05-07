# ratatui-kotlin Transliteration Guide

This project is a **Kotlin Multiplatform Native** port of [ratatui](https://github.com/ratatui-org/ratatui), the Rust TUI library.

## Translator's mindset

This is a translation project, not a software-engineering project. While porting a file, you are
the Kotlin author of the same document a Rust author wrote. Architecture, optimization, design
critique, drift measurement — all later. While translating, the only job is the translation.

The discipline:

1. **Read the whole upstream file before you type.** A line-by-line port composes only when you
   know how the file ends. If the file is too long to read in one sitting, split your turn into
   "read the file" and "write the file" — never start typing on a file you've only half-read.

2. **One Rust file → one Kotlin file. Always.** No splitting one `.rs` across several `.kt`. No
   merging several `.rs` into one `.kt`. The 1:1 mapping is the contract; everything downstream
   (ast_distance, port-lint headers, code review) assumes it. If a `.rs` is genuinely too big for
   one Kotlin file, that's a sign you're in `mod.rs`-equivalent territory and the upstream itself
   is a re-export — verify, don't split.

3. **Translate top to bottom in upstream order.** Preserve the declaration order. Don't reorder
   for "logical flow" — the upstream's order *is* the logical flow. The reader who already knows
   the Rust file should be able to scroll the Kotlin file and find every item in the same place.

4. **Comments are content.** License header, module-level doc, every `///` block, every inline
   `//` note, every upstream `// TODO`/`// FIXME` — all translate. Rust syntax inside doc comments
   gets rewritten to Kotlin equivalents (`Vec<T>` → `List<T>`, `Self::foo()` → `foo()`, lifetimes
   dropped, `cfg(test)` and `#[derive(...)]` lifted into prose). You are translating a *document*,
   not just the code.

5. **When a Rust idiom has no Kotlin analog, apply the mapping rule and move on.** `Box<T>`,
   `Arc<T>`, `Cell<T>`, `RefCell<T>`, `Rc<T>`, lifetimes, `PhantomData`, `mem::forget`,
   `drop_in_place`, `Pin`, `MaybeUninit`, `dyn Trait` — all collapse per the mapping table.
   Don't relitigate. A proc-macro becomes a builder/runtime API, not nothing. An upstream Rust
   crate with no KMP equivalent becomes a *separate Kotlin port*, not a `// TODO` placeholder.
   Pay the snowball cost upfront — the next consumer will thank you.

6. **Don't measure mid-port.** ast_distance, FnSim, similarity reports — useful *after* a file is
   done, useless *during*. Mid-translation measurement is procrastination dressed as rigor. Run
   the tools when a file lands or when a port phase wraps, not while you're choosing between
   `Result<T>` and `T?`.

7. **Don't optimize the translation.** "This Kotlin shape would be simpler" is the wrong
   thought. The upstream shape is the spec. If a faithful translation produces a function that
   takes a parameter you'd never write in Kotlin from scratch, take it. Optimization is a
   separate, named pass after parity is reached — never blended into the translation.

8. **Don't re-architect mid-port.** "This whole module would be cleaner if..." — write the
   thought on a sticky note, throw the sticky note away, finish the file. The current architecture
   is the upstream's architecture. Earn the right to redesign by first reaching parity.

9. **Compile errors during translation are normal and expected.** A bottom-of-tree file compiles
   when its deps are ported, not before. Don't pause to "make it compile" mid-port — that pulls
   you into stub-shaped fixes that you'll have to undo. Climb the dep tree bottom-up; the leaves
   compile first, then their parents, then everything compiles together at the end.

10. **Bottom-up always.** Port dependencies before consumers. If `state.rs` uses `EvalException`,
    port `eval_exception.rs` first. If `eval_exception.rs` uses `Error`/`WithDiagnostic`/`CallStack`,
    port those first. The order isn't optional; trying to port top-down produces a tree of stubs
    that all need replacing.

11. **Hard files are not skippable.** logos-codegen, lalrpop's table generator, an annotate-snippets
    equivalent — when you hit one, port it. Skipping leaves a `// TODO`-shaped hole that grows
    every time another consumer needs it. The snowball is the whole point: each hard port done
    makes the next port easier, because the dep is now in Kotlin.

12. **Warnings are real, but `@Suppress` is never the answer.** `UNUSED_PARAMETER` on a callback
    helper means the function shape doesn't fit Kotlin — restructure the signature, don't suppress.
    `UNCHECKED_CAST` means the type system is missing an invariant — encode it. Every warning is
    either a real bug or a translation choice that needs revisiting; treat them as compile errors.

13. **Stop at file boundaries, not function boundaries.** After every completed file, exhale,
    commit, move on. Don't pause mid-function to second-guess a choice. The whole-file context
    is what makes individual choices coherent.

14. **Doc-port discipline applies even when the upstream doc is awkward.** If the upstream
    author wrote a tortured English sentence in a doc comment, translate the tortured sentence.
    Don't smooth it. Don't paraphrase. Their doc is the contract for the Kotlin doc.

15. **The cheat detector is your friend.** If `ast_distance` forces your file's score to 0
    because you left snake_case identifiers or `pub` keywords in Kotlin comments, take it as a
    literal instruction: rewrite those comments to be Kotlin-native. Rust syntax in Kotlin source
    — code or comments — is the cheat we're catching.

The sticky-note version: **"Read the file. Translate it. Don't think about anything else."**

## Core Principle: In-Place Transliteration

The `.rs` files have been renamed to `.kt` and placed in the appropriate package directories. Your job is to **transliterate** the Rust code to Kotlin **in-place**, preserving the ability to track changes via git diff.

### What This Means

- **DO NOT** delete large sections of code wholesale
- **DO NOT** rewrite from scratch
- **DO** convert function-by-function, preserving structure
- **DO** keep all comments, updating them for Kotlin idioms
- **DO** assume dependencies (Buffer, Rect, Cell, etc.) already exist

## Kotlin Native Constraints

- **NO JVM** - This is Kotlin/Native. No `@JvmInline`, no `java.*` imports, no JVM-specific APIs
- **NO NumPy/JVM interop** - Pure Kotlin only
- Target platforms: macOS (arm64, x64), Linux (x64), Windows (mingw x64)

## Rust → Kotlin Mapping

### Types

| Rust | Kotlin |
|------|--------|
| `u16` | `UShort` |
| `u32` | `UInt` |
| `u64` | `ULong` |
| `i16` | `Short` |
| `i32` | `Int` |
| `i64` | `Long` |
| `usize` | `Int` (or `UInt` if semantic) |
| `bool` | `Boolean` |
| `String` | `String` |
| `&str` | `String` |
| `Vec<T>` | `MutableList<T>` |
| `&[T]` | `List<T>` |
| `Option<T>` | `T?` |
| `Result<T, E>` | Return `T` or throw exception |
| `(A, B)` | `Pair<A, B>` |
| `(A, B, C)` | `Triple<A, B, C>` |

### Syntax

| Rust | Kotlin |
|------|--------|
| `fn foo()` | `fun foo()` |
| `pub fn` | `fun` (public by default) |
| `let x` | `val x` |
| `let mut x` | `var x` |
| `struct Foo { ... }` | `data class Foo(...)` or `class Foo(...)` |
| `impl Foo { ... }` | Methods inside class body |
| `impl Trait for Foo` | `class Foo : Trait` |
| `Foo::new()` | `Foo.new()` (companion object) |
| `self.field` | `field` or `this.field` |
| `&self` | (implicit `this`) |
| `&mut self` | (implicit `this`, mutate freely) |
| `match x { ... }` | `when (x) { ... }` |
| `if let Some(v) = x` | `x?.let { v -> ... }` or `if (x != null)` |
| `x.unwrap()` | `x!!` |
| `x.unwrap_or(default)` | `x ?: default` |
| `x?` (try operator) | Throw or use `?.` |

### Collections & Iterators

| Rust | Kotlin |
|------|--------|
| `vec![]` | `mutableListOf()` |
| `vec![val; n]` | `MutableList(n) { val }` |
| `[a, b, c]` | `listOf(a, b, c)` |
| `.iter()` | (implicit, collections are iterable) |
| `.iter().enumerate()` | `.withIndex()` |
| `.iter().map()` | `.map()` |
| `.iter().filter()` | `.filter()` |
| `.iter().collect::<Vec<_>>()` | `.toList()` or `.toMutableList()` |
| `.chunks(n)` | `.chunked(n)` |
| `0..n` (exclusive end) | `0 until n` or `0..<n` |
| `0..=n` (inclusive end) | `0..n` |
| `.len()` | `.size` |
| `.is_empty()` | `.isEmpty()` |
| `.push(x)` | `.add(x)` |
| `.extend(iter)` | `.addAll(iter)` |
| `slice[start..end]` | `list.subList(start, end)` |

### Numeric Operations

| Rust | Kotlin |
|------|--------|
| `x.saturating_sub(y)` | `(x - y).coerceAtLeast(0)` or manual check |
| `x.saturating_add(y)` | `(x + y).coerceAtMost(MAX)` |
| `x.min(y)` | `minOf(x, y)` |
| `x.max(y)` | `maxOf(x, y)` |
| `x as u16` | `x.toUShort()` |
| `x as usize` | `x.toInt()` |
| `u16::MAX` | `UShort.MAX_VALUE` |

### Documentation

| Rust | Kotlin |
|------|--------|
| `/// Doc comment` | `/** Doc comment */` |
| `//! Module doc` | `/** Module doc */` at top |
| `# Panics` section | `@throws IllegalStateException` |
| `# Examples` section | Keep as markdown code block |
| `` `code` `` | `` `code` `` (same) |
| `[`Type`]` | `[Type]` (KDoc link) |

### Error Handling

| Rust | Kotlin |
|------|--------|
| `panic!("msg")` | `error("msg")` or `throw IllegalStateException("msg")` |
| `assert!(cond)` | `check(cond)` |
| `assert_eq!(a, b)` | `check(a == b)` or `assertEquals(a, b)` in tests |
| `#[should_panic]` | `assertFailsWith<IllegalStateException> { ... }` |
| `Result::Ok(x)` | Return `x` directly |
| `Result::Err(e)` | Throw appropriate exception |

### Testing

| Rust | Kotlin |
|------|--------|
| `#[cfg(test)]` | (separate test source set, or inline) |
| `mod tests { ... }` | `class FooTest { ... }` |
| `#[test]` | `@Test` |
| `assert_eq!(a, b)` | `assertEquals(a, b)` |
| `assert!(cond)` | `assertTrue(cond)` |
| `assert!(!cond)` | `assertFalse(cond)` |
| `#[should_panic = "msg"]` | `assertFailsWith<Exception>("msg") { ... }` |

### Naming Conventions

| Rust (snake_case) | Kotlin (camelCase) |
|-------------------|-------------------|
| `fn get_cursor_position` | `fun getCursorPosition` |
| `let line_count` | `val lineCount` |
| `struct TestBackend` | `class TestBackend` (unchanged) |
| `CONSTANT_VALUE` | `CONSTANT_VALUE` (unchanged) |
| `mod my_module` | `package my.module` |

### Special Patterns

**Rust `impl` blocks → Kotlin class structure:**
```rust
// Rust
impl Foo {
    pub fn new() -> Self { ... }      // Factory
    pub fn bar(&self) { ... }          // Instance method
}
```
```kotlin
// Kotlin
class Foo {
    companion object {
        fun new(): Foo { ... }         // Factory in companion
    }
    fun bar() { ... }                  // Instance method
}
```

**Rust `Default` trait:**
```rust
impl Default for Foo {
    fn default() -> Self { ... }
}
```
```kotlin
// Either provide defaults in primary constructor:
data class Foo(val x: Int = 0)
// Or companion factory:
companion object {
    fun default(): Foo = Foo(x = 0)
}
```

**Rust struct update syntax:**
```rust
Foo { field: new_val, ..self }
```
```kotlin
copy(field = newVal)  // data class
```

## Trait default methods with `where` clauses → method-level Kotlin generic bounds

Rust traits routinely declare a default method whose body only typechecks
when the type parameter satisfies a stricter bound:

```rust
pub trait RangeBounds<T> {
    fn start_bound(&self) -> Bound<&T>;
    fn end_bound(&self) -> Bound<&T>;

    fn is_empty(&self) -> bool
    where T: PartialOrd,
    { /* default body uses < */ }
}
```

The trait stays unconstrained; the *method* picks up the bound via its
own `where` clause. Kotlin has no per-method `where` on an interface
member. Three obvious mappings fail:

1. **Tighten the interface to `<T : Comparable<T>>`.** Breaks every
   caller that holds the unbounded interface type.
2. **Make the method abstract on the interface.** Forces every concrete
   impl to invent a body and pile on `override` boilerplate, even when
   the Rust counterpart inherits the default unchanged.
3. **Runtime cast helper** — `if (left is Comparable<*> ...) ... else throw IllegalStateException(...)`.
   Compile-time bounds become runtime crashes; the cheat detector flags
   this and zeros the file's score.

### The faithful pattern

Translate the default to a Kotlin **extension function whose own type
parameter carries the bound**:

```kotlin
interface RangeBounds<T> {
    fun startBound(): Bound<T>
    fun endBound(): Bound<T>
}

fun <T : Comparable<T>> RangeBounds<T>.isEmpty(): Boolean { /* default body */ }
```

Concrete impls that want to specialise the default supply a same-named
**member function**. Kotlin resolves `range.isEmpty()` to the member
when the static receiver type is the concrete class and to the
extension when it is the interface — exactly mirroring Rust's
"default method, per-impl override". No `override` keyword on the
member; there is nothing on the interface to override.

Recipe:

1. Interface keeps only the methods declared without where-clauses.
2. Each default-method-with-where-clause becomes a Kotlin extension
   whose own type-parameter bound mirrors the where-clause.
3. Concrete subtypes specialise by declaring a same-named member.
4. Callers holding the unbounded interface type cannot invoke the
   comparison-using methods — correct, Rust would reject the same
   call without the bound.

### Pair with the dual-overload pattern when both paths are needed

When a function has to work in both the comparator-aware and natural-order
paths, expose two overloads — the unbounded one takes the comparator
explicitly, the bounded one is sugar that delegates. The canonical
implementation lives in [`btree-kotlin`](../btree-kotlin/) `Search.kt::searchTree`
/ `searchNode` / `findLowerBoundEdge` / `findUpperBoundEdge` and
`Navigate.kt::searchTreeForBifurcation` / `lowerBound` / `upperBound`.

### Why this is faithful, not engineering

- Interface mirrors Rust's trait declaration shape exactly.
- Extension's bound mirrors Rust's `where` clause exactly.
- Concrete-class members shadow the extension exactly the way Rust
  inherent-impl methods override a trait default.
- No runtime casts, no `IllegalStateException`, no `is Comparable<*>`.

When the bound is on a *class* type parameter rather than a trait method,
fall back to the `Comparator<in K>` field pattern with a comparator-or-natural
dispatch helper. The fallback is the design contract, not a translation hack.

## Git Commit Messages

- NO AI attribution or branding
- Clear, descriptive messages about the technical changes
- No emoji unless requested

## Workflow

1. Read the current `.kt` file (which contains Rust code)
2. Convert one function/section at a time
3. Preserve all comments, updating terminology
4. Keep the same logical structure
5. Use Kotlin idioms but maintain parity with Rust behavior

## Re-exports from upstream `mod.rs` files

When an upstream Rust `mod.rs` is **only re-exporting** something that actually lives elsewhere
(`pub use <crate-path>::<Name>;`, often under a different name), do **not** preserve that
re-export shape in Kotlin as a "central alias" API. Do not write a `typealias` for the
re-exported name. The existing `Forbidden` rule against "Re-export typealias files at root
packages" is enforced through this procedure.

Workflow:

1. **Identify what the `mod.rs` is re-exporting and the name it's exported as.** Record both
   the original symbol's fully-qualified upstream path and the (possibly different) re-export
   name.

2. **Find callers across the kotlinmania monorepo.** A caller is any Kotlin file in another
   `*-kotlin` repo that has both a `tmp/` folder and a Cargo.toml depending on the Rust
   counterpart of *this* crate, where the file references the re-exported name. Search for:
   - direct imports: `import <reexport-package>.<Name>`
   - wildcard imports of the re-export package, when `<Name>` is used in the file body
   - fully-qualified inline references

3. **Rewrite each caller to reference the upstream/original symbol directly.** If the caller
   still needs to write `<Name>` unchanged, use Kotlin aliasing:
   `import <upstream-fully-qualified-name> as <Name>`. Never bridge with a Kotlin `typealias`.

4. **Keep `Mod.kt` (or the equivalent file for that package) as a tracking file.** It carries
   the translated upstream module-level comments and a literal-quoted reference to each upstream
   `pub use` line (e.g. `// pub use crate::lib::result::Result;`). Each time a caller is migrated
   off the re-export, append the caller's absolute path under a `// Callers migrated:` ledger in
   `Mod.kt`. Append, never delete. Once all callers are migrated, the `typealias` (if any) is
   removed; the tracking file remains as the ledger of the migration.

Reference example: [/Volumes/stuff/Projects/kotlinmania/serde-kotlin/tmp/serde/serde_core/src/private/mod.rs](/Volumes/stuff/Projects/kotlinmania/serde-kotlin/tmp/serde/serde_core/src/private/mod.rs)
re-exports `Result` from `crate::lib::result`. The Kotlin tracking file lives at
[/Volumes/stuff/Projects/kotlinmania/serde-kotlin/src/commonMain/kotlin/io/github/kotlinmania/serde/core/private/Mod.kt](/Volumes/stuff/Projects/kotlinmania/serde-kotlin/src/commonMain/kotlin/io/github/kotlinmania/serde/core/private/Mod.kt).
A caller that previously did `import io.github.kotlinmania.serde.core.private.Result` is
rewritten to `import kotlin.Result as Result` (or just removes the import and relies on the
auto-imported `kotlin.Result`).
