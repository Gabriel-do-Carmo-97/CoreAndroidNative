package br.com.wgc.core.device.audio

import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor

/**
 * Diagnostic status of hardware-assisted DSP audio processing units.
 *
 * @property isAecAvailable Acoustic Echo Cancellation hardware DSP is supported.
 * @property isNoiseSuppressorAvailable Noise Suppression hardware DSP is supported.
 * @property isAgcAvailable Automatic Gain Control hardware DSP is supported.
 */
data class AudioProcessingCapabilities(
    val isAecAvailable: Boolean,
    val isNoiseSuppressorAvailable: Boolean,
    val isAgcAvailable: Boolean,
)

/**
 * Enterprise inspector for Android AudioRecord hardware DSP effects (AEC, NS, AGC).
 */
object AudioProcessingInspector {
    /**
     * Inspects host audio DSP chip capabilities.
     */
    fun inspect(): AudioProcessingCapabilities {
        val aec =
            try {
                AcousticEchoCanceler.isAvailable()
            } catch (ignored: Exception) {
                false
            }
        val ns =
            try {
                NoiseSuppressor.isAvailable()
            } catch (ignored: Exception) {
                false
            }
        val agc =
            try {
                AutomaticGainControl.isAvailable()
            } catch (ignored: Exception) {
                false
            }

        return AudioProcessingCapabilities(
            isAecAvailable = aec,
            isNoiseSuppressorAvailable = ns,
            isAgcAvailable = agc,
        )
    }
}
