package br.com.wgc.core.network

import io.mockk.coEvery
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var server: MockWebServer
    private val tokenProvider: TokenProvider = mockk()

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun intercept_whenTokenExists_shouldAttachBearerHeader() {
        coEvery { tokenProvider.getAccessToken() } returns "my_valid_token"

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()

        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))

        val request = Request.Builder()
            .url(server.url("/test"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(200, response.code)

        val recordedRequest = server.takeRequest()
        assertEquals("Bearer my_valid_token", recordedRequest.getHeader("Authorization"))
    }

    @Test
    fun intercept_whenTokenIsNull_shouldNotAttachBearerHeader() {
        coEvery { tokenProvider.getAccessToken() } returns null

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()

        server.enqueue(MockResponse().setResponseCode(200).setBody("OK"))

        val request = Request.Builder()
            .url(server.url("/test"))
            .build()

        client.newCall(request).execute()

        val recordedRequest = server.takeRequest()
        assertEquals(null, recordedRequest.getHeader("Authorization"))
    }
}
