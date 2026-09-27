package br.com.wgc.core.testing.chaos

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.util.Random

/**
 * Interceptor de Chaos Engineering para testes de resiliência e injeção controlada de falhas em chamadas HTTP.
 * Permite simular latência de rede móvel, taxas de erro randômicas (500/503/504) e corrupção de payloads.
 */
class ChaosEngineeringInterceptor(
    var simulatedLatencyMs: Long = 0L,
    var failureRate: Float = 0f, // 0.0 (sem falha) a 1.0 (100% de falha)
    var failureStatusCode: Int = 500,
    var shouldCorruptPayload: Boolean = false,
    private val random: Random = Random(),
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 1. Simulação de latência artificial
        if (simulatedLatencyMs > 0L) {
            try {
                Thread.sleep(simulatedLatencyMs)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                throw IOException("Chaos: Connection interrupted during simulated latency")
            }
        }

        // 2. Simulação de falha controlada (Packet Loss / Server Outage)
        if (failureRate > 0f && random.nextFloat() < failureRate) {
            val request = chain.request()
            return Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(failureStatusCode)
                .message("Chaos Injected Fault ($failureStatusCode)")
                .body("""{"error": "Chaos simulated error"}""".toResponseBody("application/json".toMediaTypeOrNull()))
                .build()
        }

        val response = chain.proceed(chain.request())

        // 3. Simulação de corrupção de payload JSON
        if (shouldCorruptPayload && response.isSuccessful && response.body != null) {
            val raw = response.body?.string() ?: ""
            val corrupted =
                if (raw.length > 5) {
                    raw.substring(0, raw.length / 2) + " [CORRUPTED_CHAOS_PAYLOAD] }}}"
                } else {
                    "INVALID_JSON_CORRUPTED"
                }

            return response
                .newBuilder()
                .body(corrupted.toResponseBody(response.body?.contentType()))
                .build()
        }

        return response
    }
}
