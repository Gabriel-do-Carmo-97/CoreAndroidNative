package br.com.wgc.core.network.webrtc

import kotlinx.coroutines.flow.Flow

/**
 * Signaling message types exchanged during WebRTC peer connection negotiation.
 */
sealed interface WebRtcSignalingMessage {
    data class Offer(
        val sdp: String,
    ) : WebRtcSignalingMessage

    data class Answer(
        val sdp: String,
    ) : WebRtcSignalingMessage

    data class IceCandidate(
        val sdpMid: String,
        val sdpMLineIndex: Int,
        val candidate: String,
    ) : WebRtcSignalingMessage
}

/**
 * Enterprise contract for WebRTC peer-to-peer data channel and media streaming.
 */
interface WebRtcPeerBridge {
    /**
     * Emits signaling events for outbound negotiation.
     */
    val outboundSignaling: Flow<WebRtcSignalingMessage>

    /**
     * Ingests inbound signaling message from remote peer.
     */
    suspend fun handleInboundSignaling(message: WebRtcSignalingMessage)

    /**
     * Sends raw binary bytes across WebRTC RTCDataChannel.
     */
    fun sendData(data: ByteArray): Boolean

    /**
     * Closes the peer connection and releases native media streams.
     */
    fun close()
}
