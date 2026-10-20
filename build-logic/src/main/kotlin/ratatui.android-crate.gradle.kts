plugins {
    id("ratatui.crate")
    id("com.android.kotlin.multiplatform.library")
}

// Applied only to full KMP crates. Native desktop backend/tooling projects do not
// acquire empty Android variants or an unconfigured AGP target.
kotlin {
    android {
        namespace = "io.github.kotlinmania." + project.name.replace("-", "")
        compileSdk = providers.gradleProperty("android.compileSdk").get().toInt()
        minSdk = providers.gradleProperty("android.minSdk").get().toInt()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }
    }
}
