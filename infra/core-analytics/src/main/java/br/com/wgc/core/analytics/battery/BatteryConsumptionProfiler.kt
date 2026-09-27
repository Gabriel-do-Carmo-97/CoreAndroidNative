package br.com.wgc.core.analytics.battery

import android.os.Process
import android.os.SystemClock

/**
 * Snapshot de consumo energético e de processador de uma operação.
 */
data class BatteryUsageSnapshot(
    val elapsedRealtimeMs: Long,
    val elapsedCpuTimeMs: Long,
    val cpuUtilizationRatio: Float,
)

/**
 * Monitor corporativo de consumo de CPU e impacto na bateria do dispositivo.
 */
class BatteryConsumptionProfiler(
    private val realtimeProvider: () -> Long = { SystemClock.elapsedRealtime() },
    private val cpuTimeProvider: () -> Long = { Process.getElapsedCpuTime() },
) {
    private var startRealtimeMs: Long = 0L
    private var startCpuTimeMs: Long = 0L

    fun start() {
        startRealtimeMs = realtimeProvider()
        startCpuTimeMs = cpuTimeProvider()
    }

    fun stop(): BatteryUsageSnapshot {
        val totalRealtime = (realtimeProvider() - startRealtimeMs).coerceAtLeast(1L)
        val totalCpu = (cpuTimeProvider() - startCpuTimeMs).coerceAtLeast(0L)
        val ratio = (totalCpu.toFloat() / totalRealtime.toFloat()).coerceIn(0f, 1f)

        return BatteryUsageSnapshot(
            elapsedRealtimeMs = totalRealtime,
            elapsedCpuTimeMs = totalCpu,
            cpuUtilizationRatio = ratio,
        )
    }
}
