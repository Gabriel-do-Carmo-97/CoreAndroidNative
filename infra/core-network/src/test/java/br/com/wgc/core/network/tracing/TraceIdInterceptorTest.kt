package br.com.wgc.core.network.tracing

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class TraceIdInterceptorTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        okHttpClient =
            OkHttpClient
                .Builder()
                .addInterceptor(TraceIdInterceptor())
                .build()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should inject X-Correlation-ID and W3C traceparent when missing`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val request =
            Request
                .Builder()
                .url(mockWebServer.url("/test"))
                .build()

        val response = okHttpClient.newCall(request).execute()
        response.close()

        val recordedRequest = mockWebServer.takeRequest()
        val correlationId = recordedRequest.getHeader(TraceIdInterceptor.HEADER_CORRELATION_ID)
        val traceparent = recordedRequest.getHeader(TraceIdInterceptor.HEADER_TRACEPARENT)

        assertNotNull(correlationId)
        UUID.fromString(correlationId) // Valida formato UUIDv4 sem lançar exceção

        assertNotNull(traceparent)
        // Validação da sintaxe W3C: 00-{32 hex}-{16 hex}-01
        val regex = Regex("^00-[0-9a-f]{32}-[0-9a-f]{16}-01$")
        assertTrue("traceparent '$traceparent' deve seguir o padrão W3C", regex.matches(traceparent!!))
    }

    @Test
    fun `should preserve existing correlationId and traceparent if already present`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val existingCorrelationId = "custom-correlation-123"
        val existingTraceParent = "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01"

        val request =
            Request
                .Builder()
                .url(mockWebServer.url("/test"))
                .header(TraceIdInterceptor.HEADER_CORRELATION_ID, existingCorrelationId)
                .header(TraceIdInterceptor.HEADER_TRACEPARENT, existingTraceParent)
                .build()

        val response = okHttpClient.newCall(request).execute()
        response.close()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals(existingCorrelationId, recordedRequest.getHeader(TraceIdInterceptor.HEADER_CORRELATION_ID))
        assertEquals(existingTraceParent, recordedRequest.getHeader(TraceIdInterceptor.HEADER_TRACEPARENT))
    }
}
