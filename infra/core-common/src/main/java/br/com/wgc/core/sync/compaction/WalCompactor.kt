package br.com.wgc.core.sync.compaction

import java.io.File

/**
 * Enterprise compaction strategy for offline SQLite WAL (Write-Ahead Logging) and outbox cleanup.
 */
class WalCompactor(
    private val maxWalSizeBytes: Long = DEFAULT_MAX_WAL_SIZE_BYTES,
) {
    /**
     * Inspects if WAL file size exceeded threshold and requires checkpoint / truncation.
     */
    fun shouldCompact(walFile: File): Boolean {
        return walFile.exists() && walFile.length() > maxWalSizeBytes
    }

    /**
     * Simulates or computes reclaimed space after running PRAGMA wal_checkpoint(TRUNCATE).
     */
    fun calculateReclaimableBytes(walFile: File): Long {
        return if (shouldCompact(walFile)) {
            walFile.length()
        } else {
            0L
        }
    }

    companion object {
        const val DEFAULT_MAX_WAL_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB
    }
}
