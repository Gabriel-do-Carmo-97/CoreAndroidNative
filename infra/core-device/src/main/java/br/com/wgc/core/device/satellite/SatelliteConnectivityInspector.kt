package br.com.wgc.core.device.satellite

import android.os.Build

/**
 * Diagnostic status of NTN (Non-Terrestrial Network) / Satellite messaging support.
 *
 * @property isSatelliteSupported Whether device and OS support satellite connectivity (Android 15+).
 */
data class SatelliteConnectivityStatus(
    val isSatelliteSupported: Boolean,
)

/**
 * Enterprise inspector for Android 15 Non-Terrestrial Network (NTN) satellite connectivity.
 */
object SatelliteConnectivityInspector {
    /**
     * Checks if OS version contains NTN satellite framework APIs.
     */
    fun inspect(): SatelliteConnectivityStatus {
        val supported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
        return SatelliteConnectivityStatus(isSatelliteSupported = supported)
    }
}
