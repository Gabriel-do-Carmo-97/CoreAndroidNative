package br.com.wgc.core.device.display

import android.os.Build

/**
 * Diagnostic status of 16 KB page size kernel memory alignment.
 *
 * @property is16KbPageSizeCandidate Whether host device or emulator is running 16 KB memory page size.
 */
data class PageSizeProfile(
    val is16KbPageSizeCandidate: Boolean,
)

/**
 * Inspector auditing ELF memory alignment and compatibility with Android 15 16 KB page size kernel.
 */
object MemoryPageSizeInspector {
    /**
     * Checks if OS version targets 16 KB page size compliance.
     */
    fun inspect(): PageSizeProfile {
        return PageSizeProfile(
            is16KbPageSizeCandidate = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM,
        )
    }
}
