package br.com.wgc.core.network.sync

/**
 * Entidade genérica sincronizável com suporte a causalidade por Vector Clock.
 */
data class SyncEntity<T>(
    val id: String,
    val data: T,
    val clock: VectorClock,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Decisão de resolução de sincronização.
 */
sealed interface SyncAction<T> {
    data class ApplyRemote<T>(
        val entity: SyncEntity<T>,
    ) : SyncAction<T>

    data class PushLocal<T>(
        val entity: SyncEntity<T>,
    ) : SyncAction<T>

    data class InSync<T>(
        val entity: SyncEntity<T>,
    ) : SyncAction<T>

    data class ConflictResolved<T>(
        val resolved: SyncEntity<T>,
    ) : SyncAction<T>
}

/**
 * Estratégia de resolução de conflitos concorrentes.
 */
fun interface ConflictResolver<T> {
    fun resolve(
        local: SyncEntity<T>,
        remote: SyncEntity<T>,
    ): SyncEntity<T>
}

/**
 * Resolução padrão baseada em Last-Write-Wins (LWW) caso ocorra conflito concorrente.
 */
class LastWriteWinsResolver<T> : ConflictResolver<T> {
    override fun resolve(
        local: SyncEntity<T>,
        remote: SyncEntity<T>,
    ): SyncEntity<T> {
        val mergedClock = local.clock.merge(remote.clock)
        return if (remote.timestamp >= local.timestamp) {
            remote.copy(clock = mergedClock)
        } else {
            local.copy(clock = mergedClock)
        }
    }
}

/**
 * Motor corporativo de sincronização bidirecional offline-first.
 * Avalia entidades locais contra versões remotas do servidor e determina a ação causal exata.
 */
class BidirectionalSyncEngine<T>(
    private val conflictResolver: ConflictResolver<T> = LastWriteWinsResolver(),
) {
    fun reconcile(
        local: SyncEntity<T>?,
        remote: SyncEntity<T>?,
    ): SyncAction<T> {
        if (local == null && remote != null) {
            return SyncAction.ApplyRemote(remote)
        }
        if (local != null && remote == null) {
            return SyncAction.PushLocal(local)
        }
        if (local == null && remote == null) {
            throw IllegalArgumentException("Both local and remote cannot be null")
        }

        val l = local!!
        val r = remote!!

        return when (l.clock.compareToClock(r.clock)) {
            CausalRelation.EQUAL -> SyncAction.InSync(l)
            CausalRelation.BEFORE -> SyncAction.ApplyRemote(r)
            CausalRelation.AFTER -> SyncAction.PushLocal(l)
            CausalRelation.CONCURRENT -> {
                val resolved = conflictResolver.resolve(l, r)
                SyncAction.ConflictResolved(resolved)
            }
        }
    }
}
