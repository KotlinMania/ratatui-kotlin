plugins { id("ratatui.android-crate") }

kotlin.sourceSets {
    commonMain.dependencies {
        api(project(":ratatui-core"))
        api(project(":ratatui-widgets"))
    }
}
