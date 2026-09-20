package br.com.wgc.core.network.metrics

import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress

/**
 * Dados consolidados de telemetria e latência de uma chamada de rede.
 */
data class NetworkMetric(
    val url: String,
    val method: String,
    val statusCode: Int?,
    val totalDurationMs: Long,
    val dnsDurationMs: Long,
    val tlsDurationMs: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null,
)

/**
 * Listener de eventos OkHttp para coleta agnóstica de métricas de APM.
 */
class NetworkMetricsEventListener(
    private val onMetricCaptured: (NetworkMetric) -> Unit,
) : EventListener() {
    private var callStartTime = 0L
    private var dnsStartTime = 0L
    private var dnsDuration = 0L
    private var tlsStartTime = 0L
    private var tlsDuration = 0L
    private var statusCode: Int? = null

    override fun callStart(call: Call) {
        callStartTime = System.currentTimeMillis()
    }

    override fun dnsStart(
        call: Call,
        domainName: String,
    ) {
        dnsStartTime = System.currentTimeMillis()
    }

    override fun dnsEnd(
        call: Call,
        domainName: String,
        inetAddressList: List<InetAddress>,
    ) {
        dnsDuration = System.currentTimeMillis() - dnsStartTime
    }

    override fun secureConnectStart(call: Call) {
        tlsStartTime = System.currentTimeMillis()
    }

    override fun secureConnectEnd(
        call: Call,
        handshake: okhttp3.Handshake?,
    ) {
        tlsDuration = System.currentTimeMillis() - tlsStartTime
    }

    override fun responseHeadersEnd(
        call: Call,
        response: Response,
    ) {
        statusCode = response.code
    }

    override fun callEnd(call: Call) {
        val totalDuration = System.currentTimeMillis() - callStartTime
        val metric =
            NetworkMetric(
                url = call.request().url.toString(),
                method = call.request().method,
                statusCode = statusCode,
                totalDurationMs = totalDuration,
                dnsDurationMs = dnsDuration,
                tlsDurationMs = tlsDuration,
                isSuccess = true,
            )
        onMetricCaptured(metric)
    }

    override fun callFailed(
        call: Call,
        ioe: IOException,
    ) {
        val totalDuration = System.currentTimeMillis() - callStartTime
        val metric =
            NetworkMetric(
                url = call.request().url.toString(),
                method = call.request().method,
                statusCode = statusCode,
                totalDurationMs = totalDuration,
                dnsDurationMs = dnsDuration,
                tlsDurationMs = tlsDuration,
                isSuccess = false,
                errorMessage = ioe.message,
            )
        onMetricCaptured(metric)
    }

    /**
     * Fábrica de [EventListener] para registro com `eventListenerFactory`.
     */
    class Factory(
        private val onMetricCaptured: (NetworkMetric) -> Unit,
    ) : EventListener.Factory {
        override fun create(call: Call): EventListener = NetworkMetricsEventListener(onMetricCaptured)
    }
}
