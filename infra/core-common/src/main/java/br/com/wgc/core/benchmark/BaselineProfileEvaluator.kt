package br.com.wgc.core.benchmark

/**
 * Benchmark measurement metrics captured across Macrobenchmark runs.
 *
 * @property startupTimeMs Cold startup time in milliseconds.
 * @property frameJankRate Percentage of dropped UI frames.
 * @property peakMemoryMb Peak RAM consumption in Megabytes.
 */
data class MacrobenchmarkMetric(
    val startupTimeMs: Long,
    val frameJankRate: Float,
    val peakMemoryMb: Float,
)

/**
 * Enterprise analyzer verifying Macrobenchmark baseline profiles and startup performance targets.
 */
class BaselineProfileEvaluator {
    /**
     * Assesses whether captured metric meets core performance SLA.
     */
    fun isCompliant(metric: MacrobenchmarkMetric): Boolean {
        return metric.startupTimeMs <= MAX_ACCEPTABLE_STARTUP_MS &&
            metric.frameJankRate <= MAX_ACCEPTABLE_JANK_RATE &&
            metric.peakMemoryMb <= MAX_ACCEPTABLE_MEMORY_MB
    }

    companion object {
        const val MAX_ACCEPTABLE_STARTUP_MS = 1200L
        const val MAX_ACCEPTABLE_JANK_RATE = 5.0f // 5%
        const val MAX_ACCEPTABLE_MEMORY_MB = 256.0f
    }
}
