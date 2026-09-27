package br.com.wgc.core.analytics.crash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CrashCatcherTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `uncaughtException serializes crash report to disk`() {
        val dir = tempFolder.newFolder("crashes")
        val catcher = CrashCatcher(dir, defaultHandler = null)

        val exception = RuntimeException("Fatal null pointer in payment flow")
        catcher.uncaughtException(Thread.currentThread(), exception)

        val pending = catcher.getPendingReports()
        assertEquals(1, pending.size)

        val content = pending[0].readText()
        assertTrue(content.contains("Fatal null pointer in payment flow"))
        assertTrue(content.contains("RuntimeException"))

        catcher.clearReports()
        assertTrue(catcher.getPendingReports().isEmpty())
    }
}
