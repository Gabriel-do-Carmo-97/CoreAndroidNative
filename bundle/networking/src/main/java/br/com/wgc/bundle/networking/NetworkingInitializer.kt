package br.com.wgc.bundle.networking

import android.content.Context
import br.com.wgc.core.network.NetworkMonitor
import br.com.wgc.core.network.sse.ServerSentEventClient
import br.com.wgc.core.network.websocket.CoreWebSocketClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * Inicializador e provedor de dependências Hilt para o Bundle de Networking.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkingInitializer {
    @Provides
    @Singleton
    fun provideNetworkingFacade(
        @ApplicationContext context: Context,
    ): NetworkingFacade = NetworkingFacade(context)
}

/**
 * Ponto de entrada unificado para requisições de rede, WebSocket, SSE e monitoramento de conectividade.
 *
 * @property context O contexto da aplicação consumidora.
 */
class NetworkingFacade(
    private val context: Context,
) {
    /** Monitor reativo do estado da conectividade com a internet. */
    val networkMonitor: NetworkMonitor by lazy { NetworkMonitor(context) }

    /**
     * Retorna o status de inicialização do bundle de networking.
     */
    fun getStatus(): String = "Networking Bundle initialized successfully for package ${context.packageName}"

    /**
     * Cria uma instância de [CoreWebSocketClient] reativo sobre corrotinas.
     */
    fun createWebSocketClient(okHttpClient: OkHttpClient = OkHttpClient()): CoreWebSocketClient =
        CoreWebSocketClient(okHttpClient)

    /**
     * Cria uma instância de [ServerSentEventClient] para streaming de eventos SSE.
     */
    fun createServerSentEventClient(okHttpClient: OkHttpClient = OkHttpClient()): ServerSentEventClient =
        ServerSentEventClient(okHttpClient)
}
