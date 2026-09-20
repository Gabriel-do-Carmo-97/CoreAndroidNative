package br.com.wgc.core.network.tracing

import okhttp3.Interceptor
import okhttp3.Response
import java.security.SecureRandom
import java.util.UUID

/**
 * Interceptor para Observabilidade e Tracing Distribuído.
 *
 * Injeta cabeçalhos corporativos de rastreamento ponta-a-ponta:
 * - `X-Correlation-ID`: Identificador único de transação (UUIDv4).
 * - `traceparent`: Especificação oficial W3C Trace Context (versão 00, 16-byte traceId, 8-byte spanId, sample flag 01).
 */
class TraceIdInterceptor(
    private val random: SecureRandom = SecureRandom(),
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val correlationId =
            originalRequest.header(HEADER_CORRELATION_ID)
                ?: UUID.randomUUID().toString()
        requestBuilder.header(HEADER_CORRELATION_ID, correlationId)

        val traceparent =
            originalRequest.header(HEADER_TRACEPARENT)
                ?: generateW3cTraceParent()
        requestBuilder.header(HEADER_TRACEPARENT, traceparent)

        return chain.proceed(requestBuilder.build())
    }

    private fun generateW3cTraceParent(): String {
        val traceIdBytes = ByteArray(TRACE_ID_BYTE_COUNT).also(random::nextBytes)
        val spanIdBytes = ByteArray(SPAN_ID_BYTE_COUNT).also(random::nextBytes)

        val traceId = traceIdBytes.joinToString("") { "%02x".format(it) }
        val spanId = spanIdBytes.joinToString("") { "%02x".format(it) }

        return "00-$traceId-$spanId-01"
    }

    companion object {
        const val HEADER_CORRELATION_ID = "X-Correlation-ID"
        const val HEADER_TRACEPARENT = "traceparent"
        private const val TRACE_ID_BYTE_COUNT = 16
        private const val SPAN_ID_BYTE_COUNT = 8
    }
}
