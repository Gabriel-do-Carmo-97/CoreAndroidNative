package br.com.wgc.core.network

import br.com.wgc.core.network.metrics.NetworkMetric
import br.com.wgc.core.network.metrics.NetworkMetricsEventListener
import br.com.wgc.core.network.tracing.TraceIdInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Fábrica padronizada de instâncias [OkHttpClient] configuradas com políticas corporativas:
 * timeouts conservadores, autenticação transparente, certificate pinning e observabilidade de rede.
 */
object NetworkClientFactory {
    private const val DEFAULT_TIMEOUT_SECONDS = 15L

    /**
     * Cria uma instância pré-configurada de [OkHttpClient].
     *
     * @param tokenProvider Provedor de credenciais para autenticação Bearer e auto-refresh 401.
     * @param connectTimeoutSeconds Tempo limite de conexão em segundos.
     * @param readTimeoutSeconds Tempo limite de leitura em segundos.
     * @param writeTimeoutSeconds Tempo limite de escrita em segundos.
     * @param enableLogging Se `true`, ativa interceptor de log HTTP em nível BODY.
     * @param certificatePins Mapa contendo o padrão de domínio e hashes SHA-256 públicos para certificate pinning.
     * @param enableDistributedTracing Se `true`, injeta headers de correlação e W3C Trace Context (traceparent).
     * @param metricsListener Callback opcional para telemetria de latência (DNS, TLS, Response Time).
     * @return Instância configurada de [OkHttpClient].
     */
    fun createOkHttpClient(
        tokenProvider: TokenProvider? = null,
        connectTimeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
        readTimeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
        writeTimeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS,
        enableLogging: Boolean = false,
        certificatePins: Map<String, List<String>>? = null,
        enableDistributedTracing: Boolean = true,
        metricsListener: ((NetworkMetric) -> Unit)? = null,
    ): OkHttpClient {
        val builder =
            OkHttpClient
                .Builder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(writeTimeoutSeconds, TimeUnit.SECONDS)

        if (enableDistributedTracing) {
            builder.addInterceptor(TraceIdInterceptor())
        }

        if (metricsListener != null) {
            builder.eventListenerFactory(NetworkMetricsEventListener.Factory(metricsListener))
        }

        if (tokenProvider != null) {
            builder.addInterceptor(AuthInterceptor(tokenProvider))
            builder.authenticator(TokenAuthenticator(tokenProvider))
        }

        if (certificatePins != null && certificatePins.isNotEmpty()) {
            SslPinningHelper.configureCertificatePinner(builder, certificatePins)
        }

        if (enableLogging) {
            val logging =
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }
}
