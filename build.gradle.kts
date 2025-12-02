plugins {
    kotlin("multiplatform") version "2.2.21"
}

kotlin {
    applyDefaultHierarchyTemplate()

    macosArm64()
    macosX64()
    linuxX64()
    mingwX64()

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            }
        }

        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
