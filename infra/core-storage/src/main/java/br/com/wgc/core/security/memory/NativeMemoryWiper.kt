package br.com.wgc.core.security.memory

import java.nio.ByteBuffer
import java.util.Arrays

/**
 * Enterprise direct memory zeroizer for off-heap ByteBuffers and sensitive native arrays.
 */
object NativeMemoryWiper {
    /**
     * Zeros out a direct [ByteBuffer] memory region securely.
     */
    fun wipe(buffer: ByteBuffer?) {
        if (buffer == null) return
        val currentPosition = buffer.position()
        val limit = buffer.limit()

        buffer.clear()
        val zeroBlock = ByteArray(CHUNK_SIZE)
        while (buffer.hasRemaining()) {
            val toWrite = minOf(buffer.remaining(), CHUNK_SIZE)
            buffer.put(zeroBlock, 0, toWrite)
        }

        buffer.position(currentPosition)
        buffer.limit(limit)
    }

    /**
     * Zeros out a primitive byte array.
     */
    fun wipe(array: ByteArray?) {
        if (array == null || array.isEmpty()) return
        Arrays.fill(array, 0.toByte())
    }

    /**
     * Zeros out a primitive char array.
     */
    fun wipe(array: CharArray?) {
        if (array == null || array.isEmpty()) return
        Arrays.fill(array, '\u0000')
    }

    private const val CHUNK_SIZE = 1024
}
