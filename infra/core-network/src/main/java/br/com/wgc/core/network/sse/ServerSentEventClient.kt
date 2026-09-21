package br.com.wgc.core.network.sse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Representa um evento individual transmitido por um fluxo Server-Sent Events (SSE).
 *
 * @property id Identificador do evento emitido pelo servidor.
 * @property event Nome/tipo do evento (padrão: "message").
 * @property data Conteúdo útil transportado no corpo do evento.
 */
data class SseEvent(
    val id: String? = null,
    val event: String = "message",
    val data: String = "",
)

/**
 * Cliente reativo para consumo de transmissões Server-Sent Events (SSE) via corrotinas e [Flow].
 *
 * @property okHttpClient Instância OkHttp utilizada para abrir e manter o canal streaming HTTP.
 */
class ServerSentEventClient(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
) {
    fun openStream(
        url: String,
        headers: Map<String, String> = emptyMap(),
    ): Flow<SseEvent> =
        flow {
            val requestBuilder =
                Request
                    .Builder()
                    .url(url)
                    .addHeader("Accept", "text/event-stream")
                    .addHeader("Cache-Control", "no-cache")

            headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful) {
                response.close()
                error("SSE stream failed with HTTP ${response.code}")
            }

            val source = response.body?.source() ?: return@flow
            try {
                var currentId: String? = null
                var currentEvent = "message"
                val dataBuffer = StringBuilder()

                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: break
                    if (line.isEmpty()) {
                        if (dataBuffer.isNotEmpty()) {
                            emit(SseEvent(id = currentId, event = currentEvent, data = dataBuffer.toString()))
                            dataBuffer.clear()
                        }
                        currentEvent = "message"
                    } else if (line.startsWith("data:")) {
                        val data = line.removePrefix("data:").trim()
                        if (dataBuffer.isNotEmpty()) dataBuffer.append("\n")
                        dataBuffer.append(data)
                    } else if (line.startsWith("event:")) {
                        currentEvent = line.removePrefix("event:").trim()
                    } else if (line.startsWith("id:")) {
                        currentId = line.removePrefix("id:").trim()
                    }
                }

                if (dataBuffer.isNotEmpty()) {
                    emit(SseEvent(id = currentId, event = currentEvent, data = dataBuffer.toString()))
                }
            } finally {
                response.close()
            }
        }.flowOn(Dispatchers.IO)
}
