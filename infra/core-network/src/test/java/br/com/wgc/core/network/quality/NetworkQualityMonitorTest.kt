package br.com.wgc.core.network.quality

import br.com.wgc.core.network.metrics.NetworkMetric
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkQualityMonitorTest {
    @Test
    fun `when fewer than minSamples quality is UNKNOWN`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 3)
        assertEquals(NetworkQuality.UNKNOWN, monitor.quality.value)

        monitor.recordSample(100L, true)
        assertEquals(NetworkQuality.UNKNOWN, monitor.quality.value)
        assertEquals(100L, monitor.averageLatencyMs.value)

        monitor.recordSample(120L, true)
        assertEquals(NetworkQuality.UNKNOWN, monitor.quality.value)
    }

    @Test
    fun `classifies EXCELLENT when latency is below 150ms`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 3)
        monitor.recordSample(80L, true)
        monitor.recordSample(100L, true)
        monitor.recordSample(120L, true)

        assertEquals(NetworkQuality.EXCELLENT, monitor.quality.value)
        assertEquals(100L, monitor.averageLatencyMs.value)
    }

    @Test
    fun `classifies GOOD when latency is between 150ms and 400ms`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 3)
        monitor.recordSample(200L, true)
        monitor.recordSample(250L, true)
        monitor.recordSample(300L, true)

        assertEquals(NetworkQuality.GOOD, monitor.quality.value)
    }

    @Test
    fun `classifies MODERATE when latency is between 400ms and 1000ms`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 3)
        monitor.recordSample(500L, true)
        monitor.recordSample(600L, true)
        monitor.recordSample(700L, true)

        assertEquals(NetworkQuality.MODERATE, monitor.quality.value)
    }

    @Test
    fun `classifies POOR when latency is above 1000ms or failure rate is high`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 3)
        monitor.recordSample(1200L, true)
        monitor.recordSample(1100L, true)
        monitor.recordSample(1300L, true)

        assertEquals(NetworkQuality.POOR, monitor.quality.value)

        // Test high failure rate
        monitor.reset()
        monitor.recordSample(50L, false)
        monitor.recordSample(50L, false)
        monitor.recordSample(50L, true) // 2 failures out of 3 = 66% failure rate
        assertEquals(NetworkQuality.POOR, monitor.quality.value)
    }

    @Test
    fun `onMetricCaptured updates quality using NetworkMetric`() {
        val monitor = NetworkQualityMonitor(windowSize = 5, minSamplesThreshold = 3)
        val metric =
            NetworkMetric(
                url = "https://api.example.com",
                method = "GET",
                statusCode = 200,
                totalDurationMs = 90L,
                dnsDurationMs = 10L,
                tlsDurationMs = 20L,
                isSuccess = true,
            )
        monitor.onMetricCaptured(metric)
        monitor.onMetricCaptured(metric)
        monitor.onMetricCaptured(metric)

        assertEquals(NetworkQuality.EXCELLENT, monitor.quality.value)
    }
}
