package br.com.wgc.core.sharedPreferences

import android.content.Context
import androidx.core.content.edit

class SharedPreferencesCore(
    private val context: Context,
    private val sharedPreferencesName: String
) {
    private val sharedPreferences = context.getSharedPreferences(
        sharedPreferencesName,
        Context.MODE_PRIVATE
    )

    fun saveString(
        key: String,
        value: String
    ) = sharedPreferences.edit {
        putString(key, value)
    }

    fun getString(
        key: String,
        defaultValue: String? = null
    ) = sharedPreferences.getString(key, defaultValue)

    fun saveInt(
        key: String,
        value: Int
    ) = sharedPreferences.edit {
        putInt(key, value)
    }

    fun getInt(
        key: String,
        defaultValue: Int = 0
    ) = sharedPreferences.getInt(key, defaultValue)

    fun saveBoolean(
        key: String,
        value: Boolean
    ) = sharedPreferences.edit {
        putBoolean(key, value)
    }

    fun getBoolean(
        key: String,
        defaultValue: Boolean = false
    ) = sharedPreferences.getBoolean(key, defaultValue)

    fun saveFloat(
        key: String,
        value: Float
    ) = sharedPreferences.edit {
        putFloat(key, value)
    }

    fun getFloat(
        key: String,
        defaultValue: Float = 0f
    ) = sharedPreferences.getFloat(key, defaultValue)

    fun saveLong(
        key: String,
        value: Long
    ) = sharedPreferences.edit {
        putLong(key, value)
    }

    fun getLong(
        key: String,
        defaultValue: Long = 0L
    ) = sharedPreferences.getLong(key, defaultValue)

    fun saveStringSet(
        key: String,
        value: Set<String>
    ) = sharedPreferences.edit {
        putStringSet(key, value)
    }

    fun getStringSet(
        key: String,
        defaultValue: Set<String> = emptySet()
    ) = sharedPreferences.getStringSet(key, defaultValue)

    fun clear() = sharedPreferences.edit {
        clear()
    }


    fun remove(
        key: String
    ) = sharedPreferences.edit {
        remove(key)
    }

    fun contains(key: String) = sharedPreferences.contains(key)
    fun getAll(): Map<String, *> = sharedPreferences.all
}