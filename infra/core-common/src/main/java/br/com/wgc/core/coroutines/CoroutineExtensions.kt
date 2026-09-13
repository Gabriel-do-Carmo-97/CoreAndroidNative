package br.com.wgc.core.coroutines

import kotlinx.coroutines.delay

/**
 * Executa uma operação suspensa com política de retentativa e recuo exponencial (Exponential Backoff).
 *
 * @param times Número máximo de tentativas antes de relançar a exceção final. Padrão: 3.
 * @param initialDelayMs Atraso inicial em milissegundos antes da primeira retentativa. Padrão: 1000ms.
 * @param factor Fator multiplicador de recuo aplicado a cada falha. Padrão: 2.0.
 * @param maxDelayMs Tempo máximo de espera permitido entre tentativas. Padrão: 10000ms.
 * @param block Bloco suspenso a ser executado.
 * @return O resultado da execução do bloco [block].
 * @throws Throwable Caso todas as [times] tentativas falhem.
 */
suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelayMs: Long = 1000L,
    factor: Double = 2.0,
    maxDelayMs: Long = 10000L,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelayMs
    repeat(times - 1) {
        try {
            return block()
        } catch (
            @Suppress("TooGenericExceptionCaught") exception: Throwable,
        ) {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
        }
    }
    return block()
}
