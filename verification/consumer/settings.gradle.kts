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
                    listOf("ratatui-kotlin", "ratatui-core", "ratatui-widgets", "ratatui-macros").forEach { owner ->
                        listOf("", "-android", "-jvm").forEach { suffix ->
                            includeModule("io.github.kotlinmania", owner + suffix)
                        }
                    }
                }
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "ratatui-public-api-consumer"
