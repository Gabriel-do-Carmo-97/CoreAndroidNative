package br.com.wgc.core.device

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.io.File

/**
 * Detailed report containing device security and integrity diagnostic findings.
 *
 * @param isRooted Whether root indicators were detected. Always false if root detection was disabled.
 * @param isEmulator Whether emulator hardware fingerprints were detected.
 * @param isAdbEnabled Whether USB debugging / ADB is active on the device.
 * @param isRootDetectionEnabled Whether root detection checks were executed as part of the analysis.
 * @param isSecure Overall integrity status indicating no flagged security risks.
 */
data class DeviceSecurityReport(
    val isRooted: Boolean,
    val isEmulator: Boolean,
    val isAdbEnabled: Boolean,
    val isRootDetectionEnabled: Boolean,
    val isSecure: Boolean
)

/**
 * Interface contract for verifying device integrity, root status, and development mode flags.
 */
interface DeviceSecurityHelper {
    /**
     * Whether root detection is active. Configurable so non-banking or e-commerce apps
     * are not unnecessarily blocked or subjected to root inspection overhead.
     */
    val isRootDetectionEnabled: Boolean

    /**
     * Checks if the host device has indicators of being rooted (su binaries, test keys).
     * Returns false immediately if [isRootDetectionEnabled] is false.
     *
     * @return true if root indicators are detected and root detection is enabled.
     */
    fun isRooted(): Boolean

    /**
     * Checks if the app is currently running within an Android emulator environment.
     *
     * @return true if emulator characteristics are identified.
     */
    fun isEmulator(): Boolean

    /**
     * Checks if Android Debug Bridge (ADB) / USB debugging is enabled on the device.
     *
     * @return true if ADB is enabled.
     */
    fun isAdbEnabled(): Boolean

    /**
     * Executes a comprehensive device security audit based on configured policies.
     *
     * @return [DeviceSecurityReport] summarizing the device's integrity posture.
     */
    fun checkSecurity(): DeviceSecurityReport
}

/**
 * Default production implementation of [DeviceSecurityHelper].
 *
 * @param context Android context used to read system settings.
 * @param isRootDetectionEnabled Flag to toggle root detection checks. Defaults to false.
 */
class DefaultDeviceSecurityHelper(
    private val context: Context,
    override val isRootDetectionEnabled: Boolean = false
) : DeviceSecurityHelper {

    override fun isRooted(): Boolean {
        if (!isRootDetectionEnabled) {
            return false
        }
        return checkBuildTags() || checkSuPaths() || checkSuperuserApk()
    }

    override fun isEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT
        val model = Build.MODEL
        val manufacturer = Build.MANUFACTURER
        val brand = Build.BRAND
        val device = Build.DEVICE
        val product = Build.PRODUCT

        return fingerprint.startsWith("generic") ||
            fingerprint.startsWith("unknown") ||
            model.contains("google_sdk") ||
            model.contains("Emulator") ||
            model.contains("Android SDK built for x86") ||
            manufacturer.contains("Genymotion") ||
            brand.startsWith("generic") && device.startsWith("generic") ||
            product.contains("sdk") ||
            product.contains("vbox86p") ||
            product.contains("emulator")
    }

    override fun isAdbEnabled(): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) != 0
        } catch (_: SecurityException) {
            false
        }
    }

    override fun checkSecurity(): DeviceSecurityReport {
        val rooted = isRooted()
        val emulator = isEmulator()
        val adb = isAdbEnabled()
        val secure = !emulator && !adb && (!isRootDetectionEnabled || !rooted)

        return DeviceSecurityReport(
            isRooted = rooted,
            isEmulator = emulator,
            isAdbEnabled = adb,
            isRootDetectionEnabled = isRootDetectionEnabled,
            isSecure = secure
        )
    }

    private fun checkBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkSuPaths(): Boolean {
        val paths = listOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkSuperuserApk(): Boolean {
        return File("/system/app/Superuser/Superuser.apk").exists()
    }
}
