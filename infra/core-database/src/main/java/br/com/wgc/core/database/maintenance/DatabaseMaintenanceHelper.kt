package br.com.wgc.core.database.maintenance

import android.content.Context
import androidx.room.RoomDatabase
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Relatório de diagnóstico pós-execução das rotinas de manutenção da base de dados.
 *
 * @property initialSizeBytes Tamanho consolidado do arquivo de banco antes do procedimento.
 * @property finalSizeBytes Tamanho do banco após compressão e vacuum.
 * @property durationMs Tempo total decorrido na operação em milissegundos.
 * @property isSuccessful `true` se todas as etapas executaram sem erros.
 * @property error Exceção capturada em caso de falha no procedimento.
 */
data class MaintenanceReport(
    val initialSizeBytes: Long,
    val finalSizeBytes: Long,
    val durationMs: Long,
    val isSuccessful: Boolean,
    val error: Throwable? = null,
)

/**
 * Assistente de manutenção, desfragmentação e otimização de I/O para bancos de dados Room / SQLite.
 *
 * Executa comandos PRAGMA de baixa intensidade para evitar degradação de performance
 * em bancos antigos com alto volume de escritas e deleções (`VACUUM`, `wal_checkpoint(TRUNCATE)` e `ANALYZE`).
 */
@Singleton
class DatabaseMaintenanceHelper
    @Inject
    constructor() {
        /**
         * Força o esvaziamento e truncamento do log Write-Ahead (WAL), integrando as páginas pendentes ao arquivo principal.
         */
        fun checkpoint(database: RoomDatabase) {
            database.openHelper.writableDatabase.execSQL("PRAGMA wal_checkpoint(TRUNCATE);")
        }

        /**
         * Reconstrói todo o arquivo de banco de dados, liberando páginas desfragmentadas e reduzindo o consumo de disco.
         */
        fun vacuum(database: RoomDatabase) {
            database.openHelper.writableDatabase.execSQL("VACUUM;")
        }

        /**
         * Atualiza os índices estatísticos internos do otimizador de consultas SQLite (`sqlite_stat1`).
         */
        fun analyze(database: RoomDatabase) {
            database.openHelper.writableDatabase.execSQL("ANALYZE;")
        }

        /**
         * Calcula o tamanho total em disco ocupado pela base de dados (arquivo `.db` + `-wal` + `-shm`).
         */
        fun getDatabaseSizeBytes(
            context: Context,
            databaseName: String,
        ): Long {
            val dbFile = context.getDatabasePath(databaseName)
            if (!dbFile.exists()) return 0L

            var total = dbFile.length()
            val walFile = File(dbFile.parentFile, "$databaseName-wal")
            if (walFile.exists()) total += walFile.length()

            val shmFile = File(dbFile.parentFile, "$databaseName-shm")
            if (shmFile.exists()) total += shmFile.length()

            return total
        }

        /**
         * Executa o ciclo completo de manutenção: Checkpoint WAL -> VACUUM -> ANALYZE.
         */
        @Suppress("TooGenericExceptionCaught")
        fun runFullMaintenance(
            database: RoomDatabase,
            context: Context? = null,
            databaseName: String? = null,
        ): MaintenanceReport {
            val startTime = System.currentTimeMillis()
            val initialSize =
                if (context != null && databaseName != null) {
                    getDatabaseSizeBytes(context, databaseName)
                } else {
                    0L
                }

            return try {
                checkpoint(database)
                vacuum(database)
                analyze(database)

                val finalSize =
                    if (context != null && databaseName != null) {
                        getDatabaseSizeBytes(context, databaseName)
                    } else {
                        0L
                    }

                val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(0L)
                MaintenanceReport(
                    initialSizeBytes = initialSize,
                    finalSizeBytes = finalSize,
                    durationMs = duration,
                    isSuccessful = true,
                )
            } catch (t: Throwable) {
                val duration = (System.currentTimeMillis() - startTime).coerceAtLeast(0L)
                MaintenanceReport(
                    initialSizeBytes = initialSize,
                    finalSizeBytes = initialSize,
                    durationMs = duration,
                    isSuccessful = false,
                    error = t,
                )
            }
        }
    }
