package br.com.wgc.bundle.persistence

import android.content.Context
import br.com.wgc.core.storage.cache.TwoLevelCache
import br.com.wgc.core.sync.DefaultOutboxQueue
import br.com.wgc.core.sync.OutboxQueue
import br.com.wgc.core.sync.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Inicializador e provedor de dependências Hilt para o Bundle de Persistência.
 *
 * Fornece acesso centralizado e configuração inicial aos recursos combinados de
 * [br.com.wgc.core.storage] e [br.com.wgc.core.database].
 */
@Module
@InstallIn(SingletonComponent::class)
object PersistenceInitializer {
    /**
     * Provê a instância singleton de [PersistenceFacade].
     *
     * @param context O contexto da aplicação injetado pelo Hilt.
     * @return Instância singleton configurada da fachada de persistência.
     */
    @Provides
    @Singleton
    fun providePersistenceFacade(
        @ApplicationContext context: Context,
    ): PersistenceFacade = PersistenceFacade(context)
}

/**
 * Ponto de entrada unificado para consulta de integridade, sincronização offline e cache do bundle de persistência.
 *
 * @property context O contexto da aplicação consumidora.
 * @property outboxQueue Fila Outbox thread-safe compartilhada.
 */
class PersistenceFacade(
    private val context: Context,
    val outboxQueue: OutboxQueue = DefaultOutboxQueue(),
) {
    /** Orquestrador de sincronização em segundo plano via WorkManager. */
    val syncManager: SyncManager by lazy { SyncManager(context, outboxQueue) }

    /**
     * Retorna o status de inicialização do bundle de persistência contendo o nome do pacote consumidor.
     *
     * @return Mensagem descritiva de confirmação.
     */
    fun getStatus(): String = "Persistence Bundle initialized successfully for package ${context.packageName}"

    /**
     * Cria uma instância gerenciada de cache em dois níveis (RAM + Disco) com suporte a TTL.
     */
    fun <K : Any, V : Any> createTwoLevelCache(maxMemoryEntries: Int = DEFAULT_CACHE_ENTRIES): TwoLevelCache<K, V> =
        TwoLevelCache(maxMemoryEntries)

    companion object {
        private const val DEFAULT_CACHE_ENTRIES = 100
    }
}
