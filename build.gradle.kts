plugins {
    kotlin("multiplatform") version "2.2.10"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.kotlinmania"
version = "0.1.0-SNAPSHOT"

kotlin {
    applyDefaultHierarchyTemplate()

    // Native targets
    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            // Uses standard src/commonMain/kotlin layout
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nativeMain by getting {
            // No external dependencies - ratatui-kotlin is a pure Kotlin library
        }

        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(group.toString(), "ratatui-kotlin", version.toString())

    pom {
        name.set("ratatui-kotlin")
        description.set("Kotlin Multiplatform port of ratatui - a library for building terminal user interfaces")
        inceptionYear.set("2024")
        url.set("https://github.com/KotlinMania/ratatui-kotlin")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("sydneyrenee")
                name.set("Sydney Renee")
                email.set("sydney@solace.ofharmony.ai")
                url.set("https://github.com/sydneyrenee")
            }
        }

        scm {
            url.set("https://github.com/KotlinMania/ratatui-kotlin")
            connection.set("scm:git:git://github.com/KotlinMania/ratatui-kotlin.git")
            developerConnection.set("scm:git:ssh://github.com/KotlinMania/ratatui-kotlin.git")
        }
    }
}
