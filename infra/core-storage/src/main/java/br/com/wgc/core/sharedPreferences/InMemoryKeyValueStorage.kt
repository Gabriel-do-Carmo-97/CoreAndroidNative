package br.com.wgc.core.sharedPreferences

import java.util.concurrent.ConcurrentHashMap

/**
 * Implementação puramente em memória e thread-safe de [KeyValueStorage].
 * Ideal para testes unitários, execução em JVM sem mocks de Android e
 * como base agnóstica para plataformas não-Android (KMP).
 */
class InMemoryKeyValueStorage : KeyValueStorage {
    private val storage = ConcurrentHashMap<String, Any>()

    override fun saveString(
        key: String,
        value: String,
    ) {
        storage[key] = value
    }

    override fun getString(
        key: String,
        defaultValue: String?,
    ): String? {
        return (storage[key] as? String) ?: defaultValue
    }

    override fun saveInt(
        key: String,
        value: Int,
    ) {
        storage[key] = value
    }

    override fun getInt(
        key: String,
        defaultValue: Int,
    ): Int {
        return (storage[key] as? Int) ?: defaultValue
    }

    override fun saveBoolean(
        key: String,
        value: Boolean,
    ) {
        storage[key] = value
    }

    override fun getBoolean(
        key: String,
        defaultValue: Boolean,
    ): Boolean {
        return (storage[key] as? Boolean) ?: defaultValue
    }

    override fun saveLong(
        key: String,
        value: Long,
    ) {
        storage[key] = value
    }

    override fun getLong(
        key: String,
        defaultValue: Long,
    ): Long {
        return (storage[key] as? Long) ?: defaultValue
    }

    override fun saveFloat(
        key: String,
        value: Float,
    ) {
        storage[key] = value
    }

    override fun getFloat(
        key: String,
        defaultValue: Float,
    ): Float {
        return (storage[key] as? Float) ?: defaultValue
    }

    override fun saveStringSet(
        key: String,
        value: Set<String>,
    ) {
        storage[key] = value.toSet()
    }

    override fun getStringSet(
        key: String,
        defaultValue: Set<String>,
    ): Set<String> {
        @Suppress("UNCHECKED_CAST")
        return (storage[key] as? Set<String>) ?: defaultValue
    }

    override fun remove(key: String) {
        storage.remove(key)
    }

    override fun clear() {
        storage.clear()
    }

    override fun contains(key: String): Boolean {
        return storage.containsKey(key)
    }

    override fun getAll(): Map<String, *> {
        return HashMap(storage)
    }
}
