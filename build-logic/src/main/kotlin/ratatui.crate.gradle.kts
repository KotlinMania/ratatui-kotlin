import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

group = providers.gradleProperty("project.group").get()
version = providers.gradleProperty("project.version").get()
val catalogs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val crate = project.name
val desktopOnly = crate in setOf("ratatui-crossterm", "xtask")
val framework = when (crate) {
    "ratatui-core" -> "RatatuiCore"
    "ratatui-widgets" -> "RatatuiWidgets"
    "ratatui-macros" -> "RatatuiMacros"
    "ratatui-crossterm" -> "RatatuiCrossterm"
    "xtask" -> "RatatuiXtask"
    else -> error("Unmapped Rust crate $crate")
}
val crateNamespace = "io.github.kotlinmania." + crate.replace("-", "")
val kotlinVersion = catalogs.findVersion("kotlin").get().requiredVersion
val projectDependencyHandler = project.dependencies
configurations.configureEach {
    if (name == "swiftExportClasspath") {
        dependencies.add(projectDependencyHandler.create("org.jetbrains.kotlin:swift-export-embeddable:$kotlinVersion"))
        dependencies.add(projectDependencyHandler.create("org.jetbrains.intellij.deps.kotlinx:kotlinx-coroutines-core-jvm:" +
            providers.gradleProperty("versions.intellij.coroutines").getOrElse("1.10.2-intellij-1")))
    }
}

kotlin {
    jvmToolchain(providers.gradleProperty("jvm.toolchain").get().toInt())
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_4)
        apiVersion.set(KotlinVersion.KOTLIN_2_4)
        allWarningsAsErrors.set(true)
        optIn.addAll("kotlin.time.ExperimentalTime", "kotlin.concurrent.atomics.ExperimentalAtomicApi", "kotlin.ExperimentalUnsignedTypes")
        freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xsuppress-version-warnings")
    }
    val xcf = XCFramework(framework)
    fun KotlinNativeTarget.framework(static: Boolean = false) {
        binaries.framework {
            if (crate in setOf("ratatui-widgets", "ratatui-macros", "ratatui-crossterm")) export(project(":ratatui-core"))
            if (crate == "ratatui-macros") export(project(":ratatui-widgets"))
            baseName = framework
            isStatic = static
            binaryOption("bundleId", crateNamespace)
            xcf.add(this)
        }
    }
    macosArm64 { framework() }
    linuxX64()
    mingwX64()
    if (!desktopOnly) {
        iosArm64 { framework(true) }
        iosSimulatorArm64 { framework(true) }
        tvosArm64 { framework() }
        tvosSimulatorArm64 { framework() }
        watchosDeviceArm64 { framework() }
        watchosSimulatorArm64 { framework() }
        linuxArm64()
        androidNativeArm64()
        androidNativeX64()
        js { browser(); nodejs() }
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs { browser(); nodejs() }
        @OptIn(ExperimentalWasmDsl::class)
        wasmWasi { nodejs() }

        jvm { compilerOptions { jvmTarget.set(JvmTarget.fromTarget(providers.gradleProperty("jvm.toolchain").get())) } }
    }
    applyDefaultHierarchyTemplate()
    swiftExport {
        moduleName = framework
        flattenPackage = crateNamespace
        @OptIn(org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl::class)
        configure { settings.put("enableCoroutinesSupport", "true") }
    }
    sourceSets.commonTest.dependencies { implementation(kotlin("test")) }
}

tasks.withType<AbstractTestTask>().configureEach {
    testLogging {
        events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
tasks.register("test") {
    group = "verification"
    dependsOn("allTests")
    if (!desktopOnly) dependsOn("testAndroidHostTest")
}
tasks.named("build") {
    dependsOn("test")
    dependsOn(kotlin.targets.withType<KotlinNativeTarget>().map { "${it.name}Binaries" })
    dependsOn(kotlin.targets.withType<KotlinNativeTarget>().map { "${it.name}TestBinaries" })
    dependsOn("assemble${framework}XCFramework")
    if (!desktopOnly) dependsOn("compileAndroidMain", "compileAndroidHostTest", "compileAndroidDeviceTest",
        "assembleAndroidMain", "assembleUnitTest", "assembleAndroidTest", "assembleAndroidDeviceTest",
        "jvmMainClasses", "jvmTestClasses", "jsMainClasses", "jsTestClasses",
        "wasmJsMainClasses", "wasmJsTestClasses", "wasmWasiMainClasses", "wasmWasiTestClasses")
}

// Each library owns its real KMP coordinates and AAR. Shared deployment, no manual extra AAR.
val javadoc = tasks.register<Jar>("emptyJavadocJar") { archiveClassifier.set("javadoc") }
publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(javadoc)
        pom {
            name.set(crate)
            description.set("Kotlin Multiplatform port of Rust's $crate crate")
            url.set("https://github.com/KotlinMania/ratatui-kotlin")
            licenses { license { name.set("MIT"); url.set("https://opensource.org/licenses/MIT") } }
            developers { developer { id.set("sydneyrenee"); name.set("Sydney Renee") } }
            scm {
                url.set("https://github.com/KotlinMania/ratatui-kotlin")
                connection.set("scm:git:git://github.com/KotlinMania/ratatui-kotlin.git")
                developerConnection.set("scm:git:ssh://github.com/KotlinMania/ratatui-kotlin.git")
            }
        }
    }
    repositories { maven { name = "centralPortalStaging"; url = uri(rootProject.layout.buildDirectory.dir("staging-deploy")) } }
}
signing {
    val key = providers.gradleProperty("signingInMemoryKey").orNull
    if (project.findProperty("RELEASE_SIGNING_ENABLED") != "false" && key != null) {
        useInMemoryPgpKeys(providers.gradleProperty("signingInMemoryKeyId").orNull, key,
            providers.gradleProperty("signingInMemoryKeyPassword").orNull)
        sign(publishing.publications)
    }
}
// xtask is build tooling, never a Maven library.
if (crate == "xtask") tasks.withType<org.gradle.api.publish.maven.tasks.PublishToMavenRepository>().configureEach {
    doFirst { error("xtask is not a publication boundary") }
}
