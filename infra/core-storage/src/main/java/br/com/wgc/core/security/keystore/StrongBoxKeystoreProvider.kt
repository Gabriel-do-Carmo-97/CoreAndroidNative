package br.com.wgc.core.security.keystore

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Result metadata indicating whether cryptographic material was isolated in StrongBox HSM.
 *
 * @property alias Key alias in AndroidKeyStore.
 * @property isStrongBoxBacked Whether hardware security module (StrongBox) was used.
 */
data class KeystoreSecurityLevel(
    val alias: String,
    val isStrongBoxBacked: Boolean,
)

/**
 * Enterprise provider for Hardware Security Module (HSM) and StrongBox-backed cryptography.
 *
 * Automatically provisions AES-GCM-256 keys inside dedicated hardware (SE / StrongBox)
 * on supported Android 9+ devices, falling back gracefully to TEE (Trusted Execution Environment)
 * when StrongBox hardware is absent.
 */
@Singleton
class StrongBoxKeystoreProvider
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val keyStore: KeyStore =
            KeyStore.getInstance(ANDROID_KEY_STORE).apply {
                load(null)
            }

        /**
         * Checks if the device hardware includes a StrongBox Keymaster/Keymint chip.
         */
        fun isStrongBoxSupported(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
            } else {
                false
            }
        }

        /**
         * Generates or retrieves an AES-GCM-256 SecretKey, preferring StrongBox HSM.
         *
         * @param alias The unique alias for the key.
         * @param requireAuthentication If true, mandates biometric or lockscreen auth before cipher usage.
         * @return [KeystoreSecurityLevel] detailing key alias and hardware backing.
         */
        fun getOrCreateAesKey(
            alias: String,
            requireAuthentication: Boolean = false,
        ): KeystoreSecurityLevel {
            if (keyStore.containsAlias(alias)) {
                return KeystoreSecurityLevel(
                    alias = alias,
                    isStrongBoxBacked = isStrongBoxSupported(),
                )
            }

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            val useStrongBox = isStrongBoxSupported()

            val specBuilder =
                KeyGenParameterSpec
                    .Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(AES_KEY_SIZE_BITS)
                    .setUserAuthenticationRequired(requireAuthentication)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && useStrongBox) {
                try {
                    specBuilder.setIsStrongBoxBacked(true)
                    keyGenerator.init(specBuilder.build())
                    keyGenerator.generateKey()
                    return KeystoreSecurityLevel(alias = alias, isStrongBoxBacked = true)
                } catch (ignored: Exception) {
                    // Fall back to TEE if StrongBox provisioning fails (e.g., resource limit)
                    specBuilder.setIsStrongBoxBacked(false)
                }
            }

            keyGenerator.init(specBuilder.build())
            keyGenerator.generateKey()
            return KeystoreSecurityLevel(alias = alias, isStrongBoxBacked = false)
        }

        /**
         * Retrieves the SecretKey from AndroidKeyStore.
         */
        fun getSecretKey(alias: String): SecretKey? {
            return (keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry)?.secretKey
        }

        /**
         * Deletes the key from AndroidKeyStore.
         */
        fun deleteKey(alias: String): Boolean {
            return if (keyStore.containsAlias(alias)) {
                keyStore.deleteEntry(alias)
                true
            } else {
                false
            }
        }

        /**
         * Returns an initialized Cipher instance for the specified operation mode.
         */
        fun getCipher(
            alias: String,
            mode: Int,
        ): Cipher {
            val key = getSecretKey(alias) ?: error("Key not found in AndroidKeyStore for alias: $alias")
            val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
            cipher.init(mode, key)
            return cipher
        }

        companion object {
            private const val ANDROID_KEY_STORE = "AndroidKeyStore"
            private const val AES_KEY_SIZE_BITS = 256
            private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
        }
    }
