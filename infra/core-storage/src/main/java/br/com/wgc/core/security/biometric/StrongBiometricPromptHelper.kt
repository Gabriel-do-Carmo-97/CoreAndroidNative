package br.com.wgc.core.security.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt

/**
 * Auxiliar corporativo para configuração de prompts biométricos Classe 3 (Strong Biometrics).
 * Exige autenticação forte de hardware e confirmação explícita de presença do usuário.
 */
object StrongBiometricPromptHelper {
    /**
     * Constrói [BiometricPrompt.PromptInfo] em conformidade estrita com as diretrizes de biometria forte.
     *
     * @param title Título do prompt biométrico exibido ao usuário.
     * @param subtitle Subtítulo descritivo ou nome da transação sensível.
     * @param negativeButtonText Rótulo do botão de cancelamento (ex: "Cancelar").
     * @param requireConfirmation Exige clique explícito após leitura biométrica (ex: Face Unlock).
     */
    fun createStrongPromptInfo(
        title: String,
        subtitle: String? = null,
        negativeButtonText: String = "Cancelar",
        requireConfirmation: Boolean = true,
    ): BiometricPrompt.PromptInfo {
        val builder =
            BiometricPrompt.PromptInfo
                .Builder()
                .setTitle(title)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setConfirmationRequired(requireConfirmation)
                .setNegativeButtonText(negativeButtonText)

        if (subtitle != null) {
            builder.setSubtitle(subtitle)
        }

        return builder.build()
    }
}
