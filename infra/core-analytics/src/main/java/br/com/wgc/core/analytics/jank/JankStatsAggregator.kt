package br.com.wgc.core.analytics.jank

/**
 * Categorization of UI frame rendering latency based on Android Vitals thresholds.
 */
enum class FramePerformanceLevel {
    PERFECT,
    SLOW,
    FROZEN,
}

/**
 * Diagnostic record of UI frame rendering metrics.
 *
 * @property frameDurationNs Total duration taken to draw the frame in nanoseconds.
 * @property level Performance category (PERFECT < 16ms, SLOW >= 16ms, FROZEN >= 700ms).
 */
data class JankFrameMetric(
    val frameDurationNs: Long,
    val level: FramePerformanceLevel,
)

/**
 * Enterprise monitor and aggregator for Android JankStats and frame drops.
 */
class JankStatsAggregator {
    private var totalFrames = 0L
    private var slowFrames = 0L
    private var frozenFrames = 0L

    /**
     * Records a frame duration and updates vital counters.
     */
    @Synchronized
    fun recordFrame(durationNs: Long): JankFrameMetric {
        totalFrames++
        val durationMs = durationNs / NANOS_PER_MILLI
        val level =
            when {
                durationMs >= FROZEN_FRAME_THRESHOLD_MS -> {
                    frozenFrames++
                    FramePerformanceLevel.FROZEN
                }
                durationMs >= SLOW_FRAME_THRESHOLD_MS -> {
                    slowFrames++
                    FramePerformanceLevel.SLOW
                }
                else -> FramePerformanceLevel.PERFECT
            }
        return JankFrameMetric(durationNs, level)
    }

    /**
     * Returns percentage of janky frames (slow + frozen) over total frames.
     */
    @Synchronized
    fun getJankPercentage(): Float {
        if (totalFrames == 0L) return 0f
        return ((slowFrames + frozenFrames).toFloat() / totalFrames.toFloat()) * PERCENT_FACTOR
    }

    @Synchronized
    fun reset() {
        totalFrames = 0L
        slowFrames = 0L
        frozenFrames = 0L
    }

    companion object {
        private const val NANOS_PER_MILLI = 1_000_000L
        private const val SLOW_FRAME_THRESHOLD_MS = 16L
        private const val FROZEN_FRAME_THRESHOLD_MS = 700L
        private const val PERCENT_FACTOR = 100f
    }
}
