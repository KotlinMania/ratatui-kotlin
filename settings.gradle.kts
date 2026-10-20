pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ratatui-kotlin"

// Real crate ownership matching the pinned upstream Cargo workspace.
include(":ratatui-core", ":ratatui-widgets", ":ratatui-macros", ":ratatui-crossterm", ":xtask")
