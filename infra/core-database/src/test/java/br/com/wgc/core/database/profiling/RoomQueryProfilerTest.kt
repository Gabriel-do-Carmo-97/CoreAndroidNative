package br.com.wgc.core.database.profiling

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomQueryProfilerTest {
    @Test
    fun `recordExecution detects fast query correctly`() {
        var slowEvent: QueryProfileEvent? = null
        val profiler =
            RoomQueryProfiler(slowQueryThresholdMs = 16L) {
                slowEvent = it
            }

        val event = profiler.recordExecution("SELECT * FROM users", emptyList(), durationMs = 5L)
        assertFalse(event.isSlow)
        assertEquals(null, slowEvent)
    }

    @Test
    fun `recordExecution alerts on slow query exceeding threshold`() {
        var slowEvent: QueryProfileEvent? = null
        val profiler =
            RoomQueryProfiler(slowQueryThresholdMs = 16L) {
                slowEvent = it
            }

        val event = profiler.recordExecution("SELECT * FROM large_table", emptyList(), durationMs = 35L)
        assertTrue(event.isSlow)
        assertEquals(35L, slowEvent?.durationMs)
        assertEquals("SELECT * FROM large_table", slowEvent?.sqlQuery)
    }
}
