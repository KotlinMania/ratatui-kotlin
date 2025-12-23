pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins { kotlin("multiplatform") version "2.3.0" }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ratatui-kotlin"

// Include local kasuari-kotlin for development (until kasuari-kotlin 0.1.1+ is published with all targets)
// Remove this once kasuari-kotlin is published to Maven Central with iOS/JS/WASM/Android targets
includeBuild("../kasuari-kotlin") {
    dependencySubstitution {
        substitute(module("io.github.kotlinmania:kasuari-kotlin")).using(project(":"))
    }
}
