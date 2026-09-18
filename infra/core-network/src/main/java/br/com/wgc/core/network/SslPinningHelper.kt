package br.com.wgc.core.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

/**
 * Utilitário corporativo para facilitação e aplicação de Certificate Pinning (SSL Pinning)
 * em instâncias de [OkHttpClient.Builder], prevenindo ataques Man-In-The-Middle (MITM).
 */
object SslPinningHelper {
    /**
     * Constrói uma instância de [CertificatePinner] a partir de um mapa de domínios e hashes SHA-256.
     *
     * @param pins Mapa contendo o padrão de domínio (ex: `"*.empresa.com.br"`) e a lista de hashes SHA-256 públicos.
     * @return Instância configurada de [CertificatePinner].
     */
    fun buildCertificatePinner(pins: Map<String, List<String>>): CertificatePinner {
        val pinnerBuilder = CertificatePinner.Builder()
        for ((pattern, hashes) in pins) {
            for (hash in hashes) {
                pinnerBuilder.add(pattern, hash)
            }
        }
        return pinnerBuilder.build()
    }

    /**
     * Aplica um mapa de regras de Certificate Pinning ao builder do [OkHttpClient].
     *
     * @param builder O [OkHttpClient.Builder] a ser configurado.
     * @param pins Mapa contendo o padrão de domínio (ex: `"*.empresa.com.br"`) e a lista de hashes SHA-256 públicos.
     * @return A mesma instância de [OkHttpClient.Builder] com o [CertificatePinner] aplicado.
     */
    fun configureCertificatePinner(
        builder: OkHttpClient.Builder,
        pins: Map<String, List<String>>,
    ): OkHttpClient.Builder = builder.certificatePinner(buildCertificatePinner(pins))
}
