package br.com.wgc.core.device.privatespace

import android.os.Build

/**
 * Diagnostic report of photo picker and partial media permissions isolation.
 *
 * @property isPartialMediaPermissionsSupported Whether READ_MEDIA_VISUAL_USER_SELECTED is supported.
 */
data class PartialMediaPermissionReport(
    val isPartialMediaPermissionsSupported: Boolean,
)

/**
 * Enterprise inspector for Android 14+ selected photo/video access (READ_MEDIA_VISUAL_USER_SELECTED).
 */
object PartialMediaAccessInspector {
    /**
     * Inspects partial media permissions availability.
     */
    fun inspect(): PartialMediaPermissionReport {
        return PartialMediaPermissionReport(
            isPartialMediaPermissionsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
        )
    }
}
