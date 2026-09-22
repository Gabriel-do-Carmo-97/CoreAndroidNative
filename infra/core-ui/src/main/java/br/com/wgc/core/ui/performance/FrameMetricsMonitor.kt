package br.com.wgc.core.ui.performance

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.FrameMetrics
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Dados de métrica de renderização de um frame com lentidão (jank) detectado.
 *
 * @property totalDurationMs Tempo total de renderização do frame em milissegundos.
 * @property isFrozen Indica se o frame foi congelado (duração >= 700ms, critério oficial do Google Vitals).
 * @property timestamp Horário em que o jank foi registrado.
 */
data class JankReport(
    val totalDurationMs: Long,
    val isFrozen: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Monitor agnóstico de Jank e taxas de renderização utilizando [Window.OnFrameMetricsAvailableListener].
 *
 * Detecta frames que excedem a meta de 60Hz/120Hz (16ms) ou frames congelados (> 700ms) sem
 * impactar a thread de UI principal.
 *
 * @param jankThresholdMs Limiar em milissegundos para classificar o frame como lentidão (default: 16ms).
 */
class FrameMetricsMonitor(
    private val jankThresholdMs: Long = DEFAULT_JANK_THRESHOLD_MS,
) {
    private var listener: Window.OnFrameMetricsAvailableListener? = null
    private var isMonitoring: Boolean = false

    /**
     * Inicia o monitoramento de frames na janela especificada.
     *
     * @param window Janela da Activity a ser monitorada.
     * @param handler Handler para despacho dos eventos (default: thread de background ou Main).
     * @param onJankDetected Callback disparado quando um frame exceder [jankThresholdMs].
     */
    fun startMonitoring(
        window: Window,
        handler: Handler = Handler(Looper.getMainLooper()),
        onJankDetected: (JankReport) -> Unit,
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        if (isMonitoring) return

        val frameListener =
            Window.OnFrameMetricsAvailableListener { _, frameMetrics, _ ->
                val durationNs = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
                val durationMs = durationNs / NANOS_PER_MILLI
                if (durationMs >= jankThresholdMs) {
                    val isFrozen = durationMs >= FROZEN_THRESHOLD_MS
                    onJankDetected(
                        JankReport(
                            totalDurationMs = durationMs,
                            isFrozen = isFrozen,
                        ),
                    )
                }
            }

        this.listener = frameListener
        window.addOnFrameMetricsAvailableListener(frameListener, handler)
        this.isMonitoring = true
    }

    /**
     * Encerra o monitoramento de frames na janela especificada.
     */
    fun stopMonitoring(window: Window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return
        listener?.let {
            try {
                window.removeOnFrameMetricsAvailableListener(it)
            } catch (
                @Suppress("SwallowedException") _: IllegalArgumentException,
            ) {
                // Listener já removido
            }
        }
        listener = null
        isMonitoring = false
    }

    /**
     * Retorna se o monitoramento está atualmente ativo.
     */
    fun isMonitoring(): Boolean = isMonitoring

    companion object {
        const val DEFAULT_JANK_THRESHOLD_MS: Long = 16L
        const val FROZEN_THRESHOLD_MS: Long = 700L
        private const val NANOS_PER_MILLI: Long = 1_000_000L
    }
}

/**
 * Composable Effect para monitoramento automático de Jank durante o ciclo de vida da tela Compose.
 */
@Composable
fun TrackJankEffect(
    thresholdMs: Long = FrameMetricsMonitor.DEFAULT_JANK_THRESHOLD_MS,
    onJankDetected: (JankReport) -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return

    DisposableEffect(activity, thresholdMs) {
        val monitor = FrameMetricsMonitor(thresholdMs)
        monitor.startMonitoring(activity.window, onJankDetected = onJankDetected)

        onDispose {
            monitor.stopMonitoring(activity.window)
        }
    }
}
