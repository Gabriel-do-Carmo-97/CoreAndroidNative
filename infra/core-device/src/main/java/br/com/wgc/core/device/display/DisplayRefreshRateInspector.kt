package br.com.wgc.core.device.display

import android.os.Build
import android.view.Display

/**
 * Display refresh rate capabilities (e.g. 60Hz, 90Hz, 120Hz ProMotion/LTPO).
 *
 * @property currentRefreshRate Active display refresh rate in Hertz.
 * @property supportedRefreshRates Array of supported refresh rates on this panel.
 * @property isHighRefreshRate Whether panel supports 90Hz+ smooth display.
 */
data class DisplayRefreshRateProfile(
    val currentRefreshRate: Float,
    val supportedRefreshRates: List<Float>,
    val isHighRefreshRate: Boolean,
)

/**
 * Inspector querying Android Display LTPO / High Refresh Rate (90Hz, 120Hz) profiles.
 */
object DisplayRefreshRateInspector {
    /**
     * Inspects active display mode and refresh rates.
     */
    fun inspect(display: Display?): DisplayRefreshRateProfile {
        if (display == null) {
            return DisplayRefreshRateProfile(
                currentRefreshRate = DEFAULT_REFRESH_RATE,
                supportedRefreshRates = listOf(DEFAULT_REFRESH_RATE),
                isHighRefreshRate = false,
            )
        }

        val currentRate = display.refreshRate
        val supportedRates =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                display.supportedModes.map { it.refreshRate }.distinct()
            } else {
                listOf(currentRate)
            }

        val highRate = supportedRates.any { it >= HIGH_REFRESH_RATE_THRESHOLD }

        return DisplayRefreshRateProfile(
            currentRefreshRate = currentRate,
            supportedRefreshRates = supportedRates,
            isHighRefreshRate = highRate,
        )
    }

    private const val DEFAULT_REFRESH_RATE = 60.0f
    private const val HIGH_REFRESH_RATE_THRESHOLD = 90.0f
}
