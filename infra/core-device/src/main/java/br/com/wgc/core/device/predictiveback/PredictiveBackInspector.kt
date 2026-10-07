package br.com.wgc.core.device.predictiveback

import android.os.Build

/**
 * Enterprise inspector for Android 14/15 Predictive Back gesture and animation support.
 */
object PredictiveBackInspector {
    /**
     * Checks if native predictive back gesture dispatching is supported on host OS.
     */
    fun isPredictiveBackSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    /**
     * Checks if predictive back is enforced by default (Android 15+).
     */
    fun isPredictiveBackDefaultEnforced(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
    }
}
