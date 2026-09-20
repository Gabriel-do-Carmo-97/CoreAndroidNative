package br.com.wgc.core.testing

import br.com.wgc.core.testing.fakes.FakeTokenProvider
import br.com.wgc.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class FakeTokenProviderTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `refreshToken should update token and increment counter`() =
        runTest {
            val fakeProvider =
                FakeTokenProvider(
                    initialToken = "initial-token",
                    nextRefreshToken = "new-refreshed-token",
                )

            assertEquals("initial-token", fakeProvider.getCachedAccessToken())
            assertEquals(0, fakeProvider.refreshTokenCallCount)

            val refreshed = fakeProvider.refreshToken()
            assertEquals("new-refreshed-token", refreshed)
            assertEquals("new-refreshed-token", fakeProvider.getCachedAccessToken())
            assertEquals(1, fakeProvider.refreshTokenCallCount)
        }

    @Test
    fun `onSessionExpired should clear token and increment expired counter`() =
        runTest {
            val fakeProvider = FakeTokenProvider(initialToken = "valid-token")

            fakeProvider.onSessionExpired()

            assertNull(fakeProvider.getCachedAccessToken())
            assertEquals(1, fakeProvider.sessionExpiredCalledCount)
        }
}
