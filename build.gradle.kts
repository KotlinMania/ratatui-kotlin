import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("com.android.kotlin.multiplatform.library") version "9.2.0"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.kotlinmania"
version = "0.1.9"

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

    compilerOptions {
        allWarningsAsErrors.set(true)
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
        languageSettings.optIn("kotlin.time.ExperimentalTime")
    }

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

    swiftExport {
        moduleName = "Ratatui"
        flattenPackage = "io.github.kotlinmania.ratatui"
    }

    android {
        namespace = "io.github.kotlinmania.ratatui"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.kotlinmania:kasuari-kotlin:0.1.2")
                api("io.github.kotlinmania:anstyle-kotlin:0.1.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.6.0")
            }
        }

        val desktopMain by creating {
            dependsOn(commonMain)
            dependencies {
                api("io.github.kotlinmania:crossterm-kotlin:0.1.4")
            }
        }

        val nativeMain by getting {
            dependencies {
            }
        }

        val appleMain by getting {
            dependencies {}
        }

        val macosMain by getting {
            dependsOn(desktopMain)
        }

        val linuxMain by getting {
            dependsOn(desktopMain)
            dependencies {}
        }

        val mingwMain by getting {
            dependsOn(desktopMain)
            dependencies {}
        }

        val jsMain by getting {
            dependencies {}
        }

        val wasmJsMain by getting {
            dependencies {}
        }

        val androidMain by getting {
            dependencies {}
        }

        val commonTest by getting { dependencies { implementation(kotlin("test")) } }
    }
    jvmToolchain(21)
}

rootProject.extensions.configure<YarnRootExtension>("kotlinYarn") {
    resolution("diff", "8.0.3")
    resolution("serialize-javascript", "7.0.5")
    resolution("webpack", "5.106.2")
    resolution("follow-redirects", "1.16.0")
    resolution("lodash", "4.18.1")
    resolution("ajv", "8.20.0")
    resolution("brace-expansion", "5.0.5")
    resolution("flatted", "3.4.2")
    resolution("minimatch", "10.2.5")
    resolution("picomatch", "4.0.4")
    resolution("qs", "6.15.1")
    resolution("socket.io-parser", "4.2.6")
}

val enableIosSimulatorTests =
    providers.gradleProperty("enableIosSimulatorTests").map { it.toBoolean() }.orElse(true)

tasks.withType<KotlinNativeTest>().configureEach {
    if (!enableIosSimulatorTests.get() && (name == "iosX64Test" || name == "iosSimulatorArm64Test")) {
        enabled = false
    }
}


mavenPublishing {
    publishToMavenCentral()
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
