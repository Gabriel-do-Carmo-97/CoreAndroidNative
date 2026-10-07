package br.com.wgc.core.sync.conflict

/**
 * Strategy applied when reconciling concurrent modifications to the same domain entity.
 */
enum class ConflictResolutionStrategy {
    LAST_WRITE_WINS,
    CLIENT_WINS,
    SERVER_WINS,
    CUSTOM_MERGE,
}

/**
 * Result returned by a conflict reconciliation resolver.
 */
data class ResolutionResult<T>(
    val resolvedValue: T,
    val wasConflict: Boolean,
    val strategyUsed: ConflictResolutionStrategy,
)

/**
 * Interface contract for resolving domain conflicts between local client state and remote server state.
 */
fun interface DomainConflictResolver<T> {
    /**
     * Resolves conflict between [localState] and [remoteState].
     */
    fun resolve(
        localState: T,
        remoteState: T,
        strategy: ConflictResolutionStrategy,
    ): ResolutionResult<T>
}
