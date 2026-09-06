package br.com.wgc.core.result

sealed interface ResultState<out T> {
    data object Idle : ResultState<Nothing>
    data object Loading : ResultState<Nothing>
    data class Success<out T>(val data: T) : ResultState<T>
    data class Error(
        val throwable: Throwable? = null,
        val message: String? = throwable?.localizedMessage
    ) : ResultState<Nothing>
}

fun <T> ResultState<T>.getOrNull(): T? = when (this) {
    is ResultState.Success -> data
    else -> null
}

fun <T> ResultState<T>.isSuccess(): Boolean = this is ResultState.Success
fun <T> ResultState<T>.isError(): Boolean = this is ResultState.Error
fun <T> ResultState<T>.isLoading(): Boolean = this is ResultState.Loading

inline fun <T, R> ResultState<T>.map(transform: (T) -> R): ResultState<R> = when (this) {
    is ResultState.Idle -> ResultState.Idle
    is ResultState.Loading -> ResultState.Loading
    is ResultState.Success -> ResultState.Success(transform(data))
    is ResultState.Error -> ResultState.Error(throwable, message)
}
