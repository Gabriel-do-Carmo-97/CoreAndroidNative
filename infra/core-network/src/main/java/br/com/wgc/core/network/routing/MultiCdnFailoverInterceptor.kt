package br.com.wgc.core.network.routing

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Enterprise multi-CDN and dynamic gateway failover interceptor.
 *
 * @param primaryHost The main API hostname (e.g. "api.company.com").
 * @param fallbackHost The secondary fallback CDN hostname (e.g. "api-fallback.company.com").
 */
class MultiCdnFailoverInterceptor(
    private val primaryHost: String,
    private val fallbackHost: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            val response = chain.proceed(request)
            if (response.isSuccessful || !shouldFailover(response.code)) {
                response
            } else {
                response.close()
                retryWithFallback(chain, request)
            }
        } catch (ignored: Exception) {
            retryWithFallback(chain, request)
        }
    }

    private fun retryWithFallback(
        chain: Interceptor.Chain,
        originalRequest: okhttp3.Request,
    ): Response {
        val originalUrl = originalRequest.url
        if (originalUrl.host != primaryHost) {
            return chain.proceed(originalRequest)
        }

        val fallbackUrl =
            originalUrl
                .newBuilder()
                .host(fallbackHost)
                .build()

        val fallbackRequest =
            originalRequest
                .newBuilder()
                .url(fallbackUrl)
                .build()

        return chain.proceed(fallbackRequest)
    }

    private fun shouldFailover(statusCode: Int): Boolean {
        return statusCode in HTTP_SERVER_ERROR_START..HTTP_SERVER_ERROR_END
    }

    companion object {
        private const val HTTP_SERVER_ERROR_START = 500
        private const val HTTP_SERVER_ERROR_END = 599
    }
}
