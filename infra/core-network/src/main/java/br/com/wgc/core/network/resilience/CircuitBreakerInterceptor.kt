package br.com.wgc.core.network.resilience

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Estados do Circuit Breaker para proteção contra falhas em cascata de serviços de backend.
 */
enum class CircuitState {
    /** Circuito fechado: requisições fluem normalmente. */
    CLOSED,

    /** Circuito aberto: requisições são bloqueadas imediatamente sem onerar a rede. */
    OPEN,

    /** Meio-aberto: permite requisições de sonda (probe) para testar a recuperação do serviço. */
    HALF_OPEN,
}

/**
 * Exceção lançada quando uma requisição é barrada por um Circuit Breaker aberto.
 */
class CircuitBreakerOpenException(
    message: String = "Circuit breaker is OPEN. Requests blocked to prevent cascading failures.",
) : IOException(message)

/**
 * Interceptor OkHttp que implementa o padrão de resiliência Circuit Breaker.
 *
 * Interrompe o envio de requisições a serviços remotos instáveis após um número consecutivo
 * de falhas de rede (erros 5xx ou Timeouts), retornando [CircuitBreakerOpenException] localmente
 * até que o período de esfriamento (cooldown) expire e o serviço se recupere.
 *
 * @param failureThreshold Número de falhas consecutivas necessárias para abrir o circuito (default: 5).
 * @param resetTimeoutMs Período de esfriamento em milissegundos antes de tentar [CircuitState.HALF_OPEN] (default: 30s).
 */
class CircuitBreakerInterceptor(
    private val failureThreshold: Int = DEFAULT_FAILURE_THRESHOLD,
    private val resetTimeoutMs: Long = DEFAULT_RESET_TIMEOUT_MS,
) : Interceptor {
    private val state = AtomicReference(CircuitState.CLOSED)
    private val consecutiveFailures = AtomicInteger(0)
    private val lastStateChangeTime = AtomicLong(0L)

    /** Retorna o estado atual do circuito. */
    fun currentState(): CircuitState = state.get()

    /** Retorna a contagem atual de falhas consecutivas registradas. */
    fun currentFailureCount(): Int = consecutiveFailures.get()

    /** Força o fechamento manual do circuito e zera a contagem de falhas. */
    fun reset() {
        consecutiveFailures.set(0)
        state.set(CircuitState.CLOSED)
        lastStateChangeTime.set(System.currentTimeMillis())
    }

    /** Força a abertura manual do circuito. */
    fun forceOpen() {
        state.set(CircuitState.OPEN)
        lastStateChangeTime.set(System.currentTimeMillis())
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        checkStateTransition()

        if (state.get() == CircuitState.OPEN) {
            throw CircuitBreakerOpenException()
        }

        val request = chain.request()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            onExecutionFailure()
            throw e
        }

        if (response.code >= HTTP_SERVER_ERROR_MIN) {
            onExecutionFailure()
        } else {
            onExecutionSuccess()
        }

        return response
    }

    private fun checkStateTransition() {
        if (state.get() == CircuitState.OPEN) {
            val now = System.currentTimeMillis()
            if (now - lastStateChangeTime.get() >= resetTimeoutMs) {
                state.compareAndSet(CircuitState.OPEN, CircuitState.HALF_OPEN)
            }
        }
    }

    private fun onExecutionSuccess() {
        consecutiveFailures.set(0)
        state.set(CircuitState.CLOSED)
    }

    private fun onExecutionFailure() {
        val failures = consecutiveFailures.incrementAndGet()
        if (state.get() == CircuitState.HALF_OPEN || failures >= failureThreshold) {
            state.set(CircuitState.OPEN)
            lastStateChangeTime.set(System.currentTimeMillis())
        }
    }

    companion object {
        const val DEFAULT_FAILURE_THRESHOLD: Int = 5
        const val DEFAULT_RESET_TIMEOUT_MS: Long = 30_000L
        private const val HTTP_SERVER_ERROR_MIN = 500
    }
}
