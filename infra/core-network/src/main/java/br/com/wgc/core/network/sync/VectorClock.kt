package br.com.wgc.core.network.sync

/**
 * Relação causal entre dois Vector Clocks.
 */
enum class CausalRelation {
    BEFORE,
    AFTER,
    EQUAL,
    CONCURRENT, // Conflito detectado: ambas as versões sofreram mutações independentes
}

/**
 * Implementação de Vector Clock para ordenação causal determinística de eventos em sistemas distribuídos offline-first.
 * Elimina anomalias causadas pelo relógio de parede (desvios de horário NTP do dispositivo móvel).
 */
data class VectorClock(
    val clocks: Map<String, Long> = emptyMap(),
) {
    /**
     * Retorna uma nova cópia do relógio com o contador do nó incrementado em 1.
     */
    fun increment(nodeId: String): VectorClock {
        val current = clocks[nodeId] ?: 0L
        return copy(clocks = clocks + (nodeId to current + 1L))
    }

    /**
     * Mescla este relógio com outro, adotando o valor máximo para cada nó.
     */
    fun merge(other: VectorClock): VectorClock {
        val allKeys = clocks.keys + other.clocks.keys
        val merged =
            allKeys.associateWith { key ->
                maxOf(clocks[key] ?: 0L, other.clocks[key] ?: 0L)
            }
        return VectorClock(merged)
    }

    /**
     * Compara a causalidade entre este relógio e outro.
     */
    fun compareToClock(other: VectorClock): CausalRelation {
        if (this.clocks == other.clocks) return CausalRelation.EQUAL

        val allKeys = clocks.keys + other.clocks.keys
        var thisHasBigger = false
        var otherHasBigger = false

        for (k in allKeys) {
            val v1 = clocks[k] ?: 0L
            val v2 = other.clocks[k] ?: 0L
            if (v1 > v2) thisHasBigger = true
            if (v2 > v1) otherHasBigger = true
        }

        return when {
            thisHasBigger && !otherHasBigger -> CausalRelation.AFTER
            !thisHasBigger && otherHasBigger -> CausalRelation.BEFORE
            thisHasBigger && otherHasBigger -> CausalRelation.CONCURRENT
            else -> CausalRelation.EQUAL
        }
    }
}
