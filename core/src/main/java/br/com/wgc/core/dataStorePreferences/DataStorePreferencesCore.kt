package br.com.wgc.core.dataStorePreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStorePreferencesCore @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreName: String
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)


    suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            val preferencesKey = stringPreferencesKey(key)
            preferences[preferencesKey] = value
        }
    }

    fun getStringFlow(key: String, defaultValue: String? = null): Flow<String?> {
        val preferencesKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveInt(key: String, value: Int) {
        context.dataStore.edit { preferences ->
            val preferencesKey = intPreferencesKey(key)
            preferences[preferencesKey] = value
        }
    }

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        val preferencesKey = intPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        context.dataStore.edit { preferences ->
            val preferencesKey = booleanPreferencesKey(key)
            preferences[preferencesKey] = value
        }
    }

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        val preferencesKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveFloat(key: String, value: Float) {
        context.dataStore.edit { preferences ->
            val preferencesKey = floatPreferencesKey(key)
            preferences[preferencesKey] = value
        }
    }

    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> {
        val preferencesKey = floatPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveLong(key: String, value: Long) {
        context.dataStore.edit { preferences ->
            val preferencesKey = longPreferencesKey(key)
            preferences[preferencesKey] = value
        }
    }

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        val preferencesKey = longPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveStringSet(key: String, value: Set<String>) {
        context.dataStore.edit { preferences ->
            val preferencesKey = stringPreferencesKey(key)
            preferences[preferencesKey] = value.joinToString(",")
        }
    }

    fun getStringSetFlow(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>> {
        val preferencesKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            val stringValue = preferences[preferencesKey] ?: defaultValue.joinToString(",")
            stringValue.split(",").toSet()
        }
    }

    suspend fun saveAny(key: String, value: Any) {
        context.dataStore.edit { preferences ->
            when (value) {
                is String -> preferences[stringPreferencesKey(key)] = value
                is Int -> preferences[intPreferencesKey(key)] = value
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                is Float -> preferences[floatPreferencesKey(key)] = value
                is Long -> preferences[longPreferencesKey(key)] = value
                else -> throw IllegalArgumentException("Unsupported data type")
            }
        }
    }

    suspend fun getAll(): Map<Preferences.Key<*>, Any> {
        val preferences = context.dataStore.data.first()
        return preferences.asMap()
    }

    suspend fun contains(key: String): Boolean {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences.contains(preferencesKey)
    }


    suspend fun remove(key: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }


    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}