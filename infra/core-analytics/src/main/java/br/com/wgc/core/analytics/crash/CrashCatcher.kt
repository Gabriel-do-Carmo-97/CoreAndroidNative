package br.com.wgc.core.analytics.crash

import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.UUID

/**
 * Modelo estruturado de relatório de falha não tratada (Uncaught Exception).
 */
data class CrashReport(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val threadName: String,
    val exceptionClass: String,
    val message: String?,
    val stackTrace: String,
)

/**
 * Interceptor global de travamentos não capturados da aplicação.
 * Persiste relatórios atômicos em disco antes do encerramento forçado do processo,
 * permitindo que sejam despachados para a esteira de telemetria no próximo arranque do app.
 */
class CrashCatcher(
    private val reportsDir: File,
    private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler(),
) : Thread.UncaughtExceptionHandler {
    init {
        reportsDir.mkdirs()
    }

    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable,
    ) {
        try {
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))

            val report =
                CrashReport(
                    threadName = thread.name,
                    exceptionClass = throwable.javaClass.name,
                    message = throwable.message,
                    stackTrace = sw.toString(),
                )

            val file = File(reportsDir, "crash_${report.id}.json")
            val payload =
                """
                {
                    "id": "${report.id}",
                    "timestamp": ${report.timestamp},
                    "threadName": "${report.threadName}",
                    "exceptionClass": "${report.exceptionClass}",
                    "message": "${report.message?.replace("\"", "\\\"") ?: ""}",
                    "stackTrace": "${report.stackTrace.replace("\n", "\\n").replace("\"", "\\\"")}"
                }
                """.trimIndent()

            file.writeText(payload)
        } catch (_: Throwable) {
            // Garante que falha na persistência nunca impeça o encerramento do processo
        } finally {
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun getPendingReports(): List<File> {
        return reportsDir
            .listFiles { file -> file.name.startsWith("crash_") && file.name.endsWith(".json") }
            ?.toList() ?: emptyList()
    }

    fun clearReports() {
        getPendingReports().forEach { it.delete() }
    }
}
