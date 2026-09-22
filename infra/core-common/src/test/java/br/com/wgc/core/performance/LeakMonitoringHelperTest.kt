package br.com.wgc.core.performance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@Suppress("ExplicitGarbageCollectionCall")
class LeakMonitoringHelperTest {
    @Test
    fun `watch retains reference while strongly referenced`() {
        var leakedCallbackCalled = false
        val helper =
            LeakMonitoringHelper {
                leakedCallbackCalled = true
            }

        var targetObject: Any? = Any()
        val token = helper.watch(targetObject!!, "TestObject")

        assertEquals(1, helper.activeWatchCount())
        assertTrue(helper.isRetained(token))
        assertTrue(leakedCallbackCalled)
        assertEquals(1, helper.getRetainedObjects().size)
        assertEquals("TestObject", helper.getRetainedObjects()[token])

        // Release strong reference and trigger GC
        targetObject = null
        System.gc()
        System.runFinalization()

        // Wait a short bit and check
        val retainedAfterGc = helper.isRetained(token)
        // If GC collected it, retainedAfterGc is false
        if (!retainedAfterGc) {
            assertEquals(0, helper.getRetainedObjects().size)
        }
    }

    @Test
    fun `isRetained with unknown token returns false`() {
        val helper = LeakMonitoringHelper()
        assertFalse(helper.isRetained("unknown-token"))
    }

    @Test
    fun `clear removes all watched references`() {
        val helper = LeakMonitoringHelper()
        val obj = Any()
        helper.watch(obj, "Obj")
        assertEquals(1, helper.activeWatchCount())

        helper.clear()
        assertEquals(0, helper.activeWatchCount())
    }
}
