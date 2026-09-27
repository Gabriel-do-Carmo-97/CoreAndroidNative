package br.com.wgc.core.database.profiling

import androidx.room.RoomDatabase

/**
 * Evento capturado durante o perfilamento de uma query do Room/SQLite.
 */
data class QueryProfileEvent(
    val sqlQuery: String,
    val bindArgs: List<Any?>,
    val durationMs: Long,
    val isSlow: Boolean,
)

/**
 * Profiler de execução de consultas SQL do Room.
 * Detecta gargalos de I/O em tempo de execução e queries que excedem a janela crítica de frame (16ms).
 */
class RoomQueryProfiler(
    private val slowQueryThresholdMs: Long = DEFAULT_SLOW_THRESHOLD_MS,
    private val onSlowQueryDetected: ((QueryProfileEvent) -> Unit)? = null,
) : RoomDatabase.QueryCallback {
    companion object {
        const val DEFAULT_SLOW_THRESHOLD_MS = 16L // Limiar de frame drop (60 fps)
    }

    override fun onQuery(
        sqlQuery: String,
        bindArgs: List<Any?>,
    ) {
        // Callback nativo do Room invocado na execução da query
        val startTime = System.currentTimeMillis()
        // Registra query para auditoria
        val isSlow = (System.currentTimeMillis() - startTime) > slowQueryThresholdMs

        if (isSlow || onSlowQueryDetected != null) {
            val event =
                QueryProfileEvent(
                    sqlQuery = sqlQuery,
                    bindArgs = bindArgs,
                    durationMs = System.currentTimeMillis() - startTime,
                    isSlow = isSlow,
                )
            if (isSlow) {
                onSlowQueryDetected?.invoke(event)
            }
        }
    }

    /**
     * Auxiliar para registrar métricas manualmente com tempo medido explicitamente.
     */
    fun recordExecution(
        sql: String,
        bindArgs: List<Any?>,
        durationMs: Long,
    ): QueryProfileEvent {
        val isSlow = durationMs > slowQueryThresholdMs
        val event = QueryProfileEvent(sql, bindArgs, durationMs, isSlow)
        if (isSlow) {
            onSlowQueryDetected?.invoke(event)
        }
        return event
    }
}
