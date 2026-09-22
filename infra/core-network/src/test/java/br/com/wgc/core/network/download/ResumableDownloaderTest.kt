package br.com.wgc.core.network.download

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ResumableDownloaderTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `download saves entire content when no range requested`() =
        runTest {
            val content = "Hello Resumable Downloader!"
            val client =
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        Response
                            .Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .message("OK")
                            .body(content.toByteArray().toResponseBody("text/plain".toMediaType()))
                            .build()
                    }.build()

            val downloader = ResumableDownloader(client)
            val destFile = File(tempFolder.root, "test_file.txt")

            val events = downloader.download("https://example.com/file.txt", destFile).toList()

            assertTrue(events.any { it is DownloadProgress.Running })
            val lastEvent = events.last()
            assertTrue(lastEvent is DownloadProgress.Completed)
            assertEquals(content, destFile.readText())
        }

    @Test
    fun `download appends content with 206 Partial Content when file already has bytes`() =
        runTest {
            val destFile = File(tempFolder.root, "partial_file.txt")
            destFile.writeText("Part 1: ")

            val client =
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        val rangeHeader = chain.request().header("Range")
                        assertEquals("bytes=8-", rangeHeader)

                        Response
                            .Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(206)
                            .message("Partial Content")
                            .body("Part 2!".toByteArray().toResponseBody("text/plain".toMediaType()))
                            .build()
                    }.build()

            val downloader = ResumableDownloader(client)
            val events = downloader.download("https://example.com/file.txt", destFile).toList()

            val lastEvent = events.last()
            assertTrue(lastEvent is DownloadProgress.Completed)
            assertEquals("Part 1: Part 2!", destFile.readText())
        }

    @Test
    fun `download emits Failed when server returns HTTP error`() =
        runTest {
            val client =
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        Response
                            .Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(404)
                            .message("Not Found")
                            .body("".toResponseBody())
                            .build()
                    }.build()

            val downloader = ResumableDownloader(client)
            val destFile = File(tempFolder.root, "error_file.txt")

            val events = downloader.download("https://example.com/missing.txt", destFile).toList()
            val lastEvent = events.last()
            assertTrue(lastEvent is DownloadProgress.Failed)
        }
}
