package br.com.wgc.core.database.security

import android.util.Base64
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de criptografia transparente para bancos de dados Room utilizando SQLCipher.
 *
 * Esta classe é responsável por gerar, armazenar com segurança e fornecer a chave de criptografia
 * (passphrase) necessária para abrir ou criar um banco de dados protegido por SQLCipher.
 * A passphrase gerada é mantida no [EncryptedSharedPreferencesCore], garantindo proteção
 * em repouso respaldada pelo AndroidKeyStore do dispositivo.
 *
 * ### Exemplo de Uso com Room:
 * ```kotlin
 * val factory = databaseEncrypter.getSupportFactory()
 * val db = Room.databaseBuilder(context, AppDatabase::class.java, "secure.db")
 *     .openHelperFactory(factory)
 *     .build()
 * ```
 *
 * @property encryptedStorage Instância segura de armazenamento criptografado baseada em hardware.
 */
@Singleton
class DatabaseEncrypter @Inject constructor(
    private val encryptedStorage: EncryptedSharedPreferencesCore
) {

    /**
     * Cria e retorna uma [SupportFactory] configurada com a chave criptográfica segura.
     *
     * Se uma chave já tiver sido criada para este dispositivo, ela será recuperada; caso contrário,
     * uma nova chave pseudoaleatória de 256 bits criptograficamente segura será gerada e salva.
     *
     * @return Instância de [SupportFactory] pronta para ser passada ao Room `openHelperFactory`.
     */
    fun getSupportFactory(): SupportFactory {
        val passphrase = getOrCreatePassphrase()
        return SupportFactory(passphrase)
    }

    /**
     * Recupera a passphrase existente do armazenamento seguro ou gera uma nova de 256 bits com [SecureRandom].
     */
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
        /** Chave utilizada no armazenamento seguro para salvar a passphrase do banco. */
        private const val KEY_PASSPHRASE = "wgc_db_passphrase"
    }
}
