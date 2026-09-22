package br.com.wgc.core.network.idempotency

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IdempotencyKeyInterceptorTest {
    @Test
    fun `attaches idempotency key to POST requests`() {
        var interceptedKey: String? = null
        val interceptor =
            IdempotencyKeyInterceptor(
                keyGenerator = { "fixed-test-uuid-1234" },
            )

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    interceptedKey = chain.request().header("X-Idempotency-Key")
                    Response
                        .Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("{}".toResponseBody("application/json".toMediaType()))
                        .build()
                }.build()

        val request =
            Request
                .Builder()
                .url("https://api.example.com/orders")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

        client.newCall(request).execute().close()

        assertEquals("fixed-test-uuid-1234", interceptedKey)
    }

    @Test
    fun `does not attach idempotency key to GET requests`() {
        var interceptedKey: String? = null
        val interceptor = IdempotencyKeyInterceptor()

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    interceptedKey = chain.request().header("X-Idempotency-Key")
                    Response
                        .Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("{}".toResponseBody("application/json".toMediaType()))
                        .build()
                }.build()

        val request =
            Request
                .Builder()
                .url("https://api.example.com/orders")
                .get()
                .build()
        client.newCall(request).execute().close()

        assertNull(interceptedKey)
    }

    @Test
    fun `preserves existing idempotency key if already present in request`() {
        var interceptedKey: String? = null
        val interceptor =
            IdempotencyKeyInterceptor(
                keyGenerator = { "should-not-be-used" },
            )

        val client =
            OkHttpClient
                .Builder()
                .addInterceptor(interceptor)
                .addInterceptor { chain ->
                    interceptedKey = chain.request().header("X-Idempotency-Key")
                    Response
                        .Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("{}".toResponseBody("application/json".toMediaType()))
                        .build()
                }.build()

        val request =
            Request
                .Builder()
                .url("https://api.example.com/orders")
                .header("X-Idempotency-Key", "pre-existing-key-4321")
                .post("{}".toRequestBody("application/json".toMediaType()))
                .build()

        client.newCall(request).execute().close()

        assertEquals("pre-existing-key-4321", interceptedKey)
    }
}
