package br.com.wgc.core.qa.flaky

/**
 * Enterprise reporter classifying and isolating non-deterministic (flaky) test executions.
 */
class FlakyTestDetector {
    /**
     * Inspects multiple test run outcomes to identify inconsistent pass/fail results.
     */
    fun isFlaky(outcomes: List<Boolean>): Boolean {
        if (outcomes.size < MIN_RUNS_FOR_FLAKINESS) return false
        val hasPass = outcomes.contains(true)
        val hasFail = outcomes.contains(false)
        return hasPass && hasFail
    }

    companion object {
        private const val MIN_RUNS_FOR_FLAKINESS = 2
    }
}
