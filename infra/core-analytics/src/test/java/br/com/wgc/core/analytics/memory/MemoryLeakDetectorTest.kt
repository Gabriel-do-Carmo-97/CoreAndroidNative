package br.com.wgc.core.analytics.memory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryLeakDetectorTest {
    @Test
    fun `detectRetainedObjects reports strongly held objects`() {
        val detector = MemoryLeakDetector()
        val leakedObject = Any()

        detector.watch(leakedObject, "MainActivity Leak")
        val retained = detector.detectRetainedObjects()

        assertEquals(1, retained.size)
        assertTrue(retained[0].contains("MainActivity Leak"))
    }

    @Test
    fun `clear removes all watched references`() {
        val detector = MemoryLeakDetector()
        detector.watch(Any(), "Dummy")
        detector.clear()

        assertTrue(detector.detectRetainedObjects().isEmpty())
    }
}
