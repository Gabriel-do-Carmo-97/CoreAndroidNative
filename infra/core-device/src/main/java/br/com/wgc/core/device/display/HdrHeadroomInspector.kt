package br.com.wgc.core.device.display

import android.os.Build

/**
 * Diagnostic report of HDR headroom and dynamic range tone mapping.
 *
 * @property isHdrHeadroomSupported Whether OS supports querying HDR to SDR headroom ratio.
 */
data class HdrHeadroomReport(
    val isHdrHeadroomSupported: Boolean,
)

/**
 * Enterprise inspector for Android 14+ Display HDR Headroom and tone mapping APIs.
 */
object HdrHeadroomInspector {
    /**
     * Inspects whether HDR headroom querying is supported.
     */
    fun inspect(): HdrHeadroomReport {
        return HdrHeadroomReport(
            isHdrHeadroomSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
        )
    }
}
