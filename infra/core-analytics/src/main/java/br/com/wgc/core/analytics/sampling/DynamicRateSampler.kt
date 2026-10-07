package br.com.wgc.core.analytics.sampling

import kotlin.random.Random

/**
 * Adaptive dynamic rate sampler reducing telemetry overhead on high-traffic sessions.
 *
 * @param initialSampleRate Probability between 0.0 (0%) and 1.0 (100%).
 */
class DynamicRateSampler(
    initialSampleRate: Double = DEFAULT_SAMPLE_RATE,
) {
    @Volatile
    private var sampleRate: Double = initialSampleRate.coerceIn(0.0, 1.0)

    /**
     * Determines whether the current event should be sampled based on probability.
     */
    fun shouldSample(): Boolean {
        if (sampleRate >= 1.0) return true
        if (sampleRate <= 0.0) return false
        return Random.nextDouble() < sampleRate
    }

    /**
     * Dynamically updates the sampling probability rate.
     */
    fun updateRate(newRate: Double) {
        sampleRate = newRate.coerceIn(0.0, 1.0)
    }

    fun currentRate(): Double = sampleRate

    companion object {
        const val DEFAULT_SAMPLE_RATE = 0.1 // 10%
    }
}
