package br.com.wgc.core.network.websocket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Enterprise contract for full-duplex WebSocket streaming with heartbeat and auto-reconnect.
 */
interface AdvancedWebSocketClient {
    /**
     * Active connection state of the WebSocket.
     */
    val connectionState: StateFlow<Boolean>

    /**
     * Stream of incoming text messages.
     */
    val incomingMessages: Flow<String>

    /**
     * Connects to the given WebSocket URI.
     */
    fun connect(url: String)

    /**
     * Sends a text message across the socket.
     */
    fun send(text: String): Boolean

    /**
     * Sends a binary message payload across the socket.
     */
    fun send(bytes: ByteArray): Boolean

    /**
     * Disconnects the socket gracefully with the given code and reason.
     */
    fun disconnect(
        code: Int = NORMAL_CLOSURE_STATUS,
        reason: String? = null,
    )

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}
