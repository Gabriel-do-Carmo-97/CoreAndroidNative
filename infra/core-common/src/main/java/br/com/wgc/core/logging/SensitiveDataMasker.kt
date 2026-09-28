package br.com.wgc.core.logging

import java.security.MessageDigest

/**
 * Sanitizador de alta performance para detecção e mascaramento de dados sensíveis (PII / PCI-DSS).
 * Oferece também geração determinística de hashes unidirecionais para fins de rastreamento e correlação sem expor texto puro.
 */
object SensitiveDataMasker {
    private val CPF_REGEX = Regex("""\b\d{3}\.?\d{3}\.?\d{3}-?\d{2}\b""")
    private val CARD_REGEX = Regex("""\b(?:\d{4}[ -]?){3}\d{4}\b""")
    private val BEARER_REGEX = Regex("""(?i)(bearer\s+)[A-Za-z0-9\-._~+/]+=*""")
    private val PASSWORD_JSON_REGEX = Regex("""(?i)("password"|"secret"|"pin"|"token")\s*:\s*"([^"]+)"""")
    private val PASSWORD_QUERY_REGEX = Regex("""(?i)(password|secret|pin|access_token)=([^&\s]+)""")
    private val EMAIL_REGEX = Regex("""\b([A-Za-z0-9._%+-])[A-Za-z0-9._%+-]*@([A-Za-z0-9.-]+\.[A-Za-z]{2,})\b""")

    private const val CPF_MASK = "***.***.***-**"
    private const val CARD_MASK = "****-****-****-****"
    private const val TRACE_HASH_LENGTH = 16

    /**
     * Aplica regras exaustivas de mascaramento em uma string de log ou payload JSON/URL.
     */
    fun mask(message: String): String {
        if (message.isEmpty()) return message

        return message
            .replace(CPF_REGEX, CPF_MASK)
            .replace(CARD_REGEX, CARD_MASK)
            .replace(BEARER_REGEX, "$1[MASKED_TOKEN]")
            .replace(PASSWORD_JSON_REGEX, """$1: "[REDACTED]"""")
            .replace(PASSWORD_QUERY_REGEX, "$1=[REDACTED]")
            .replace(EMAIL_REGEX, "$1***@$2")
    }

    /**
     * Gera um pseudônimo seguro (hash SHA-256 truncado em 16 caracteres hex) para identificadores sensíveis,
     * permitindo correlacionar logs de um mesmo usuário/dispositivo sem expor seu valor real.
     */
    fun hashForTracing(
        sensitiveId: String,
        salt: String = "WGC_CORE_TRACE_SALT",
    ): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray(Charsets.UTF_8))
        val digest = md.digest(sensitiveId.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }.take(TRACE_HASH_LENGTH)
    }
}
