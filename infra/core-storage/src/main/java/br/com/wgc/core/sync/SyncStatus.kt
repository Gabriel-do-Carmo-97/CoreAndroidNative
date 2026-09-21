package br.com.wgc.core.sync

/**
 * Representa os estados do ciclo de sincronização em segundo plano.
 */
sealed class SyncStatus {
    data object Idle : SyncStatus()

    data class Syncing(
        val pendingCount: Int,
    ) : SyncStatus()

    data class Success(
        val processedCount: Int,
    ) : SyncStatus()

    data class Failed(
        val error: Throwable,
    ) : SyncStatus()
}
