package br.com.wgc.core.analytics.watchdog

import android.os.Handler
import android.os.Looper

/**
 * Relatório de ANR preventivo contendo stack traces e duração do travamento da thread principal.
 */
data class AnrReport(
    val blockedDurationMs: Long,
    val mainThreadStackTrace: String,
    val allThreadsStackTrace: Map<String, String>,
)

/**
 * Watchdog assíncrono para detecção precoce de Application Not Responding (ANR).
 * Dispara alertas preventivos quando a Main Thread congela por mais de [timeoutMs] (padrão 3000ms),
 * antes que o sistema operacional Android exiba o diálogo de encerramento forçado do app (5000ms).
 */
class AnrWatchdog(
    private val timeoutMs: Long = DEFAULT_ANR_TIMEOUT_MS,
    private val mainHandler: Handler = Handler(Looper.getMainLooper()),
    private val onAnrDetected: (AnrReport) -> Unit,
) : Thread("WGC-ANR-Watchdog") {
    companion object {
        const val DEFAULT_ANR_TIMEOUT_MS = 3000L
    }

    @Volatile
    private var tick = 0L

    @Volatile
    private var isRunning = true

    private val ticker =
        Runnable {
            tick = (tick + 1L) % Long.MAX_VALUE
        }

    override fun run() {
        while (isRunning && !isInterrupted) {
            val lastTick = tick
            mainHandler.post(ticker)

            try {
                sleep(timeoutMs)
            } catch (_: InterruptedException) {
                break
            }

            if (tick == lastTick) {
                // Main thread congelou e não executou o ticker no intervalo estipulado
                val mainThread = Looper.getMainLooper().thread
                val mainStack = mainThread.stackTrace.joinToString("\n") { it.toString() }

                val allStacks =
                    Thread
                        .getAllStackTraces()
                        .map { (thread, stack) ->
                            thread.name to stack.joinToString("\n") { it.toString() }
                        }.toMap()

                val report =
                    AnrReport(
                        blockedDurationMs = timeoutMs,
                        mainThreadStackTrace = mainStack,
                        allThreadsStackTrace = allStacks,
                    )
                onAnrDetected(report)
            }
        }
    }

    fun stopWatchdog() {
        isRunning = false
        interrupt()
    }
}
