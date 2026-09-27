package br.com.wgc.core.network.quality

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveNetworkStrategyTest {
    @Test
    fun `currentPolicy adapts correctly across different network qualities`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 2)
        val strategy = AdaptiveNetworkStrategy(monitor)

        // Initially UNKNOWN
        val unknownPolicy = strategy.currentPolicy()
        assertFalse(unknownPolicy.shouldPrefetch)
        assertEquals(5, unknownPolicy.recommendedBatchSize)

        // Excellent: latencies < 150ms
        monitor.recordSample(50L, true)
        monitor.recordSample(70L, true)
        val excellentPolicy = strategy.currentPolicy()
        assertTrue(excellentPolicy.shouldPrefetch)
        assertEquals(50, excellentPolicy.recommendedBatchSize)
        assertTrue(excellentPolicy.allowHighResolutionMedia)

        // Poor: high failure rate or latency > 1000ms
        monitor.reset()
        monitor.recordSample(2000L, false)
        monitor.recordSample(3000L, false)
        val poorPolicy = strategy.currentPolicy()
        assertFalse(poorPolicy.shouldPrefetch)
        assertEquals(5, poorPolicy.recommendedBatchSize)
        assertFalse(poorPolicy.allowHighResolutionMedia)
    }
}
