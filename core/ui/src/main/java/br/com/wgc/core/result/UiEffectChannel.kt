package br.com.wgc.core.result

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Contrato para canais de efeitos colaterais de UI de disparo único (single-shot events),
 * essencial em arquiteturas MVI (Model-View-Intent) ou MVVM para navegação, toasts, dialogs e snackbars.
 *
 * Garante que eventos transitórios sejam consumidos exatamente uma vez pela camada de apresentação,
 * evitando reexecuções indevidas durante recomposições de tela no Jetpack Compose ou recriação de Activities.
 *
 * @param T Tipo do evento de efeito colateral.
 */
interface UiEffectChannel<T> {

    /**
     * Fluxo frio ([Flow]) observável que emite os efeitos enviados ao canal.
     * Consumidores na UI devem coletar este fluxo utilizando operadores adequados de ciclo de vida.
     */
    val effects: Flow<T>

    /**
     * Envia um efeito para o canal de forma suspensa caso a capacidade do buffer esteja cheia.
     *
     * @param effect O efeito a ser emitido para a UI.
     */
    suspend fun sendEffect(effect: T)

    /**
     * Tenta enviar imediatamente um efeito sem suspender a corrotina.
     *
     * @param effect O efeito a ser emitido para a UI.
     * @return `true` se o efeito foi enfileirado com sucesso, `false` caso contrário.
     */
    fun trySendEffect(effect: T): Boolean
}

/**
 * Implementação padrão de [UiEffectChannel] utilizando Kotlin Coroutines [Channel].
 *
 * @param T Tipo do evento de efeito colateral.
 * @property capacity Capacidade do buffer do canal, por padrão [Channel.BUFFERED].
 * @property onBufferOverflow Estratégia de descarte de buffer, por padrão [BufferOverflow.SUSPEND].
 */
class DefaultUiEffectChannel<T>(
    capacity: Int = Channel.BUFFERED,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : UiEffectChannel<T> {

    private val channel = Channel<T>(
        capacity = capacity,
        onBufferOverflow = onBufferOverflow
    )

    override val effects: Flow<T> = channel.receiveAsFlow()

    override suspend fun sendEffect(effect: T) {
        channel.send(effect)
    }

    override fun trySendEffect(effect: T): Boolean {
        return channel.trySend(effect).isSuccess
    }
}
