package br.com.wgc.core.analytics

import br.com.wgc.core.analytics.breadcrumbs.RingBufferBreadcrumbs
import br.com.wgc.core.analytics.deadlock.DeadlockDetector
import br.com.wgc.core.analytics.jank.FramePerformanceLevel
import br.com.wgc.core.analytics.jank.JankStatsAggregator
import br.com.wgc.core.analytics.metrics.ApmMetricCollector
import br.com.wgc.core.analytics.metrics.ApmMetricPoint
import br.com.wgc.core.analytics.metrics.MetricType
import br.com.wgc.core.analytics.sampling.DynamicRateSampler
import br.com.wgc.core.analytics.session.SessionTracker
import br.com.wgc.core.analytics.thermal.ThermalSeverity
import br.com.wgc.core.analytics.thermal.ThermalThrottlingGovernor
import br.com.wgc.core.analytics.trace.DistributedSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsBlockSixTest {
    @Test
    fun testJankStatsAggregator() {
        val aggregator = JankStatsAggregator()
        val perfect = aggregator.recordFrame(10_000_000L) // 10ms
        assertEquals(FramePerformanceLevel.PERFECT, perfect.level)

        val slow = aggregator.recordFrame(20_000_000L) // 20ms
        assertEquals(FramePerformanceLevel.SLOW, slow.level)

        val frozen = aggregator.recordFrame(800_000_000L) // 800ms
        assertEquals(FramePerformanceLevel.FROZEN, frozen.level)

        assertTrue(aggregator.getJankPercentage() > 50f)
    }

    @Test
    fun testRingBufferBreadcrumbs() {
        val ring = RingBufferBreadcrumbs<String>(capacity = 3)
        ring.add("A")
        ring.add("B")
        ring.add("C")
        assertEquals(listOf("A", "B", "C"), ring.snapshot())

        ring.add("D")
        assertEquals(listOf("B", "C", "D"), ring.snapshot())
    }

    @Test
    fun testThermalGovernor() {
        assertFalse(ThermalThrottlingGovernor.shouldThrottleWorkload(ThermalSeverity.LIGHT))
        assertTrue(ThermalThrottlingGovernor.shouldThrottleWorkload(ThermalSeverity.SEVERE))
        assertTrue(ThermalThrottlingGovernor.shouldThrottleWorkload(ThermalSeverity.CRITICAL))
    }

    @Test
    fun testApmMetricsCollector() {
        val collector = ApmMetricCollector()
        collector.record(ApmMetricPoint("cpu_usage", 45.0, MetricType.GAUGE))
        assertEquals(1, collector.count())

        val drained = collector.drain()
        assertEquals(1, drained.size)
        assertEquals(0, collector.count())
    }

    @Test
    fun testDistributedSpanW3c() {
        val span = DistributedSpan.newRootSpan("test-operation")
        val header = span.toW3cTraceparent()
        assertTrue(header.startsWith("00-"))
        span.end()
        assertNotNull(span.endTimeMs)
    }

    @Test
    fun testDynamicRateSampler() {
        val samplerZero = DynamicRateSampler(0.0)
        assertFalse(samplerZero.shouldSample())

        val samplerFull = DynamicRateSampler(1.0)
        assertTrue(samplerFull.shouldSample())
    }

    @Test
    fun testSessionTracker() {
        val tracker = SessionTracker(sessionTimeoutMs = 1000L)
        val s1 = tracker.touchActivity()
        assertNotNull(s1)
        val s2 = tracker.touchActivity()
        assertEquals(s1, s2)
    }

    @Test
    fun testDeadlockDetector() {
        val report = DeadlockDetector.detectDeadlocks()
        assertNotNull(report)
    }
}
