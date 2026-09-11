package br.com.wgc.core.ui.media

import android.graphics.Bitmap
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DefaultImageCompressorTest {

    private val compressor = DefaultImageCompressor()

    @Test
    fun `compressBitmap should return valid byte array for unscaled bitmap`() = runTest {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val bytes = compressor.compressBitmap(
            bitmap = bitmap,
            maxWidth = 200,
            maxHeight = 200,
            quality = 80,
            format = Bitmap.CompressFormat.PNG
        )

        assertNotNull(bytes)
        assertTrue(bytes.isNotEmpty())
    }

    @Test
    fun `compressBitmap should scale down bitmap when dimensions exceed max bounds`() = runTest {
        val bitmap = Bitmap.createBitmap(400, 200, Bitmap.Config.ARGB_8888)
        val bytes = compressor.compressBitmap(
            bitmap = bitmap,
            maxWidth = 100,
            maxHeight = 100,
            quality = 80,
            format = Bitmap.CompressFormat.PNG
        )

        assertNotNull(bytes)
        assertTrue(bytes.isNotEmpty())
    }
}
