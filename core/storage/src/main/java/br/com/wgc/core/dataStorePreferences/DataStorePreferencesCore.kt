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
import br.com.wgc.core.exceptions.StorageException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação Singleton de [KeyValueDataStore] baseada em Jetpack Preferences DataStore.
 *
 * Fornece operações reativas seguras através de coroutines e [Flow], com tratamento
 * automático de falhas de I/O na inicialização para prevenir encerramentos inesperados.
 *
 * Exemplo de injeção e uso:
 * ```kotlin
 * @Inject
 * lateinit var dataStore: KeyValueDataStore
 *
 * // Salvar:
 * lifecycleScope.launch {
 *     dataStore.saveString("user_name", "Gabriel")
 * }
 *
 * // Observar:
 * dataStore.getStringFlow("user_name").collect { name ->
 *     println("Nome: $name")
 * }
 * ```
 *
 * @param context Contexto da aplicação Android.
 * @param dataStoreName Nome do arquivo em disco do DataStore.
 */
@Singleton
class DataStorePreferencesCore @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dataStoreName: String = DEFAULT_DATASTORE_NAME
) : KeyValueDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = dataStoreName)

    /**
     * Intercepta o fluxo de dados do DataStore para capturar [IOException]s
     * de leitura em disco, emitindo [emptyPreferences] como fallback seguro.
     */
    private fun safeDataStore(): Flow<Preferences> {
        return context.dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
    }

    override suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    override fun getStringFlow(key: String, defaultValue: String?): Flow<String?> {
        val preferencesKey = stringPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    override suspend fun saveInt(key: String, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }

    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
        val preferencesKey = intPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    override suspend fun saveBoolean(key: String, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        val preferencesKey = booleanPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    override suspend fun saveFloat(key: String, value: Float) {
        context.dataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }

    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> {
        val preferencesKey = floatPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    override suspend fun saveLong(key: String, value: Long) {
        context.dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }

    override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> {
        val preferencesKey = longPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    override suspend fun saveStringSet(key: String, value: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[stringSetPreferencesKey(key)] = value
        }
    }

    override fun getStringSetFlow(key: String, defaultValue: Set<String>): Flow<Set<String>> {
        val preferencesKey = stringSetPreferencesKey(key)
        return safeDataStore().map { preferences ->
            preferences[preferencesKey] ?: defaultValue
        }
    }

    override suspend fun saveAny(key: String, value: Any) {
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
                else -> throw StorageException.UnsupportedTypeException(value::class.java.name)
            }
        }
    }

    override suspend fun getAll(): Map<Preferences.Key<*>, Any> {
        val preferences = safeDataStore().first()
        return preferences.asMap()
    }

    override suspend fun containsKey(key: String): Boolean {
        val preferences = safeDataStore().first()
        return preferences.asMap().keys.any { it.name == key }
    }

    override suspend fun <T> contains(key: Preferences.Key<T>): Boolean {
        val preferences = safeDataStore().first()
        return preferences.contains(key)
    }

    override suspend fun <T> remove(key: Preferences.Key<T>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    override suspend fun removeKey(key: String) {
        context.dataStore.edit { preferences ->
            val targetKey = preferences.asMap().keys.firstOrNull { it.name == key }
            if (targetKey != null) {
                preferences.remove(targetKey)
            }
        }
    }

    override suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        const val DEFAULT_DATASTORE_NAME = "wgc_core_datastore"
    }
}
