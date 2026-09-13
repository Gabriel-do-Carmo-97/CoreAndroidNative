package br.com.wgc.core.security.biometric

import androidx.biometric.BiometricManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BiometricAuthHelperTest {
    @Test
    fun `default config should have standard properties`() {
        val config =
            BiometricPromptConfig(
                title = "Autenticação biométrica",
                subtitle = "Confirme sua identidade",
            )

        assertEquals("Autenticação biométrica", config.title)
        assertEquals("Confirme sua identidade", config.subtitle)
        assertEquals("Cancelar", config.negativeButtonText)
        assertTrue(config.confirmationRequired)
        assertEquals(BiometricManager.Authenticators.BIOMETRIC_STRONG, config.authenticators)
    }

    @Test
    fun `biometric auth results should represent correct states`() {
        val success = BiometricAuthResult.Success()
        val failed = BiometricAuthResult.Failed
        val cancelled = BiometricAuthResult.Cancelled
        val error = BiometricAuthResult.Error(1, "Hardware error")

        assertTrue(success is BiometricAuthResult.Success)
        assertTrue(failed is BiometricAuthResult.Failed)
        assertTrue(cancelled is BiometricAuthResult.Cancelled)
        assertTrue(error is BiometricAuthResult.Error)
        assertEquals(1, error.errorCode)
        assertEquals("Hardware error", error.errorMessage)
    }

    @Test
    fun `biometric auth status representations`() {
        val statuses =
            listOf(
                BiometricAuthStatus.Ready,
                BiometricAuthStatus.NotAvailable,
                BiometricAuthStatus.NoneEnrolled,
                BiometricAuthStatus.HardwareUnavailable,
                BiometricAuthStatus.SecurityUpdateRequired,
                BiometricAuthStatus.Unknown,
            )

        assertEquals(6, statuses.size)
    }
}
