plugins { `kotlin-dsl` }
repositories { google(); mavenCentral(); gradlePluginPortal() }
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    implementation("com.android.kotlin.multiplatform.library:com.android.kotlin.multiplatform.library.gradle.plugin:${libs.versions.androidKmp.get()}")
}
kotlin { jvmToolchain(21) }
