package br.com.wgc.core.ui.media

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.coroutines.DefaultCoroutineDispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private const val DEFAULT_MAX_WIDTH = 1920
private const val DEFAULT_MAX_HEIGHT = 1080
private const val DEFAULT_QUALITY = 80

/**
 * Interface contract for asynchronous image compression and aspect-ratio scaling.
 */
interface ImageCompressor {
    /**
     * Resizes and compresses an existing [Bitmap] into an in-memory byte array.
     *
     * @param bitmap Source bitmap to compress.
     * @param maxWidth Maximum allowed width in pixels.
     * @param maxHeight Maximum allowed height in pixels.
     * @param quality Compression quality rating between 0 and 100.
     * @param format Output image encoding format.
     * @return Compressed byte array.
     */
    suspend fun compressBitmap(
        bitmap: Bitmap,
        maxWidth: Int = DEFAULT_MAX_WIDTH,
        maxHeight: Int = DEFAULT_MAX_HEIGHT,
        quality: Int = DEFAULT_QUALITY,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    ): ByteArray

    /**
     * Reads, compresses, and writes an image file to a destination file path.
     *
     * @param sourceFile File containing original uncompressed image.
     * @param destinationFile Target file where compressed image will be written.
     * @param maxWidth Maximum allowed width in pixels.
     * @param maxHeight Maximum allowed height in pixels.
     * @param quality Compression quality rating between 0 and 100.
     * @param format Output image encoding format.
     * @return The written [destinationFile].
     */
    suspend fun compressFile(
        sourceFile: File,
        destinationFile: File,
        maxWidth: Int = DEFAULT_MAX_WIDTH,
        maxHeight: Int = DEFAULT_MAX_HEIGHT,
        quality: Int = DEFAULT_QUALITY,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    ): File
}

/**
 * Default production implementation of [ImageCompressor] backed by Kotlin Coroutines.
 *
 * @param dispatchers Coroutine dispatchers for offloading heavy image I/O to background threads.
 */
class DefaultImageCompressor(
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers(),
) : ImageCompressor {
    override suspend fun compressBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        format: Bitmap.CompressFormat,
    ): ByteArray =
        withContext(dispatchers.default) {
            val scaledBitmap = scaleBitmapIfNeeded(bitmap, maxWidth, maxHeight)
            ByteArrayOutputStream().use { stream ->
                scaledBitmap.compress(format, quality, stream)
                if (scaledBitmap != bitmap) {
                    scaledBitmap.recycle()
                }
                stream.toByteArray()
            }
        }

    override suspend fun compressFile(
        sourceFile: File,
        destinationFile: File,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        format: Bitmap.CompressFormat,
    ): File =
        withContext(dispatchers.io) {
            val bitmap =
                BitmapFactory.decodeFile(sourceFile.absolutePath)
                    ?: error("Unable to decode source image file: ${sourceFile.path}")

            val compressedBytes = compressBitmap(bitmap, maxWidth, maxHeight, quality, format)
            bitmap.recycle()

            FileOutputStream(destinationFile).use { output ->
                output.write(compressedBytes)
                output.flush()
            }
            destinationFile
        }

    private fun scaleBitmapIfNeeded(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }
}
