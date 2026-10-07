package br.com.wgc.core.device.nfc

import android.content.Context
import android.nfc.NfcAdapter

/**
 * Diagnostic status of host device NFC controller.
 *
 * @property isNfcHardwarePresent Device has an NFC controller chipset.
 * @property isNfcEnabled NFC is currently powered on and enabled in settings.
 * @property isHostCardEmulationSupported Host Card Emulation (HCE) for contactless payments is supported.
 */
data class NfcStatusReport(
    val isNfcHardwarePresent: Boolean,
    val isNfcEnabled: Boolean,
    val isHostCardEmulationSupported: Boolean,
)

/**
 * Enterprise inspector for Near Field Communication (NFC) and Host Card Emulation (HCE).
 */
class NfcCapabilityInspector(
    private val context: Context,
) {
    /**
     * Inspects device NFC controller status and HCE capability.
     */
    fun inspect(): NfcStatusReport {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        val hasHce =
            context.packageManager.hasSystemFeature(
                android.content.pm.PackageManager.FEATURE_NFC_HOST_CARD_EMULATION,
            )

        return NfcStatusReport(
            isNfcHardwarePresent = adapter != null,
            isNfcEnabled = adapter?.isEnabled ?: false,
            isHostCardEmulationSupported = hasHce,
        )
    }
}
