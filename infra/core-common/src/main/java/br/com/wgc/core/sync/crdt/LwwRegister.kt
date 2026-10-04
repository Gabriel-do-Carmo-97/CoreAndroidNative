package br.com.wgc.core.sync.crdt

/**
 * Enterprise Last-Write-Wins Register (LWW-Register) CRDT.
 * Guarantees eventual consistency across distributed replicas.
 *
 * @param value The encapsulated value.
 * @param timestamp Logical or physical timestamp (epoch millis).
 * @param peerId Node identifier breaking ties if timestamps collide.
 */
data class LwwRegister<T>(
    val value: T,
    val timestamp: Long,
    val peerId: String,
) {
    /**
     * Merges this register with an incoming replica using Last-Write-Wins rule.
     */
    fun merge(other: LwwRegister<T>): LwwRegister<T> {
        return when {
            other.timestamp > this.timestamp -> other
            other.timestamp < this.timestamp -> this
            else -> if (other.peerId >= this.peerId) other else this
        }
    }
}
