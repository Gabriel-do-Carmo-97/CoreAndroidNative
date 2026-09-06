package br.com.wgc.core.file

import android.content.Context
import java.io.File
import java.io.IOException

/**
 * Contrato corporativo para operações de gerenciamento de arquivos temporários e manutenção do cache local da aplicação.
 */
interface FileManager {

    /**
     * Cria um arquivo temporário no diretório de cache da aplicação.
     *
     * @param prefix Prefixo do nome do arquivo, por padrão `"temp_"`.
     * @param suffix Extensão do arquivo, por padrão `".tmp"`.
     * @return Objeto [File] recém-criado em disco.
     * @throws IOException Caso ocorra falha de I/O na criação do arquivo.
     */
    @Throws(IOException::class)
    fun createTempFile(prefix: String = "temp_", suffix: String = ".tmp"): File

    /**
     * Calcula o volume total em bytes consumido pelo cache interno e externo da aplicação.
     *
     * @return Tamanho total ocupado em bytes.
     */
    fun getCacheSizeBytes(): Long

    /**
     * Limpa de forma recursiva o conteúdo dos diretórios de cache interno e externo.
     *
     * @return `true` se a limpeza ocorreu sem falhas, `false` caso contrário.
     */
    fun clearCache(): Boolean

    /**
     * Exclui um arquivo ou diretório de forma recursiva e segura.
     *
     * @param file Arquivo ou diretório a ser deletado.
     * @return `true` se excluído com sucesso, `false` caso contrário.
     */
    fun deleteFile(file: File): Boolean
}

/**
 * Implementação padrão de [FileManager] operando sobre o armazenamento de contexto da aplicação.
 *
 * @property context Contexto da aplicação utilizado para localizar os diretórios de cache.
 */
class DefaultFileManager(private val context: Context) : FileManager {

    override fun createTempFile(prefix: String, suffix: String): File {
        val cacheDirectory = context.cacheDir
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs()
        }
        return File.createTempFile(prefix, suffix, cacheDirectory)
    }

    override fun getCacheSizeBytes(): Long {
        var size = calculateDirectorySize(context.cacheDir)
        context.externalCacheDir?.let {
            size += calculateDirectorySize(it)
        }
        return size
    }

    override fun clearCache(): Boolean {
        var success = deleteDirectoryContents(context.cacheDir)
        context.externalCacheDir?.let {
            success = success && deleteDirectoryContents(it)
        }
        return success
    }

    override fun deleteFile(file: File): Boolean {
        return if (file.isDirectory) {
            file.deleteRecursively()
        } else {
            file.delete()
        }
    }

    private fun calculateDirectorySize(directory: File?): Long {
        if (directory == null || !directory.exists()) return 0L
        var length = 0L
        val files = directory.listFiles() ?: return 0L
        for (file in files) {
            length += if (file.isFile) {
                file.length()
            } else {
                calculateDirectorySize(file)
            }
        }
        return length
    }

    private fun deleteDirectoryContents(directory: File?): Boolean {
        if (directory == null || !directory.exists()) return true
        var allDeleted = true
        val files = directory.listFiles() ?: return true
        for (file in files) {
            val deleted = deleteFile(file)
            if (!deleted) {
                allDeleted = false
            }
        }
        return allDeleted
    }
}
