package br.com.wgc.core.testing.fakes

import br.com.wgc.core.network.TokenProvider

/**
 * Implementação em memória de [TokenProvider] para uso em testes unitários e de integração.
 */
class FakeTokenProvider(
    private var initialToken: String? = "test-fake-token-123",
    var nextRefreshToken: String? = "test-refreshed-token-456",
) : TokenProvider {
    var sessionExpiredCalledCount: Int = 0
        private set

    var refreshTokenCallCount: Int = 0
        private set

    override fun getCachedAccessToken(): String? = initialToken

    override suspend fun getAccessToken(): String? = initialToken

    override suspend fun refreshToken(): String? {
        refreshTokenCallCount++
        initialToken = nextRefreshToken
        return nextRefreshToken
    }

    override suspend fun onSessionExpired() {
        sessionExpiredCalledCount++
        initialToken = null
    }

    /**
     * Atualiza o token armazenado em memória.
     */
    fun setToken(token: String?) {
        initialToken = token
    }
}
