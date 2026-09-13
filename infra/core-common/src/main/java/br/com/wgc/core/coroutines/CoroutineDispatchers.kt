package br.com.wgc.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Interface que abstrai os despachantes de Coroutines ([CoroutineDispatcher]) utilizados na aplicação.
 *
 * Promove a testabilidade ao permitir a injeção de despachantes virtuais ou controlados
 * (como `StandardTestDispatcher`) em testes unitários, substituindo despachantes reais do runtime.
 */
interface CoroutineDispatchers {
    /** Despachante associado à Main Thread do Android (atualizações de UI). */
    val main: CoroutineDispatcher

    /** Despachante otimizado para operações de entrada e saída (disco, rede, banco de dados). */
    val io: CoroutineDispatcher

    /** Despachante otimizado para tarefas intensivas de processamento de CPU. */
    val default: CoroutineDispatcher

    /** Despachante não confinado a nenhuma thread específica. */
    val unconfined: CoroutineDispatcher
}
