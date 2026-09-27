package br.com.wgc.core.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Fachada corporativa para orquestrar sincronizações em segundo plano e retenção de requisições offline.
 *
 * @property context Contexto de aplicação para inicialização de serviços do sistema.
 * @property outboxQueue Fila Outbox responsável pela retenção ordenada dos itens.
 */
class SyncManager(
    private val context: Context,
    val outboxQueue: OutboxQueue = DefaultOutboxQueue(),
) {
    private val workManager by lazy { WorkManager.getInstance(context) }

    suspend fun enqueue(request: OutboxRequest) {
        val headersWithIdempotency =
            request.headers.toMutableMap().apply {
                putIfAbsent("X-Idempotency-Key", request.id)
            }
        val enriched = request.copy(headers = headersWithIdempotency)
        outboxQueue.enqueue(enriched)
        scheduleSyncWork()
    }

    fun scheduleSyncWork(requireUnmetered: Boolean = false) {
        val networkType = if (requireUnmetered) NetworkType.UNMETERED else NetworkType.CONNECTED
        val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(networkType)
                .build()

        val requestBuilder =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    15,
                    java.util.concurrent.TimeUnit.SECONDS,
                )

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            requestBuilder.build(),
        )
    }

    fun observeSyncWorkInfo(): Flow<WorkInfo?> {
        return workManager
            .getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map { list -> list.firstOrNull() }
    }

    companion object {
        const val SYNC_WORK_NAME = "wgc_core_outbox_sync_work"
    }
}
