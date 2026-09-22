package br.com.wgc.core.network.mock

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Definição de resposta simulada para testes locais e modo offline.
 *
 * @property statusCode Código HTTP retornado (default: 200).
 * @property body Corpo da resposta como String (JSON, XML ou texto puro).
 * @property contentType Tipo MIME do corpo retornado (default: "application/json").
 * @property headers Cabeçalhos HTTP customizados inclusos na resposta.
 * @property delayMs Atraso simulado em milissegundos para emular latência de rede.
 */
data class MockResponse(
    val statusCode: Int = 200,
    val body: String = "",
    val contentType: String = "application/json",
    val headers: Map<String, String> = emptyMap(),
    val delayMs: Long = 0L,
)

/**
 * Regra de correspondência para interceptação e simulação de requisições.
 */
data class MockRule(
    val predicate: (Request) -> Boolean,
    val responseProvider: (Request) -> MockResponse,
)

/**
 * Interceptor de simulação de rede (Mock Engine) declarativo.
 *
 * Permite interceptar chamadas HTTP locais em testes instrumentados, previews do Jetpack Compose
 * ou ambientes de desenvolvimento offline sem necessidade de servidores externos.
 */
class MockEngineInterceptor(
    private var isEnabled: Boolean = true,
) : Interceptor {
    private val rules = CopyOnWriteArrayList<MockRule>()

    /**
     * Habilita ou desabilita o mock interceptor dinamicamente.
     */
    fun setEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }

    /**
     * Retorna se a interceptação de mocks está ativa.
     */
    fun isEnabled(): Boolean = isEnabled

    /**
     * Registra uma regra simulada baseada em predicado genérico de [Request].
     */
    fun addRule(
        predicate: (Request) -> Boolean,
        response: MockResponse,
    ): MockEngineInterceptor {
        rules.add(MockRule(predicate) { response })
        return this
    }

    /**
     * Registra uma regra simulada para um caminho de URL e método específicos.
     *
     * @param urlSubstring Trecho da URL a ser verificado com contains.
     * @param method Método HTTP esperado ("GET", "POST", etc) ou null para aceitar qualquer método.
     * @param response Resposta simulada retornada quando a rota coincidir.
     */
    fun addRule(
        urlSubstring: String,
        method: String? = null,
        response: MockResponse,
    ): MockEngineInterceptor {
        val predicate: (Request) -> Boolean = { req ->
            val matchUrl = req.url.toString().contains(urlSubstring)
            val matchMethod = method == null || req.method.equals(method, ignoreCase = true)
            matchUrl && matchMethod
        }
        rules.add(MockRule(predicate) { response })
        return this
    }

    /**
     * Limpa todas as regras de mock cadastradas.
     */
    fun clearRules() {
        rules.clear()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!isEnabled) {
            return chain.proceed(request)
        }

        val matchedRule = rules.firstOrNull { it.predicate(request) }
        if (matchedRule == null) {
            return chain.proceed(request)
        }

        val mockResponse = matchedRule.responseProvider(request)

        if (mockResponse.delayMs > 0) {
            Thread.sleep(mockResponse.delayMs)
        }

        val mediaType = mockResponse.contentType.toMediaTypeOrNull()
        val responseBody = mockResponse.body.toResponseBody(mediaType)

        val builder =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(mockResponse.statusCode)
                .message(resolveStatusMessage(mockResponse.statusCode))
                .body(responseBody)

        mockResponse.headers.forEach { (key, value) ->
            builder.addHeader(key, value)
        }

        return builder.build()
    }

    @Suppress("MagicNumber")
    private fun resolveStatusMessage(statusCode: Int): String =
        when (statusCode) {
            200 -> "OK"
            201 -> "Created"
            204 -> "No Content"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            500 -> "Internal Server Error"
            503 -> "Service Unavailable"
            else -> "HTTP $statusCode"
        }
}
