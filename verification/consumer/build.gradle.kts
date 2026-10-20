plugins {
    kotlin("multiplatform") version "2.4.20"
    id("com.android.kotlin.multiplatform.library") version "9.4.1"
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
    jvm()
    android {
        namespace = "io.github.kotlinmania.ratatui.consumer"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
    }
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.kotlinmania:ratatui-kotlin:${providers.gradleProperty("producerVersion").get()}")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
