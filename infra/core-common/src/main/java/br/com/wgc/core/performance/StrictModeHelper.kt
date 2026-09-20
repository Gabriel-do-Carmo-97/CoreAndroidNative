package br.com.wgc.core.performance

import android.os.StrictMode

/**
 * Utilitário para ativação de políticas de execução estrita (StrictMode).
 *
 * Em builds de debug, monitora violações de thread (I/O na Main Thread)
 * e vazamentos de recursos (Cursores SQLite e Closeables não finalizados).
 */
object StrictModeHelper {
    /**
     * Habilita as políticas do StrictMode se [isDebug] for `true`.
     */
    fun enableStrictModeInDebug(isDebug: Boolean) {
        if (!isDebug) return

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy
                .Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build(),
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy
                .Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build(),
        )
    }
}
