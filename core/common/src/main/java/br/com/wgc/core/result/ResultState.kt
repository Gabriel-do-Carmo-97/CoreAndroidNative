package br.com.wgc.core.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Interface selada que modela os possíveis estados de uma operação assíncrona ou chamada de rede.
 *
 * Padrão recomendado para arquiteturas Clean Architecture, MVI e MVVM para comunicação
 * entre UseCases/Repositories e a camada de UI (Jetpack Compose).
 *
 * @param T O tipo de dado retornado em caso de sucesso.
 */
sealed interface ResultState<out T> {

    /**
     * Estado inicial inativo, aguardando disparo de ação.
     */
    data object Idle : ResultState<Nothing>

    /**
     * Estado indicando que uma operação está em andamento (loading).
     */
    data object Loading : ResultState<Nothing>

    /**
     * Estado indicando que a operação foi concluída com sucesso.
     *
     * @param data O dado resultante da operação.
     */
    data class Success<out T>(val data: T) : ResultState<T>

    /**
     * Estado indicando que ocorreu uma falha durante a execução.
     *
     * @param throwable Exceção opcional capturada.
     * @param message Mensagem de erro amigável ou localizada.
     */
    data class Error(
        val throwable: Throwable? = null,
        val message: String? = throwable?.localizedMessage
    ) : ResultState<Nothing>
}

/**
 * Retorna o dado contido em caso de [ResultState.Success] ou `null` para qualquer outro estado.
 */
fun <T> ResultState<T>.getOrNull(): T? = when (this) {
    is ResultState.Success -> data
    else -> null
}

/**
 * Retorna `true` se o estado for [ResultState.Success].
 */
fun <T> ResultState<T>.isSuccess(): Boolean = this is ResultState.Success

/**
 * Retorna `true` se o estado for [ResultState.Error].
 */
fun <T> ResultState<T>.isError(): Boolean = this is ResultState.Error

/**
 * Retorna `true` se o estado for [ResultState.Loading].
 */
fun <T> ResultState<T>.isLoading(): Boolean = this is ResultState.Loading

/**
 * Transforma o dado contido em caso de [ResultState.Success] utilizando a função [transform],
 * preservando os estados [ResultState.Idle], [ResultState.Loading] ou [ResultState.Error].
 *
 * @param transform Função de mapeamento de `T` para `R`.
 * @return Novo [ResultState] contendo o dado transformado.
 */
inline fun <T, R> ResultState<T>.map(transform: (T) -> R): ResultState<R> = when (this) {
    is ResultState.Idle -> ResultState.Idle
    is ResultState.Loading -> ResultState.Loading
    is ResultState.Success -> ResultState.Success(transform(data))
    is ResultState.Error -> ResultState.Error(throwable, message)
}

/**
 * Converte qualquer [Flow] emitindo `T` em um [Flow] gerenciado emitindo [ResultState] de `T`.
 *
 * Emite [ResultState.Loading] no início da coleta, [ResultState.Success] para cada emissão
 * de item e [ResultState.Error] interceptando exceções lançadas na cadeia assíncrona.
 *
 * @return [Flow] que encapsula o ciclo de vida da chamada em [ResultState].
 */
fun <T> Flow<T>.asResultState(): Flow<ResultState<T>> = this
    .map<T, ResultState<T>> { ResultState.Success(it) }
    .onStart { emit(ResultState.Loading) }
    .catch { emit(ResultState.Error(throwable = it, message = it.localizedMessage)) }
