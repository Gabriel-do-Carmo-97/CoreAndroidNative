package br.com.wgc.core.io

import java.io.File
import java.io.FileOutputStream

/**
 * Interface abstrata para operações no sistema de arquivos,
 * facilitando testes e desacoplamento de APIs específicas do SO.
 */
interface CoreFileSystem {
    fun readBytes(path: String): ByteArray

    fun writeBytes(
        path: String,
        bytes: ByteArray,
    )

    fun exists(path: String): Boolean

    fun delete(path: String): Boolean

    fun list(directoryPath: String): List<String>

    fun createDirectories(path: String): Boolean

    fun size(path: String): Long
}

/**
 * Implementação padrão de [CoreFileSystem] baseada na API Java standard.
 */
class DefaultCoreFileSystem : CoreFileSystem {
    override fun readBytes(path: String): ByteArray {
        val file = File(path)
        if (!file.exists()) throw NoSuchFileException(file)
        return file.readBytes()
    }

    override fun writeBytes(
        path: String,
        bytes: ByteArray,
    ) {
        val file = File(path)
        file.parentFile?.mkdirs()
        FileOutputStream(file).use { it.write(bytes) }
    }

    override fun exists(path: String): Boolean {
        return File(path).exists()
    }

    override fun delete(path: String): Boolean {
        return File(path).delete()
    }

    override fun list(directoryPath: String): List<String> {
        val dir = File(directoryPath)
        return dir.list()?.toList() ?: emptyList()
    }

    override fun createDirectories(path: String): Boolean {
        return File(path).mkdirs()
    }

    override fun size(path: String): Long {
        return File(path).length()
    }
}
