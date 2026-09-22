package br.com.wgc.core.network.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Estados emitidos durante o progresso de um download com suporte a retomada.
 */
sealed interface DownloadProgress {
    /**
     * Download em andamento com bytes transferidos até o momento.
     *
     * @property bytesDownloaded Quantidade acumulada de bytes baixados.
     * @property totalBytes Tamanho total do arquivo remoto (se informado pelo servidor).
     * @property progressPercentage Porcentagem concluída (0.0f a 1.0f) ou null se tamanho indefinido.
     */
    data class Running(
        val bytesDownloaded: Long,
        val totalBytes: Long?,
        val progressPercentage: Float?,
    ) : DownloadProgress

    /**
     * Download finalizado com sucesso.
     *
     * @property destinationFile Arquivo persistido no sistema de arquivos local.
     * @property totalBytes Tamanho final do arquivo baixado em bytes.
     */
    data class Completed(
        val destinationFile: File,
        val totalBytes: Long,
    ) : DownloadProgress

    /**
     * Falha durante o download.
     *
     * @property error Exceção causadora da interrupção.
     * @property bytesDownloaded Bytes baixados com sucesso antes da falha (preservados no disco para retomada).
     */
    data class Failed(
        val error: Throwable,
        val bytesDownloaded: Long,
    ) : DownloadProgress
}

/**
 * Cliente de download resiliente com suporte nativo a HTTP Range (`Range: bytes=X-`).
 *
 * Permite retomar downloads interrompidos por perda de conexão sem rebaixar os dados já salvos
 * em disco, emitindo atualizações reativas de progresso via Kotlin [Flow].
 */
@Singleton
class ResumableDownloader
    @Inject
    constructor(
        private val okHttpClient: OkHttpClient,
    ) {
        /**
         * Executa o download de um recurso remoto para o arquivo de destino informado.
         *
         * @param url URL pública do arquivo a ser baixado.
         * @param destinationFile Arquivo destino onde os bytes serão gravados.
         * @return [Flow] reativo emitindo estados de [DownloadProgress].
         */
        @Suppress("TooGenericExceptionCaught", "CyclomaticComplexMethod")
        fun download(
            url: String,
            destinationFile: File,
        ): Flow<DownloadProgress> =
            flow {
                val existingLength = if (destinationFile.exists()) destinationFile.length() else 0L

                val requestBuilder = Request.Builder().url(url)
                if (existingLength > 0L) {
                    requestBuilder.addHeader(HEADER_RANGE, "bytes=$existingLength-")
                }

                val request = requestBuilder.build()

                try {
                    okHttpClient.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Download failed with HTTP code ${response.code}")
                        }

                        val body = response.body ?: throw IOException("Empty response body")
                        val contentLength = body.contentLength()
                        val isPartial = response.code == HTTP_PARTIAL_CONTENT

                        val totalBytes =
                            if (contentLength > 0L) {
                                if (isPartial) existingLength + contentLength else contentLength
                            } else {
                                null
                            }

                        val appendMode = isPartial && existingLength > 0L
                        destinationFile.parentFile?.mkdirs()

                        var downloaded = if (appendMode) existingLength else 0L

                        body.byteStream().use { input ->
                            FileOutputStream(destinationFile, appendMode).use { output ->
                                val buffer = ByteArray(BUFFER_SIZE)
                                var readBytes = input.read(buffer)

                                while (readBytes != -1) {
                                    output.write(buffer, 0, readBytes)
                                    downloaded += readBytes

                                    val percentage =
                                        totalBytes?.let { total ->
                                            if (total > 0) (downloaded.toFloat() / total).coerceIn(0f, 1f) else null
                                        }

                                    emit(
                                        DownloadProgress.Running(
                                            bytesDownloaded = downloaded,
                                            totalBytes = totalBytes,
                                            progressPercentage = percentage,
                                        ),
                                    )

                                    readBytes = input.read(buffer)
                                }
                                output.flush()
                            }
                        }

                        emit(DownloadProgress.Completed(destinationFile, downloaded))
                    }
                } catch (t: Throwable) {
                    val saved = if (destinationFile.exists()) destinationFile.length() else 0L
                    emit(DownloadProgress.Failed(t, saved))
                }
            }.flowOn(Dispatchers.IO)

        companion object {
            private const val HEADER_RANGE = "Range"
            private const val HTTP_PARTIAL_CONTENT = 206
            private const val BUFFER_SIZE = 8192
        }
    }
