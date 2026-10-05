package br.com.wgc.core.qa.slas

/**
 * Service Level Objective (SLO) definition and compliance evaluation.
 *
 * @property name Objective name (e.g., "P99 HTTP Latency < 200ms").
 * @property targetThreshold Max or min acceptable threshold.
 * @property actualObserved Measured metric value.
 */
data class ServiceLevelObjective(
    val name: String,
    val targetThreshold: Double,
    val actualObserved: Double,
    val isLowerBetter: Boolean = true,
) {
    val isMet: Boolean
        get() = if (isLowerBetter) actualObserved <= targetThreshold else actualObserved >= targetThreshold
}

/**
 * Enterprise reporter assessing aggregate SLO and SLA compliance across core modules.
 */
class SloComplianceReporter {
    /**
     * Calculates overall percentage of met objectives.
     */
    fun calculateCompliance(objectives: List<ServiceLevelObjective>): Float {
        if (objectives.isEmpty()) return 100f
        val metCount = objectives.count { it.isMet }
        return (metCount.toFloat() / objectives.size.toFloat()) * PERCENT_FACTOR
    }

    companion object {
        private const val PERCENT_FACTOR = 100f
    }
}
