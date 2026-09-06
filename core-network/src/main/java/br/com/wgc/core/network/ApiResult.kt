package br.com.wgc.core.network

/**
 * Hierarquia de exceções de rede tipadas para tratamento padronizado em aplicações Android.
 */
sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /** Falha decorrente de ausência de conectividade de rede no aparelho. */
    class NoInternetException(
        message: String = "Sem conexão com a internet",
        cause: Throwable? = null
    ) : NetworkException(message, cause)

    /** Erro HTTP 401: Falha de autenticação ou token de acesso revogado. */
    class UnauthorizedException(
        val code: Int = 401,
        message: String = "Acesso não autorizado"
    ) : NetworkException(message)

    /** Erro HTTP 5xx: Instabilidade ou falha no servidor remoto. */
    class ServerException(
        val code: Int,
        message: String = "Erro interno no servidor remoto"
    ) : NetworkException(message)

    /** Erro HTTP 4xx (exceto 401): Erro na composição da requisição pelo cliente. */
    class ClientException(
        val code: Int,
        message: String = "Erro na requisição"
    ) : NetworkException(message)

    /** Esgotamento do tempo limite de conexão ou leitura da requisição. */
    class TimeoutException(
        message: String = "Tempo limite da requisição esgotado",
        cause: Throwable? = null
    ) : NetworkException(message, cause)

    /** Erro genérico ou inesperado na comunicação HTTP. */
    class UnknownNetworkException(
        message: String = "Erro de rede desconhecido",
        cause: Throwable? = null
    ) : NetworkException(message, cause)
}

/**
 * Representação selada e tipada para resultados de chamadas de API remotas.
 *
 * @param T Tipo do dado retornado em caso de sucesso.
 */
sealed interface ApiResult<out T> {

    /**
     * Resposta bem-sucedida da API.
     *
     * @property data Conteúdo decodificado da resposta.
     * @property statusCode Código HTTP retornado pelo servidor (ex: 200, 201).
     */
    data class Success<out T>(val data: T, val statusCode: Int = 200) : ApiResult<T>

    /**
     * Falha na execução da requisição HTTP encapsulando uma [NetworkException].
     *
     * @property exception A exceção com detalhes do erro.
     */
    data class Failure(val exception: NetworkException) : ApiResult<Nothing>
}
