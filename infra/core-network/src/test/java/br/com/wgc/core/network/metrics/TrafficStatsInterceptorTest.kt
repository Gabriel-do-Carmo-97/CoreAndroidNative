package br.com.wgc.core.network.metrics

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

class TrafficStatsInterceptorTest {
    @Test
    fun `traffic stats records upload and download bytes correctly`() {
        val stats = NetworkTrafficStats()
        val interceptor = TrafficStatsInterceptor(stats)

        val responseContent = "Response Payload 1234567890" // 27 bytes
        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    Response
                        .Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(responseContent.toByteArray().toResponseBody("text/plain".toMediaType()))
                        .build()
                }.build()

        val uploadContent = "12345" // 5 bytes
        val request =
            Request
                .Builder()
                .url("https://api.example.com/data")
                .post(uploadContent.toRequestBody("text/plain".toMediaType()))
                .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
            assertEquals(responseContent, body)
        }

        assertEquals(5L, stats.getBytesSent())
        assertEquals(responseContent.toByteArray().size.toLong(), stats.getBytesReceived())
        assertEquals(5L + responseContent.toByteArray().size, stats.getTotalBytes())

        stats.reset()
        assertEquals(0L, stats.getTotalBytes())
    }
}
