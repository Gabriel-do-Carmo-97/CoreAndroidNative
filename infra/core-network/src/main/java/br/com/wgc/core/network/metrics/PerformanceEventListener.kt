package br.com.wgc.core.network.metrics

import okhttp3.Call
import okhttp3.EventListener
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Performance event telemetry captured during OkHttp call execution.
 *
 * @property dnsDurationMs Time taken in milliseconds for DNS resolution.
 * @property connectDurationMs Time taken in milliseconds for TCP/TLS handshake.
 * @property totalDurationMs Total elapsed time for full request-response lifecycle.
 */
data class HttpCallTelemetry(
    val callUrl: String,
    val dnsDurationMs: Long,
    val connectDurationMs: Long,
    val totalDurationMs: Long,
    val isSuccessful: Boolean,
)

/**
 * Listener capturing timing metrics across DNS, TLS, and HTTP phases.
 */
class PerformanceEventListener(
    private val onMetricsCaptured: (HttpCallTelemetry) -> Unit,
) : EventListener() {
    private var callStartTime: Long = 0L
    private var dnsStartTime: Long = 0L
    private var dnsDuration: Long = 0L
    private var connectStartTime: Long = 0L
    private var connectDuration: Long = 0L

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

    override fun connectStart(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
    ) {
        connectStartTime = System.currentTimeMillis()
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: okhttp3.Protocol?,
    ) {
        connectDuration = System.currentTimeMillis() - connectStartTime
    }

    override fun callEnd(call: Call) {
        val total = System.currentTimeMillis() - callStartTime
        onMetricsCaptured(
            HttpCallTelemetry(
                callUrl = call.request().url.toString(),
                dnsDurationMs = dnsDuration,
                connectDurationMs = connectDuration,
                totalDurationMs = total,
                isSuccessful = true,
            ),
        )
    }

    override fun callFailed(
        call: Call,
        ioe: IOException,
    ) {
        val total = System.currentTimeMillis() - callStartTime
        onMetricsCaptured(
            HttpCallTelemetry(
                callUrl = call.request().url.toString(),
                dnsDurationMs = dnsDuration,
                connectDurationMs = connectDuration,
                totalDurationMs = total,
                isSuccessful = false,
            ),
        )
    }
}
