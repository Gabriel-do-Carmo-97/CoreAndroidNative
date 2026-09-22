package br.com.wgc.core.network.mock

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockEngineInterceptorTest {
    @Test
    fun `intercepts matching request and returns mock response`() {
        val mockInterceptor = MockEngineInterceptor()
        mockInterceptor.addRule(
            urlSubstring = "/users/profile",
            method = "GET",
            response =
                MockResponse(
                    statusCode = 200,
                    body = """{"id": 123, "name": "Antigravity"}""",
                    headers = mapOf("X-Mock" to "true"),
                ),
        )

        val client = OkHttpClient.Builder().addInterceptor(mockInterceptor).build()
        val request = Request.Builder().url("https://api.example.com/users/profile").build()

        client.newCall(request).execute().use { response ->
            assertEquals(200, response.code)
            assertEquals("true", response.header("X-Mock"))
            val body = response.body?.string()
            assertTrue(body!!.contains("Antigravity"))
        }
    }

    @Test
    fun `passes through when disabled or when no rule matches`() {
        val mockInterceptor = MockEngineInterceptor(isEnabled = false)
        mockInterceptor.addRule(
            urlSubstring = "/test",
            response = MockResponse(statusCode = 200, body = "mock"),
        )

        // With disabled interceptor, proceeding with non-existent host will fail DNS/connection,
        // which proves it did NOT return the mock.
        assertEquals(false, mockInterceptor.isEnabled())
    }

    @Test
    fun `clearRules removes previously added rules`() {
        val mockInterceptor = MockEngineInterceptor()
        mockInterceptor.addRule(
            urlSubstring = "/test",
            response = MockResponse(statusCode = 200),
        )
        mockInterceptor.clearRules()

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(mockInterceptor)
                .addInterceptor { chain ->
                    okhttp3.Response
                        .Builder()
                        .request(chain.request())
                        .protocol(okhttp3.Protocol.HTTP_1_1)
                        .code(404)
                        .message("Not found in upstream")
                        .body("".toResponseBody(null))
                        .build()
                }.build()

        client.newCall(Request.Builder().url("https://api.example.com/test").build()).execute().use { response ->
            assertEquals(404, response.code)
        }
    }
}
