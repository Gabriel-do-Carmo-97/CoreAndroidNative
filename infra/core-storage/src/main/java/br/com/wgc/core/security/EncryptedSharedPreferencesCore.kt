package br.com.wgc.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.wgc.core.sharedPreferences.KeyValueStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação Singleton de [KeyValueStorage] com criptografia ponta a ponta via Android KeyStore.
 *
 * Utiliza o AndroidX Security Crypto com os seguintes padrões de segurança:
 * - **Chaves criptográficas em hardware**: [MasterKey] com esquema [MasterKey.KeyScheme.AES256_GCM].
 * - **Chaves de preferência**: Criptografia [EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV].
 * - **Valores de preferência**: Criptografia [EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM].
 *
 * Indicado para armazenar credenciais sensíveis: tokens JWT, chaves de API e dados pessoais confidenciais (PII).
 *
 * @param context Contexto da aplicação Android.
 * @param fileName Nome do arquivo seguro em disco.
 */
@Singleton
@Suppress("DEPRECATION")
class EncryptedSharedPreferencesCore
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val fileName: String = DEFAULT_ENCRYPTED_PREFS_FILE,
    ) : KeyValueStorage {
        private val masterKey =
            MasterKey
                .Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        private val sharedPreferences: SharedPreferences by lazy {
            EncryptedSharedPreferences.create(
                context,
                fileName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        override fun saveString(
            key: String,
            value: String,
        ) = sharedPreferences.edit {
            putString(key, value)
        }

        override fun getString(
            key: String,
            defaultValue: String?,
        ): String? = sharedPreferences.getString(key, defaultValue)

        override fun saveInt(
            key: String,
            value: Int,
        ) = sharedPreferences.edit {
            putInt(key, value)
        }

        override fun getInt(
            key: String,
            defaultValue: Int,
        ): Int = sharedPreferences.getInt(key, defaultValue)

        override fun saveBoolean(
            key: String,
            value: Boolean,
        ) = sharedPreferences.edit {
            putBoolean(key, value)
        }

        override fun getBoolean(
            key: String,
            defaultValue: Boolean,
        ): Boolean = sharedPreferences.getBoolean(key, defaultValue)

        override fun saveFloat(
            key: String,
            value: Float,
        ) = sharedPreferences.edit {
            putFloat(key, value)
        }

        override fun getFloat(
            key: String,
            defaultValue: Float,
        ): Float = sharedPreferences.getFloat(key, defaultValue)

        override fun saveLong(
            key: String,
            value: Long,
        ) = sharedPreferences.edit {
            putLong(key, value)
        }

        override fun getLong(
            key: String,
            defaultValue: Long,
        ): Long = sharedPreferences.getLong(key, defaultValue)

        override fun saveStringSet(
            key: String,
            value: Set<String>,
        ) = sharedPreferences.edit {
            putStringSet(key, value)
        }

        override fun getStringSet(
            key: String,
            defaultValue: Set<String>,
        ): Set<String> = sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue

        override fun remove(key: String) =
            sharedPreferences.edit {
                remove(key)
            }

        override fun clear() =
            sharedPreferences.edit {
                clear()
            }

        override fun contains(key: String): Boolean = sharedPreferences.contains(key)

        override fun getAll(): Map<String, *> = sharedPreferences.all

        companion object {
            /**
             * Nome padrão do arquivo seguro de preferências em disco.
             */
            const val DEFAULT_ENCRYPTED_PREFS_FILE = "wgc_encrypted_preferences"
        }
    }
