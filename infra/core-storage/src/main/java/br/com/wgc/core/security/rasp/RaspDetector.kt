package br.com.wgc.core.security.rasp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import java.io.File
import java.net.Socket
import java.security.MessageDigest

/**
 * Runtime Application Self-Protection (RASP) engine.
 * Detects dynamic hooking (Frida, Xposed, Substrate), Zygisk, debuggers,
 * emulators, and binary tampering/re-signing.
 */
class RaspDetector(
    private val context: Context? = null,
) {
    enum class ThreatLevel {
        NONE,
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL,
    }

    data class DetectedThreat(
        val name: String,
        val description: String,
        val level: ThreatLevel,
    )

    data class RaspAssessment(
        val isCompromised: Boolean,
        val highestThreatLevel: ThreatLevel,
        val threats: List<DetectedThreat>,
    )

    /**
     * Executes all security checks and returns a comprehensive assessment.
     */
    fun assessThreats(expectedSigningSha256: String? = null): RaspAssessment {
        val threats = mutableListOf<DetectedThreat>()

        if (isFridaDetected()) {
            threats.add(
                DetectedThreat(
                    name = "FRIDA_HOOKING",
                    description = "Frida server or gadget detected in memory or filesystem",
                    level = ThreatLevel.CRITICAL,
                ),
            )
        }

        if (isHookingFrameworkDetected()) {
            threats.add(
                DetectedThreat(
                    name = "HOOKING_FRAMEWORK",
                    description = "Xposed, Substrate or Zygisk hooking framework detected",
                    level = ThreatLevel.CRITICAL,
                ),
            )
        }

        if (isDebuggerAttached()) {
            threats.add(
                DetectedThreat(
                    name = "DEBUGGER_ATTACHED",
                    description = "Debugger is actively attached or waiting for debugger",
                    level = ThreatLevel.HIGH,
                ),
            )
        }

        if (isEmulator()) {
            threats.add(
                DetectedThreat(
                    name = "EMULATOR_ENVIRONMENT",
                    description = "Application is executing inside an emulated device",
                    level = ThreatLevel.MEDIUM,
                ),
            )
        }

        if (expectedSigningSha256 != null && !isSignatureValid(expectedSigningSha256)) {
            threats.add(
                DetectedThreat(
                    name = "SIGNATURE_MISMATCH",
                    description = "APK signature does not match expected release fingerprint",
                    level = ThreatLevel.CRITICAL,
                ),
            )
        }

        val highestLevel = threats.maxOfOrNull { it.level } ?: ThreatLevel.NONE
        val isCompromised = highestLevel in listOf(ThreatLevel.HIGH, ThreatLevel.CRITICAL)

        return RaspAssessment(
            isCompromised = isCompromised,
            highestThreatLevel = highestLevel,
            threats = threats,
        )
    }

    /**
     * Detects Frida through ports, files, and memory maps.
     */
    fun isFridaDetected(): Boolean {
        // 1. Check known Frida ports
        val fridaPorts = listOf(27042, 27043)
        for (port in fridaPorts) {
            try {
                Socket("127.0.0.1", port).use { return true }
            } catch (_: Exception) {
                // Port closed, continue
            }
        }

        // 2. Check filesystem for frida artifacts
        val fridaFiles =
            listOf(
                "/data/local/tmp/frida-server",
                "/data/local/tmp/re.frida.server",
                "/system/bin/frida-server",
            )
        for (path in fridaFiles) {
            if (File(path).exists()) return true
        }

        // 3. Inspect /proc/self/maps for frida-gadget or frida-agent
        try {
            val mapsFile = File("/proc/self/maps")
            if (mapsFile.exists()) {
                val hasFridaLib = mapsFile.useLines { lines ->
                    lines.any { line ->
                        line.contains("frida-gadget", ignoreCase = true) ||
                            line.contains("frida-agent", ignoreCase = true) ||
                            line.contains("libgadget.so", ignoreCase = true)
                    }
                }
                if (hasFridaLib) return true
            }
        } catch (_: Exception) {
            // Ignore access errors on hardened Android
        }

        return false
    }

    /**
     * Detects Xposed, Substrate and Zygisk reflection hooks.
     */
    fun isHookingFrameworkDetected(): Boolean {
        val hookClasses =
            listOf(
                "de.robv.android.xposed.XposedBridge",
                "de.robv.android.xposed.XposedHelpers",
                "com.saurik.substrate.MS$2",
                "me.weishu.epic.art.Epic",
            )
        for (className in hookClasses) {
            try {
                Class.forName(className)
                return true
            } catch (_: ClassNotFoundException) {
                // Expected when clean
            }
        }

        // Check for Xposed / Zygisk file artifacts
        val hookFiles =
            listOf(
                "/system/framework/XposedBridge.jar",
                "/system/lib/libxposed_art.so",
                "/data/adb/zygisk",
                "/data/adb/modules",
            )
        for (path in hookFiles) {
            if (File(path).exists()) return true
        }

        return false
    }

    /**
     * Checks if a Java or Native debugger is attached.
     */
    fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    /**
     * Identifies if the app is executing within an emulator.
     */
    fun isEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()
        val hardware = Build.HARDWARE.lowercase()

        return fingerprint.startsWith("generic") ||
            fingerprint.startsWith("unknown") ||
            fingerprint.contains("google/sdk_gphone") ||
            fingerprint.contains("vbox86p") ||
            model.contains("google_sdk") ||
            model.contains("emulator") ||
            model.contains("android sdk built for") ||
            manufacturer.contains("genymotion") ||
            brand.startsWith("generic") &&
            device.startsWith("generic") ||
            hardware.contains("goldfish") ||
            hardware.contains("ranchu") ||
            product.contains("sdk_google") ||
            product.contains("google_sdk") ||
            product.contains("sdk") ||
            product.contains("vbox86p")
    }

    /**
     * Verifies APK certificate SHA-256 against an expected fingerprint to detect re-signing.
     */
    fun isSignatureValid(expectedSha256: String): Boolean {
        if (context == null) return false
        return try {
            val packageInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES,
                    )
                } else {
                    @Suppress("DEPRECATION")
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        PackageManager.GET_SIGNATURES,
                    )
                }

            val signatures =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.signingInfo?.apkContentsSigners
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.signatures
                }

            if (signatures.isNullOrEmpty()) return false

            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(signatures[0].toByteArray())
            val hex = digest.joinToString("") { "%02X".format(it) }

            hex.equals(expectedSha256.replace(":", "").uppercase(), ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }
}
