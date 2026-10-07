package br.com.wgc.core.device.display

import android.os.Build

/**
 * Diagnostic report of system edge-to-edge layout enforcement status.
 *
 * @property isEdgeToEdgeDefaultEnforced Whether Android 15 enforces edge-to-edge window decor by default.
 */
data class EdgeToEdgeEnforcementReport(
    val isEdgeToEdgeDefaultEnforced: Boolean,
)

/**
 * Enterprise inspector for Android 15 edge-to-edge window layout insets enforcement.
 */
object EdgeToEdgeInspector {
    /**
     * Inspects whether edge-to-edge window decor is strictly enforced by the OS.
     */
    fun inspect(): EdgeToEdgeEnforcementReport {
        return EdgeToEdgeEnforcementReport(
            isEdgeToEdgeDefaultEnforced = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM,
        )
    }
}
