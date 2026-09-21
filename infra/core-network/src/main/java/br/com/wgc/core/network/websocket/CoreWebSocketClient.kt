package br.com.wgc.core.network.websocket

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Cliente corporativo reativo de WebSocket estruturado sobre [OkHttpClient] emitindo [Flow] de [WebSocketEvent].
 *
 * @property okHttpClient Instância OkHttp configurada para o transporte do socket.
 */
class CoreWebSocketClient(
    private val okHttpClient: OkHttpClient = OkHttpClient(),
) {
    private var activeSocket: WebSocket? = null

    fun connect(
        url: String,
        headers: Map<String, String> = emptyMap(),
    ): Flow<WebSocketEvent> =
        callbackFlow {
            val requestBuilder = Request.Builder().url(url)
            headers.forEach { (key, value) -> requestBuilder.addHeader(key, value) }

            val listener =
                object : WebSocketListener() {
                    override fun onOpen(
                        webSocket: WebSocket,
                        response: Response,
                    ) {
                        trySend(WebSocketEvent.Open(response))
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        text: String,
                    ) {
                        trySend(WebSocketEvent.Message(text))
                    }

                    override fun onMessage(
                        webSocket: WebSocket,
                        bytes: ByteString,
                    ) {
                        trySend(WebSocketEvent.BinaryMessage(bytes))
                    }

                    override fun onClosing(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        trySend(WebSocketEvent.Closing(code, reason))
                    }

                    override fun onClosed(
                        webSocket: WebSocket,
                        code: Int,
                        reason: String,
                    ) {
                        trySend(WebSocketEvent.Closed(code, reason))
                        close()
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: Response?,
                    ) {
                        trySend(WebSocketEvent.Failure(t, response))
                        close(t)
                    }
                }

            val socket = okHttpClient.newWebSocket(requestBuilder.build(), listener)
            activeSocket = socket

            awaitClose {
                socket.close(NORMAL_CLOSURE_STATUS, "Connection closed by client flow")
                activeSocket = null
            }
        }

    fun sendMessage(text: String): Boolean {
        return activeSocket?.send(text) ?: false
    }

    fun close(
        code: Int = NORMAL_CLOSURE_STATUS,
        reason: String = "Client disconnect",
    ): Boolean {
        val closed = activeSocket?.close(code, reason) ?: false
        activeSocket = null
        return closed
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}
