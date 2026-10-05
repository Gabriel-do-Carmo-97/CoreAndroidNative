package br.com.wgc.core.analytics.thermal

import android.os.PowerManager

/**
 * Diagnostic status indicating device thermal throttling severity.
 */
enum class ThermalSeverity {
    NONE,
    LIGHT,
    MODERATE,
    SEVERE,
    CRITICAL,
    EMERGENCY,
    SHUTDOWN,
}

/**
 * Enterprise listener mapping Android 10+ PowerManager thermal status to adaptive load throttling.
 */
object ThermalThrottlingGovernor {
    /**
     * Maps Android PowerManager THERMAL_STATUS_* constants to [ThermalSeverity].
     */
    fun mapStatusCode(status: Int): ThermalSeverity {
        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> ThermalSeverity.NONE
            PowerManager.THERMAL_STATUS_LIGHT -> ThermalSeverity.LIGHT
            PowerManager.THERMAL_STATUS_MODERATE -> ThermalSeverity.MODERATE
            PowerManager.THERMAL_STATUS_SEVERE -> ThermalSeverity.SEVERE
            PowerManager.THERMAL_STATUS_CRITICAL -> ThermalSeverity.CRITICAL
            PowerManager.THERMAL_STATUS_EMERGENCY -> ThermalSeverity.EMERGENCY
            PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalSeverity.SHUTDOWN
            else -> ThermalSeverity.NONE
        }
    }

    /**
     * Determines whether background workloads (video encoding, AI, heavy DB) should be paused.
     */
    fun shouldThrottleWorkload(severity: ThermalSeverity): Boolean {
        return severity >= ThermalSeverity.SEVERE
    }
}
