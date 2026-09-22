package br.com.wgc.core.network.quality

import br.com.wgc.core.network.metrics.NetworkMetric
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classificação da qualidade da conexão de rede com base na latência média (RTT) e taxa de sucesso.
 */
enum class NetworkQuality {
    /** Menos de 3 amostras coletadas para determinação estatística confiável. */
    UNKNOWN,

    /** Latência inferior a 150ms e taxa de sucesso elevada. */
    EXCELLENT,

    /** Latência entre 150ms e 400ms. */
    GOOD,

    /** Latência entre 400ms e 1000ms. */
    MODERATE,

    /** Latência superior a 1000ms ou taxa de falhas superior a 50%. */
    POOR,
}

private data class NetworkSample(
    val durationMs: Long,
    val isSuccess: Boolean,
)

/**
 * Monitor dinâmico de qualidade da rede baseado em janela deslizante de requisições recentes.
 *
 * Utiliza o tempo de resposta e sucesso/falha das chamadas HTTP reais para classificar a conexão
 * em tempo real, permitindo adaptações proativas na UI (e.g. degradar qualidade de imagens,
 * desativar auto-play ou alertar conectividade lenta).
 *
 * @param windowSize Tamanho da janela de amostras (default: 10).
 * @param minSamplesThreshold Número mínimo de amostras necessárias antes de calcular a qualidade (default: 3).
 */
@Singleton
class NetworkQualityMonitor(
    private val windowSize: Int = DEFAULT_WINDOW_SIZE,
    private val minSamplesThreshold: Int = DEFAULT_MIN_SAMPLES,
) {
    @Inject
    constructor() : this(DEFAULT_WINDOW_SIZE, DEFAULT_MIN_SAMPLES)

    private val lock = Any()
    private val samples = ArrayDeque<NetworkSample>(windowSize)

    private val _quality = MutableStateFlow(NetworkQuality.UNKNOWN)
    val quality: StateFlow<NetworkQuality> = _quality.asStateFlow()

    private val _averageLatencyMs = MutableStateFlow(0L)
    val averageLatencyMs: StateFlow<Long> = _averageLatencyMs.asStateFlow()

    /**
     * Registra uma nova medição de requisição de rede.
     *
     * @param durationMs Tempo total decorrido da requisição em milissegundos.
     * @param isSuccess `true` se a requisição completou com sucesso HTTP, `false` se lançou exceção/timeout.
     */
    fun recordSample(
        durationMs: Long,
        isSuccess: Boolean,
    ) {
        synchronized(lock) {
            if (samples.size >= windowSize) {
                samples.pollFirst()
            }
            samples.addLast(NetworkSample(durationMs.coerceAtLeast(0L), isSuccess))
            recalculateQualityLocked()
        }
    }

    /**
     * Adaptador para registrar métricas geradas por [br.com.wgc.core.network.metrics.NetworkMetricsEventListener].
     */
    fun onMetricCaptured(metric: NetworkMetric) {
        recordSample(metric.totalDurationMs, metric.isSuccess)
    }

    /**
     * Reinicia as amostras e reseta a qualidade para [NetworkQuality.UNKNOWN].
     */
    fun reset() {
        synchronized(lock) {
            samples.clear()
            _quality.value = NetworkQuality.UNKNOWN
            _averageLatencyMs.value = 0L
        }
    }

    private fun recalculateQualityLocked() {
        if (samples.size < minSamplesThreshold) {
            _quality.value = NetworkQuality.UNKNOWN
            val avg = if (samples.isEmpty()) 0L else samples.map { it.durationMs }.average().toLong()
            _averageLatencyMs.value = avg
            return
        }

        val totalSamples = samples.size
        val failureCount = samples.count { !it.isSuccess }
        val failureRate = failureCount.toDouble() / totalSamples

        val avgLatency = samples.map { it.durationMs }.average().toLong()
        _averageLatencyMs.value = avgLatency

        _quality.value =
            when {
                failureRate > FAILURE_RATE_THRESHOLD -> NetworkQuality.POOR
                avgLatency < LATENCY_EXCELLENT_MAX -> NetworkQuality.EXCELLENT
                avgLatency < LATENCY_GOOD_MAX -> NetworkQuality.GOOD
                avgLatency < LATENCY_MODERATE_MAX -> NetworkQuality.MODERATE
                else -> NetworkQuality.POOR
            }
    }

    companion object {
        const val DEFAULT_WINDOW_SIZE: Int = 10
        const val DEFAULT_MIN_SAMPLES: Int = 3
        private const val FAILURE_RATE_THRESHOLD = 0.50
        private const val LATENCY_EXCELLENT_MAX = 150L
        private const val LATENCY_GOOD_MAX = 400L
        private const val LATENCY_MODERATE_MAX = 1000L
    }
}
