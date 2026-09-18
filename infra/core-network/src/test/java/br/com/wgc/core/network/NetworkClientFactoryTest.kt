package br.com.wgc.core.network

import io.mockk.mockk
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkClientFactoryTest {
    @Test
    fun createOkHttpClient_withDefaults_shouldConfigure15sTimeouts() {
        val client = NetworkClientFactory.createOkHttpClient()

        assertEquals(15000, client.connectTimeoutMillis)
        assertEquals(15000, client.readTimeoutMillis)
        assertEquals(15000, client.writeTimeoutMillis)
    }

    @Test
    fun createOkHttpClient_withCustomTimeouts_shouldApplyParameters() {
        val client =
            NetworkClientFactory.createOkHttpClient(
                connectTimeoutSeconds = 30L,
                readTimeoutSeconds = 45L,
                writeTimeoutSeconds = 60L,
            )

        assertEquals(30000, client.connectTimeoutMillis)
        assertEquals(45000, client.readTimeoutMillis)
        assertEquals(60000, client.writeTimeoutMillis)
    }

    @Test
    fun createOkHttpClient_withTokenProvider_shouldAddAuthInterceptorAndAuthenticator() {
        val tokenProvider: TokenProvider = mockk(relaxed = true)

        val client = NetworkClientFactory.createOkHttpClient(tokenProvider = tokenProvider)

        val hasAuthInterceptor = client.interceptors.any { it is AuthInterceptor }
        assertTrue("Expected AuthInterceptor to be added", hasAuthInterceptor)
        assertTrue("Expected TokenAuthenticator to be configured", client.authenticator is TokenAuthenticator)
    }

    @Test
    fun createOkHttpClient_withLoggingEnabled_shouldAddHttpLoggingInterceptor() {
        val client = NetworkClientFactory.createOkHttpClient(enableLogging = true)

        val hasLoggingInterceptor = client.interceptors.any { it is HttpLoggingInterceptor }
        assertTrue("Expected HttpLoggingInterceptor to be added", hasLoggingInterceptor)
    }

    @Test
    fun createOkHttpClient_withPins_shouldConfigureCertificatePinner() {
        val pins = mapOf("api.example.com" to listOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="))

        val client = NetworkClientFactory.createOkHttpClient(certificatePins = pins)

        assertNotNull(client.certificatePinner)
        assertEquals(1, client.certificatePinner.pins.size)
    }
}
