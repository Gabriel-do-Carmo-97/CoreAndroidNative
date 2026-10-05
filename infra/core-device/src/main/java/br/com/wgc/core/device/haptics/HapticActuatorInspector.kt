package br.com.wgc.core.device.haptics

import android.os.Build
import android.os.Vibrator

/**
 * Diagnostic capabilities of the host device vibration motor and haptic actuator.
 *
 * @property hasVibrator Device contains a physical haptic actuator.
 * @property hasAmplitudeControl Actuator supports granular amplitude modulation (Linear Resonant Actuator - LRA).
 */
data class HapticActuatorCapabilities(
    val hasVibrator: Boolean,
    val hasAmplitudeControl: Boolean,
)

/**
 * Inspector querying haptic motor type and amplitude modulation capabilities.
 */
class HapticActuatorInspector(
    private val vibrator: Vibrator?,
) {
    /**
     * Inspects actuator capabilities.
     */
    fun inspect(): HapticActuatorCapabilities {
        val hasVibrator = vibrator?.hasVibrator() ?: false
        val hasAmplitude =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.hasAmplitudeControl() ?: false
            } else {
                false
            }

        return HapticActuatorCapabilities(
            hasVibrator = hasVibrator,
            hasAmplitudeControl = hasAmplitude,
        )
    }
}
