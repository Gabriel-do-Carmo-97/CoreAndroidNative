package br.com.wgc.core.qa.canary

import java.security.MessageDigest

/**
 * Enterprise user bucketing algorithm for phased canary rollouts and gradual feature deployment.
 */
class CanaryDeploymentBucketCalculator {
    /**
     * Maps a user identifier and feature key deterministically to a rollout bucket between 0 and 99.
     */
    fun computeBucket(
        userId: String,
        featureKey: String,
    ): Int {
        val input = "$featureKey:$userId"
        val hashBytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        val positiveInt =
            (hashBytes[0].toInt() and BYTE_MASK) or
                ((hashBytes[1].toInt() and BYTE_MASK) shl SHIFT_8)
        return positiveInt % BUCKET_COUNT
    }

    /**
     * Determines whether user qualifies for an active canary percentage rollout (e.g. 10%, 25%).
     */
    fun isEligible(
        userId: String,
        featureKey: String,
        targetPercentage: Int,
    ): Boolean {
        if (targetPercentage <= 0) return false
        if (targetPercentage >= BUCKET_COUNT) return true
        val bucket = computeBucket(userId, featureKey)
        return bucket < targetPercentage
    }

    companion object {
        private const val BUCKET_COUNT = 100
        private const val BYTE_MASK = 0xFF
        private const val SHIFT_8 = 8
    }
}
