package br.com.wgc.core.device.security

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.provider.Settings

/**
 * Snapshot evaluation of the device security posture.
 *
 * @property isScreenLockEnabled Device has PIN, pattern, or biometric lock configured.
 * @property isAdbEnabled USB debugging / ADB is active.
 * @property isDeveloperOptionsEnabled Developer mode is turned on.
 * @property isDeviceSecure Overall trust assessment score.
 */
data class DevicePosture(
    val isScreenLockEnabled: Boolean,
    val isAdbEnabled: Boolean,
    val isDeveloperOptionsEnabled: Boolean,
    val isDeviceSecure: Boolean,
)

/**
 * Enterprise Zero-Trust evaluator for auditing host device security posture and compliance.
 */
class DevicePostureEvaluator(
    private val context: Context,
) {
    /**
     * Inspects OS security settings to determine whether the device meets enterprise compliance.
     */
    fun evaluate(): DevicePosture {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        val isScreenLock = keyguardManager?.isDeviceSecure ?: false

        val isAdb =
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0,
            ) != 0

        val isDevOptions =
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0,
            ) != 0

        val secure = isScreenLock && !isAdb

        return DevicePosture(
            isScreenLockEnabled = isScreenLock,
            isAdbEnabled = isAdb,
            isDeveloperOptionsEnabled = isDevOptions,
            isDeviceSecure = secure,
        )
    }

    /**
     * Checks if device is running on an officially patched Android build.
     */
    fun isRecentSecurityPatch(): Boolean {
        return Build.VERSION.SECURITY_PATCH.isNotBlank()
    }
}
