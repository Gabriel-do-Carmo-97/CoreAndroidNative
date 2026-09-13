package br.com.wgc.core.logging

/**
 * Níveis de severidade para filtragem e emissão de mensagens de log no [CoreLogger].
 */
enum class LogLevel(
    val priority: Int,
) {
    /** Detalhamento minucioso para diagnóstico profundo. */
    VERBOSE(2),

    /** Mensagens úteis durante o desenvolvimento e depuração. */
    DEBUG(3),

    /** Informações operacionais relevantes sobre o fluxo da aplicação. */
    INFO(4),

    /** Alertas sobre situações anômalas que não interrompem a execução. */
    WARN(5),

    /** Erros graves ou exceções capturadas que requerem atenção imediata. */
    ERROR(6),

    /** Desativa completamente a emissão de logs. */
    NONE(Int.MAX_VALUE),
}

/**
 * Contrato corporativo para registro e observabilidade de eventos e erros em tempo de execução.
 *
 * Fornece métodos estruturados para diferentes níveis de severidade e suporte nativo a mascaramento
 * de dados sensíveis (PII - Personally Identifiable Information).
 */
interface CoreLogger {
    /**
     * Registra mensagem no nível [LogLevel.VERBOSE].
     *
     * @param tag Identificador de origem da mensagem.
     * @param message Conteúdo da mensagem a ser registrada.
     * @param throwable Exceção opcional associada ao log.
     */
    fun v(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Registra mensagem no nível [LogLevel.DEBUG].
     *
     * @param tag Identificador de origem da mensagem.
     * @param message Conteúdo da mensagem a ser registrada.
     * @param throwable Exceção opcional associada ao log.
     */
    fun d(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Registra mensagem no nível [LogLevel.INFO].
     *
     * @param tag Identificador de origem da mensagem.
     * @param message Conteúdo da mensagem a ser registrada.
     * @param throwable Exceção opcional associada ao log.
     */
    fun i(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Registra mensagem no nível [LogLevel.WARN].
     *
     * @param tag Identificador de origem da mensagem.
     * @param message Conteúdo da mensagem a ser registrada.
     * @param throwable Exceção opcional associada ao log.
     */
    fun w(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Registra mensagem no nível [LogLevel.ERROR].
     *
     * @param tag Identificador de origem da mensagem.
     * @param message Conteúdo da mensagem a ser registrada.
     * @param throwable Exceção opcional associada ao log.
     */
    fun e(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Aplica regras de sanitização em uma string, ocultando dados pessoais identificáveis (PII).
     *
     * @param message Texto original a ser sanitizado.
     * @return Texto com dados sensíveis mascarados.
     */
    fun maskPii(message: String): String
}
