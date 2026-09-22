package br.com.wgc.core.network.resilience

import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class CircuitBreakerInterceptorTest {
    private val request = Request.Builder().url("https://api.example.com/test").build()

    @Test
    fun `successful responses keep circuit CLOSED`() {
        val interceptor = CircuitBreakerInterceptor(failureThreshold = 3, resetTimeoutMs = 1000L)
        val chain = mockk<Interceptor.Chain>()

        val okResponse =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()

        every { chain.request() } returns request
        every { chain.proceed(any()) } returns okResponse

        val response = interceptor.intercept(chain)
        assertEquals(200, response.code)
        assertEquals(CircuitState.CLOSED, interceptor.currentState())
        assertEquals(0, interceptor.currentFailureCount())
    }

    @Test
    fun `consecutive 500 errors open the circuit`() {
        val interceptor = CircuitBreakerInterceptor(failureThreshold = 2, resetTimeoutMs = 500L)
        val chain = mockk<Interceptor.Chain>()

        val errorResponse =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(503)
                .message("Service Unavailable")
                .build()

        every { chain.request() } returns request
        every { chain.proceed(any()) } returns errorResponse

        // 1st failure
        interceptor.intercept(chain)
        assertEquals(CircuitState.CLOSED, interceptor.currentState())
        assertEquals(1, interceptor.currentFailureCount())

        // 2nd failure -> opens circuit
        interceptor.intercept(chain)
        assertEquals(CircuitState.OPEN, interceptor.currentState())

        // Next call should be rejected immediately without calling chain.proceed
        assertThrows(CircuitBreakerOpenException::class.java) {
            interceptor.intercept(chain)
        }
    }

    @Test
    fun `network IOException increments failures and opens circuit`() {
        val interceptor = CircuitBreakerInterceptor(failureThreshold = 1, resetTimeoutMs = 500L)
        val chain = mockk<Interceptor.Chain>()

        every { chain.request() } returns request
        every { chain.proceed(any()) } throws IOException("Connection timeout")

        assertThrows(IOException::class.java) {
            interceptor.intercept(chain)
        }
        assertEquals(CircuitState.OPEN, interceptor.currentState())
    }

    @Test
    fun `reset restores circuit to CLOSED`() {
        val interceptor = CircuitBreakerInterceptor()
        interceptor.forceOpen()
        assertEquals(CircuitState.OPEN, interceptor.currentState())

        interceptor.reset()
        assertEquals(CircuitState.CLOSED, interceptor.currentState())
        assertEquals(0, interceptor.currentFailureCount())
    }
}
