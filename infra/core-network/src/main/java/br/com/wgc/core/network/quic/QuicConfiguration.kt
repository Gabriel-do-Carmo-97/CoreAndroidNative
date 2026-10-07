package br.com.wgc.core.network.quic

/**
 * Supported HTTP wire protocol variants negotiated during ALPN.
 */
enum class CoreHttpProtocol {
    HTTP_1_1,
    HTTP_2,
    HTTP_3_QUIC,
}

/**
 * Enterprise QUIC / HTTP/3 connection configuration parameters.
 *
 * @property isZeroRttEnabled Enables 0-RTT early data resumption.
 * @property connectionMigrationEnabled Enables client IP/network handover without tearing down TCP/TLS session.
 * @property maxIdleTimeoutMs Maximum UDP connection idle duration before keep-alive packet.
 */
data class QuicConfiguration(
    val isZeroRttEnabled: Boolean = true,
    val connectionMigrationEnabled: Boolean = true,
    val maxIdleTimeoutMs: Long = DEFAULT_MAX_IDLE_TIMEOUT_MS,
) {
    companion object {
        const val DEFAULT_MAX_IDLE_TIMEOUT_MS = 30000L
    }
}
