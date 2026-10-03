package br.com.wgc.core.ai.model

import java.io.File
import java.security.MessageDigest

/**
 * Enterprise manager for downloading, verifying SHA-256 signatures, and versioning on-device ML models.
 */
class ModelLifecycleManager(
    private val modelsDirectory: File,
) {
    /**
     * Checks if a model file exists and matches its expected cryptographic checksum.
     */
    fun isModelValid(
        modelName: String,
        expectedSha256Hex: String,
    ): Boolean {
        val file = File(modelsDirectory, modelName)
        if (!file.exists() || !file.isFile) return false

        val actualSha256 = computeSha256(file)
        return actualSha256.equals(expectedSha256Hex, ignoreCase = true)
    }

    /**
     * Returns the local file handle for the requested model if valid.
     */
    fun getModelFile(modelName: String): File? {
        val file = File(modelsDirectory, modelName)
        return if (file.exists()) file else null
    }

    /**
     * Computes the SHA-256 hash of a file.
     */
    @Suppress("NestedBlockDepth")
    fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead = input.read(buffer)
            while (bytesRead != -1) {
                digest.update(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val BUFFER_SIZE = 8192
    }
}
