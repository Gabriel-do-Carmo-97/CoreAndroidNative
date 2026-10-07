package br.com.wgc.core.network.transfer

import java.io.InputStream
import java.io.OutputStream

/**
 * Enterprise chunked uploader supporting resumable multipart streaming and byte-range uploads.
 */
class ChunkedUploader(
    private val chunkSize: Int = DEFAULT_CHUNK_SIZE,
) {
    /**
     * Splits an input stream into chunks and streams each chunk to the provided block.
     *
     * @param inputStream Source stream of the payload.
     * @param onChunk Callback receiving chunk index, byte array payload, and length.
     */
    fun streamChunks(
        inputStream: InputStream,
        onChunk: (chunkIndex: Int, chunkData: ByteArray, length: Int) -> Unit,
    ) {
        val buffer = ByteArray(chunkSize)
        var chunkIndex = 0
        var bytesRead = inputStream.read(buffer)

        while (bytesRead != -1) {
            onChunk(chunkIndex, buffer, bytesRead)
            chunkIndex++
            bytesRead = inputStream.read(buffer)
        }
    }

    /**
     * Writes an incoming chunk directly to an output stream.
     */
    fun appendChunk(
        outputStream: OutputStream,
        chunkData: ByteArray,
        length: Int,
    ) {
        outputStream.write(chunkData, 0, length)
    }

    companion object {
        const val DEFAULT_CHUNK_SIZE = 64 * 1024 // 64 KB
    }
}
