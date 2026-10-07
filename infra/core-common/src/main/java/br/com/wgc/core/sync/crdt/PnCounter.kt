package br.com.wgc.core.sync.crdt

/**
 * Enterprise Positive-Negative Counter (PN-Counter) CRDT.
 * Supports concurrent increments and decrements across distributed nodes.
 */
data class PnCounter(
    val nodeIncrements: Map<String, Long> = emptyMap(),
    val nodeDecrements: Map<String, Long> = emptyMap(),
) {
    /**
     * Resolves the current distributed value.
     */
    val value: Long
        get() = nodeIncrements.values.sum() - nodeDecrements.values.sum()

    /**
     * Increments counter value for specified node.
     */
    fun increment(
        nodeId: String,
        amount: Long = 1L,
    ): PnCounter {
        require(amount >= 0) { "Increment amount must be non-negative" }
        val current = nodeIncrements[nodeId] ?: 0L
        return copy(nodeIncrements = nodeIncrements + (nodeId to current + amount))
    }

    /**
     * Decrements counter value for specified node.
     */
    fun decrement(
        nodeId: String,
        amount: Long = 1L,
    ): PnCounter {
        require(amount >= 0) { "Decrement amount must be non-negative" }
        val current = nodeDecrements[nodeId] ?: 0L
        return copy(nodeDecrements = nodeDecrements + (nodeId to current + amount))
    }

    /**
     * Merges this counter with another replica by taking the maximum count per node.
     */
    fun merge(other: PnCounter): PnCounter {
        val allNodes =
            this.nodeIncrements.keys +
                other.nodeIncrements.keys +
                this.nodeDecrements.keys +
                other.nodeDecrements.keys
        val mergedInc = mutableMapOf<String, Long>()
        val mergedDec = mutableMapOf<String, Long>()

        for (node in allNodes) {
            val maxInc = maxOf(this.nodeIncrements[node] ?: 0L, other.nodeIncrements[node] ?: 0L)
            if (maxInc > 0L) mergedInc[node] = maxInc

            val maxDec = maxOf(this.nodeDecrements[node] ?: 0L, other.nodeDecrements[node] ?: 0L)
            if (maxDec > 0L) mergedDec[node] = maxDec
        }

        return PnCounter(mergedInc, mergedDec)
    }
}
