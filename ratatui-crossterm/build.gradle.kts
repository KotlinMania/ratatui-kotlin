plugins { id("ratatui.crate") }

kotlin.sourceSets {
    named("commonMain") {
        dependencies {
            api(project(":ratatui-core"))
            implementation(libs.crossterm)
        }
    }
}
