package br.com.wgc.core.dataStorePreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStorePreferencesCore @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dataStoreName: String
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)

    private fun safeDataStore(): Flow<Preferences> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
    }

    suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    fun getStringFlow(key: String, defaultValue: String? = null): Flow<String?> {
        val preferencesKey = stringPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveInt(key: String, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int> {
        val preferencesKey = intPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        val preferencesKey = booleanPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveFloat(key: String, value: Float) {
        context.dataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }

    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float> {
        val preferencesKey = floatPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveLong(key: String, value: Long) {
        context.dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long> {
        val preferencesKey = longPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    suspend fun saveStringSet(key: String, value: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(key)] = value
        }
    }

    fun getStringSetFlow(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>> {
        val preferencesKey = stringSetPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
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
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    preferences[stringSetPreferencesKey(key)] = value as Set<String>
                }
                else -> throw IllegalArgumentException("Unsupported data type: ${value::class.java.name}")
            }
        }
    }

    suspend fun getAll(): Map<Preferences.Key<*>, Any> {
        val preferences = safeDataStore().first()
        return preferences.asMap()
    }

    suspend fun containsKey(key: String): Boolean {
        val preferences = safeDataStore().first()
        return preferences.asMap().keys.any { it.name == key }
    }

    suspend fun <T> contains(key: Preferences.Key<T>): Boolean {
        val preferences = safeDataStore().first()
        return preferences.contains(key)
    }

    suspend fun <T> remove(key: Preferences.Key<T>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    suspend fun removeKey(key: String) {
        context.dataStore.edit { preferences ->
            val targetKey = preferences.asMap().keys.firstOrNull { it.name == key }
            if (targetKey != null) {
                preferences.remove(targetKey)
            }
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
