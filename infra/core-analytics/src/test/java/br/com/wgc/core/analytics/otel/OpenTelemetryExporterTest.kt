package br.com.wgc.core.analytics.otel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenTelemetryExporterTest {
    private val exporter = OpenTelemetryExporter()

    @Test
    fun `generateTraceId and SpanId produce valid hex sizes`() {
        val traceId = exporter.generateTraceId()
        val spanId = exporter.generateSpanId()

        assertEquals(32, traceId.length)
        assertEquals(16, spanId.length)
    }

    @Test
    fun `formatAsW3CTraceParent formats correctly`() {
        val traceId = "4bf92f3577b34da6a3ce929d0e0e4736"
        val spanId = "00f067aa0ba902b7"
        val header = exporter.formatAsW3CTraceParent(traceId, spanId)

        assertEquals("00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01", header)
    }

    @Test
    fun `exportSpan and flush serializes spans to OTLP payload`() {
        val span =
            OtelSpan(
                name = "HTTP GET /users",
                traceId = "12345678901234567890123456789012",
                spanId = "1234567890123456",
                startTimestampNanos = 1000L,
                endTimestampNanos = 2000L,
                attributes = mapOf("http.status_code" to "200"),
            )
        exporter.exportSpan(span)

        val flushed = exporter.flush()
        assertEquals(1, flushed.size)
        assertTrue(exporter.flush().isEmpty())

        val otlpJson = exporter.buildOtlpJsonPayload(flushed)
        assertTrue(otlpJson.contains("HTTP GET /users"))
        assertTrue(otlpJson.contains("http.status_code"))
    }
}
