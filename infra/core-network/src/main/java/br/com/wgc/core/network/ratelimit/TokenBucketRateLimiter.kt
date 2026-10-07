package br.com.wgc.core.network.ratelimit

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Enterprise Token-Bucket rate limiter enforcing request throttling and backpressure.
 *
 * @param capacity Maximum burst capacity of tokens.
 * @param refillRatePerSecond Number of tokens replenished per second.
 */
class TokenBucketRateLimiter(
    private val capacity: Int,
    private val refillRatePerSecond: Double,
) {
    private val mutex = Mutex()
    private var availableTokens: Double = capacity.toDouble()
    private var lastRefillTimestamp: Long = System.currentTimeMillis()

    /**
     * Attempts to acquire [tokensToConsume] tokens. Returns `true` if permitted, `false` otherwise.
     */
    suspend fun tryAcquire(tokensToConsume: Int = 1): Boolean =
        mutex.withLock {
            refill()
            if (availableTokens >= tokensToConsume) {
                availableTokens -= tokensToConsume
                true
            } else {
                false
            }
        }

    private fun refill() {
        val now = System.currentTimeMillis()
        val elapsedSeconds = (now - lastRefillTimestamp).coerceAtLeast(0) / MILLIS_PER_SECOND
        val tokensToAdd = elapsedSeconds * refillRatePerSecond
        availableTokens = (availableTokens + tokensToAdd).coerceAtMost(capacity.toDouble())
        lastRefillTimestamp = now
    }

    companion object {
        private const val MILLIS_PER_SECOND = 1000.0
    }
}
