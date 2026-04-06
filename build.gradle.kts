import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin("multiplatform") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("com.android.kotlin.multiplatform.library") version "8.6.0"
    id("com.vanniktech.maven.publish") version "0.30.0"
}

group = "io.github.kotlinmania"
version = "0.1.8"

val androidSdkDir: String? =
    providers.environmentVariable("ANDROID_SDK_ROOT").orNull
        ?: providers.environmentVariable("ANDROID_HOME").orNull

if (androidSdkDir != null && file(androidSdkDir).exists()) {
    val localProperties = rootProject.file("local.properties")
    if (!localProperties.exists()) {
        val sdkDirPropertyValue = file(androidSdkDir).absolutePath.replace("\\", "/")
        localProperties.writeText("sdk.dir=$sdkDirPropertyValue")
    }
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
                api("io.github.kotlinmania:kasuari-kotlin:0.1.1")
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
            }
        }

        val desktopMain by creating {
            dependsOn(commonMain)
            dependencies {
                api("io.github.kotlinmania:crossterm-kotlin:0.1.2")
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

        val macosMain by getting {
            dependsOn(desktopMain)
        }

        val linuxMain by getting {
            dependsOn(desktopMain)
            dependencies {
                implementation("io.ktor:ktor-client-curl:3.0.3")
            }
        }

        val mingwMain by getting {
            dependsOn(desktopMain)
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

val enableIosSimulatorTests =
    providers.gradleProperty("enableIosSimulatorTests").map { it.toBoolean() }.orElse(false)

tasks.withType<KotlinNativeTest>().configureEach {
    if (!enableIosSimulatorTests.get() && (name == "iosX64Test" || name == "iosSimulatorArm64Test")) {
        enabled = false
    }
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
