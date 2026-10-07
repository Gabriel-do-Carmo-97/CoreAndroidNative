package br.com.wgc.core.device.battery

import android.content.Context
import android.os.BatteryManager
import android.os.Build

/**
 * Diagnostic report of hardware battery health and charge cycle wear.
 *
 * @property cycleCount Battery full charge cycles counted by kernel (Android 14+).
 * @property health Battery health status constant.
 * @property isCharging Wireless or wired charging actively engaged.
 */
data class BatteryHealthReport(
    val cycleCount: Int,
    val health: Int,
    val isCharging: Boolean,
)

/**
 * Inspector querying Android 14+ BatteryManager charge cycles and battery health.
 */
class BatteryHealthInspector(
    private val context: Context,
) {
    private val batteryManager: BatteryManager? by lazy {
        context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    }

    /**
     * Inspects battery health and cycle count.
     */
    fun inspect(): BatteryHealthReport {
        val cycleCount =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                batteryManager?.getIntProperty(BATTERY_PROPERTY_CYCLE_COUNT) ?: -1
            } else {
                -1
            }

        val health =
            batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
                ?: BatteryManager.BATTERY_HEALTH_UNKNOWN
        val isCharging = batteryManager?.isCharging ?: false

        return BatteryHealthReport(
            cycleCount = cycleCount,
            health = health,
            isCharging = isCharging,
        )
    }

    companion object {
        private const val BATTERY_PROPERTY_CYCLE_COUNT = 7
    }
}
