package br.com.wgc.core.testing.network

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TrafficReplayInterceptorTest {
    @Test
    fun `replays registered interaction accurately without real network connection`() {
        val replayInterceptor = TrafficReplayInterceptor()
        replayInterceptor.register(
            TrafficReplayInterceptor.MockInteraction(
                method = "GET",
                urlPath = "/api/v1/users",
                statusCode = 200,
                responseBody = """[{"id": "1", "name": "Gabriel"}]""",
                headers = mapOf("X-Mock-Source" to "CoreTesting"),
            ),
        )

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(replayInterceptor)
                .build()

        val request =
            Request
                .Builder()
                .url("https://mock-domain-does-not-exist.local/api/v1/users")
                .get()
                .build()

        val response = client.newCall(request).execute()

        assertEquals(200, response.code)
        assertEquals("CoreTesting", response.header("X-Mock-Source"))
        val body = response.body?.string()
        assertNotNull(body)
        assertEquals("""[{"id": "1", "name": "Gabriel"}]""", body)
    }
}
