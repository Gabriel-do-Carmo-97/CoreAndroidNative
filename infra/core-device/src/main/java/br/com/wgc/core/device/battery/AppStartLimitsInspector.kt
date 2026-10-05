package br.com.wgc.core.device.battery

import android.os.Build

/**
 * Diagnostic report of Android 15 Application Start Limits and background resource consumption.
 *
 * @property isBackgroundStartRestrictionActive Whether strict background activity starts are enforced.
 */
data class AppStartLimitsReport(
    val isBackgroundStartRestrictionActive: Boolean,
)

/**
 * Enterprise inspector for Android 15 background service and activity start limits.
 */
object AppStartLimitsInspector {
    /**
     * Inspects background start limits enforcement.
     */
    fun inspect(): AppStartLimitsReport {
        return AppStartLimitsReport(
            isBackgroundStartRestrictionActive = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM,
        )
    }
}
