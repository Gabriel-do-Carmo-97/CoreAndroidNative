package br.com.wgc.bundle.persistence

import android.content.Context
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
        @ApplicationContext context: Context
    ): PersistenceFacade {
        return PersistenceFacade(context)
    }
}

/**
 * Ponto de entrada unificado para consulta de integridade e diagnósticos do bundle de persistência.
 *
 * @property context O contexto da aplicação consumidora.
 */
class PersistenceFacade(
    private val context: Context
) {
    /**
     * Retorna o status de inicialização do bundle de persistência contendo o nome do pacote consumidor.
     *
     * @return Mensagem descritiva de confirmação.
     */
    fun getStatus(): String = "Persistence Bundle initialized successfully for package ${context.packageName}"
}
