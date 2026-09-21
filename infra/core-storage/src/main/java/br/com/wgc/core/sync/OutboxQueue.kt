package br.com.wgc.core.sync

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Interface de fila persistente para retenção ordenada de requisições offline (Outbox Pattern).
 */
interface OutboxQueue {
    suspend fun enqueue(request: OutboxRequest)

    suspend fun dequeue(): OutboxRequest?

    suspend fun peek(): OutboxRequest?

    suspend fun remove(id: String)

    suspend fun getAllPending(): List<OutboxRequest>

    suspend fun clear()

    fun observePendingCount(): Flow<Int>
}

/**
 * Implementação padrão e thread-safe de [OutboxQueue] em memória para orquestração reativa.
 */
class DefaultOutboxQueue : OutboxQueue {
    private val mutex = Mutex()
    private val queue = ArrayDeque<OutboxRequest>()
    private val pendingCountState = MutableStateFlow(0)

    override suspend fun enqueue(request: OutboxRequest) =
        mutex.withLock {
            queue.addLast(request)
            pendingCountState.value = queue.size
        }

    override suspend fun dequeue(): OutboxRequest? =
        mutex.withLock {
            val item = queue.removeFirstOrNull()
            pendingCountState.value = queue.size
            item
        }

    override suspend fun peek(): OutboxRequest? =
        mutex.withLock {
            queue.firstOrNull()
        }

    override suspend fun remove(id: String) =
        mutex.withLock {
            queue.removeAll { it.id == id }
            pendingCountState.value = queue.size
        }

    override suspend fun getAllPending(): List<OutboxRequest> =
        mutex.withLock {
            queue.toList()
        }

    override suspend fun clear() =
        mutex.withLock {
            queue.clear()
            pendingCountState.value = 0
        }

    override fun observePendingCount(): Flow<Int> = pendingCountState.asStateFlow()
}
