package br.com.wgc.core.network

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {

    private lateinit var server: MockWebServer
    private val tokenProvider: TokenProvider = mockk(relaxed = true)

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
    fun authenticate_when401Occurs_shouldCallRefreshTokenAndRetry() {
        coEvery { tokenProvider.getAccessToken() } returns "old_token"
        coEvery { tokenProvider.refreshToken() } returns "refreshed_token"

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .authenticator(TokenAuthenticator(tokenProvider))
            .build()

        // 1st request -> 401 Unauthorized
        server.enqueue(MockResponse().setResponseCode(401))
        // 2nd request (retry) -> 200 OK
        server.enqueue(MockResponse().setResponseCode(200).setBody("Success"))

        val request = Request.Builder()
            .url(server.url("/secure"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(200, response.code)

        val firstRequest = server.takeRequest()
        assertEquals("Bearer old_token", firstRequest.getHeader("Authorization"))

        val secondRequest = server.takeRequest()
        assertEquals("Bearer refreshed_token", secondRequest.getHeader("Authorization"))

        coVerify(exactly = 1) { tokenProvider.refreshToken() }
    }

    @Test
    fun authenticate_whenRefreshFails_shouldTriggerOnSessionExpired() {
        coEvery { tokenProvider.getAccessToken() } returns "expired_token"
        coEvery { tokenProvider.refreshToken() } returns null

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .authenticator(TokenAuthenticator(tokenProvider))
            .build()

        server.enqueue(MockResponse().setResponseCode(401))

        val request = Request.Builder()
            .url(server.url("/secure"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(401, response.code)

        coVerify(atLeast = 1) { tokenProvider.onSessionExpired() }
    }
}
