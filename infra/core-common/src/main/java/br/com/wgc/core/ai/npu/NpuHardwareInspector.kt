package br.com.wgc.core.ai.npu

import android.os.Build

/**
 * Diagnostics information regarding host NPU (Neural Processing Unit) and Android NNAPI.
 *
 * @property isNpuSupported Device OS and hardware support Android Neural Networks API.
 * @property apiLevel Host Android API level.
 * @property hardwareIdentifier Chipset manufacturer and hardware descriptor.
 */
data class NpuHardwareProfile(
    val isNpuSupported: Boolean,
    val apiLevel: Int,
    val hardwareIdentifier: String,
)

/**
 * Inspector for querying hardware acceleration capabilities for neural networks.
 */
object NpuHardwareInspector {
    /**
     * Inspects device parameters to determine if NNAPI / NPU hardware execution is supported.
     */
    fun inspect(): NpuHardwareProfile {
        val isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
        return NpuHardwareProfile(
            isNpuSupported = isSupported,
            apiLevel = Build.VERSION.SDK_INT,
            hardwareIdentifier = "${Build.MANUFACTURER} ${Build.HARDWARE}",
        )
    }
}
