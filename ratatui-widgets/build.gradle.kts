plugins { id("ratatui.android-crate") }

kotlin.sourceSets {
    named("commonMain") {
        dependencies {
            api(project(":ratatui-core"))
            implementation(libs.kotlinx.datetime)
        }
    }
}
