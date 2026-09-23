package br.com.wgc.core.analytics.performance

import android.app.Activity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FrameMetricsMonitorTest {
    @Test
    fun `startMonitoring attaches listener and sets isMonitoring true`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val monitor = FrameMetricsMonitor(16L)

        assertFalse(monitor.isMonitoring())

        monitor.startMonitoring(activity.window) { _ -> }
        assertTrue(monitor.isMonitoring())

        monitor.stopMonitoring(activity.window)
        assertFalse(monitor.isMonitoring())
    }

    @Test
    fun `stopMonitoring when not started does not throw`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val monitor = FrameMetricsMonitor(16L)

        monitor.stopMonitoring(activity.window)
        assertFalse(monitor.isMonitoring())
    }
}
