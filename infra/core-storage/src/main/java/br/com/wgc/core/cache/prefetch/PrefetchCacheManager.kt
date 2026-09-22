package br.com.wgc.core.cache.prefetch

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Representação interna de um valor em cache com controle temporal de validade.
 */
data class CachedItem<T>(
    val value: T,
    val cachedAtMs: Long,
    val ttlMs: Long,
) {
    /**
     * Retorna se o item excedeu seu Time To Live (TTL).
     */
    fun isExpired(now: Long = System.currentTimeMillis()): Boolean = (now - cachedAtMs) > ttlMs
}

/**
 * Contrato de política preditiva para antecipação de carregamento de dados (Prefetch).
 */
interface PrefetchPolicy {
    /**
     * Avalia se um item em cache deve ser recarregado proativamente em segundo plano.
     *
     * @param elapsedMs Tempo decorrido desde a gravação do item no cache em milissegundos.
     * @param ttlMs Tempo de vida total configurado para o item.
     * @param isMeteredNetwork `true` se o dispositivo estiver em conexão tarifada (dados móveis).
     * @param batteryPercent Nível atual de bateria entre 0.0f e 1.0f.
     */
    fun shouldPrefetch(
        elapsedMs: Long,
        ttlMs: Long,
        isMeteredNetwork: Boolean,
        batteryPercent: Float,
    ): Boolean
}

/**
 * Implementação padrão com base no percentual de idade do cache e conservação de bateria/dados.
 */
class DefaultPrefetchPolicy(
    private val prefetchRatio: Float = DEFAULT_PREFETCH_RATIO,
    private val minBatteryLevel: Float = DEFAULT_MIN_BATTERY,
) : PrefetchPolicy {
    override fun shouldPrefetch(
        elapsedMs: Long,
        ttlMs: Long,
        isMeteredNetwork: Boolean,
        batteryPercent: Float,
    ): Boolean {
        // Não antecipa se bateria estiver criticamente baixa ou em rede tarifada
        if (batteryPercent < minBatteryLevel || isMeteredNetwork) {
            return false
        }
        val threshold = (ttlMs * prefetchRatio).toLong()
        return elapsedMs >= threshold
    }

    companion object {
        const val DEFAULT_PREFETCH_RATIO = 0.70f
        const val DEFAULT_MIN_BATTERY = 0.20f
    }
}

/**
 * Gerenciador corporativo de cache em memória com suporte a estratégias preditivas de prefetching.
 */
@Singleton
class PrefetchCacheManager<K : Any, V : Any>(
    private val policy: PrefetchPolicy = DefaultPrefetchPolicy(),
) {
    @Inject
    constructor() : this(DefaultPrefetchPolicy())

    private val cache = ConcurrentHashMap<K, CachedItem<V>>()

    /**
     * Salva um valor no cache com o TTL especificado em milissegundos.
     */
    fun put(
        key: K,
        value: V,
        ttlMs: Long = DEFAULT_TTL_MS,
    ) {
        cache[key] = CachedItem(value = value, cachedAtMs = System.currentTimeMillis(), ttlMs = ttlMs)
    }

    /**
     * Recupera o valor do cache se ainda for válido.
     */
    fun get(key: K): V? {
        val item = cache[key] ?: return null
        return if (item.isExpired()) {
            cache.remove(key)
            null
        } else {
            item.value
        }
    }

    /**
     * Avalia se a chave informada atende aos critérios da política preditiva para prefetching em background.
     */
    fun shouldPrefetch(
        key: K,
        isMeteredNetwork: Boolean = false,
        batteryPercent: Float = 1.0f,
    ): Boolean {
        val item = cache[key] ?: return true
        val elapsed = (System.currentTimeMillis() - item.cachedAtMs).coerceAtLeast(0L)
        return policy.shouldPrefetch(
            elapsedMs = elapsed,
            ttlMs = item.ttlMs,
            isMeteredNetwork = isMeteredNetwork,
            batteryPercent = batteryPercent,
        )
    }

    /** Invalida uma chave individual. */
    fun invalidate(key: K) {
        cache.remove(key)
    }

    /** Limpa integralmente o cache em memória. */
    fun clear() {
        cache.clear()
    }

    /** Retorna a quantidade de itens presentes no cache. */
    fun size(): Int = cache.size

    companion object {
        const val DEFAULT_TTL_MS = 300_000L // 5 minutos
    }
}
