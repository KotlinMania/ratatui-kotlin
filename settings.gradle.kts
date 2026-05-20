pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins { kotlin("multiplatform") version "2.3.21" }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ratatui-kotlin"

fun includeSiblingPort(path: String, coordinate: String) {
    val siblingBuild = file(path)
    if (siblingBuild.isDirectory) {
        includeBuild(siblingBuild) {
            dependencySubstitution {
                substitute(module(coordinate)).using(project(":"))
            }
        }
    }
}

includeSiblingPort("../anstyle-kotlin", "io.github.kotlinmania:anstyle-kotlin")
includeSiblingPort("../bitflags-kotlin", "io.github.kotlinmania:bitflags-kotlin")
includeSiblingPort("../itertools-kotlin", "io.github.kotlinmania:itertools-kotlin")
includeSiblingPort("../kasuari-kotlin", "io.github.kotlinmania:kasuari-kotlin")
includeSiblingPort("../lru-kotlin", "io.github.kotlinmania:lru-kotlin")
includeSiblingPort("../serde-kotlin", "io.github.kotlinmania:serde-kotlin")
includeSiblingPort("../strum-kotlin", "io.github.kotlinmania:strum-kotlin")
includeSiblingPort("../thiserror-kotlin", "io.github.kotlinmania:thiserror-kotlin")
includeSiblingPort("../time-kotlin", "io.github.kotlinmania:time-kotlin")
includeSiblingPort("../unicode-segmentation-kotlin", "io.github.kotlinmania:unicode-segmentation-kotlin")
includeSiblingPort("../unicode-width-kotlin", "io.github.kotlinmania:unicode-width-kotlin")
includeSiblingPort("../crossterm-kotlin", "io.github.kotlinmania:crossterm-kotlin")
