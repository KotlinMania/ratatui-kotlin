import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("com.android.kotlin.multiplatform.library") version "8.6.0"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.kotlinmania"
version = "0.1.2"

// Android setup
val sdkDir = file(".android-sdk")
val licensesDir = sdkDir.resolve("licenses")
if (!licensesDir.exists()) licensesDir.mkdirs()
val licenseFile = licensesDir.resolve("android-sdk-license")
if (!licenseFile.exists()) {
    licenseFile.writeText(
        """
        8933bad161af4178b1185d1a37fbf41ea5269c55
        d56f5187479451eabf01fb74abc367c344559d7b
        24333f8a63b6825ea9c5514f83c2829b004d1fee
        """.trimIndent()
    )
}
val localProperties: File? = rootProject.file("local.properties")
if (localProperties?.exists() == false) {
    localProperties.writeText("sdk.dir=${sdkDir.absolutePath}")
}

kotlin {
    applyDefaultHierarchyTemplate()

    sourceSets.all { languageSettings.optIn("kotlin.time.ExperimentalTime") }

    val xcf = XCFramework("Ratatui")

    macosArm64 {
        binaries.framework {
            baseName = "Ratatui"
            xcf.add(this)
        }
    }
    macosX64 {
        binaries.framework {
            baseName = "Ratatui"
            xcf.add(this)
        }
    }
    linuxX64()
    mingwX64()
    iosArm64 {
        binaries.framework {
            baseName = "Ratatui"
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "Ratatui"
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Ratatui"
            xcf.add(this)
        }
    }
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    androidLibrary {
        namespace = "io.github.kotlinmania.ratatui"
        compileSdk = 34
        minSdk = 24
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.kotlinmania:kasuari-kotlin:0.1.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")

                // Ktor HTTP client for multiplatform
                implementation("io.ktor:ktor-client-core:3.0.3")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
                implementation("io.ktor:ktor-client-auth:3.0.3")

                // File I/O
                implementation("com.squareup.okio:okio:3.9.1")

                // Character encoding support (for legacy codepage conversion)
                // fleeksoft-io provides JDK-like IO classes for Kotlin Multiplatform
                implementation("com.fleeksoft.io:io-core:0.0.4")
                implementation("com.fleeksoft.io:io:0.0.4")
                implementation("com.fleeksoft.charset:charset:0.0.4")
                implementation("com.fleeksoft.charset:charset-ext:0.0.4")
            }
        }

        val nativeMain by getting {
            dependencies {
            }
        }

        val appleMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.0.3")
            }
        }

        val linuxMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-curl:3.0.3")
            }
        }

        val mingwMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-curl:3.0.3")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:3.0.3")
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:3.0.3")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:3.0.3")
            }
        }

        val commonTest by getting { dependencies { implementation(kotlin("test")) } }
    }
    jvmToolchain(21)
}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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
