package br.com.wgc.core.analytics.storage

import java.io.File

/**
 * Diagnostic snapshot of application storage disk footprint.
 *
 * @property appDataSizeBytes Size of internal data directory in bytes.
 * @property cacheSizeBytes Size of cache directory in bytes.
 * @property totalFootprintBytes Aggregate storage used by the application.
 */
data class StorageFootprintReport(
    val appDataSizeBytes: Long,
    val cacheSizeBytes: Long,
    val totalFootprintBytes: Long,
)

/**
 * Inspector computing application disk footprint and cache growth.
 */
object StorageFootprintInspector {
    /**
     * Recursively computes total size of the given directory.
     */
    fun computeDirectorySize(directory: File?): Long {
        if (directory == null || !directory.exists()) return 0L
        var total = 0L
        val files = directory.listFiles() ?: return 0L
        for (file in files) {
            total +=
                if (file.isDirectory) {
                    computeDirectorySize(file)
                } else {
                    file.length()
                }
        }
        return total
    }

    /**
     * Generates a storage footprint report for data and cache directories.
     */
    fun inspect(
        dataDir: File?,
        cacheDir: File?,
    ): StorageFootprintReport {
        val dataSize = computeDirectorySize(dataDir)
        val cacheSize = computeDirectorySize(cacheDir)
        return StorageFootprintReport(
            appDataSizeBytes = dataSize,
            cacheSizeBytes = cacheSize,
            totalFootprintBytes = dataSize + cacheSize,
        )
    }
}
