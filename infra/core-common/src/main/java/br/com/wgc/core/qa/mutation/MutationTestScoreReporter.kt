package br.com.wgc.core.qa.mutation

/**
 * Mutant status evaluation during mutation testing.
 */
enum class MutantStatus {
    KILLED,
    SURVIVED,
    TIMED_OUT,
}

/**
 * Mutation operator candidate applied to source bytecode.
 *
 * @property id Identifier of mutant.
 * @property sourceLocation File and line location.
 * @property mutationType Operator category (e.g. "ConditionInversion", "MathMutator").
 * @property status Outcome when executed against the test suite.
 */
data class MutationCandidate(
    val id: String,
    val sourceLocation: String,
    val mutationType: String,
    val status: MutantStatus = MutantStatus.SURVIVED,
)

/**
 * Enterprise reporter calculating Mutation Score Indicator (MSI).
 */
class MutationTestScoreReporter {
    /**
     * Computes the MSI percentage: (Killed / Total) * 100%.
     */
    fun calculateScore(mutants: List<MutationCandidate>): Float {
        if (mutants.isEmpty()) return 100f
        val killedCount = mutants.count { it.status == MutantStatus.KILLED || it.status == MutantStatus.TIMED_OUT }
        return (killedCount.toFloat() / mutants.size.toFloat()) * PERCENT_FACTOR
    }

    companion object {
        private const val PERCENT_FACTOR = 100f
    }
}
