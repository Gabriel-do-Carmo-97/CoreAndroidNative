package br.com.wgc.core.sharedPreferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação Singleton de [KeyValueStorage] baseada em [android.content.SharedPreferences] padrão.
 *
 * Utiliza o modo privado ([Context.MODE_PRIVATE]) e extensões do AndroidX KTX para gravações atômicas e seguras.
 * Recomendado para dados simples de configuração da aplicação que não exijam criptografia.
 *
 * @param context Contexto da aplicação Android.
 * @param sharedPreferencesName Nome do arquivo XML de preferências no armazenamento privado do app.
 */
@Singleton
class SharedPreferencesCore @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val sharedPreferencesName: String = DEFAULT_SHARED_PREFERENCES_NAME
) : KeyValueStorage {

    private val sharedPreferences = context.getSharedPreferences(
        sharedPreferencesName,
        Context.MODE_PRIVATE
    )

    override fun saveString(
        key: String,
        value: String
    ) = sharedPreferences.edit {
        putString(key, value)
    }

    override fun getString(
        key: String,
        defaultValue: String?
    ): String? = sharedPreferences.getString(key, defaultValue)

    override fun saveInt(
        key: String,
        value: Int
    ) = sharedPreferences.edit {
        putInt(key, value)
    }

    override fun getInt(
        key: String,
        defaultValue: Int
    ): Int = sharedPreferences.getInt(key, defaultValue)

    override fun saveBoolean(
        key: String,
        value: Boolean
    ) = sharedPreferences.edit {
        putBoolean(key, value)
    }

    override fun getBoolean(
        key: String,
        defaultValue: Boolean
    ): Boolean = sharedPreferences.getBoolean(key, defaultValue)

    override fun saveFloat(
        key: String,
        value: Float
    ) = sharedPreferences.edit {
        putFloat(key, value)
    }

    override fun getFloat(
        key: String,
        defaultValue: Float
    ): Float = sharedPreferences.getFloat(key, defaultValue)

    override fun saveLong(
        key: String,
        value: Long
    ) = sharedPreferences.edit {
        putLong(key, value)
    }

    override fun getLong(
        key: String,
        defaultValue: Long
    ): Long = sharedPreferences.getLong(key, defaultValue)

    override fun saveStringSet(
        key: String,
        value: Set<String>
    ) = sharedPreferences.edit {
        putStringSet(key, value)
    }

    override fun getStringSet(
        key: String,
        defaultValue: Set<String>
    ): Set<String> = sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue

    override fun clear() = sharedPreferences.edit {
        clear()
    }

    override fun remove(
        key: String
    ) = sharedPreferences.edit {
        remove(key)
    }

    override fun contains(key: String): Boolean = sharedPreferences.contains(key)

    override fun getAll(): Map<String, *> = sharedPreferences.all

    companion object {
        const val DEFAULT_SHARED_PREFERENCES_NAME = "wgc_core_preferences"
    }
}