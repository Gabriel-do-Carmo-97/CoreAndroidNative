import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle Convention Plugin corporativo 'wgc.core.setup'.
 * Permite que aplicações e módulos consumidores configurem a infraestrutura do Core
 * com apenas um bloco DSL declarativo padronizado.
 */
open class WgcCoreExtension {
    var includeSecurity: Boolean = true
    var includeNetwork: Boolean = true
    var includeStorage: Boolean = true
    var includeAnalytics: Boolean = true
    var includeDatabase: Boolean = false
    var enableProguardDefaults: Boolean = true
}

class CoreSetupConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("wgcCore", WgcCoreExtension::class.java)

            afterEvaluate {
                if (plugins.hasPlugin("com.android.application") || plugins.hasPlugin("com.android.library")) {
                    dependencies.apply {
                        if (extension.includeSecurity) {
                            add("implementation", project(":infra:core-security"))
                        }
                        if (extension.includeNetwork) {
                            add("implementation", project(":infra:core-network"))
                        }
                        if (extension.includeStorage) {
                            add("implementation", project(":infra:core-storage"))
                        }
                        if (extension.includeAnalytics) {
                            add("implementation", project(":infra:core-analytics"))
                        }
                        if (extension.includeDatabase) {
                            add("implementation", project(":infra:core-database"))
                        }
                    }
                }
            }
        }
    }
}
