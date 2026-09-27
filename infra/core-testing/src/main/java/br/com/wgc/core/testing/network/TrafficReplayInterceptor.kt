package br.com.wgc.core.testing.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.ConcurrentHashMap

/**
 * Interceptor hermético de gravação e reprodução de tráfego de rede para testes de integração offline.
 */
class TrafficReplayInterceptor : Interceptor {
    data class MockInteraction(
        val method: String,
        val urlPath: String,
        val statusCode: Int = 200,
        val responseBody: String = "{}",
        val contentType: String = "application/json",
        val headers: Map<String, String> = emptyMap(),
    )

    private val interactions = ConcurrentHashMap<String, MockInteraction>()

    fun register(interaction: MockInteraction) {
        val key = makeKey(interaction.method, interaction.urlPath)
        interactions[key] = interaction
    }

    fun clear() {
        interactions.clear()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val key = makeKey(request.method, path)

        val interaction =
            interactions[key]
                ?: interactions.entries
                    .firstOrNull { entry ->
                        entry.key.startsWith("${request.method}:") && path.endsWith(entry.value.urlPath)
                    }?.value

        if (interaction != null) {
            val responseBody =
                interaction.responseBody.toResponseBody(
                    interaction.contentType.toMediaTypeOrNull(),
                )

            val builder =
                Response
                    .Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(interaction.statusCode)
                    .message("Mocked Replay Response")
                    .body(responseBody)

            interaction.headers.forEach { (k, v) ->
                builder.addHeader(k, v)
            }

            return builder.build()
        }

        return chain.proceed(request)
    }

    private fun makeKey(
        method: String,
        path: String,
    ): String = "${method.uppercase()}:$path"
}
