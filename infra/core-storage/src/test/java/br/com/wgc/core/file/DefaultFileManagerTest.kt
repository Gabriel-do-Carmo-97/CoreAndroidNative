package br.com.wgc.core.file

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DefaultFileManagerTest {
    private lateinit var context: Context
    private lateinit var fileManager: DefaultFileManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fileManager = DefaultFileManager(context)
    }

    @Test
    fun createTempFile_shouldCreateFileInCacheDir() {
        val tempFile = fileManager.createTempFile("test_prefix_", ".dat")
        assertTrue(tempFile.exists())
        assertTrue(tempFile.name.startsWith("test_prefix_"))
        assertTrue(tempFile.name.endsWith(".dat"))
    }

    @Test
    fun getCacheSizeBytes_andClearCache_shouldCalculateAndPurgeFiles() {
        val testFile = File(context.cacheDir, "sample.txt")
        val sampleData = "Hello CoreAndroidNative Cache!".toByteArray()
        testFile.writeBytes(sampleData)

        val initialSize = fileManager.getCacheSizeBytes()
        assertTrue(initialSize >= sampleData.size)

        val cleared = fileManager.clearCache()
        assertTrue(cleared)

        val sizeAfterClear = fileManager.getCacheSizeBytes()
        assertEquals(0L, sizeAfterClear)
    }

    @Test
    fun deleteFile_shouldDeleteSingleFile() {
        val tempFile = fileManager.createTempFile("to_delete_", ".tmp")
        assertTrue(tempFile.exists())

        val deleted = fileManager.deleteFile(tempFile)
        assertTrue(deleted)
        assertFalse(tempFile.exists())
    }
}
