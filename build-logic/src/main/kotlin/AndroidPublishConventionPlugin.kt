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
                            val releaseVersion =
                                target.findProperty("version")?.toString()?.takeIf { it != "unspecified" }
                                    ?: System.getenv("RELEASE_VERSION")
                                    ?: "1.0.0"
                            version = releaseVersion

                            val releaseComponent = components.findByName("release")
                            if (releaseComponent != null) {
                                from(releaseComponent)
                            }

                            pom {
                                name.set(artifactId)
                                description.set("CoreAndroidNative - Enterprise Android Infrastructure Library: $artifactId")
                                url.set("https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
                                licenses {
                                    license {
                                        name.set("The Apache Software License, Version 2.0")
                                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                                        distribution.set("repo")
                                    }
                                }
                                developers {
                                    developer {
                                        id.set("Gabriel-do-Carmo-97")
                                        name.set("Gabriel do Carmo")
                                        email.set("gabriel.desenvolvedor.97@gmail.com")
                                    }
                                }
                                scm {
                                    connection.set("scm:git:github.com/Gabriel-do-Carmo-97/CoreAndroidNative.git")
                                    developerConnection.set("scm:git:ssh://github.com/Gabriel-do-Carmo-97/CoreAndroidNative.git")
                                    url.set("https://github.com/Gabriel-do-Carmo-97/CoreAndroidNative")
                                }
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
