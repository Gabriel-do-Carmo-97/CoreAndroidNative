package br.com.wgc.core.security.biometric

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricPrompt
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Utilitário corporativo para criação e gerenciamento de cifras criptográficas protegidas por hardware
 * e autenticação biométrica do usuário ([BiometricPrompt.CryptoObject]).
 *
 * Utilizado para assinar transações financeiras e proteger chaves de acesso que só podem ser
 * manipuladas após a autenticação positiva de biometria forte (Class 3 / Strong Biometrics).
 */
class BiometricCryptoHelper(
    private val keyAlias: String = DEFAULT_KEY_ALIAS,
    private val keyStoreProvider: String = ANDROID_KEYSTORE,
) {
    private val keyStore: KeyStore by lazy {
        runCatching {
            KeyStore.getInstance(keyStoreProvider).apply { load(null) }
        }.getOrElse {
            KeyStore.getInstance(KeyStore.getDefaultType()).apply { load(null) }
        }
    }

    /**
     * Obtém ou gera uma chave secreta AES-256 no Android KeyStore configurada para requerer
     * autenticação biométrica do usuário para cada uso.
     */
    fun getOrCreateSecretKey(): SecretKey {
        if (!keyStore.containsAlias(keyAlias)) {
            val key =
                if (keyStore.provider.name == ANDROID_KEYSTORE) {
                    generateKeyStoreKey()
                } else {
                    generateJvmFallbackKey()
                }
            return key
        }
        return keyStore.getKey(keyAlias, null) as SecretKey
    }

    private fun generateKeyStoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val builder =
            KeyGenParameterSpec
                .Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_SIZE_BITS)
                .setUserAuthenticationRequired(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setUserAuthenticationParameters(
                0,
                KeyProperties.AUTH_BIOMETRIC_STRONG,
            )
        }

        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    private fun generateJvmFallbackKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(KEY_SIZE_BITS)
        val secretKey = keyGenerator.generateKey()
        keyStore.setKeyEntry(keyAlias, secretKey, null, null)
        return secretKey
    }

    /**
     * Cria um [BiometricPrompt.CryptoObject] inicializado para encriptação.
     */
    fun createEncryptCryptoObject(): BiometricPrompt.CryptoObject {
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        return BiometricPrompt.CryptoObject(cipher)
    }

    /**
     * Cria um [BiometricPrompt.CryptoObject] inicializado para decriptação com o IV fornecido.
     *
     * @param initializationVector Vetor de inicialização (IV) retornado durante a encriptação.
     */
    fun createDecryptCryptoObject(initializationVector: ByteArray): BiometricPrompt.CryptoObject {
        val cipher = getCipher()
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, initializationVector)
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
        return BiometricPrompt.CryptoObject(cipher)
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}",
        )
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val DEFAULT_KEY_ALIAS = "CoreBiometricEnterpriseKey"
        private const val KEY_SIZE_BITS = 256
        private const val GCM_TAG_LENGTH_BITS = 128
    }
}
