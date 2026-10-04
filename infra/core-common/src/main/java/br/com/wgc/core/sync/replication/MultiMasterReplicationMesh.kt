package br.com.wgc.core.sync.replication

/**
 * Representation of a replication peer in a decentralized multi-master mesh.
 */
data class PeerReplica(
    val peerId: String,
    val endpointUrl: String,
    val lastSyncedTimestamp: Long = 0L,
)

/**
 * Anti-entropy sync gossip protocol status.
 */
enum class AntiEntropyStatus {
    IDLE,
    EXCHANGING_DIGESTS,
    STREAMING_DELTAS,
    COMPLETED,
    FAILED,
}

/**
 * Enterprise contract for Multi-Master Anti-Entropy Gossip synchronization.
 */
interface MultiMasterReplicationMesh {
    /**
     * Registers a known peer replica.
     */
    fun registerPeer(peer: PeerReplica)

    /**
     * Initiates anti-entropy digest exchange with registered peers.
     */
    suspend fun synchronizePeers(): AntiEntropyStatus
}
