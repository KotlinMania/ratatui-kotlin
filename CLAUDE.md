# ratatui-kotlin Transliteration Guide

This project is a **Kotlin Multiplatform Native** port of [ratatui](https://github.com/ratatui-org/ratatui), the Rust TUI library.

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
