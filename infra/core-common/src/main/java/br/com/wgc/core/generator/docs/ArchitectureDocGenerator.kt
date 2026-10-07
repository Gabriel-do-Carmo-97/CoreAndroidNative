package br.com.wgc.core.generator.docs

/**
 * Enterprise generator creating architectural Markdown documentation from registered modules.
 */
class ArchitectureDocGenerator {
    /**
     * Renders Markdown table of modules, their owners, and architectural layer.
     */
    fun generateModuleSummary(
        modules: List<Triple<String, String, String>>, // path, layer, owner
    ): String {
        val header = "| Module | Layer | Owner |\n|---|---|---|\n"
        val rows =
            modules.joinToString("\n") { (path, layer, owner) ->
                "| `$path` | $layer | $owner |"
            }
        return "# Architecture Modules\n\n$header$rows\n"
    }
}
