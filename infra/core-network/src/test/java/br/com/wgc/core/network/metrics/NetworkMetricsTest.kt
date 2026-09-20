package br.com.wgc.core.network.metrics

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

class NetworkMetricsTest {
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should capture network metric on successful request`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("OK"))

        var capturedMetric: NetworkMetric? = null
        val client =
            OkHttpClient
                .Builder()
                .eventListenerFactory(
                    NetworkMetricsEventListener.Factory { metric ->
                        capturedMetric = metric
                    },
                ).build()

        val request =
            Request
                .Builder()
                .url(mockWebServer.url("/metrics-test"))
                .build()

        val response = client.newCall(request).execute()
        response.close()

        assertNotNull(capturedMetric)
        assertEquals(200, capturedMetric?.statusCode)
        assertEquals("GET", capturedMetric?.method)
        assertTrue(capturedMetric?.isSuccess == true)
        assertTrue((capturedMetric?.totalDurationMs ?: 0L) >= 0L)
    }
}
