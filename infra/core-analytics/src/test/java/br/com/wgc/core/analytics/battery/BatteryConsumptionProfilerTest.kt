package br.com.wgc.core.analytics.battery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BatteryConsumptionProfilerTest {
    @Test
    fun `stop returns valid usage snapshot with custom providers`() {
        var mockRealtime = 1000L
        var mockCpu = 200L

        val profiler =
            BatteryConsumptionProfiler(
                realtimeProvider = { mockRealtime },
                cpuTimeProvider = { mockCpu },
            )

        profiler.start()
        mockRealtime += 100L // 100ms elapsed
        mockCpu += 50L // 50ms CPU elapsed

        val snapshot = profiler.stop()

        assertNotNull(snapshot)
        assertEquals(100L, snapshot.elapsedRealtimeMs)
        assertEquals(50L, snapshot.elapsedCpuTimeMs)
        assertEquals(0.5f, snapshot.cpuUtilizationRatio, 0.01f)
    }
}
