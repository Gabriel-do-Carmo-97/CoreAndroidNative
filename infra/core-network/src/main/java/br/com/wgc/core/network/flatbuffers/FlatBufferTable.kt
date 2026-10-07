package br.com.wgc.core.network.flatbuffers

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Enterprise contract for zero-copy binary serialization (FlatBuffers / Cap'n Proto).
 */
interface FlatBufferTable<T> {
    /**
     * Initializes the table instance from a direct or array-backed [ByteBuffer] without object allocation.
     */
    fun initFromBuffer(
        buffer: ByteBuffer,
        offset: Int,
    ): T

    /**
     * Serializes this object model into an existing [ByteBuffer].
     */
    fun serializeToBuffer(
        target: ByteBuffer,
        data: T,
    ): Int
}

/**
 * Helper utility for zero-copy ByteBuffer byte order and alignment checks.
 */
object ZeroCopyBufferHelper {
    /**
     * Creates a Little-Endian ordered ByteBuffer optimal for FlatBuffers memory alignment.
     */
    fun allocateLittleEndian(capacity: Int): ByteBuffer {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.LITTLE_ENDIAN)
    }

    /**
     * Verifies buffer has sufficient remaining capacity without resizing.
     */
    fun hasCapacity(
        buffer: ByteBuffer,
        requiredBytes: Int,
    ): Boolean {
        return buffer.remaining() >= requiredBytes
    }
}
