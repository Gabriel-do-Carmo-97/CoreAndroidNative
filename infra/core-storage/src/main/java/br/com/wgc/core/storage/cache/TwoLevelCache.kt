package br.com.wgc.core.storage.cache

import android.util.LruCache
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Cache híbrido em dois níveis (L1: Memória RAM rápida via [LruCache] + L2: Fallback persistente)
 * com suporte a invalidação temporal (TTL).
 *
 * @param K Tipo da chave.
 * @param V Tipo do valor armazenado.
 * @param maxMemoryEntries Capacidade máxima de itens mantidos em memória volátil.
 */
class TwoLevelCache<K : Any, V : Any>(
    maxMemoryEntries: Int = 100,
) {
    private val mutex = Mutex()
    private val memoryCache = object : LruCache<K, CacheEntry<V>>(maxMemoryEntries) {}
    private val diskFallback = mutableMapOf<K, CacheEntry<V>>()

    suspend fun put(
        key: K,
        value: V,
        ttlMs: Long? = null,
    ) = mutex.withLock {
        val entry = CacheEntry(value = value, ttlMs = ttlMs)
        memoryCache.put(key, entry)
        diskFallback[key] = entry
    }

    suspend fun get(key: K): V? =
        mutex.withLock {
            val memEntry = memoryCache.get(key)
            if (memEntry != null) {
                if (memEntry.isExpired()) {
                    memoryCache.remove(key)
                    diskFallback.remove(key)
                    return null
                }
                return memEntry.value
            }

            val diskEntry = diskFallback[key]
            if (diskEntry != null) {
                if (diskEntry.isExpired()) {
                    diskFallback.remove(key)
                    return null
                }
                memoryCache.put(key, diskEntry)
                return diskEntry.value
            }

            return null
        }

    suspend fun evict(key: K) =
        mutex.withLock {
            memoryCache.remove(key)
            diskFallback.remove(key)
        }

    suspend fun clear() =
        mutex.withLock {
            memoryCache.evictAll()
            diskFallback.clear()
        }

    suspend fun size(): Int =
        mutex.withLock {
            diskFallback.size
        }
}
