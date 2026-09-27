package br.com.wgc.core.testing.chaos

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChaosEngineeringInterceptorTest {
    private lateinit var server: MockWebServer
    private lateinit var chaosInterceptor: ChaosEngineeringInterceptor
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        chaosInterceptor = ChaosEngineeringInterceptor()
        client =
            OkHttpClient
                .Builder()
                .addInterceptor(chaosInterceptor)
                .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `intercept injects failure when failureRate is 100 percent`() {
        chaosInterceptor.failureRate = 1.0f
        chaosInterceptor.failureStatusCode = 503

        val request = Request.Builder().url(server.url("/test")).build()
        val response = client.newCall(request).execute()

        assertEquals(503, response.code)
        assertTrue(response.body?.string()?.contains("Chaos") == true)
    }

    @Test
    fun `intercept passes normally when failureRate is zero`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"status":"ok"}"""))

        val request = Request.Builder().url(server.url("/normal")).build()
        val response = client.newCall(request).execute()

        assertEquals(200, response.code)
        assertEquals("""{"status":"ok"}""", response.body?.string())
    }

    @Test
    fun `intercept corrupts payload when enabled`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"valid":"payload"}"""))
        chaosInterceptor.shouldCorruptPayload = true

        val request = Request.Builder().url(server.url("/corrupt")).build()
        val response = client.newCall(request).execute()

        assertEquals(200, response.code)
        assertTrue(response.body?.string()?.contains("CORRUPTED_CHAOS_PAYLOAD") == true)
    }
}
