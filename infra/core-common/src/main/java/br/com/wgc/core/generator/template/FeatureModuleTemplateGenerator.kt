package br.com.wgc.core.generator.template

/**
 * Enterprise generator creating boilerplate-free clean architecture modules.
 */
class FeatureModuleTemplateGenerator {
    /**
     * Produces standard build.gradle.kts content for a new core or feature library module.
     */
    fun generateBuildGradleKts(
        moduleNamespace: String,
        includeCompose: Boolean = false,
    ): String {
        val composePlugin = if (includeCompose) "    id(\"wgc.android.library.compose\")\n" else ""
        return """
            |plugins {
            |    id("wgc.android.library")
            |$composePlugin    id("wgc.android.hilt")
            |    id("wgc.android.publish")
            |}
            |
            |android {
            |    namespace = "$moduleNamespace"
            |}
            |
            |dependencies {
            |    api(project(":infra:core-common"))
            |    implementation(libs.androidx.core.ktx)
            |}
            """.trimMargin()
    }
}
