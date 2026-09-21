package br.com.wgc.core.storage.cache

/**
 * Envelope para itens em cache com timestamp e política de expiração TTL.
 *
 * @property value Valor armazenado no cache.
 * @property createdAt Timestamp epoch em milissegundos da inserção.
 * @property ttlMs Tempo de vida útil em milissegundos (ou null se for perpétuo).
 */
data class CacheEntry<T>(
    val value: T,
    val createdAt: Long = System.currentTimeMillis(),
    val ttlMs: Long? = null,
) {
    fun isExpired(now: Long = System.currentTimeMillis()): Boolean {
        return ttlMs != null && (now - createdAt) > ttlMs
    }
}
