package br.com.wgc.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_PREFIX = "Bearer "

/**
 * Interceptor do OkHttp que injeta de forma transparente o cabeçalho HTTP `Authorization: Bearer <token>`
 * obtido a partir de [TokenProvider] caso a requisição não possua autenticação explícita já definida.
 *
 * @property tokenProvider Provedor responsável por fornecer o token de autenticação atual.
 */
class AuthInterceptor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { tokenProvider.getAccessToken() }

        val request = if (!token.isNullOrBlank() && originalRequest.header(AUTHORIZATION_HEADER) == null) {
            originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}
