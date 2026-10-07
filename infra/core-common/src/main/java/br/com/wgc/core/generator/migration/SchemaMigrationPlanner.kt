package br.com.wgc.core.generator.migration

/**
 * Migration step definition executed during schema or data migrations.
 */
data class MigrationStep(
    val fromVersion: Int,
    val toVersion: Int,
    val description: String,
)

/**
 * Enterprise runner calculating shortest migration execution path between versions.
 */
class SchemaMigrationPlanner {
    /**
     * Determines required sequential steps to upgrade from [currentVersion] to [targetVersion].
     */
    fun planMigration(
        currentVersion: Int,
        targetVersion: Int,
        availableSteps: List<MigrationStep>,
    ): List<MigrationStep> {
        require(targetVersion >= currentVersion) { "Target version cannot be lower than current version" }
        if (currentVersion == targetVersion) return emptyList()

        val planned = mutableListOf<MigrationStep>()
        var cursor = currentVersion

        while (cursor < targetVersion) {
            val nextStep =
                availableSteps.firstOrNull { it.fromVersion == cursor }
                    ?: error("Missing migration path from version $cursor")
            planned.add(nextStep)
            cursor = nextStep.toVersion
        }

        return planned
    }
}
