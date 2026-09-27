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
     * Valida se um determinado padrão possui pelo menos um backup pin registrado (mínimo de 2 pins por host, RFC 7469).
     */
    fun hasBackupPins(pattern: String): Boolean {
        return (pinStore[pattern]?.size ?: 0) >= 2
    }

    /**
     * Realiza a rotação segura de pins validando a assinatura criptográfica HMAC-SHA256 da payload,
     * impedindo ataques de MITM com injeção de hashes falsos via Remote Config.
     *
     * @param pattern Hostname alvo
     * @param newPins Novos hashes a serem registrados
     * @param signatureHex Assinatura HMAC-SHA256 em hexadecimal
     * @param sharedSecret Chave secreta compartilhada para verificação
     * @return true se a assinatura for válida e os pins forem atualizados com sucesso
     */
    fun rotatePinsWithSignature(
        pattern: String,
        newPins: List<String>,
        signatureHex: String,
        sharedSecret: ByteArray,
    ): Boolean {
        val payload = "$pattern:" + newPins.sorted().joinToString(",")
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(sharedSecret, "HmacSHA256"))
        val expected = mac.doFinal(payload.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

        if (!expected.equals(signatureHex, ignoreCase = true)) {
            return false
        }

        val set = pinStore.computeIfAbsent(pattern) { ConcurrentHashMap.newKeySet() }
        set.clear()
        set.addAll(newPins)
        return true
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
