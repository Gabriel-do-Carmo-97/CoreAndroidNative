package br.com.wgc.core.security.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Represents the biometric availability and capability state on the device.
 */
sealed interface BiometricAuthStatus {
    /** Hardware is present, functional, and user has enrolled biometric credentials. */
    data object Ready : BiometricAuthStatus

    /** No biometric hardware is present on the device. */
    data object NotAvailable : BiometricAuthStatus

    /** Biometric hardware is present, but no biometric credentials are currently enrolled. */
    data object NoneEnrolled : BiometricAuthStatus

    /** Biometric sensor is currently unavailable or busy. */
    data object HardwareUnavailable : BiometricAuthStatus

    /** A security vulnerability was found and an update is required before using biometrics. */
    data object SecurityUpdateRequired : BiometricAuthStatus

    /** Biometric state cannot be determined or unsupported status returned. */
    data object Unknown : BiometricAuthStatus
}

/**
 * Result dispatched after a biometric authentication challenge.
 */
sealed interface BiometricAuthResult {
    /** Authentication succeeded with optional authenticated [cryptoObject]. */
    data class Success(val cryptoObject: BiometricPrompt.CryptoObject? = null) : BiometricAuthResult

    /** Authentication failed because biometric data did not match enrolled credentials. */
    data object Failed : BiometricAuthResult

    /** Authentication was explicitly cancelled by the user or system. */
    data object Cancelled : BiometricAuthResult

    /**
     * Unrecoverable authentication error occurred.
     *
     * @param errorCode Raw error code from [BiometricPrompt].
     * @param errorMessage Human-readable description of the error.
     */
    data class Error(val errorCode: Int, val errorMessage: CharSequence) : BiometricAuthResult
}

/**
 * Configuration parameters for displaying a biometric prompt dialog.
 *
 * @param title Title displayed in the biometric dialog.
 * @param subtitle Optional subtitle displayed below the title.
 * @param description Optional detailed description displayed in the dialog.
 * @param negativeButtonText Text for cancellation button. Ignored if credentials are allowed.
 * @param confirmationRequired Whether explicit confirmation (e.g. tap) is required after passive biometric.
 * @param authenticators Bitmask of allowed authenticators from [BiometricManager.Authenticators].
 */
data class BiometricPromptConfig(
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val negativeButtonText: String = "Cancelar",
    val confirmationRequired: Boolean = true,
    val authenticators: Int = BiometricManager.Authenticators.BIOMETRIC_STRONG
)

/**
 * Interface contract for checking biometric support and prompting user authentication.
 */
interface BiometricAuthHelper {
    /**
     * Checks whether biometric authentication is available and configured on the device.
     *
     * @param authenticators Bitmask of authenticators to verify against.
     * @return [BiometricAuthStatus] indicating device capability.
     */
    fun canAuthenticate(
        authenticators: Int = BiometricManager.Authenticators.BIOMETRIC_STRONG
    ): BiometricAuthStatus

    /**
     * Displays the biometric prompt dialog and emits the authentication result.
     *
     * @param activity Hosting [FragmentActivity] used to bind the prompt lifecycle.
     * @param config Dialog presentation and policy configuration.
     * @param cryptoObject Optional cryptography object for hardware-backed signature/cipher operations.
     * @param callback Result listener invoked on main thread upon completion.
     */
    fun authenticate(
        activity: FragmentActivity,
        config: BiometricPromptConfig,
        cryptoObject: BiometricPrompt.CryptoObject? = null,
        callback: (BiometricAuthResult) -> Unit
    )
}

/**
 * Default production implementation of [BiometricAuthHelper] using AndroidX Biometric library.
 *
 * @param context Application context used for biometric manager queries.
 */
class DefaultBiometricAuthHelper(
    private val context: Context
) : BiometricAuthHelper {

    override fun canAuthenticate(authenticators: Int): BiometricAuthStatus {
        val manager = BiometricManager.from(context)
        return when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthStatus.Ready
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAuthStatus.NotAvailable
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAuthStatus.HardwareUnavailable
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAuthStatus.NoneEnrolled
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAuthStatus.SecurityUpdateRequired
            else -> BiometricAuthStatus.Unknown
        }
    }

    override fun authenticate(
        activity: FragmentActivity,
        config: BiometricPromptConfig,
        cryptoObject: BiometricPrompt.CryptoObject?,
        callback: (BiometricAuthResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val promptCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                callback(BiometricAuthResult.Success(result.cryptoObject))
            }

            override fun onAuthenticationFailed() {
                callback(BiometricAuthResult.Failed)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                ) {
                    callback(BiometricAuthResult.Cancelled)
                } else {
                    callback(BiometricAuthResult.Error(errorCode, errString))
                }
            }
        }

        val promptInfoBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(config.title)
            .setConfirmationRequired(config.confirmationRequired)

        config.subtitle?.let { promptInfoBuilder.setSubtitle(it) }
        config.description?.let { promptInfoBuilder.setDescription(it) }

        val allowsDeviceCredential = (config.authenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL) != 0
        if (allowsDeviceCredential) {
            promptInfoBuilder.setAllowedAuthenticators(config.authenticators)
        } else {
            promptInfoBuilder.setNegativeButtonText(config.negativeButtonText)
            promptInfoBuilder.setAllowedAuthenticators(config.authenticators)
        }

        val prompt = BiometricPrompt(activity, executor, promptCallback)
        if (cryptoObject != null) {
            prompt.authenticate(promptInfoBuilder.build(), cryptoObject)
        } else {
            prompt.authenticate(promptInfoBuilder.build())
        }
    }
}
