package br.com.wgc.core.testing.fakes

import br.com.wgc.core.logging.CoreLogger
import br.com.wgc.core.logging.LogLevel

/**
 * Fake em memória de [CoreLogger] projetado para capturar e asserir logs em testes unitários.
 */
class FakeCoreLogger : CoreLogger {
    data class LogMessage(
        val level: LogLevel,
        val tag: String,
        val message: String,
        val throwable: Throwable? = null,
    )

    val logs = mutableListOf<LogMessage>()

    override fun v(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        logs.add(LogMessage(LogLevel.VERBOSE, tag, message, throwable))
    }

    override fun d(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        logs.add(LogMessage(LogLevel.DEBUG, tag, message, throwable))
    }

    override fun i(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        logs.add(LogMessage(LogLevel.INFO, tag, message, throwable))
    }

    override fun w(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        logs.add(LogMessage(LogLevel.WARN, tag, message, throwable))
    }

    override fun e(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        logs.add(LogMessage(LogLevel.ERROR, tag, message, throwable))
    }

    override fun maskPii(message: String): String = message

    fun clear() {
        logs.clear()
    }

    fun hasMessageWithTag(tag: String): Boolean = logs.any { it.tag == tag }

    fun hasMessageContaining(content: String): Boolean = logs.any { it.message.contains(content) }
}
