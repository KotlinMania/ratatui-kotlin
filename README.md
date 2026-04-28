# ratatui-kotlin

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3+-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](#license)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kotlinmania/ratatui-kotlin?color=blue)](https://central.sonatype.com/artifact/io.github.kotlinmania/ratatui-kotlin)
[![GitHub](https://img.shields.io/badge/github-KotlinMania%2Fratatui--kotlin-blue?logo=github)](https://github.com/KotlinMania/ratatui-kotlin)

A **Kotlin Multiplatform** library for building terminal user interfaces (TUIs). This is a port
of the Rust [ratatui](https://github.com/ratatui/ratatui) crate.

## Overview

ratatui-kotlin provides a flexible framework for creating text-based user interfaces in the terminal.
It can be used for command-line applications, dashboards, and other interactive console programs.

The library is a faithful transliteration of the original Rust implementation, maintaining API parity
while adapting to idiomatic Kotlin patterns.

## Features

- **Widgets** - Paragraph, List, Table, Tabs, Block, Gauge, Chart, Canvas, and more
- **Layout** - Constraint-based layout system powered by the Cassowary algorithm (via kasuari-kotlin)
- **Styling** - Full color support (16, 256, RGB), text attributes (bold, italic, underline, etc.)
- **Buffer** - Efficient double-buffered rendering with diff-based terminal updates
- **Text** - Rich text with spans, lines, and styled content
- **Backend** - Terminal backend abstraction (via crossterm-kotlin)

## Supported Platforms

| Platform | Status | Notes |
|----------|--------|-------|
| macOS (arm64, x64) | Full | Native terminal support |
| Linux (x64) | Full | Native terminal support |
| Windows (x64) | Full | Native console API via MinGW |
| iOS (arm64, x64, simulatorArm64) | Partial | Styling only (no TTY) |
| Android (API 24+) | Partial | Styling only (no TTY) |
| JS (Browser, Node.js) | Partial | ANSI output only |
| WasmJS | Partial | ANSI output only |

## Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.kotlinmania:ratatui-kotlin:0.1.7")
}
```

### As a Git Submodule

```bash
git submodule add https://github.com/KotlinMania/ratatui-kotlin.git
```

Then in your `settings.gradle.kts`:

```kotlin
include(":ratatui-kotlin")
```

And in your module's `build.gradle.kts`:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":ratatui-kotlin"))
            }
        }
    }
}
```

## Quick Start

```kotlin
import ratatui.backend.CrosstermBackend
import ratatui.terminal.Terminal
import ratatui.widgets.Block
import ratatui.widgets.Borders
import ratatui.widgets.Paragraph
import ratatui.style.Style
import ratatui.style.Color

// Create a terminal with the crossterm backend
val backend = CrosstermBackend()
val terminal = Terminal(backend)

// Draw a simple UI
terminal.draw { frame ->
    val block = Block.default()
        .title("Hello ratatui-kotlin")
        .borders(Borders.ALL)

    val paragraph = Paragraph.new("Welcome to TUI programming in Kotlin!")
        .block(block)
        .style(Style.default().fg(Color.Cyan))

    frame.renderWidget(paragraph, frame.area())
}
```

## Dependencies

ratatui-kotlin builds on other KotlinMania libraries:

- [crossterm-kotlin](https://github.com/KotlinMania/crossterm-kotlin) - Terminal manipulation
- [kasuari-kotlin](https://github.com/KotlinMania/kasuari-kotlin) - Cassowary constraint solver for layout
- [anstyle-kotlin](https://github.com/KotlinMania/anstyle-kotlin) - ANSI style definitions

## Building from Source

```bash
git clone https://github.com/KotlinMania/ratatui-kotlin.git
cd ratatui-kotlin
./gradlew assemble
./gradlew allTests
```

## Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## License

This project is licensed under the [MIT License](./LICENSE).

---

## Acknowledgments

This Kotlin Multiplatform port was created by **Sydney Renee** of [The Solace Project](mailto:sydney@solace.ofharmony.ai)
for [KotlinMania](https://github.com/KotlinMania).

Special thanks to the original authors and community:

- The [Ratatui](https://github.com/ratatui) team for the excellent Rust TUI framework
- [Florian Dehau](https://github.com/fdehau) for the original [tui-rs](https://crates.io/crates/tui) crate that Ratatui was forked from
