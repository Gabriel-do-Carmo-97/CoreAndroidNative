pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: providers.environmentVariable("GITHUB_ACTOR").orNull
                password = providers.gradleProperty("gpr.key").orNull ?: providers.environmentVariable("GITHUB_TOKEN").orNull
            }
        }
    }
}

rootProject.name = "CoreAndroidNative"
include(":app")

fun registerModule(name: String, path: String) {
    include(":$name")
    project(":$name").projectDir = file("modules/$path")
}

// 📦 Core Umbrella & Submódulos organizados dentro de modules/
registerModule("core", "core")
registerModule("core-common", "common")
registerModule("core-storage", "storage")
registerModule("core-device", "device")
registerModule("core-network", "network")
registerModule("core-ui", "ui")
registerModule("core-database", "database")
registerModule("core-location", "location")
registerModule("core-camera", "camera")
registerModule("core-analytics", "analytics")
