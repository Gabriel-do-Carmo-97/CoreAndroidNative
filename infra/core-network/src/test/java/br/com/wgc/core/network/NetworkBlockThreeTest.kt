package br.com.wgc.core.network

import br.com.wgc.core.network.flatbuffers.ZeroCopyBufferHelper
import br.com.wgc.core.network.ratelimit.TokenBucketRateLimiter
import br.com.wgc.core.network.transfer.ChunkedUploader
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder

class NetworkBlockThreeTest {
    @Test
    fun testZeroCopyBufferAllocation() {
        val buffer = ZeroCopyBufferHelper.allocateLittleEndian(128)
        assertEquals(ByteOrder.LITTLE_ENDIAN, buffer.order())
        assertTrue(ZeroCopyBufferHelper.hasCapacity(buffer, 64))
        assertFalse(ZeroCopyBufferHelper.hasCapacity(buffer, 256))
    }

    @Test
    fun testTokenBucketRateLimiter() =
        runBlocking {
            val limiter = TokenBucketRateLimiter(capacity = 2, refillRatePerSecond = 0.0)
            assertTrue(limiter.tryAcquire(1))
            assertTrue(limiter.tryAcquire(1))
            assertFalse(limiter.tryAcquire(1)) // Exhausted
        }

    @Test
    fun testChunkedUploaderStreaming() {
        val sampleData = "Hello Chunked World! Testing streaming uploader".toByteArray()
        val inputStream = ByteArrayInputStream(sampleData)
        val outputStream = ByteArrayOutputStream()
        val uploader = ChunkedUploader(chunkSize = 10)

        var chunksCount = 0
        uploader.streamChunks(inputStream) { _, chunkData, length ->
            chunksCount++
            uploader.appendChunk(outputStream, chunkData, length)
        }

        assertTrue(chunksCount > 1)
        assertEquals(String(sampleData), outputStream.toString())
    }
}
