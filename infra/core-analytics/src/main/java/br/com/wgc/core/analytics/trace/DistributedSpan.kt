package br.com.wgc.core.analytics.trace

import java.util.UUID

/**
 * Distributed tracing span compliant with W3C Trace Context specifications.
 *
 * @property traceId 32-character lowercase hex string identifying the overall trace.
 * @property spanId 16-character lowercase hex string identifying the current operation span.
 * @property parentSpanId Optional parent span identifier.
 * @property name Human-readable span operation name.
 * @property startTimeMs Start timestamp.
 * @property endTimeMs End timestamp.
 */
data class DistributedSpan(
    val traceId: String,
    val spanId: String,
    val parentSpanId: String? = null,
    val name: String,
    val startTimeMs: Long = System.currentTimeMillis(),
    var endTimeMs: Long? = null,
    val attributes: MutableMap<String, String> = mutableMapOf(),
) {
    /**
     * Formats this span into a standard W3C `traceparent` header value.
     */
    fun toW3cTraceparent(): String {
        val parent = spanId.padStart(16, '0')
        return "00-$traceId-$parent-01"
    }

    /**
     * Marks span as completed.
     */
    fun end() {
        endTimeMs = System.currentTimeMillis()
    }

    companion object {
        fun newRootSpan(name: String): DistributedSpan {
            val traceId = UUID.randomUUID().toString().replace("-", "")
            val spanId =
                UUID
                    .randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 16)
            return DistributedSpan(traceId = traceId, spanId = spanId, name = name)
        }
    }
}
