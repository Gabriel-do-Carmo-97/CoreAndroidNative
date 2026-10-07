package br.com.wgc.core.network.grpc

import kotlinx.coroutines.flow.Flow

/**
 * Metadata key-value headers for gRPC wire calls.
 */
data class GrpcMetadata(
    val headers: Map<String, String> = emptyMap(),
)

/**
 * Enterprise abstraction for gRPC / Protobuf RPC unary and bidirectional streaming calls.
 */
interface GrpcClientBridge {
    /**
     * Executes a unary RPC call sending request payload [requestBytes] to method [methodPath].
     */
    suspend fun executeUnaryRpc(
        methodPath: String,
        requestBytes: ByteArray,
        metadata: GrpcMetadata = GrpcMetadata(),
    ): ByteArray

    /**
     * Executes a server-streaming RPC call yielding a flow of serialized byte arrays.
     */
    fun executeServerStreamingRpc(
        methodPath: String,
        requestBytes: ByteArray,
        metadata: GrpcMetadata = GrpcMetadata(),
    ): Flow<ByteArray>
}

/**
 * Result of a gRPC channel connectivity state check.
 */
enum class GrpcConnectivityState {
    CONNECTING,
    READY,
    TRANSIENT_FAILURE,
    IDLE,
    SHUTDOWN,
}
