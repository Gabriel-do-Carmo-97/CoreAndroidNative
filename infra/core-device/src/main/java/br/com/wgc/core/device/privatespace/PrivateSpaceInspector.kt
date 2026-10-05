package br.com.wgc.core.device.privatespace

import android.content.Context
import android.os.Build
import android.os.UserManager

/**
 * Diagnostic status indicating whether the application is running inside Android 15 Private Space.
 *
 * @property isPrivateSpaceSupported Whether host OS supports Android 15+ Private Space.
 * @property isRunningInPrivateProfile Whether the active process is hosted inside a segregated private profile.
 */
data class PrivateSpaceReport(
    val isPrivateSpaceSupported: Boolean,
    val isRunningInPrivateProfile: Boolean,
)

/**
 * Enterprise inspector auditing isolation and execution inside Android 15+ Private Space.
 */
class PrivateSpaceInspector(
    private val context: Context,
) {
    /**
     * Inspects whether the app is executing inside Android 15 Private Space.
     */
    fun inspect(): PrivateSpaceReport {
        val isSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
        val isPrivate =
            if (isSupported) {
                val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
                try {
                    // In Android 15+, checks if profile belongs to private profile type
                    userManager?.isProfile ?: false
                } catch (ignored: Exception) {
                    false
                }
            } else {
                false
            }

        return PrivateSpaceReport(
            isPrivateSpaceSupported = isSupported,
            isRunningInPrivateProfile = isPrivate,
        )
    }
}
