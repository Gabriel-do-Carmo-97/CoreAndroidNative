package br.com.wgc.core.analytics.startup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AppStartupTracerTest {
    private lateinit var tracer: AppStartupTracer

    @Before
    fun setUp() {
        tracer = AppStartupTracer()
    }

    @Test
    fun `when trace not started markStage returns null and finishTrace returns null`() {
        assertNull(tracer.markStage("stage1"))
        assertNull(tracer.markFirstDraw())
        assertNull(tracer.finishTrace())
        assertFalse(tracer.isTracing())
    }

    @Test
    fun `startTrace initiates tracing and records elapsed milestones`() {
        val startTime = System.currentTimeMillis() - 100L
        tracer.startTrace(StartupType.COLD, startTime)

        assertTrue(tracer.isTracing())

        val stage1Elapsed = tracer.markStage("di_init")
        assertNotNull(stage1Elapsed)
        assertTrue(stage1Elapsed!! >= 100L)

        val firstDrawElapsed = tracer.markFirstDraw()
        assertNotNull(firstDrawElapsed)
        assertTrue(firstDrawElapsed!! >= stage1Elapsed)

        val metrics = tracer.finishTrace()
        assertNotNull(metrics)
        assertEquals(StartupType.COLD, metrics!!.type)
        assertTrue(metrics.totalDurationMs >= firstDrawElapsed)
        assertTrue(metrics.stages.containsKey("di_init"))
        assertTrue(metrics.stages.containsKey("first_draw"))
        assertTrue(metrics.isCompleted)
        assertFalse(tracer.isTracing())
    }

    @Test
    fun `reset clears tracer state`() {
        tracer.startTrace(StartupType.WARM)
        tracer.markStage("stage1")
        tracer.reset()

        assertFalse(tracer.isTracing())
        assertNull(tracer.finishTrace())
    }
}
