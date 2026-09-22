package br.com.wgc.core.network.security

import okhttp3.CertificatePinner
import java.util.concurrent.ConcurrentHashMap

/**
 * Gerenciador corporativo para Certificate Pinning dinâmico no OkHttp.
 *
 * Permite a rotação, adição e revogação de hashes SPKI SHA-256 em tempo de execução
 * (por exemplo, via Remote Config ou API segura) sem necessidade de novo deploy do aplicativo.
 */
class DynamicCertificatePinner(
    initialPins: Map<String, Set<String>> = emptyMap(),
) {
    private val pinStore = ConcurrentHashMap<String, MutableSet<String>>()

    init {
        initialPins.forEach { (pattern, pins) ->
            pinStore.computeIfAbsent(pattern) { ConcurrentHashMap.newKeySet() }.addAll(pins)
        }
    }

    /**
     * Adiciona ou atualiza hashes SHA-256 para o padrão de domínio especificado.
     *
     * @param pattern Padrão de hostname (ex: `"*.empresa.com.br"` ou `"api.empresa.com.br"`).
     * @param pins Hashes SPKI no formato `"sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="`.
     */
    fun addPins(
        pattern: String,
        vararg pins: String,
    ) {
        val set = pinStore.computeIfAbsent(pattern) { ConcurrentHashMap.newKeySet() }
        set.addAll(pins)
    }

    /**
     * Remove todos os pins associados ao padrão de domínio fornecido.
     *
     * @param pattern Padrão de hostname a ser removido.
     */
    fun removePins(pattern: String) {
        pinStore.remove(pattern)
    }

    /**
     * Retorna a lista de hashes registrados para um dado padrão de hostname.
     */
    fun getPins(pattern: String): Set<String> {
        return pinStore[pattern]?.toSet() ?: emptySet()
    }

    /**
     * Retorna todos os domínios registrados com seus respectivos conjuntos de pins.
     */
    fun getAllPins(): Map<String, Set<String>> {
        return pinStore.mapValues { it.value.toSet() }
    }

    /**
     * Limpa todos os pins registrados em memória.
     */
    fun clear() {
        pinStore.clear()
    }

    /**
     * Constrói e retorna uma instância imutável de [CertificatePinner] do OkHttp
     * contendo todos os hashes atualmente registrados.
     */
    fun buildCertificatePinner(): CertificatePinner {
        val builder = CertificatePinner.Builder()
        pinStore.forEach { (pattern, pins) ->
            pins.forEach { pin ->
                builder.add(pattern, pin)
            }
        }
        return builder.build()
    }
}
