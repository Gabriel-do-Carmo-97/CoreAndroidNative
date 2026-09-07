package br.com.wgc.core.database.security

import android.util.Base64
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseEncrypter @Inject constructor(
    private val encryptedStorage: EncryptedSharedPreferencesCore
) {
    fun getSupportFactory(): SupportFactory {
        val passphrase = getOrCreatePassphrase()
        return SupportFactory(passphrase)
    }

    private fun getOrCreatePassphrase(): ByteArray {
        val existing = encryptedStorage.getString(KEY_PASSPHRASE)
        if (existing != null) {
            return existing.toByteArray(Charsets.UTF_8)
        }

        val randomBytes = ByteArray(32)
        SecureRandom().nextBytes(randomBytes)
        val newPassphrase = Base64.encodeToString(randomBytes, Base64.NO_WRAP)
        encryptedStorage.saveString(KEY_PASSPHRASE, newPassphrase)
        return newPassphrase.toByteArray(Charsets.UTF_8)
    }

    companion object {
        private const val KEY_PASSPHRASE = "wgc_db_passphrase"
    }
}
