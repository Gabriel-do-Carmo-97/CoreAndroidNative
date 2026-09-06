import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidDokkaConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.dokka")
            }
        }
    }
}
