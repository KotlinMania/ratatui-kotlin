plugins { id("ratatui.android-crate") }

kotlin.sourceSets {
    named("commonMain") {
        dependencies {
            api(libs.bitflags)
            api(libs.thiserror)
            api(libs.unicode.width)
            api(libs.serde.core)
            implementation(libs.anstyle)
            implementation(libs.kasuari)
            implementation(libs.itertools)
            implementation(libs.lru)
            implementation(libs.strum)
            implementation(libs.unicode.segmentation)
        }
    }
    named("commonTest") { dependencies { implementation(libs.serde.json) } }
}
