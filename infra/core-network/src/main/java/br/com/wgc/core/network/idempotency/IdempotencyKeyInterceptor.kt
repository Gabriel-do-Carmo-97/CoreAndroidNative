package br.com.wgc.core.network.idempotency

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

/**
 * Interceptor OkHttp que anexa automaticamente uma chave de idempotência a requisições de mutação de estado.
 *
 * Garante que requisições repetidas (como retentativas de rede, pagamento, checkout ou cadastros)
 * não causem efeitos colaterais duplicados no backend, injetando o cabeçalho `X-Idempotency-Key`.
 *
 * @param headerName Nome do cabeçalho HTTP a ser adicionado (default: "X-Idempotency-Key").
 * @param targetMethods Conjunto de métodos HTTP considerados mutáveis (default: POST, PUT, PATCH).
 * @param keyGenerator Função geradora de chaves (default: [UUID.randomUUID]).
 */
class IdempotencyKeyInterceptor(
    private val headerName: String = DEFAULT_HEADER_NAME,
    private val targetMethods: Set<String> = DEFAULT_MUTATING_METHODS,
    private val keyGenerator: () -> String = { UUID.randomUUID().toString() },
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Se não for um método de mutação ou já possuir o cabeçalho definido, segue normalmente
        if (!targetMethods.contains(originalRequest.method.uppercase()) ||
            originalRequest.header(headerName) != null
        ) {
            return chain.proceed(originalRequest)
        }

        val requestWithKey =
            originalRequest
                .newBuilder()
                .header(headerName, keyGenerator())
                .build()

        return chain.proceed(requestWithKey)
    }

    companion object {
        const val DEFAULT_HEADER_NAME: String = "X-Idempotency-Key"
        val DEFAULT_MUTATING_METHODS: Set<String> = setOf("POST", "PUT", "PATCH")
    }
}
