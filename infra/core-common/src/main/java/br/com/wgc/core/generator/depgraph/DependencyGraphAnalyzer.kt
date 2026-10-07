package br.com.wgc.core.generator.depgraph

/**
 * Representation of a module node and its direct dependencies in the multi-module project graph.
 */
data class ModuleNode(
    val modulePath: String,
    val dependencies: Set<String> = emptySet(),
)

/**
 * Enterprise analyzer detecting circular dependencies and calculating module build order.
 */
class DependencyGraphAnalyzer {
    /**
     * Checks if the dependency graph contains any circular dependency cycles.
     */
    fun hasCycles(nodes: List<ModuleNode>): Boolean {
        val visited = mutableSetOf<String>()
        val recursionStack = mutableSetOf<String>()
        val nodeMap = nodes.associateBy { it.modulePath }

        fun dfs(current: String): Boolean {
            visited.add(current)
            recursionStack.add(current)

            val deps = nodeMap[current]?.dependencies ?: emptySet()
            for (dep in deps) {
                if (!visited.contains(dep)) {
                    if (dfs(dep)) return true
                } else if (recursionStack.contains(dep)) {
                    return true
                }
            }

            recursionStack.remove(current)
            return false
        }

        for (node in nodes) {
            if (!visited.contains(node.modulePath)) {
                if (dfs(node.modulePath)) return true
            }
        }

        return false
    }
}
