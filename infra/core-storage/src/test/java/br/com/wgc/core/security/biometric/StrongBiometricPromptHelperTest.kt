package br.com.wgc.core.security.biometric

import androidx.biometric.BiometricManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StrongBiometricPromptHelperTest {
    @Test
    fun `createStrongPromptInfo configures strong authenticators and confirmation`() {
        val promptInfo =
            StrongBiometricPromptHelper.createStrongPromptInfo(
                title = "Autenticação Bancária",
                subtitle = "Transferência PIX",
                negativeButtonText = "Cancelar",
                requireConfirmation = true,
            )

        assertNotNull(promptInfo)
        assertEquals("Autenticação Bancária", promptInfo.title)
        assertEquals("Transferência PIX", promptInfo.subtitle)
        assertEquals("Cancelar", promptInfo.negativeButtonText)
        assertEquals(BiometricManager.Authenticators.BIOMETRIC_STRONG, promptInfo.allowedAuthenticators)
        assertTrue(promptInfo.isConfirmationRequired)
    }
}
