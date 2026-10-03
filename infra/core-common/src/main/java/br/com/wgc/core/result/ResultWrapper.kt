package br.com.wgc.core.result

/**
 * Encapsula o resultado de uma operação computacional ou de domínio,
 * modelando explicitamente o sucesso com [ResultWrapper.Success] ou falha com [ResultWrapper.Failure].
 *
 * Projetado para ser 100% agnóstico de plataforma (compatível com KMP / commonMain).
 */
sealed interface ResultWrapper<out T> {
    data class Success<out T>(
        val value: T,
    ) : ResultWrapper<T>

    data class Failure(
        val error: CoreDomainError,
    ) : ResultWrapper<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    companion object {
        @Suppress("TooGenericExceptionCaught")
        inline fun <T> of(block: () -> T): ResultWrapper<T> =
            try {
                Success(block())
            } catch (e: Exception) {
                Failure(CoreDomainError.Unexpected(e.message ?: "Unknown error", e))
            }
    }
}

fun <T> ResultWrapper<T>.getOrNull(): T? =
    when (this) {
        is ResultWrapper.Success -> value
        is ResultWrapper.Failure -> null
    }

fun <T> ResultWrapper<T>.getOrThrow(): T =
    when (this) {
        is ResultWrapper.Success -> value
        is ResultWrapper.Failure -> throw error.cause ?: RuntimeException(error.message)
    }

inline fun <T, R> ResultWrapper<T>.map(transform: (T) -> R): ResultWrapper<R> =
    when (this) {
        is ResultWrapper.Success -> ResultWrapper.Success(transform(value))
        is ResultWrapper.Failure -> this
    }

inline fun <T, R> ResultWrapper<T>.flatMap(transform: (T) -> ResultWrapper<R>): ResultWrapper<R> =
    when (this) {
        is ResultWrapper.Success -> transform(value)
        is ResultWrapper.Failure -> this
    }

inline fun <T> ResultWrapper<T>.onSuccess(action: (T) -> Unit): ResultWrapper<T> {
    if (this is ResultWrapper.Success) action(value)
    return this
}

inline fun <T> ResultWrapper<T>.onFailure(action: (CoreDomainError) -> Unit): ResultWrapper<T> {
    if (this is ResultWrapper.Failure) action(error)
    return this
}

inline fun <T, R> ResultWrapper<T>.fold(
    onSuccess: (T) -> R,
    onFailure: (CoreDomainError) -> R,
): R =
    when (this) {
        is ResultWrapper.Success -> onSuccess(value)
        is ResultWrapper.Failure -> onFailure(error)
    }

/**
 * Modelo padronizado de erros de domínio puros para arquitetura universal.
 */
sealed class CoreDomainError(
    val message: String,
    val cause: Throwable? = null,
) {
    class Network(
        message: String,
        val statusCode: Int? = null,
        cause: Throwable? = null,
    ) : CoreDomainError(message, cause)

    class Storage(
        message: String,
        cause: Throwable? = null,
    ) : CoreDomainError(message, cause)

    class Security(
        message: String,
        cause: Throwable? = null,
    ) : CoreDomainError(message, cause)

    class Validation(
        message: String,
        val field: String? = null,
    ) : CoreDomainError(message)

    class NotFound(
        message: String,
    ) : CoreDomainError(message)

    class Unauthorized(
        message: String,
    ) : CoreDomainError(message)

    class Unexpected(
        message: String,
        cause: Throwable? = null,
    ) : CoreDomainError(message, cause)
}
