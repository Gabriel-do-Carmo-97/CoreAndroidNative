package br.com.wgc.core.device.battery

import android.os.Build

/**
 * Diagnostic report of Android 15 App Standby and background doze power optimizations.
 *
 * @property isStrictStandbyActive Whether stricter standby quotas are applied.
 */
data class AppStandbyReport(
    val isStrictStandbyActive: Boolean,
)

/**
 * Enterprise inspector for Android 15 App Standby buckets and energy quotas.
 */
object AppStandbyInspector {
    /**
     * Inspects standby quota enforcement.
     */
    fun inspect(): AppStandbyReport {
        return AppStandbyReport(
            isStrictStandbyActive = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM,
        )
    }
}
