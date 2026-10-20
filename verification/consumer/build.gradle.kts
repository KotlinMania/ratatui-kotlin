import groovy.json.JsonOutput
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.tasks.testing.Test

plugins {
    kotlin("multiplatform") version "2.4.20"
    id("com.android.kotlin.multiplatform.library") version "9.4.1"
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
    jvm()
    android {
        namespace = "io.github.kotlinmania.ratatui.consumer"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
    }
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.kotlinmania:ratatui-kotlin:${providers.gradleProperty("producerVersion").get()}")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

// Force actual fixture test execution even when validating the same staged version again.
val hostTests = tasks.withType<Test>().matching { it.name == "testAndroidHostTest" }
tasks.withType<Test>().configureEach { outputs.upToDateWhen { false } }

kotlin.targets.named("android") {
    compilations.named("main") {
        val compileDependencies = configurations.getByName(compileDependencyConfigurationName)
        val runtimeDependencies = configurations.named(
            compileDependencyConfigurationName.replace("CompileClasspath", "RuntimeClasspath"),
        )
        val classpathReceipt = layout.buildDirectory.file("reports/publication/raw-classpaths.json")
        val writeClasspath = tasks.register("writeAndroidPublicationClasspath") {
            outputs.file(classpathReceipt)
            outputs.upToDateWhen { false }
            doLast {
                val paths = mapOf(
                    "compile" to compileDependencies,
                    "runtime" to runtimeDependencies.get(),
                ).mapValues { (scope, configuration) ->
                    val artifacts = configuration.incoming.artifacts.artifacts.map { it.file }.distinct()
                    check(artifacts.isNotEmpty() && artifacts.all { it.extension in setOf("aar", "jar") }) {
                        "Unsupported raw Android $scope classpath: $artifacts"
                    }
                    artifacts.sortedBy { it.absolutePath }.map { it.absolutePath }
                }
                classpathReceipt.get().asFile.apply {
                    parentFile.mkdirs()
                    writeText(JsonOutput.prettyPrint(JsonOutput.toJson(paths)) + "\n")
                }
                val selected = compileDependencies.incoming.resolutionResult.rootComponent.get().dependencies
                    .filterIsInstance<ResolvedDependencyResult>().filter { dependency ->
                        (dependency.requested as? ModuleComponentSelector)?.module == "ratatui-kotlin"
                    }
                check(selected.size == 1) { "Expected exactly one Ratatui root dependency" }
                val variant = selected.single().resolvedVariant.externalVariant
                    .orElse(selected.single().resolvedVariant)
                val owner = variant.owner as ModuleComponentIdentifier
                val attributes = variant.attributes.keySet().associate {
                    it.name to variant.attributes.getAttribute(it).toString()
                }
                check(owner.group == "io.github.kotlinmania" && owner.module == "ratatui-kotlin-android" &&
                    owner.version == providers.gradleProperty("producerVersion").get())
                check(attributes["org.jetbrains.kotlin.platform.type"] == "androidJvm" &&
                    attributes["org.gradle.libraryelements"] == "aar" &&
                    attributes["org.gradle.jvm.environment"] == "android")
                layout.buildDirectory.file("reports/publication/selected-variant.json").get().asFile.writeText(
                    JsonOutput.prettyPrint(JsonOutput.toJson(mapOf(
                        "group" to owner.group, "module" to owner.module, "version" to owner.version,
                        "attributes" to attributes,
                    ))) + "\n",
                )
            }
        }
        val checkClasspath = tasks.register<Exec>("checkAndroidPublicationClasspath") {
            dependsOn(writeClasspath)
            mustRunAfter(hostTests)
            commandLine("python3", file("../check_android_classpath.py"), classpathReceipt.get().asFile,
                "--configurations", "--receipt", layout.buildDirectory.file("reports/publication/duplicates.json").get().asFile)
        }
        // Preserve diagnostics after a test failure without converting either result to success.
        hostTests.configureEach { finalizedBy(checkClasspath) }
    }
}
