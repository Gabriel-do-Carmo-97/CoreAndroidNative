package br.com.wgc.core.device.service

import android.os.Build

/**
 * Enterprise auditor ensuring compliance with Android 14+ strict Foreground Service (FGS) types.
 */
object ForegroundServiceAuditor {
    /**
     * Checks whether Android 14+ strict foreground service type declaration is required.
     */
    fun isStrictFgsTypeRequired(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    /**
     * Validates that an FGS type integer is not zero (SERVICE_TYPE_MANIFEST/NONE).
     */
    fun isValidFgsType(serviceType: Int): Boolean {
        return serviceType != 0
    }

    /**
     * Checks if dataSync foreground service type is valid on host build.
     */
    fun isDataSyncTypeSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }
}
