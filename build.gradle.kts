import java.io.ByteArrayInputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream
import org.gradle.api.GradleException
import org.gradle.api.tasks.ClasspathNormalizer
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec

plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("com.android.kotlin.multiplatform.library") version "9.2.1"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.kotlinmania"
version = "0.1.10"

val androidCommandLineToolsRevision = "14742923"
val projectCompileSdk = "34"
val projectAndroidBuildTools = "36.0.0"
val isWindowsHost = System.getProperty("os.name").lowercase().contains("windows")
val isMacHost = System.getProperty("os.name").lowercase().contains("mac")
val androidSdkOsName =
    when {
        isWindowsHost -> "win"
        System.getProperty("os.name").lowercase().contains("mac") -> "mac"
        System.getProperty("os.name").lowercase().contains("linux") -> "linux"
        else -> throw GradleException("Unsupported Android SDK setup OS: ${System.getProperty("os.name")}")
    }
val projectAndroidSdkDir = layout.projectDirectory.dir(".android-sdk").asFile
val androidSdkManager = projectAndroidSdkDir.resolve(
    if (isWindowsHost) {
        "cmdline-tools/latest/bin/sdkmanager.bat"
    } else {
        "cmdline-tools/latest/bin/sdkmanager"
    },
)
val androidSdkInstallMarker = projectAndroidSdkDir.resolve(".install-complete")
val maxAndroidSdkLicensePrompts = 200
val requiredAndroidSdkPackageDirs = listOf(
    projectAndroidSdkDir.resolve("platform-tools"),
    projectAndroidSdkDir.resolve("platforms/android-$projectCompileSdk"),
    projectAndroidSdkDir.resolve("build-tools/$projectAndroidBuildTools"),
)

fun isProjectAndroidSdkInstalled(): Boolean {
    val installed =
        androidSdkInstallMarker.exists() &&
            androidSdkManager.exists() &&
            requiredAndroidSdkPackageDirs.all { it.exists() }
    if (!installed && androidSdkInstallMarker.exists()) {
        androidSdkInstallMarker.delete()
    }
    return installed
}

fun writeAndroidLocalProperties() {
    val sdkDirPropertyValue = projectAndroidSdkDir.absolutePath.replace("\\", "/")
    layout.projectDirectory.file("local.properties").asFile.writeText("sdk.dir=$sdkDirPropertyValue\n")
}

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
                        if (!isWindowsHost && relativeName.startsWith("bin/")) {
                            target.setExecutable(true)
                        }
                    }
                }
                zipInput.closeEntry()
            }
        }

        if (!isWindowsHost) {
            androidSdkManager.setExecutable(true)
        }
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

    if (!androidSdkManager.exists()) {
        downloadAndroidCommandLineTools()
    }

    println("setup-android-sdk: accepting licenses")
    val licenseAnswers = "y\n".repeat(maxAndroidSdkLicensePrompts).toByteArray(Charsets.UTF_8)
    val licenseResult = execOperations.exec {
        commandLine(sdkManagerCommand("--sdk_root=${projectAndroidSdkDir.absolutePath}", "--licenses"))
        standardInput = ByteArrayInputStream(licenseAnswers)
        isIgnoreExitValue = true
    }
    if (licenseResult.exitValue != 0) {
        throw GradleException("Android SDK license acceptance failed with exit code ${licenseResult.exitValue}")
    }

    println("setup-android-sdk: installing platform-tools, android-$projectCompileSdk, build-tools;$projectAndroidBuildTools")
    val installLog = projectAndroidSdkDir.resolve("sdkmanager-install.log")
    installLog.parentFile.mkdirs()
    installLog.outputStream().use { output ->
        val installResult = execOperations.exec {
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
    println("setup-android-sdk: install log at $installLog")

    writeAndroidLocalProperties()
    androidSdkInstallMarker.writeText("")
    println("setup-android-sdk: done")
    println("  SDK at:     $projectAndroidSdkDir")
    println("  configured: local.properties -> $projectAndroidSdkDir")
}

val androidSdkExecOperations = serviceOf<ExecOperations>()
installProjectAndroidSdk(androidSdkExecOperations)

kotlin {
    applyDefaultHierarchyTemplate()

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
        languageSettings.optIn("kotlin.concurrent.atomics.ExperimentalAtomicApi")
        languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
    }

    compilerOptions {
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    val xcf = XCFramework("Ratatui")

    macosArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }
    iosArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Ratatui"
            isStatic = true
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "Ratatui"
            isStatic = true
            xcf.add(this)
        }
    }

    tvosArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }
    tvosSimulatorArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }

    watchosArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }
    watchosDeviceArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }
    watchosSimulatorArm64 {
        binaries.framework { baseName = "Ratatui"; xcf.add(this) }
    }

    linuxX64()
    linuxArm64()
    mingwX64()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }

    swiftExport {
        moduleName = "Ratatui"
        flattenPackage = "io.github.kotlinmania.ratatui"
    }

    android {
        namespace = "io.github.kotlinmania.ratatui"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
                implementation("io.github.kotlinmania:anstyle-kotlin:0.1.5")
                implementation("io.github.kotlinmania:bitflags-kotlin:0.1.1")
                implementation("io.github.kotlinmania:itertools-kotlin:0.1.1")
                implementation("io.github.kotlinmania:kasuari-kotlin:0.1.6")
                implementation("io.github.kotlinmania:serde-kotlin:0.1.1")
                implementation("io.github.kotlinmania:strum-kotlin:0.1.0")
                implementation("io.github.kotlinmania:unicode-segmentation-kotlin:0.1.1")
                implementation("io.github.kotlinmania:unicode-width-kotlin:0.1.0")
            }
        }
        val desktopMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.github.kotlinmania:crossterm-kotlin:0.1.4")
            }
        }
        val macosArm64Main by getting { dependsOn(desktopMain) }
        val linuxX64Main by getting { dependsOn(desktopMain) }
        val mingwX64Main by getting { dependsOn(desktopMain) }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

    }
    jvmToolchain(21)
}

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

rootProject.extensions.configure<NodeJsEnvSpec>("kotlinNodeJsSpec") {
    version.set("24.15.0")
}

rootProject.extensions.configure<WasmNodeJsEnvSpec>("kotlinWasmNodeJsSpec") {
    version.set("24.15.0")
}

rootProject.extensions.configure<YarnRootEnvSpec>("kotlinYarnSpec") {
    version.set("1.22.22")
}

rootProject.extensions.configure<WasmYarnRootEnvSpec>("kotlinWasmYarnSpec") {
    version.set("1.22.22")
}

rootProject.extensions.configure<YarnRootExtension>("kotlinYarn") {
    resolution("diff", "8.0.3")
    resolution("**/diff", "8.0.3")
    resolution("fast-uri", "3.1.2")
    resolution("**/fast-uri", "3.1.2")
    resolution("serialize-javascript", "7.0.5")
    resolution("**/serialize-javascript", "7.0.5")
    resolution("webpack", "5.106.2")
    resolution("**/webpack", "5.106.2")
    resolution("follow-redirects", "1.16.0")
    resolution("**/follow-redirects", "1.16.0")
    resolution("lodash", "4.18.1")
    resolution("**/lodash", "4.18.1")
    resolution("ajv", "8.20.0")
    resolution("**/ajv", "8.20.0")
    resolution("brace-expansion", "5.0.6")
    resolution("**/brace-expansion", "5.0.6")
    resolution("flatted", "3.4.2")
    resolution("**/flatted", "3.4.2")
    resolution("minimatch", "10.2.5")
    resolution("**/minimatch", "10.2.5")
    resolution("picomatch", "4.0.4")
    resolution("**/picomatch", "4.0.4")
    resolution("qs", "6.15.2")
    resolution("**/qs", "6.15.2")
    resolution("socket.io-parser", "4.2.6")
    resolution("**/socket.io-parser", "4.2.6")
    resolution("ws", "8.20.1")
    resolution("**/ws", "8.20.1")
}


val patchedKarmaWebpackPackage = rootProject.layout.projectDirectory.dir("gradle/npm/karma-webpack").asFile.absolutePath.replace("\\", "/")

rootProject.extensions.configure<NodeJsRootExtension>("kotlinNodeJs") {
    versions.webpack.version = "5.106.2"
    versions.webpackCli.version = "7.0.2"
    versions.karma.version = "npm:karma-maintained@6.4.7"
    versions.karmaWebpack.version = "file:$patchedKarmaWebpackPackage"
    versions.mocha.version = "12.0.0-beta-10"
    versions.kotlinWebHelpers.version = "3.1.0"
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "ratatui-kotlin", version.toString())

    pom {
        name.set("ratatui-kotlin")
        description.set("Kotlin Multiplatform port of ratatui-org/ratatui - A library to cook up terminal user interfaces")
        inceptionYear.set("2026")
        url.set("https://github.com/KotlinMania/ratatui-kotlin")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
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
            url.set("https://github.com/KotlinMania/ratatui-kotlin")
            connection.set("scm:git:git://github.com/KotlinMania/ratatui-kotlin.git")
            developerConnection.set("scm:git:ssh://github.com/KotlinMania/ratatui-kotlin.git")
        }
    }
}

// ---------------------------------------------------------------------------
// CodeQL Java/Kotlin extraction task
//
// .github/workflows/codeql.yml invokes `./gradlew codeqlCompileJvm` to feed
// commonMain through a standalone multiplatform-aware kotlinc invocation for
// the CodeQL Java agent.
val codeqlKotlinc: Configuration by configurations.creating {
    description = "Kotlin compiler (CodeQL extraction target only - not published)"
    isCanBeResolved = true
    isCanBeConsumed = false
}

val codeqlSourceClasspath: Configuration by configurations.creating {
    description = "Runtime classpath for CodeQL extraction of commonMain sources"
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    codeqlKotlinc("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.3.21")
    codeqlSourceClasspath("org.jetbrains.kotlin:kotlin-stdlib:2.3.21")
    codeqlSourceClasspath("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.11.0")
    codeqlSourceClasspath("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.11.0")
    codeqlSourceClasspath("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.11.0")
    codeqlSourceClasspath("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.8.0")
    codeqlSourceClasspath("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.4.0")
    codeqlSourceClasspath("io.github.kotlinmania:anstyle-kotlin-jvm:0.1.5")
    codeqlSourceClasspath("io.github.kotlinmania:bitflags-kotlin-jvm:0.1.1")
    codeqlSourceClasspath("io.github.kotlinmania:itertools-kotlin-jvm:0.1.1")
    codeqlSourceClasspath("io.github.kotlinmania:kasuari-kotlin-jvm:0.1.6")
    codeqlSourceClasspath("io.github.kotlinmania:serde-kotlin-jvm:0.1.1")
    codeqlSourceClasspath("io.github.kotlinmania:strum-kotlin-jvm:0.1.0")
    codeqlSourceClasspath("io.github.kotlinmania:unicode-segmentation-kotlin-jvm:0.1.1")
    codeqlSourceClasspath("io.github.kotlinmania:unicode-width-kotlin-jvm:0.1.0")
}

val codeqlCompileJvm = tasks.register<JavaExec>("codeqlCompileJvm") {
    description =
        "Compile commonMain Kotlin sources with kotlinc 2.3.21 for CodeQL Java/Kotlin extraction."
    group = "verification"

    classpath(codeqlKotlinc)
    mainClass.set("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler")

    val outDir = layout.buildDirectory.dir("classes/kotlin/codeql-jvm")
    val sources = fileTree("src/commonMain/kotlin") { include("**/*.kt") }
    val sentinelDir = layout.buildDirectory.dir("generated/codeql-empty-source")
    inputs.files(sources).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.files(codeqlSourceClasspath).withNormalizer(ClasspathNormalizer::class.java)
    outputs.dir(outDir)
    outputs.dir(sentinelDir)
    outputs.upToDateWhen { false }

    doFirst {
        outDir.get().asFile.mkdirs()
        val fullClasspath = codeqlSourceClasspath.resolve().joinToString(File.pathSeparator) { it.absolutePath }
        val sourceFiles = sources.files.toMutableList()
        val commonSourceFiles = sourceFiles.toMutableList()
        if (sourceFiles.isEmpty()) {
            val sentinelFile =
                sentinelDir.get().asFile.resolve("io/github/kotlinmania/ratatui/CodeqlEmptySentinel.kt")
            sentinelFile.parentFile.mkdirs()
            sentinelFile.writeText(
                """
                // Auto-generated. Present so codeqlCompileJvm has at least
                // one Kotlin source to feed kotlinc; replaced by real
                // commonMain content once porting begins.
                package io.github.kotlinmania.ratatui

                private object CodeqlEmptySentinel
                """.trimIndent(),
            )
            commonSourceFiles += sentinelFile
            sourceFiles += sentinelFile
        }
        args = listOf(
            "-d", outDir.get().asFile.absolutePath,
            "-classpath", fullClasspath,
            "-jvm-target", "21",
            "-no-stdlib",
            "-no-reflect",
            "-language-version", "2.3",
            "-api-version", "2.3",
            "-Xmulti-platform",
            "-Xcommon-sources=${commonSourceFiles.joinToString(",") { it.absolutePath }}",
            "-Xexpect-actual-classes",
            "-opt-in", "kotlin.ExperimentalUnsignedTypes",
            "-opt-in", "kotlin.time.ExperimentalTime",
            "-opt-in", "kotlin.concurrent.atomics.ExperimentalAtomicApi",
        ) + sourceFiles.map { it.absolutePath }
    }
}

tasks.register("setupAndroidSdk") {
    group = "setup"
    description = "Downloads and configures the project-local Android SDK."
    doLast {
        installProjectAndroidSdk(androidSdkExecOperations)
    }
}

val swiftExportSmokeTest = tasks.register("swiftExportSmokeTest") {
    group = "verification"
    description = "Builds the Swift Export SPM package and runs swift test against it."
    onlyIf {
        if (!isMacHost) {
            logger.lifecycle("swiftExportSmokeTest: skipped because Swift Export smoke tests require macOS")
        }
        isMacHost
    }
    outputs.upToDateWhen { false }

    doLast {
        val execOperations = serviceOf<ExecOperations>()
        val swiftBuildDir = layout.buildDirectory.dir("swift-test").get().asFile.absolutePath
        execOperations.exec {
            workingDir = projectDir
            commandLine(
                "./gradlew",
                "embedSwiftExportForXcode",
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
                    "MACOSX_DEPLOYMENT_TARGET" to "14.0",
                    "DEPLOYMENT_TARGET_SETTING_NAME" to "MACOSX_DEPLOYMENT_TARGET",
                ),
            )
        }.assertNormalExitValue()

        execOperations.exec {
            workingDir = layout.projectDirectory.dir("swift-test-harness").asFile
            commandLine("swift", "test")
        }.assertNormalExitValue()
    }
}

tasks.register("test") {
    group = "verification"
    description =
        "Runs the host-portable test suite (macOS + JS + WasmJS + Android unit + Swift smoke test). " +
        "Non-host native targets (mingwX64, linuxX64) only run on their own host."

    val defaultTestTasks = listOf(
        "macosArm64Test",
        "jvmTest",
        "jsNodeTest",
        "wasmJsNodeTest",
        "compileAndroidMain",
        "assembleUnitTest",
        "swiftExportSmokeTest",
    )

    dependsOn(defaultTestTasks.mapNotNull { taskName -> tasks.findByName(taskName) })
}

val fullTargetBuildTaskNames = setOf(
    "compileAndroidMain",
    "compileAndroidHostTest",
    "compileAndroidDeviceTest",
    "assembleAndroidMain",
    "assembleUnitTest",
    "assembleAndroidTest",
    "jvmMainClasses",
    "jvmTestClasses",
    "jsMainClasses",
    "jsTestClasses",
    "wasmJsMainClasses",
    "wasmJsTestClasses",
    "wasmWasiMainClasses",
    "wasmWasiTestClasses",
    "androidNativeArm32Binaries",
    "androidNativeArm32TestBinaries",
    "androidNativeArm64Binaries",
    "androidNativeArm64TestBinaries",
    "androidNativeX64Binaries",
    "androidNativeX64TestBinaries",
    "androidNativeX86Binaries",
    "androidNativeX86TestBinaries",
    "iosArm64Binaries",
    "iosArm64TestBinaries",
    "iosSimulatorArm64Binaries",
    "iosSimulatorArm64TestBinaries",
    "iosX64Binaries",
    "iosX64TestBinaries",
    "linuxArm64Binaries",
    "linuxArm64TestBinaries",
    "linuxX64Binaries",
    "linuxX64TestBinaries",
    "macosArm64Binaries",
    "macosArm64TestBinaries",
    "mingwX64Binaries",
    "mingwX64TestBinaries",
    "tvosArm64Binaries",
    "tvosArm64TestBinaries",
    "tvosSimulatorArm64Binaries",
    "tvosSimulatorArm64TestBinaries",
    "watchosArm64Binaries",
    "watchosArm64TestBinaries",
    "watchosDeviceArm64Binaries",
    "watchosDeviceArm64TestBinaries",
    "watchosSimulatorArm64Binaries",
    "watchosSimulatorArm64TestBinaries",
    "swiftExportSmokeTest",
    "embedSwiftExportForXcode",
    "assembleRatatuiXCFramework",
)

tasks.named("build") {
    dependsOn(fullTargetBuildTaskNames)
}

afterEvaluate {
    tasks.named("build") {
        dependsOn(
            tasks.matching {
                name.endsWith("MainClasses") ||
                    name.endsWith("TestClasses") ||
                    name.endsWith("Binaries") ||
                    name.endsWith("XCFramework")
            },
        )
    }
}

val requiredPublicationNames = setOf(
    "kotlinMultiplatform",
    "android",
    "jvm",
    "js",
    "wasmJs",
    "wasmWasi",
    "androidNativeArm32",
    "androidNativeArm64",
    "androidNativeX64",
    "androidNativeX86",
    "iosArm64",
    "iosSimulatorArm64",
    "iosX64",
    "linuxArm64",
    "linuxX64",
    "macosArm64",
    "mingwX64",
    "tvosArm64",
     "tvosSimulatorArm64",
     "watchosArm64",
    "watchosDeviceArm64",
    "watchosSimulatorArm64",
)

fun publicationMetadataTaskName(publicationName: String): String =
    "generateMetadataFileFor${publicationName.replaceFirstChar { it.titlecase() }}Publication"

val verifyPublishedTargetPublications = tasks.register("verifyPublishedTargetPublications") {
    group = "verification"
    description = "Fails when any declared Kotlin target lacks a Maven publication task."
    doLast {
        val missing = requiredPublicationNames
            .map { it to publicationMetadataTaskName(it) }
            .filterNot { (_, taskName) -> tasks.names.contains(taskName) }
        if (missing.isNotEmpty()) {
            throw GradleException(
                "Missing Maven publication tasks for target publications: " +
                    missing.joinToString { (publicationName, taskName) -> "$publicationName ($taskName)" },
            )
        }
    }
}

tasks.named("build") {
    dependsOn(verifyPublishedTargetPublications)
}

// The generated Wasm-WASI Node test runner cannot see the filesystem unless
// the project directory is preopened. Patch the runner before wasmWasiNodeTest.
val patchWasmWasiNodePreopens = tasks.register("patchWasmWasiNodePreopens") {
    description = "Preopen the project directory for the generated Wasm-WASI Node test runner."
    group = "verification"
    dependsOn("compileTestDevelopmentExecutableKotlinWasmWasi")
    outputs.upToDateWhen { false }

    doLast {
        val runnerFile = layout.buildDirectory.file(
            "compileSync/wasmWasi/test/testDevelopmentExecutable/kotlin/${rootProject.name}-test.mjs",
        ).get().asFile
        if (!runnerFile.exists()) {
            // No Wasm-WASI test runner was generated (the repo has no
            // wasmWasi test sources), so there is nothing to preopen.
            return@doLast
        }
        val text = runnerFile.readText()
        val withCwdImport = text.replace(
            "import { argv, env } from 'node:process';",
            "import { argv, env, cwd } from 'node:process';",
        )
        val patched = withCwdImport.replace(
            "const wasi = new WASI({ version: 'preview1', args: argv, env, });",
            "const wasi = new WASI({ version: 'preview1', args: argv, env, preopens: { '/': cwd() }, });",
        )
        runnerFile.writeText(patched)
    }
}

tasks.named("wasmWasiNodeTest") {
    dependsOn(patchWasmWasiNodePreopens)
}
