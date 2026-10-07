package br.com.wgc.core.sync.resilience

import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Enterprise exponential backoff calculator with randomized jitter (decorrelated / full jitter).
 * Prevents "thundering herd" problems on synchronization endpoints.
 */
class ExponentialBackoffWithJitter(
    private val baseDelayMs: Long = DEFAULT_BASE_DELAY_MS,
    private val maxDelayMs: Long = DEFAULT_MAX_DELAY_MS,
    private val factor: Double = DEFAULT_FACTOR,
) {
    /**
     * Calculates the backoff duration in milliseconds for attempt [retryAttempt].
     */
    fun calculateDelay(retryAttempt: Int): Long {
        if (retryAttempt <= 0) return 0L
        val exponential = (baseDelayMs * factor.pow((retryAttempt - 1).toDouble())).toLong()
        val ceiling = min(exponential, maxDelayMs)
        // Full jitter: uniformly random between 0 and ceiling
        return Random.nextLong(0, ceiling + 1)
    }

    /**
     * Suspends execution according to the jittered backoff time for attempt [retryAttempt].
     */
    suspend fun delayRetry(retryAttempt: Int) {
        val delayTime = calculateDelay(retryAttempt)
        if (delayTime > 0L) {
            delay(delayTime)
        }
    }

    companion object {
        const val DEFAULT_BASE_DELAY_MS = 1000L
        const val DEFAULT_MAX_DELAY_MS = 30000L
        const val DEFAULT_FACTOR = 2.0
    }
}
