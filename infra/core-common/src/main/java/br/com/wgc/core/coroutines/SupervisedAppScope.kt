package br.com.wgc.core.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 * Escopo corporativo de corrotinas desacoplado do ciclo de vida da UI (Application-scoped).
 *
 * Utiliza [SupervisorJob] combinado a um [CoroutineExceptionHandler] customizado para garantir que
 * a falha de uma tarefa em segundo plano (como envio de métricas, gravação assíncrona ou sincronização)
 * não cancele tarefas irmãs nem encerre o processo do aplicativo inesperadamente.
 *
 * @param dispatchers Provedor de despacho de threads do sistema.
 * @param onUnhandledError Callback opcional para captura e log de falhas não tratadas.
 */
@Singleton
class SupervisedAppScope(
    private val dispatchers: CoroutineDispatchers,
    private val onUnhandledError: ((Throwable) -> Unit)? = null,
) : CoroutineScope {
    @Inject
    constructor(dispatchers: CoroutineDispatchers) : this(dispatchers, null)

    private val supervisorJob = SupervisorJob()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            onUnhandledError?.invoke(throwable)
        }

    override val coroutineContext: CoroutineContext =
        supervisorJob + dispatchers.default + exceptionHandler

    /**
     * Executa uma tarefa em segundo plano de forma supervisionada (fire-and-forget).
     */
    fun launchSupervised(
        context: CoroutineContext = coroutineContext,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = launch(context, block = block)

    /**
     * Executa uma tarefa supervisionada com retorno de resultado assíncrono.
     */
    fun <T> asyncSupervised(
        context: CoroutineContext = coroutineContext,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> = async(context, block = block)

    /**
     * Cancela todas as tarefas em execução neste escopo de corrotinas.
     */
    fun cancelScope() {
        supervisorJob.cancel()
    }
}
