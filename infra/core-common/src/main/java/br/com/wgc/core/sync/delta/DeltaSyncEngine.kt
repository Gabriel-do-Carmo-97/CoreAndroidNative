package br.com.wgc.core.sync.delta

/**
 * Change operation recorded in offline mutation ledger.
 */
enum class DeltaOperation {
    INSERT,
    UPDATE,
    DELETE,
}

/**
 * Mutation entry tracking fine-grained attribute change for synchronization.
 *
 * @property entityId Target business entity identifier.
 * @property entityType Type/Table name of the entity.
 * @property operation Operation type applied.
 * @property serializedPayload JSON or binary encoded state payload.
 * @property logicalTimestamp Monotonically increasing sequence or Lamport timestamp.
 */
data class EntityDelta(
    val entityId: String,
    val entityType: String,
    val operation: DeltaOperation,
    val serializedPayload: String,
    val logicalTimestamp: Long,
)

/**
 * Engine generating and compacting entity delta changesets.
 */
class DeltaSyncEngine {
    private val deltas = mutableListOf<EntityDelta>()

    /**
     * Records a new mutation.
     */
    @Synchronized
    fun recordDelta(delta: EntityDelta) {
        deltas.add(delta)
    }

    /**
     * Compacts pending mutations per entityId, preserving only the latest state or tombstone.
     */
    @Synchronized
    fun compact(): List<EntityDelta> {
        val latestByEntity = mutableMapOf<String, EntityDelta>()
        for (delta in deltas) {
            val existing = latestByEntity[delta.entityId]
            if (existing == null || delta.logicalTimestamp > existing.logicalTimestamp) {
                latestByEntity[delta.entityId] = delta
            }
        }
        return latestByEntity.values.sortedBy { it.logicalTimestamp }
    }

    @Synchronized
    fun clear() {
        deltas.clear()
    }
}
