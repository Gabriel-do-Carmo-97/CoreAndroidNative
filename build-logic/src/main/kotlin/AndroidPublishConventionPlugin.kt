import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

class AndroidPublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
            }

            afterEvaluate {
                extensions.configure<PublishingExtension> {
                    publications {
                        create<MavenPublication>("release") {
                            groupId = "br.com.wgc"
                            artifactId =
                                when {
                                    target.name == "core" -> "core-android-native"
                                    target.parent?.name == "bundle" -> "bundle-${target.name}"
                                    else -> target.name
                                }
                            version = "0.0.${System.getenv("GITHUB_RUN_NUMBER") ?: "0.0.1-SNAPSHOT"}"

                            val releaseComponent = components.findByName("release")
                            if (releaseComponent != null) {
                                from(releaseComponent)
                            }
                        }
                    }

                    repositories {
                        maven {
                            name = "GitHubPackages"
                            url = uri("https://maven.pkg.github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
                            credentials {
                                username = providers.gradleProperty("gpr.user").orNull
                                    ?: providers.environmentVariable("GITHUB_ACTOR").orNull
                                password = providers.gradleProperty("gpr.key").orNull
                                    ?: providers.environmentVariable("GITHUB_TOKEN").orNull
                            }
                        }
                    }
                }
            }
        }
    }
}
