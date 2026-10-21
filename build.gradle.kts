import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.plugins.signing.Sign
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Base64
import java.util.UUID
import java.util.zip.ZipInputStream

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.kotlin.allopen)
    `maven-publish`
    signing
}

group = providers.gradleProperty("project.group").getOrElse("io.github.kotlinmania")
version = providers.gradleProperty("project.version").getOrElse("0.1.0-SNAPSHOT")
val frameworkName = providers.gradleProperty("project.frameworkName").getOrElse("Unnamed")
val projectNamespace = providers.gradleProperty("project.namespace").getOrElse("io.github.kotlinmania")
val kotlinVersion = providers.gradleProperty("versions.kotlin").getOrElse("2.4.0")

fun csvProperty(name: String): Set<String> =
    providers
        .gradleProperty(name)
        .map { value ->
            value
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
        }.getOrElse(emptySet())

fun optionalTrimmedProperty(name: String): String? =
    providers
        .gradleProperty(name)
        .map { it.trim() }
        .orNull
        ?.takeIf { it.isNotEmpty() }

val enabledFeatureNames = csvProperty("project.features")
val benchmarkEnabled = "benchmark" in enabledFeatureNames
val benchmarkTargetNames = csvProperty("project.benchmark.targets")
val commonBenchmarkBundleName = optionalTrimmedProperty("project.dependencies.commonBenchmarkBundle")
val commonBenchmarkDependencyBundle =
    commonBenchmarkBundleName?.let { bundleName ->
        extensions
            .getByType(VersionCatalogsExtension::class.java)
            .named("libs")
            .findBundle(bundleName)
            .orElseThrow { GradleException("Missing libs bundle '$bundleName'") }
    }
val commonTestBundleName = optionalTrimmedProperty("project.dependencies.commonTestBundle")
val commonTestDependencyBundle =
    commonTestBundleName?.let { bundleName ->
        extensions
            .getByType(VersionCatalogsExtension::class.java)
            .named("libs")
            .findBundle(bundleName)
            .orElseThrow { GradleException("Missing libs bundle '$bundleName'") }
    }
if (benchmarkEnabled && commonBenchmarkDependencyBundle == null) {
    throw GradleException("Feature 'benchmark' requires project.dependencies.commonBenchmarkBundle")
}
val benchmarkWarmups = providers.gradleProperty("project.benchmark.warmups").map { it.toInt() }.getOrElse(3)
val benchmarkIterations = providers.gradleProperty("project.benchmark.iterations").map { it.toInt() }.getOrElse(5)
val benchmarkIterationTime = providers.gradleProperty("project.benchmark.iterationTime").map { it.toLong() }.getOrElse(1L)
val benchmarkIterationTimeUnit = providers.gradleProperty("project.benchmark.iterationTimeUnit").getOrElse("s")
val intellijCoroutinesVersion =
    providers.gradleProperty("versions.intellij.coroutines").getOrElse("1.10.2-intellij-1")

// KGP runs Swift Export in an isolated worker whose classpath is
// `swiftExportClasspath`. Adding a dependency disables KGP's default
// dependency population, so keep the default embeddable runner explicit too.
val projectDependencyHandler = project.dependencies
configurations.configureEach {
    if (name == "swiftExportClasspath") {
        dependencies.add(projectDependencyHandler.create("org.jetbrains.kotlin:swift-export-embeddable:$kotlinVersion"))
        dependencies.add(
            projectDependencyHandler.create(
                "org.jetbrains.intellij.deps.kotlinx:kotlinx-coroutines-core-jvm:$intellijCoroutinesVersion",
            ),
        )
    }
}

// Opt-ins shared across Kotlin targets.
val commonOptIns =
    listOf(
        "kotlin.time.ExperimentalTime",
        "kotlin.concurrent.atomics.ExperimentalAtomicApi",
        "kotlin.ExperimentalUnsignedTypes",
    )

// ============================================================================
// Android SDK installer
// ----------------------------------------------------------------------------
// The Android Gradle Plugin resolves the SDK location at configuration time,
// so the SDK must already be on disk before the `kotlin { android { ... } }`
// block evaluates. The installer is idempotent — a .install-complete marker
// short-circuits the download on every subsequent invocation, so warm runs
// pay only a directory-existence check. CI runners pay a one-time cold cost
// the first time they touch the project.
// ============================================================================

val androidCommandLineToolsRevision =
    providers
        .gradleProperty(
            "android.commandLineTools.revision",
        ).getOrElse("14742923")
val projectCompileSdk = providers.gradleProperty("android.compileSdk").getOrElse("34")
val projectAndroidBuildTools = providers.gradleProperty("android.buildTools").getOrElse("36.0.0")
val osName = providers.systemProperty("os.name").get().lowercase()
val isWindowsHost = "windows" in osName
val isMacHost = "mac" in osName
val androidSdkOsName =
    when {
        isWindowsHost -> "win"
        isMacHost -> "mac"
        "linux" in osName -> "linux"
        else -> throw GradleException("Unsupported Android SDK setup OS: ${providers.systemProperty("os.name").get()}")
    }
val projectAndroidSdkDir = rootProject.layout.projectDirectory.dir(".android-sdk").asFile
val androidSdkManager =
    projectAndroidSdkDir.resolve(
        if (isWindowsHost) {
            "cmdline-tools/latest/bin/sdkmanager.bat"
        } else {
            "cmdline-tools/latest/bin/sdkmanager"
        },
    )
val androidSdkInstallMarker = projectAndroidSdkDir.resolve(".install-complete")
val requiredAndroidSdkPackageDirs =
    listOf(
        projectAndroidSdkDir.resolve("platform-tools"),
        projectAndroidSdkDir.resolve("platforms/android-$projectCompileSdk"),
        projectAndroidSdkDir.resolve("build-tools/$projectAndroidBuildTools"),
    )

fun writeAndroidLocalProperties() {
    projectAndroidSdkDir.mkdirs()
    val sdkDirPropertyValue = projectAndroidSdkDir.absolutePath.replace("\\", "/")
    (listOf(project) + subprojects).forEach { owner ->
        owner.layout.projectDirectory.file("local.properties").asFile.writeText("sdk.dir=$sdkDirPropertyValue\n")
    }
}

fun isProjectAndroidSdkInstalled(): Boolean =
    androidSdkInstallMarker.exists() &&
        androidSdkManager.exists() &&
        requiredAndroidSdkPackageDirs.all { it.exists() }

fun sdkManagerCommand(vararg args: String): List<String> =
    if (isWindowsHost) {
        listOf("cmd", "/c", androidSdkManager.absolutePath) + args
    } else {
        listOf(androidSdkManager.absolutePath) + args
    }

fun downloadAndroidCommandLineTools() {
    val zipName = "commandlinetools-$androidSdkOsName-${androidCommandLineToolsRevision}_latest.zip"
    val url = "https://dl.google.com/android/repository/$zipName"
    val tmpDir = projectAndroidSdkDir.resolve(".tmp/commandline-tools")
    val zipFile = tmpDir.resolve(zipName)
    val latestDir = projectAndroidSdkDir.resolve("cmdline-tools/latest")
    println("setup-android-sdk: downloading $url")
    tmpDir.deleteRecursively()
    tmpDir.mkdirs()
    try {
        URI(url).toURL().openStream().use { input ->
            Files.copy(input, zipFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        latestDir.deleteRecursively()
        latestDir.mkdirs()
        val canonicalLatestDir = latestDir.canonicalFile.toPath()
        ZipInputStream(zipFile.inputStream().buffered()).use { zipInput ->
            generateSequence { zipInput.nextEntry }.forEach { entry ->
                val relativeName = entry.name.removePrefix("cmdline-tools/").trimStart('/')
                if (relativeName.isNotEmpty()) {
                    val target = latestDir.resolve(relativeName).canonicalFile
                    if (!target.toPath().startsWith(canonicalLatestDir)) {
                        throw GradleException("Refusing to extract Android SDK entry outside $latestDir: ${entry.name}")
                    }
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile.mkdirs()
                        Files.copy(zipInput, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        if (!isWindowsHost && relativeName.startsWith("bin/")) target.setExecutable(true)
                    }
                }
                zipInput.closeEntry()
            }
        }
        if (!isWindowsHost) androidSdkManager.setExecutable(true)
    } finally {
        tmpDir.deleteRecursively()
    }
}

fun installProjectAndroidSdk(execOperations: ExecOperations) {
    if (isProjectAndroidSdkInstalled()) {
        writeAndroidLocalProperties()
        println("setup-android-sdk: SDK already installed at $projectAndroidSdkDir")
        return
    }
    if (!androidSdkManager.exists()) downloadAndroidCommandLineTools()
    println("setup-android-sdk: accepting licenses")
    val licenseAnswers = "y\n".repeat(200).toByteArray(Charsets.UTF_8)
    val licenseResult =
        execOperations.exec {
            commandLine(sdkManagerCommand("--sdk_root=${projectAndroidSdkDir.absolutePath}", "--licenses"))
            standardInput = ByteArrayInputStream(licenseAnswers)
            isIgnoreExitValue = true
        }
    if (licenseResult.exitValue != 0) {
        throw GradleException("Android SDK license acceptance failed with exit code ${licenseResult.exitValue}")
    }
    println(
        "setup-android-sdk: installing platform-tools, android-$projectCompileSdk, build-tools;$projectAndroidBuildTools",
    )
    val installLog = projectAndroidSdkDir.resolve("sdkmanager-install.log")
    installLog.parentFile.mkdirs()
    installLog.outputStream().use { output ->
        val installResult =
            execOperations.exec {
                commandLine(
                    sdkManagerCommand(
                        "--sdk_root=${projectAndroidSdkDir.absolutePath}",
                        "platform-tools",
                        "platforms;android-$projectCompileSdk",
                        "build-tools;$projectAndroidBuildTools",
                    ),
                )
                standardOutput = output
                errorOutput = output
                isIgnoreExitValue = true
            }
        if (installResult.exitValue != 0) {
            throw GradleException(
                "Android SDK package install failed with exit code ${installResult.exitValue}. " +
                    "Install log:\n${installLog.readText()}",
            )
        }
    }
    writeAndroidLocalProperties()
    androidSdkInstallMarker.writeText("")
    println("setup-android-sdk: done; SDK at $projectAndroidSdkDir")
}

// ----------------------------------------------------------------------------
// Android SDK setup is gated to follow the requested task. It must never run for
// non-Android invocations (jsTest, jvmTest, swiftExportSmokeTest, native /
// androidNative links) -- an unconditional install here is what made the SDK
// download appear on every machine and target.
//
// `writeAndroidLocalProperties()` always runs: it is cheap, hits no network, and
// only points local.properties at the project-local .android-sdk so AGP can
// resolve `sdk.dir` while the `androidLibrary {}` block evaluates.
//
// The SDK *package* download must happen at configuration time when -- and only
// when -- an Android task is in the requested build. AGP validates the packages
// while determining the dependencies of `compileAndroidMain` (task-graph
// construction, strictly before any task executes), so a plain `dependsOn`
// cannot supply them in time. We detect Android intent from the requested task
// names and install eagerly in that case. androidNative* are Kotlin/Native
// targets and need no Android SDK.
// ----------------------------------------------------------------------------
writeAndroidLocalProperties()

fun requestedTaskWantsAndroid(rawTaskName: String): Boolean {
    val taskName = rawTaskName.substringAfterLast(':')
    if (taskName.contains("AndroidNative")) return false // Kotlin/Native, no SDK
    if (taskName.contains("Android")) return true // direct AGP tasks
    return taskName in setOf("build", "assemble", "check") // aggregates pull android
}

if (gradle.startParameter.taskNames.any(::requestedTaskWantsAndroid)) {
    installProjectAndroidSdk(serviceOf())
}

val ensureAndroidSdk =
    tasks.register("ensureAndroidSdk") {
        group = "setup"
        description = "Ensures the project-local Android SDK is installed (idempotent)."
        onlyIf("Android SDK already installed at $projectAndroidSdkDir") { !isProjectAndroidSdkInstalled() }
        doLast {
            installProjectAndroidSdk(serviceOf())
        }
    }

// Secondary net: order every AGP Android task after the installer (a no-op on
// warm runs). Excludes androidNative* (Kotlin/Native) and the installer itself.
tasks
    .matching { task ->
        val taskName = task.name
        taskName != "ensureAndroidSdk" &&
            taskName.contains("Android") &&
            !taskName.contains("AndroidNative")
    }.configureEach {
        dependsOn(ensureAndroidSdk)
    }

val jvmToolchainVersion = providers.gradleProperty("jvm.toolchain").getOrElse("21").toInt()

// ============================================================================
// kotlin { … }
// ----------------------------------------------------------------------------
// Workspace product policy retires every native 32-bit target, including
//   watchosArm32 / watchosArm64 (arm64_32), androidNativeArm32 / androidNativeX86,
//   linuxArm32Hfp, and mingwX86. Do not re-add their declarations or CI tasks.
// Apple-specific x64 targets are also retired: iosX64, macosX64, tvosX64,
//   watchosX64. Linux/Windows x64 and Android native x64 remain supported.
// Every retained target is built unconditionally, with no opt-in build gates.
// ============================================================================
kotlin {
    jvmToolchain(jvmToolchainVersion)

    applyDefaultHierarchyTemplate()

    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_4)
        apiVersion.set(KotlinVersion.KOTLIN_2_4)
        allWarningsAsErrors.set(true)
        optIn.addAll(commonOptIns)
        freeCompilerArgs.addAll("-Xexpect-actual-classes", "-Xsuppress-version-warnings")
    }

    val xcf = XCFramework(frameworkName)
    val frameworkBundleId = projectNamespace

    // Local helper: attach this target's framework to the XCFramework.
    fun KotlinNativeTarget.addToXcf(static: Boolean = false) {
        val ownerTarget = name
        binaries.framework {
            export(project(":ratatui-core"))
            export(project(":ratatui-widgets"))
            export(project(":ratatui-macros"))
            if (ownerTarget == "macosArm64") export(project(":ratatui-crossterm"))
            baseName = frameworkName
            if (static) isStatic = true
            xcf.add(this)
            binaryOption("bundleId", frameworkBundleId)
        }
    }

    fun KotlinTarget.configureBenchmarkCompilation() {
        if (!benchmarkEnabled || name !in benchmarkTargetNames) return
        val mainCompilation = compilations.getByName("main")
        compilations.create("benchmark") {
            associateWith(mainCompilation)
            defaultSourceSet.dependencies {
                implementation(commonBenchmarkDependencyBundle!!)
            }
        }
    }

    // Apple — Tier 1/2 targets
    macosArm64 {
        configureBenchmarkCompilation()
        addToXcf()
    }
    iosArm64 {
        configureBenchmarkCompilation()
        addToXcf(static = true)
    }
    iosSimulatorArm64 {
        configureBenchmarkCompilation()
        addToXcf(static = true)
    }
    tvosArm64 {
        configureBenchmarkCompilation()
        addToXcf()
    }
    tvosSimulatorArm64 {
        configureBenchmarkCompilation()
        addToXcf()
    }
    // watchosArm64 (WatchOS 32 / arm64_32): retired by workspace policy (§5.5.1). WatchOS 32 is not supported.
    watchosDeviceArm64 {
        configureBenchmarkCompilation()
        addToXcf()
    }
    watchosSimulatorArm64 {
        configureBenchmarkCompilation()
        addToXcf()
    }

    // Other native — Tier 1/2
    linuxX64 { configureBenchmarkCompilation() }
    linuxArm64 { configureBenchmarkCompilation() }
    mingwX64 { configureBenchmarkCompilation() }

    // Android NDK — 64-bit only (32-bit retired §5.5.3, 2026-06-25).
    androidNativeArm64 { configureBenchmarkCompilation() }
    androidNativeX64 { configureBenchmarkCompilation() }

    // Web
    js {
        configureBenchmarkCompilation()
        browser()
        nodejs()
    }

    // wasmJs is Stable as of Kotlin 2.2; @OptIn may be removable — verify before dropping on wasmWasi.
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        configureBenchmarkCompilation()
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        configureBenchmarkCompilation()
        nodejs()
    }

    // Swift Export bridge — Experimental per Kotlin 2.4.0 release notes.
    // KGP 2.4.0 does not expose a public opt-in annotation; warnings (if any)
    // arrive via KotlinToolingDiagnostics, not @RequiresOptIn.
    @OptIn(org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl::class)
    swiftExport {
        moduleName = frameworkName
        export(project(":ratatui-core")) { moduleName = "RatatuiCore" }
        export(project(":ratatui-widgets")) { moduleName = "RatatuiWidgets" }
        export(project(":ratatui-macros")) { moduleName = "RatatuiMacros" }
        export(project(":ratatui-crossterm")) { moduleName = "RatatuiCrossterm" }
        flattenPackage = projectNamespace
        @OptIn(org.jetbrains.kotlin.gradle.swiftexport.ExperimentalSwiftExportDsl::class)
        configure {
            settings.put("enableCoroutinesSupport", "true")
        }
    }

    // Android KMP library. Block name is `android` — `androidLibrary` is deprecated in current KGP.
    android {
        namespace = projectNamespace
        compileSdk = projectCompileSdk.toInt()
        minSdk = providers.gradleProperty("android.minSdk").getOrElse("24").toInt()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }
    }

    // JVM — jvmTarget derived from the same toolchain property so they can't drift.
    jvm {
        configureBenchmarkCompilation()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(jvmToolchainVersion.toString()))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":ratatui-core"))
            api(project(":ratatui-widgets"))
            api(project(":ratatui-macros"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.serde.json)
            commonTestDependencyBundle?.let { implementation(it) }
        }

        // desktopMain: intermediate source set for desktop terminal backends
        // (crossterm). Wired only to targets where crossterm-kotlin is published:
        // macosArm64, macosX64, linuxX64, mingwX64, ios*, js, wasmJs, android.
        // Not wired to jvm, tvos*, watchos*, androidNative*, or wasmWasi (no
        // crossterm-kotlin artifact for those targets).
        val desktopMain = create("desktopMain") {
            dependsOn(commonMain.get())
        }
        desktopMain.dependencies {
            api(project(":ratatui-crossterm"))
        }

        if (benchmarkEnabled) {
            val commonBenchmark = maybeCreate("commonBenchmark")
            commonBenchmark.dependencies {
                implementation(commonBenchmarkDependencyBundle!!)
            }
            benchmarkTargetNames.forEach { targetName ->
                findByName("${targetName}Benchmark")?.dependsOn(commonBenchmark)
            }
        }
    }

    // Wire desktopMain into native desktop targets where crossterm-kotlin is published.
    targets.withType<KotlinNativeTarget>().matching { it.name in setOf("macosArm64", "linuxX64", "mingwX64") }.configureEach {
        val desktopMainSourceSet = kotlin.sourceSets.findByName("desktopMain")!!
        compilations.getByName("main").defaultSourceSet.dependsOn(desktopMainSourceSet)
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
    annotation("kotlinx.benchmark.State")
}

if (benchmarkEnabled) {
    benchmark {
        targets {
            benchmarkTargetNames.forEach { targetName ->
                register("${targetName}Benchmark")
            }
        }
        configurations {
            named("main") {
                warmups = benchmarkWarmups
                iterations = benchmarkIterations
                iterationTime = benchmarkIterationTime
                iterationTimeUnit = benchmarkIterationTimeUnit
            }
        }
    }
}

// ============================================================================
// Test logging
// ============================================================================
tasks.withType<AbstractTestTask>().configureEach {
    testLogging {
        events(
            TestLogEvent.STARTED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.STANDARD_ERROR,
        )
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
        showStandardStreams = true
    }
}

// ============================================================================
// Static analysis: Detekt + Ktlint
// ============================================================================
detekt {
    buildUponDefaultConfig.set(true)
    allRules.set(false)
    autoCorrect.set(false)
    source.setFrom(files("src", "ratatui-core/src", "ratatui-widgets/src", "ratatui-macros/src", "ratatui-crossterm/src", "xtask/src"))
    config.setFrom(files("detekt.yml"))
    parallel.set(true)
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    reports {
        html.required.set(true)
        sarif.required.set(true)
        checkstyle.required.set(false)
    }
}

ktlint {
    debug.set(false)
    verbose.set(false)
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
    }
    filter {
        exclude("**/build/**")
        include("**/src/**/kotlin/**")
    }
}

if (benchmarkEnabled) {
    tasks
        .withType<dev.detekt.gradle.Detekt>()
        .matching {
            it.name.contains("BenchmarkBenchmark")
        }.configureEach {
            enabled = false
        }

    tasks
        .matching {
            it.name.startsWith("runKtlintCheckOver") && it.name.endsWith("BenchmarkBenchmarkSourceSet")
        }.configureEach {
            enabled = false
        }
}

tasks.named("check") {
    dependsOn(tasks.withType<dev.detekt.gradle.Detekt>())
    dependsOn(tasks.named("ktlintCheck"))
    dependsOn("test")
}

// ============================================================================
// JS / Wasm toolchain pins
// ============================================================================
val nodeVersion = providers.gradleProperty("node.version").getOrElse("24.15.0")
val wasmNodeVersion = providers.gradleProperty("wasm.node.version").getOrElse(nodeVersion)
val yarnVersion = providers.gradleProperty("yarn.version").getOrElse("1.22.22")
val wasmYarnVersion = providers.gradleProperty("wasm.yarn.version").getOrElse(yarnVersion)

// webpack is pinned in kotlin-js-store/package.json — the single source of truth
// that Dependabot updates natively. Gradle reads the version from there so the
// yarn resolution and the NodeJsRootExtension pin always track the checked-in
// store; a Dependabot bump of package.json/yarn.lock is honored rather than
// overridden. (These two values previously lived in gradle.properties, which
// Dependabot cannot see, so a bump there would silently revert the build.)
@Suppress("UNCHECKED_CAST")
val webpackVersion: String =
    (groovy.json.JsonSlurper().parse(rootProject.file("kotlin-js-store/package.json")) as Map<String, Any>)
        .let { it["dependencies"] as Map<String, Any> }["webpack"] as String

rootProject.extensions.configure<NodeJsEnvSpec>("kotlinNodeJsSpec") { version.set(nodeVersion) }
rootProject.extensions.configure<WasmNodeJsEnvSpec>("kotlinWasmNodeJsSpec") { version.set(wasmNodeVersion) }
rootProject.extensions.configure<YarnRootEnvSpec>("kotlinYarnSpec") {
    version.set(yarnVersion)
    yarnLockMismatchReport.set(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport.WARNING)
    yarnLockAutoReplace.set(true)
}
rootProject.extensions.configure<WasmYarnRootEnvSpec>("kotlinWasmYarnSpec") {
    version.set(wasmYarnVersion)
    yarnLockMismatchReport.set(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport.WARNING)
    yarnLockAutoReplace.set(true)
}

rootProject.extensions.configure<YarnRootExtension>("kotlinYarn") {
    providers
        .gradlePropertiesPrefixedBy("yarn.resolution.")
        .getOrElse(emptyMap())
        .forEach { (key, ver) ->
            val pkg = key.removePrefix("yarn.resolution.")
            resolution(pkg, ver)
            resolution("**/$pkg", ver)
        }
    // webpack resolution sourced from kotlin-js-store/package.json (see above)
    // rather than a yarn.resolution.webpack property, so it can never override a
    // Dependabot bump of the store.
    resolution("webpack", webpackVersion)
    resolution("**/webpack", webpackVersion)
}

val patchedKarmaWebpackPackage =
    rootProject.layout.projectDirectory
        .dir("gradle/npm/karma-webpack")
        .asFile.absolutePath
        .replace("\\", "/")

// TODO: NodeJsRootExtension.versions.* is deprecated and will be removed when the spec-based
//       NodeJsEnvSpec API gains equivalent properties. Track KGP release notes before removing.
rootProject.extensions.configure<NodeJsRootExtension>("kotlinNodeJs") {
    versions.webpack.version = webpackVersion
    versions.webpackCli.version = providers.gradleProperty("node.webpackCli.version").getOrElse("7.0.2")
    versions.karma.version = providers.gradleProperty("node.karma.version").getOrElse("npm:karma-maintained@6.4.7")
    versions.karmaWebpack.version = "file:$patchedKarmaWebpackPackage"
    versions.mocha.version = providers.gradleProperty("node.mocha.version").getOrElse("12.0.0-beta-10")
    versions.kotlinWebHelpers.version = providers.gradleProperty("node.kotlinWebHelpers.version").getOrElse("3.1.0")
}

// Make kotlinUpgradeYarnLock and kotlinWasmUpgradeYarnLock dependencies in the build process
// for KotlinJS and other JavaScript/WASM targets so that yarn.lock is always upgraded automatically.
val jsTasksNeedingYarnLock =
    setOf(
        "compileKotlinJs",
        "compileTestKotlinJs",
        "jsProcessResources",
        "jsTestProcessResources",
        "jsNodeTest",
        "jsBrowserTest",
        "kotlinStoreYarnLock",
    )

tasks
    .matching { it.name in jsTasksNeedingYarnLock }
    .configureEach {
        dependsOn("kotlinUpgradeYarnLock")
    }

val wasmTasksNeedingYarnLock =
    setOf(
        "compileKotlinWasmJs",
        "compileTestKotlinWasmJs",
        "wasmJsProcessResources",
        "wasmJsTestProcessResources",
        "wasmJsNodeTest",
        "wasmJsBrowserTest",
        "compileKotlinWasmWasi",
        "compileTestKotlinWasmWasi",
        "wasmWasiProcessResources",
        "wasmWasiTestProcessResources",
        "wasmWasiNodeTest",
        "kotlinWasmStoreYarnLock",
    )

tasks
    .matching { it.name in wasmTasksNeedingYarnLock }
    .configureEach {
        dependsOn("kotlinWasmUpgradeYarnLock")
    }

// ============================================================================
// Maven Central publishing — Central Portal, first-party + bespoke upload
// ----------------------------------------------------------------------------
// OSSRH was sunset 2025-06-30; the Central Portal is the only path. Sonatype
// ships no first-party Gradle plugin, so we use Gradle's own maven-publish +
// signing (KGP populates the KMP publications) and upload the deployment
// bundle to the Portal API ourselves.
//
// Flow: publish all KMP publications into a local staging Maven layout ->
// zip it -> POST the zip to the Portal upload endpoint with a Bearer token.
// ============================================================================
val publishProjectName = providers.gradleProperty("project.name").getOrElse("unnamed-project")

// Central requires a Javadoc jar per publication; KMP produces none, so attach
// an empty one to every Maven publication.
val emptyJavadocJar =
    tasks.register<Jar>("emptyJavadocJar") {
        archiveClassifier.set("javadoc")
    }

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(emptyJavadocJar)
        pom {
            name.set(publishProjectName)
            description.set(providers.gradleProperty("project.pom.description").getOrElse(""))
            inceptionYear.set("2026")
            url.set("https://github.com/KotlinMania/$publishProjectName")
            licenses {
                license {
                    name.set(providers.gradleProperty("project.pom.licenseName").getOrElse("MIT"))
                    url.set(
                        providers
                            .gradleProperty("project.pom.licenseUrl")
                            .getOrElse("https://opensource.org/licenses/MIT"),
                    )
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("sydneyrenee")
                    name.set("Sydney Renee")
                    email.set("sydney@solace.ofharmony.ai")
                    url.set("https://github.com/sydneyrenee")
                }
            }
            scm {
                url.set("https://github.com/KotlinMania/$publishProjectName")
                connection.set("scm:git:git://github.com/KotlinMania/$publishProjectName.git")
                developerConnection.set("scm:git:ssh://github.com/KotlinMania/$publishProjectName.git")
            }
        }
    }

    // Stage into a local Maven layout that becomes the Portal deployment bundle.
    // maven-publish auto-generates the md5/sha1/sha256/sha512 checksums Central
    // requires; signing (below) adds the .asc signatures.
    repositories {
        maven {
            name = "centralPortalStaging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signingInMemoryKey").orNull
    val signingKeyId = providers.gradleProperty("signingInMemoryKeyId").orNull
    val signingPassword = providers.gradleProperty("signingInMemoryKeyPassword").orNull
    val signingEnabled = project.findProperty("RELEASE_SIGNING_ENABLED") != "false" && signingKey != null
    if (signingEnabled) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}

val centralPortalPublishTasks =
    tasks.withType<PublishToMavenRepository>().matching {
        it.name.endsWith("ToCentralPortalStagingRepository")
    }

centralPortalPublishTasks.configureEach {
    dependsOn(tasks.withType<Sign>())
}

// Zip the staged Maven layout into a single Central Portal deployment bundle.
val centralPortalBundle =
    tasks.register<Zip>("centralPortalBundle") {
        group = "publishing"
        description = "Bundles the staged Maven artifacts into a Central Portal deployment zip."
        dependsOn(centralPortalPublishTasks)
        from(layout.buildDirectory.dir("staging-deploy"))
        archiveFileName.set("$publishProjectName-$version-bundle.zip")
        destinationDirectory.set(layout.buildDirectory.dir("central-portal"))
    }

// Upload the bundle to the Sonatype Central Portal Publisher API.
// publishingType: USER_MANAGED (default, safe — validates then waits for a
// manual release in the Portal UI) or AUTOMATIC (publishes after validation).
val publishToCentralPortal =
    tasks.register("publishToCentralPortal") {
        group = "publishing"
        description = "Uploads the deployment bundle to the Sonatype Central Portal."
        dependsOn(centralPortalBundle)
        doLast {
            val user =
                providers.gradleProperty("mavenCentralUsername").orNull
                    ?: error("mavenCentralUsername is required to publish to the Central Portal.")
            val password =
                providers.gradleProperty("mavenCentralPassword").orNull
                    ?: error("mavenCentralPassword is required to publish to the Central Portal.")
            val publishingType = providers.gradleProperty("centralPublishingType").getOrElse("USER_MANAGED")
            val token = Base64.getEncoder().encodeToString("$user:$password".toByteArray(Charsets.UTF_8))

            val bundle =
                centralPortalBundle
                    .get()
                    .archiveFile
                    .get()
                    .asFile
            require(bundle.exists()) { "Deployment bundle not found: $bundle" }

            val boundary = "CentralPortalBoundary" + UUID.randomUUID().toString().replace("-", "")
            val crlf = "\r\n"
            val preamble =
                (
                    "--$boundary$crlf" +
                        "Content-Disposition: form-data; name=\"bundle\"; filename=\"${bundle.name}\"$crlf" +
                        "Content-Type: application/octet-stream$crlf$crlf"
                ).toByteArray(Charsets.UTF_8)
            val epilogue = "$crlf--$boundary--$crlf".toByteArray(Charsets.UTF_8)
            val body = preamble + bundle.readBytes() + epilogue

            val deploymentName = "$publishProjectName-$version"
            val uploadUri =
                URI(
                    "https://central.sonatype.com/api/v1/publisher/upload" +
                        "?name=$deploymentName&publishingType=$publishingType",
                )
            val request =
                HttpRequest
                    .newBuilder()
                    .uri(uploadUri)
                    .header("Authorization", "Bearer $token")
                    .header("Content-Type", "multipart/form-data; boundary=$boundary")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build()

            val client = HttpClient.newHttpClient()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() !in 200..299) {
                error("Central Portal upload failed: HTTP ${response.statusCode()} — ${response.body()}")
            }
            val deploymentId = response.body().trim()
            logger.lifecycle(
                "Central Portal upload accepted (deployment id: $deploymentId). " +
                    "publishingType=$publishingType.",
            )
            val automaticPublishing = publishingType.equals("AUTOMATIC", ignoreCase = true)
            val terminalStates =
                if (automaticPublishing) {
                    setOf("PUBLISHED")
                } else {
                    setOf("VALIDATED", "PUBLISHED")
                }
            val statusUri = URI("https://central.sonatype.com/api/v1/publisher/status?id=$deploymentId")
            val statusAttempts =
                providers.gradleProperty("centralPublishStatusAttempts").map(String::toInt).getOrElse(120)
            val statusDelayMillis =
                providers.gradleProperty("centralPublishStatusDelayMillis").map(String::toLong).getOrElse(10_000L)
            repeat(statusAttempts) { attempt ->
                val statusRequest =
                    HttpRequest
                        .newBuilder()
                        .uri(statusUri)
                        .header("Authorization", "Bearer $token")
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build()
                val statusResponse = client.send(statusRequest, HttpResponse.BodyHandlers.ofString())
                if (statusResponse.statusCode() !in 200..299) {
                    error("Central Portal status check failed: HTTP ${statusResponse.statusCode()} — ${statusResponse.body()}")
                }
                val statusBody = JsonSlurper().parseText(statusResponse.body()) as Map<*, *>
                val deploymentState =
                    statusBody["deploymentState"]?.toString()
                        ?: error("Central Portal status response did not contain deploymentState: ${statusResponse.body()}")
                when (deploymentState) {
                    "FAILED" -> {
                        error("Central Portal deployment failed: ${statusBody["errors"] ?: statusResponse.body()}")
                    }

                    in terminalStates -> {
                        logger.lifecycle("Central Portal deployment $deploymentId reached $deploymentState.")
                        return@doLast
                    }
                }
                logger.lifecycle(
                    "Central Portal deployment $deploymentId is $deploymentState " +
                        "(${attempt + 1}/$statusAttempts).",
                )
                if (attempt + 1 < statusAttempts) {
                    Thread.sleep(statusDelayMillis)
                }
            }
            error(
                "Central Portal deployment $deploymentId did not reach " +
                    "${terminalStates.joinToString("/")} after $statusAttempts checks.",
            )
        }
    }

// ============================================================================
// Tasks
// ============================================================================

// Exact test lifecycle task. Without this, ./gradlew test is ambiguous between
// Android test task names. This runs commonTest through the KMP allTests
// lifecycle and adds the Android host + Swift Export parity tests.
tasks.register("test") {
    group = "verification"
    description = "Runs all host-runnable KMP platform tests, Android host tests, and Swift Export smoke test."
    dependsOn("allTests")
    dependsOn("hostTests")
    dependsOn("swiftExportSmokeTest")
}

tasks.register("setupAndroidSdk") {
    group = "setup"
    description = "Downloads and configures the project-local Android SDK. (Alias for ensureAndroidSdk)"
    dependsOn("ensureAndroidSdk")
}

// Explicit test runner. Named hostTests to avoid shadowing the KMP allTests
// lifecycle task. Do not use findByName/mapNotNull here: missing test tasks
// mean the target surface drifted and must fail loudly.
tasks.register("hostTests") {
    group = "verification"
    description = "Runs the required real test suite (jvm, macosArm64, js, wasmJs, wasmWasi, android host)."
    dependsOn(
        "jvmTest",
        "macosArm64Test",
        "kotlinUpgradeYarnLock",
        "jsNodeTest",
        "kotlinWasmUpgradeYarnLock",
        "wasmJsNodeTest",
        "wasmWasiNodeTest",
        "testAndroidHostTest",
    )
}

// Require Swift 6 for every exported dependency module without deleting generated APIs.
val strictSwiftConfiguration = layout.projectDirectory.file("verification/strict-swift.xcconfig")
tasks.matching { it.name.endsWith("generateSPMPackage", ignoreCase = true) }.configureEach {
    doLast {
        val manifests = layout.buildDirectory.dir("SPMPackage").get().asFile
            .walkTopDown().filter { it.isFile && it.name == "Package.swift" }.toList()
        check(manifests.isNotEmpty()) { "No generated Swift package manifests" }
        val execOperations = serviceOf<ExecOperations>()
        manifests.forEach { manifest ->
            execOperations.exec {
                commandLine("python3", layout.projectDirectory.file("verification/enforce_swift6.py").asFile,
                    manifest, "--receipt", manifest.parentFile.resolve("swift6-manifest-receipt.json"))
            }.assertNormalExitValue()
        }
    }
}
tasks.matching { it.name.endsWith("buildSPMPackage", ignoreCase = true) }.configureEach {
    inputs.file(strictSwiftConfiguration)
    doFirst {
        val actual = providers.environmentVariable("XCODE_XCCONFIG_FILE").orNull
        check(actual != null && file(actual).canonicalFile == strictSwiftConfiguration.asFile.canonicalFile) {
            "Set XCODE_XCCONFIG_FILE=${strictSwiftConfiguration.asFile} for strict Swift 6 dependency compilation"
        }
    }
}

// Swift Export smoke test — produces the SPM package via embedSwiftExportForXcode
// (spawned with the Xcode-style env it requires) and runs `swift test` against it,
// so Swift Export breakage surfaces locally, not only in the swift.yml CI job.
// Pattern mirrors kasuari-kotlin. This task is part of the build contract and
// must fail rather than skip when the required toolchain is unavailable.
tasks.register("swiftExportSmokeTest") {
    group = "verification"
    description = "Builds the Swift Export SPM package and runs swift test against it."
    outputs.upToDateWhen { false }

    doLast {
        val execOperations = serviceOf<ExecOperations>()
        val swiftBuildFile =
            layout.buildDirectory
                .dir("swift-test")
                .get()
                .asFile
        if (swiftBuildFile.exists()) {
            swiftBuildFile.deleteRecursively()
        }
        swiftBuildFile.mkdirs()
        val swiftBuildDir = swiftBuildFile.absolutePath
        execOperations
            .exec {
                workingDir = projectDir
                commandLine(
                    "./gradlew",
                    "embedSwiftExportForXcode",
                    "--max-workers=2",
                    "-Dorg.gradle.jvmargs=-Xmx6g -XX:MaxMetaspaceSize=1g",
                    "--no-configuration-cache",
                    "--no-daemon",
                    "--console=plain",
                )
                environment(
                    mapOf(
                        "BUILT_PRODUCTS_DIR" to swiftBuildDir,
                        "TARGET_BUILD_DIR" to swiftBuildDir,
                        "SDK_NAME" to "macosx",
                        "CONFIGURATION" to "Debug",
                        "ARCHS" to "arm64",
                        "FRAMEWORKS_FOLDER_PATH" to "Frameworks",
                        "MACOSX_DEPLOYMENT_TARGET" to "15.0",
                        "DEPLOYMENT_TARGET_SETTING_NAME" to "MACOSX_DEPLOYMENT_TARGET",
                        "XCODE_XCCONFIG_FILE" to strictSwiftConfiguration.asFile.absolutePath,
                    ),
                )
            }.assertNormalExitValue()

        execOperations
            .exec {
                workingDir = layout.projectDirectory.dir("swift-test-harness").asFile
                commandLine("swift", "package", "reset")
            }.assertNormalExitValue()

        val swiftTestOutput = ByteArrayOutputStream()
        try {
            execOperations.exec {
                workingDir = layout.projectDirectory.dir("swift-test-harness").asFile
                commandLine("swift", "test", "-v",
                    "-Xswiftc", "-swift-version", "-Xswiftc", "6", "-Xswiftc", "-warnings-as-errors")
                standardOutput = swiftTestOutput
                errorOutput = swiftTestOutput
            }.assertNormalExitValue()
        } finally {
            val swiftTestLog = swiftTestOutput.toString()
            println(swiftTestLog)
            layout.buildDirectory.file("reports/swift/swift-test.log").get().asFile.apply {
                parentFile.mkdirs()
                writeText(swiftTestLog)
            }
        }
        execOperations.exec {
            commandLine(
                "python3",
                layout.projectDirectory.file("verification/verify_swift_test_output.py").asFile,
                layout.buildDirectory.file("reports/swift/swift-test.log").get().asFile,
                "--receipt",
                layout.buildDirectory.file("reports/swift/execution.json").get().asFile,
            )
        }.assertNormalExitValue()
    }
}

// ============================================================================
// `build` aggregate
// ----------------------------------------------------------------------------
// Every configured native target, unconditionally. This is the audit contract —
// it must mirror the kotlin { } target block exactly. All native 32-bit and
// Apple-specific x64 targets are retired; every retained target MUST build.
// Do not add a dynamic tasks.matching fallback here: copied templates must make
// the target surface explicit so missing declarations fail loudly in review.
// ============================================================================
val nativeTargetNames =
    listOf(
        "androidNativeArm64",
        "androidNativeX64",
        "iosArm64",
        "iosSimulatorArm64",
        "linuxArm64",
        "linuxX64",
        "macosArm64",
        "mingwX64",
        "tvosArm64",
        "tvosSimulatorArm64",
        "watchosDeviceArm64",
        "watchosSimulatorArm64",
    )

val fullTargetBuildTaskNames =
    buildSet {
        addAll(
            listOf(
                "compileAndroidMain",
                "compileAndroidHostTest",
                "compileAndroidDeviceTest",
                "assembleAndroidMain",
                "assembleUnitTest",
                "assembleAndroidTest",
                "assembleAndroidDeviceTest",
                "jvmMainClasses",
                "jvmTestClasses",
                "kotlinUpgradeYarnLock",
                "kotlinWasmUpgradeYarnLock",
                "jsMainClasses",
                "jsTestClasses",
                "wasmJsMainClasses",
                "wasmJsTestClasses",
                "wasmWasiMainClasses",
                "wasmWasiTestClasses",
                "swiftExportSmokeTest",
                "assemble${frameworkName}XCFramework",
            ),
        )
        for (target in nativeTargetNames) {
            add("${target}Binaries")
            add("${target}TestBinaries")
        }
    }

tasks.named("build") {
    dependsOn(fullTargetBuildTaskNames)
}

val fullCrates = listOf("ratatui-core", "ratatui-widgets", "ratatui-macros")
val desktopCrates = listOf("ratatui-crossterm", "xtask")
val publishedCrates = fullCrates + "ratatui-crossterm"
val desktopTargets = setOf("macosArm64", "linuxX64", "mingwX64")

// Compile examples separately, with main association for existing internal stdout glue.
// These files are not included in any published KMP main source set.
kotlin {
    val examples = sourceSets.create("desktopExamples")
    targets.withType<KotlinNativeTarget>().matching { it.name in desktopTargets }.configureEach {
        val mainCompilation = compilations.getByName("main")
        compilations.create("examples") {
            associateWith(mainCompilation)
            defaultSourceSet.dependsOn(examples)
        }
    }
}

// Every platform job's existing root selector includes the corresponding real crate tasks.
val platformSelectors = listOf("jvmMainClasses", "jvmTestClasses", "jvmTest", "compileAndroidMain",
    "compileAndroidHostTest", "testAndroidHostTest", "compileAndroidDeviceTest", "assembleAndroidMain",
    "assembleUnitTest", "assembleAndroidTest", "assembleAndroidDeviceTest", "androidSourcesJar",
    "jsMainClasses", "jsTestClasses", "jsBrowserTest", "jsNodeTest", "wasmJsMainClasses",
    "wasmJsTestClasses", "wasmJsBrowserTest", "wasmJsNodeTest", "wasmWasiMainClasses", "wasmWasiTestClasses", "wasmWasiNodeTest") +
    nativeTargetNames.flatMap { listOf("${it}Binaries", "${it}TestBinaries", "${it}Test", "compileKotlin${it.replaceFirstChar { c -> c.uppercase() }}") }
for (selector in platformSelectors) {
    tasks.matching { it.name == selector }.configureEach {
        dependsOn(fullCrates.map { ":$it:$selector" })
        val native = desktopTargets.firstOrNull { selector.startsWith(it) || selector == "compileKotlin${it.replaceFirstChar { c -> c.uppercase() }}" }
        if (native != null) dependsOn(desktopCrates.map { ":$it:$selector" })
    }
}
tasks.named("test") { dependsOn((fullCrates + desktopCrates).map { ":$it:test" }) }
tasks.named("allTests") { dependsOn((fullCrates + desktopCrates).map { ":$it:allTests" }) }
tasks.named("build") {
    dependsOn((fullCrates + desktopCrates).map { ":$it:build" })
    dependsOn(desktopTargets.map { "compileExamplesKotlin${it.replaceFirstChar { c -> c.uppercase() }}" })
}
// A single signed deployment must contain every newly referenced sibling coordinate.
tasks.named("centralPortalBundle") {
    dependsOn(publishedCrates.map { ":$it:publishAllPublicationsToCentralPortalStagingRepository" })
}
tasks.register("stageConsumerPublications") {
    group = "publishing"
    val owners = listOf("") + fullCrates.map { ":$it" }
    dependsOn(owners.flatMap { owner -> listOf("KotlinMultiplatform", "Android", "Jvm").map {
        "$owner:publish${it}PublicationToCentralPortalStagingRepository"
    } })
}
