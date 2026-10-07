package br.com.wgc.core.analytics.metrics

/**
 * Metric measurement type collected for APM monitoring.
 */
enum class MetricType {
    COUNTER,
    GAUGE,
    HISTOGRAM,
}

/**
 * An OpenTelemetry/Prometheus-compatible APM metric point.
 *
 * @property name Metric identifier (e.g., "http_requests_total", "jvm_memory_used_bytes").
 * @property value Quantitative numeric measurement.
 * @property type Category of metric.
 * @property tags Dimensional label attributes.
 * @property timestamp Epoch millis timestamp of observation.
 */
data class ApmMetricPoint(
    val name: String,
    val value: Double,
    val type: MetricType,
    val tags: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * In-memory collector for application performance metrics.
 */
class ApmMetricCollector {
    private val metrics = mutableListOf<ApmMetricPoint>()

    @Synchronized
    fun record(metric: ApmMetricPoint) {
        metrics.add(metric)
    }

    @Synchronized
    fun drain(): List<ApmMetricPoint> {
        val snapshot = metrics.toList()
        metrics.clear()
        return snapshot
    }

    @Synchronized
    fun count(): Int = metrics.size
}
