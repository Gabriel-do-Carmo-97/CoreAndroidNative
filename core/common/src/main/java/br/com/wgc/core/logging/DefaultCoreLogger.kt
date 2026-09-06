package br.com.wgc.core.logging

import android.util.Log

/**
 * Implementação corporativa padrão de [CoreLogger] com suporte a filtragem por nível de severidade,
 * mascaramento automático de PII (CPF, números de cartão de crédito e tokens de autenticação) e
 * tolerância a ambientes de teste unitário na JVM.
 *
 * @property minLogLevel Nível mínimo de severidade exigido para emissão de mensagens.
 * @property enablePiiMasking Se `true`, substitui automaticamente padrões de PII por caracteres de máscara.
 * @property logWriter Função de escrita customizável. Por padrão, direciona para [android.util.Log].
 */
class DefaultCoreLogger(
    private val minLogLevel: LogLevel = LogLevel.DEBUG,
    private val enablePiiMasking: Boolean = true,
    private val logWriter: (priority: Int, tag: String, message: String, throwable: Throwable?) -> Unit =
        ::defaultAndroidLogWriter
) : CoreLogger {

    companion object {
        private val CPF_REGEX = Regex("""\b\d{3}\.?\d{3}\.?\d{3}-?\d{2}\b""")
        private val CARD_REGEX = Regex("""\b(?:\d{4}[ -]?){3}\d{4}\b""")
        private val BEARER_REGEX = Regex("""(?i)(bearer\s+)[A-Za-z0-9\-._~+/]+=*""")

        private const val CPF_MASK = "***.***.***-**"
        private const val CARD_MASK = "****-****-****-****"
        private const val BEARER_REPLACEMENT = "$1[MASKED_TOKEN]"

        /**
         * Emissor padrão que utiliza [android.util.Log], realizando fallback para `println`
         * caso executado em ambientes JVM onde a biblioteca Android nativa não esteja inicializada.
         */
        @Suppress("SwallowedException", "TooGenericExceptionCaught")
        fun defaultAndroidLogWriter(
            priority: Int,
            tag: String,
            message: String,
            throwable: Throwable?
        ) {
            val formattedMsg = if (throwable != null) {
                "$message\n${Log.getStackTraceString(throwable)}"
            } else {
                message
            }
            try {
                Log.println(priority, tag, formattedMsg)
            } catch (e: RuntimeException) {
                // Fallback para execução em testes JVM puros sem Robolectric ativo
                println("[$priority][$tag] $formattedMsg")
            }
        }
    }

    override fun v(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.VERBOSE, tag, message, throwable)
    }

    override fun d(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.DEBUG, tag, message, throwable)
    }

    override fun i(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.INFO, tag, message, throwable)
    }

    override fun w(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.WARN, tag, message, throwable)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        log(LogLevel.ERROR, tag, message, throwable)
    }

    override fun maskPii(message: String): String {
        if (!enablePiiMasking) return message
        return message
            .replace(CPF_REGEX, CPF_MASK)
            .replace(CARD_REGEX, CARD_MASK)
            .replace(BEARER_REGEX, BEARER_REPLACEMENT)
    }

    private fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        if (level.priority < minLogLevel.priority || minLogLevel == LogLevel.NONE) {
            return
        }
        val sanitizedMessage = maskPii(message)
        logWriter(level.priority, tag, sanitizedMessage, throwable)
    }
}
