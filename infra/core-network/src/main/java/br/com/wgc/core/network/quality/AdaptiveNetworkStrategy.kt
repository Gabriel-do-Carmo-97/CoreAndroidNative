package br.com.wgc.core.network.quality

/**
 * Estratégia adaptativa de tráfego de rede corporativo baseada na qualidade observada em tempo real.
 * Recomenda políticas de paginação, pre-fetching e compressão para otimizar UX e economia de dados.
 */
data class NetworkPolicy(
    val shouldPrefetch: Boolean,
    val recommendedBatchSize: Int,
    val acceptedEncoding: String,
    val timeoutSeconds: Long,
    val allowHighResolutionMedia: Boolean,
)

class AdaptiveNetworkStrategy(
    private val qualityMonitor: NetworkQualityMonitor,
) {
    /**
     * Retorna a política de rede recomendada para o momento atual.
     */
    fun currentPolicy(): NetworkPolicy {
        return when (qualityMonitor.quality.value) {
            NetworkQuality.EXCELLENT ->
                NetworkPolicy(
                    shouldPrefetch = true,
                    recommendedBatchSize = 50,
                    acceptedEncoding = "br, gzip, deflate",
                    timeoutSeconds = 10L,
                    allowHighResolutionMedia = true,
                )
            NetworkQuality.GOOD ->
                NetworkPolicy(
                    shouldPrefetch = true,
                    recommendedBatchSize = 25,
                    acceptedEncoding = "br, gzip",
                    timeoutSeconds = 15L,
                    allowHighResolutionMedia = true,
                )
            NetworkQuality.MODERATE ->
                NetworkPolicy(
                    shouldPrefetch = false,
                    recommendedBatchSize = 10,
                    acceptedEncoding = "gzip",
                    timeoutSeconds = 30L,
                    allowHighResolutionMedia = false,
                )
            NetworkQuality.POOR, NetworkQuality.UNKNOWN ->
                NetworkPolicy(
                    shouldPrefetch = false,
                    recommendedBatchSize = 5,
                    acceptedEncoding = "gzip",
                    timeoutSeconds = 45L,
                    allowHighResolutionMedia = false,
                )
        }
    }
}
