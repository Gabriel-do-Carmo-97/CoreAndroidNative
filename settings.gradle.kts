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

fun registerModule(name: String) {
    include(":infra:$name")
    project(":infra:$name").projectDir = file("infra/$name")
}

registerModule("core")
registerModule("core-common")
registerModule("core-storage")
registerModule("core-device")
registerModule("core-ui")
registerModule("core-network")
registerModule("core-database")
registerModule("core-location")
registerModule("core-camera")
registerModule("core-analytics")

fun registerBundle(name: String) {
    include(":bundle:$name")
    project(":bundle:$name").projectDir = file("bundle/$name")
}

registerBundle("persistence")
registerBundle("networking")
registerBundle("presentation")
registerBundle("hardware")


