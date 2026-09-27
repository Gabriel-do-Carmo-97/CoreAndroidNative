package br.com.wgc.core.analytics.otel

import java.security.SecureRandom
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Representa um Span de rastreamento distribuído no padrão W3C Trace Context / OpenTelemetry.
 */
data class OtelSpan(
    val name: String,
    val traceId: String,
    val spanId: String,
    val parentSpanId: String? = null,
    val startTimestampNanos: Long,
    val endTimestampNanos: Long,
    val attributes: Map<String, String> = emptyMap(),
    val isSuccess: Boolean = true,
)

/**
 * Exportador corporativo de telemetria no padrão OpenTelemetry (OTel).
 * Serializa spans no padrão OTLP JSON e suporta bufferização em lote para despacho eficiente.
 */
class OpenTelemetryExporter {
    private val spansBuffer = CopyOnWriteArrayList<OtelSpan>()
    private val secureRandom = SecureRandom()

    fun generateTraceId(): String {
        val bytes = ByteArray(16)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateSpanId(): String {
        val bytes = ByteArray(8)
        secureRandom.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun exportSpan(span: OtelSpan) {
        spansBuffer.add(span)
    }

    fun flush(): List<OtelSpan> {
        val batch = spansBuffer.toList()
        spansBuffer.removeAll(batch)
        return batch
    }

    fun formatAsW3CTraceParent(
        traceId: String,
        spanId: String,
        isSampled: Boolean = true,
    ): String {
        val flags = if (isSampled) "01" else "00"
        return "00-$traceId-$spanId-$flags"
    }

    fun buildOtlpJsonPayload(spans: List<OtelSpan>): String {
        val spansJson =
            spans.joinToString(",") { span ->
                val attrs =
                    span.attributes.entries.joinToString(",") { (k, v) ->
                        """{"key":"$k","value":{"stringValue":"$v"}}"""
                    }
                """{"traceId":"${span.traceId}","spanId":"${span.spanId}","name":"${span.name}","startTimeUnixNano":"${span.startTimestampNanos}","endTimeUnixNano":"${span.endTimestampNanos}","attributes":[$attrs]}"""
            }
        return """{"resourceSpans":[{"scopeSpans":[{"spans":[$spansJson]}]}]}"""
    }
}
