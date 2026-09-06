package br.com.wgc.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("DEPRECATION")
class EncryptedSharedPreferencesCore @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val fileName: String = DEFAULT_ENCRYPTED_PREFS_FILE
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveString(key: String, value: String) = sharedPreferences.edit {
        putString(key, value)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) = sharedPreferences.edit {
        putInt(key, value)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun saveBoolean(key: String, value: Boolean) = sharedPreferences.edit {
        putBoolean(key, value)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveFloat(key: String, value: Float) = sharedPreferences.edit {
        putFloat(key, value)
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) = sharedPreferences.edit {
        putLong(key, value)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun saveStringSet(key: String, value: Set<String>) = sharedPreferences.edit {
        putStringSet(key, value)
    }

    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        return sharedPreferences.getStringSet(key, defaultValue) ?: defaultValue
    }

    fun remove(key: String) = sharedPreferences.edit {
        remove(key)
    }

    fun clear() = sharedPreferences.edit {
        clear()
    }

    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    fun getAll(): Map<String, *> = sharedPreferences.all

    companion object {
        const val DEFAULT_ENCRYPTED_PREFS_FILE = "wgc_encrypted_preferences"
    }
}
