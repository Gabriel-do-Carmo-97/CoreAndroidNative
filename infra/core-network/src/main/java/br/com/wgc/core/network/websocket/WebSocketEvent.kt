package br.com.wgc.core.network.websocket

import okhttp3.Response
import okio.ByteString

/**
 * Eventos reativos emitidos ao longo do ciclo de vida de uma conexão WebSocket.
 */
sealed class WebSocketEvent {
    data class Open(
        val response: Response,
    ) : WebSocketEvent()

    data class Message(
        val text: String,
    ) : WebSocketEvent()

    data class BinaryMessage(
        val bytes: ByteString,
    ) : WebSocketEvent()

    data class Closing(
        val code: Int,
        val reason: String,
    ) : WebSocketEvent()

    data class Closed(
        val code: Int,
        val reason: String,
    ) : WebSocketEvent()

    data class Failure(
        val throwable: Throwable,
        val response: Response?,
    ) : WebSocketEvent()
}
