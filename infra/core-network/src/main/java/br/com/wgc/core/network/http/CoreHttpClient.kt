package br.com.wgc.core.network.http

import br.com.wgc.core.result.ResultWrapper

/**
 * Requisição HTTP agnóstica de plataforma.
 */
data class CoreHttpRequest(
    val url: String,
    val method: HttpMethod = HttpMethod.GET,
    val headers: Map<String, String> = emptyMap(),
    val body: ByteArray? = null,
    val timeoutMs: Long = 30_000L,
) {
    enum class HttpMethod { GET, POST, PUT, DELETE, PATCH, HEAD }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoreHttpRequest

        if (url != other.url) return false
        if (method != other.method) return false
        if (headers != other.headers) return false
        if (body != null) {
            if (other.body == null) return false
            if (!body.contentEquals(other.body)) return false
        } else if (other.body != null) {
            return false
        }
        if (timeoutMs != other.timeoutMs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + method.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + (body?.contentHashCode() ?: 0)
        result = 31 * result + timeoutMs.hashCode()
        return result
    }
}

/**
 * Resposta HTTP agnóstica de plataforma.
 */
data class CoreHttpResponse(
    val statusCode: Int,
    val headers: Map<String, List<String>>,
    val body: ByteArray,
) {
    val isSuccessful: Boolean get() = statusCode in 200..299

    fun bodyAsString(charset: java.nio.charset.Charset = Charsets.UTF_8): String = String(body, charset)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoreHttpResponse

        if (statusCode != other.statusCode) return false
        if (headers != other.headers) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = statusCode
        result = 31 * result + headers.hashCode()
        result = 31 * result + body.contentHashCode()
        return result
    }
}

/**
 * Contrato abstrato de cliente HTTP desacoplado de engines de terceiros.
 * Permite alternar entre OkHttp (Android), Ktor (iOS/Desktop) e Mock em testes.
 */
interface CoreHttpClient {
    suspend fun execute(request: CoreHttpRequest): ResultWrapper<CoreHttpResponse>
}
