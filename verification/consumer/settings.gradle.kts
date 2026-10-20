pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        val stagedRepository = providers.gradleProperty("producerRepository").orNull
        if (stagedRepository != null) {
            exclusiveContent {
                forRepository { maven { url = uri(stagedRepository) } }
                filter {
                    includeModule("io.github.kotlinmania", "ratatui-kotlin")
                    includeModule("io.github.kotlinmania", "ratatui-kotlin-android")
                    includeModule("io.github.kotlinmania", "ratatui-kotlin-jvm")
                }
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "ratatui-public-api-consumer"
