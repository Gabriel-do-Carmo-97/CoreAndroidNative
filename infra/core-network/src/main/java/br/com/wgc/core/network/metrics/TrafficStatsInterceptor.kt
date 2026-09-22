package br.com.wgc.core.network.metrics

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Acumulador de consumo de dados e tráfego de rede (Upload e Download).
 */
@Singleton
class NetworkTrafficStats
    @Inject
    constructor() {
        private val bytesSent = AtomicLong(0L)
        private val bytesReceived = AtomicLong(0L)

        /**
         * Registra bytes enviados e recebidos em uma operação de rede.
         */
        fun recordTraffic(
            sent: Long,
            received: Long,
        ) {
            if (sent > 0) bytesSent.addAndGet(sent)
            if (received > 0) bytesReceived.addAndGet(received)
        }

        /** Retorna o total de bytes enviados acumulados (Upload). */
        fun getBytesSent(): Long = bytesSent.get()

        /** Retorna o total de bytes recebidos acumulados (Download). */
        fun getBytesReceived(): Long = bytesReceived.get()

        /** Retorna o total combinado de tráfego de rede consumido. */
        fun getTotalBytes(): Long = bytesSent.get() + bytesReceived.get()

        /** Zera os contadores acumulados de tráfego. */
        fun reset() {
            bytesSent.set(0L)
            bytesReceived.set(0L)
        }
    }

/**
 * Interceptor OkHttp para contabilização precisa do consumo de dados (Upload / Download) por requisição.
 */
class TrafficStatsInterceptor(
    private val trafficStats: NetworkTrafficStats,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Contabiliza upload (tamanho do body da requisição)
        val requestBody = request.body
        val sentBytes = requestBody?.contentLength()?.coerceAtLeast(0L) ?: 0L

        val response = chain.proceed(request)
        val responseBody = response.body ?: return response

        // Envolve o ResponseBody em um wrapper que contabiliza bytes conforme eles são lidos
        val countingBody =
            object : ResponseBody() {
                private var source: BufferedSource? = null

                override fun contentType(): MediaType? = responseBody.contentType()

                override fun contentLength(): Long = responseBody.contentLength()

                override fun source(): BufferedSource {
                    if (source == null) {
                        source = CountingSource(responseBody.source(), sentBytes, trafficStats).buffer()
                    }
                    return source!!
                }
            }

        return response.newBuilder().body(countingBody).build()
    }

    private class CountingSource(
        delegate: Source,
        private val sentBytes: Long,
        private val stats: NetworkTrafficStats,
    ) : ForwardingSource(delegate) {
        private var totalRead = 0L
        private var recordedSent = false

        override fun read(
            sink: Buffer,
            byteCount: Long,
        ): Long {
            if (!recordedSent) {
                stats.recordTraffic(sentBytes, 0L)
                recordedSent = true
            }

            val bytesRead = super.read(sink, byteCount)
            if (bytesRead > 0) {
                totalRead += bytesRead
                stats.recordTraffic(0L, bytesRead)
            }
            return bytesRead
        }
    }
}
