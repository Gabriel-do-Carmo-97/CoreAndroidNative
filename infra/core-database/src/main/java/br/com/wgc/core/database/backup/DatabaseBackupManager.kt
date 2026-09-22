package br.com.wgc.core.database.backup

import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador corporativo de backup e restauração de bases de dados locais com criptografia AES-256-GCM.
 *
 * Permite exportar e importar dumps de banco de dados SQLite/Room com integridade criptográfica
 * garantida por autenticação AEAD (Galois/Counter Mode).
 */
@Singleton
class DatabaseBackupManager
    @Inject
    constructor() {
        /**
         * Realiza o backup cifrado de um arquivo de banco de dados local.
         *
         * @param sourceDbFile Arquivo de banco de dados de origem (`.db`).
         * @param destinationBackupFile Arquivo de saída para o dump encriptado.
         * @param secretKey Chave ou senha utilizada para a derivação criptográfica AES-256.
         * @return O tamanho final em bytes do arquivo de backup gerado.
         */
        fun backupDatabase(
            sourceDbFile: File,
            destinationBackupFile: File,
            secretKey: ByteArray,
        ): Long {
            require(sourceDbFile.exists()) { "Source database file does not exist: ${sourceDbFile.absolutePath}" }

            val keySpec = deriveKeySpec(secretKey)
            val iv = ByteArray(GCM_IV_LENGTH_BYTES)
            SecureRandom().nextBytes(iv)

            val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

            val plainBytes = sourceDbFile.readBytes()
            val cipherBytes = cipher.doFinal(plainBytes)

            destinationBackupFile.parentFile?.mkdirs()
            FileOutputStream(destinationBackupFile).use { output ->
                output.write(iv)
                output.write(cipherBytes)
                output.flush()
            }

            return destinationBackupFile.length()
        }

        /**
         * Restaura uma base de dados a partir de um arquivo de backup previamente cifrado.
         *
         * @param backupSourceFile Arquivo de backup encriptado contendo o IV e o ciphertext.
         * @param destinationDbFile Arquivo de destino do banco onde os dados decifrados serão gravados.
         * @param secretKey Chave ou senha utilizada na criação do backup.
         * @return `true` se restaurado e autenticado com sucesso, `false` se a chave estiver incorreta ou dados corrompidos.
         */
        @Suppress("TooGenericExceptionCaught")
        fun restoreDatabase(
            backupSourceFile: File,
            destinationDbFile: File,
            secretKey: ByteArray,
        ): Boolean {
            if (!backupSourceFile.exists() || backupSourceFile.length() <= GCM_IV_LENGTH_BYTES) {
                return false
            }

            return try {
                val backupBytes = backupSourceFile.readBytes()
                val iv = backupBytes.copyOfRange(0, GCM_IV_LENGTH_BYTES)
                val cipherBytes = backupBytes.copyOfRange(GCM_IV_LENGTH_BYTES, backupBytes.size)

                val keySpec = deriveKeySpec(secretKey)
                val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, keySpec, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

                val plainBytes = cipher.doFinal(cipherBytes)

                destinationDbFile.parentFile?.mkdirs()
                destinationDbFile.writeBytes(plainBytes)
                true
            } catch (
                @Suppress("SwallowedException") _: Exception,
            ) {
                false
            }
        }

        private fun deriveKeySpec(keyBytes: ByteArray): SecretKeySpec {
            val key =
                if (keyBytes.size == AES_256_KEY_SIZE_BYTES) {
                    keyBytes
                } else {
                    MessageDigest.getInstance("SHA-256").digest(keyBytes)
                }
            return SecretKeySpec(key, "AES")
        }

        companion object {
            private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
            private const val GCM_IV_LENGTH_BYTES = 12
            private const val GCM_TAG_LENGTH_BITS = 128
            private const val AES_256_KEY_SIZE_BYTES = 32
        }
    }
