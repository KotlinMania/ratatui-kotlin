plugins { id("ratatui.crate") }

kotlin.sourceSets {
    commonMain.dependencies {
        api(project(":ratatui-core"))
        implementation(libs.crossterm)
    }
}
