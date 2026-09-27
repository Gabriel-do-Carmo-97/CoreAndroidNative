package br.com.wgc.core.device.sensor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SensorFusionEngineTest {
    @Test
    fun `initial update sets baseline orientation from gravity vector`() {
        val engine = SensorFusionEngine()
        // Device resting flat: Accel Z = 9.8 m/s^2, X = 0, Y = 0
        val angles =
            engine.update(
                accelX = 0f,
                accelY = 0f,
                accelZ = 9.8f,
                gyroX = 0f,
                gyroY = 0f,
                timestampNs = 1_000_000_000L,
            )

        assertNotNull(angles)
        assertEquals(0f, angles.pitch, 0.5f)
        assertEquals(0f, angles.roll, 0.5f)
    }

    @Test
    fun `subsequent updates fuse gyroscope smoothly`() {
        val engine = SensorFusionEngine(alpha = 0.98f)
        engine.update(0f, 0f, 9.8f, 0f, 0f, 1_000_000_000L)

        // Rotate at 1 rad/sec around X axis for 0.1s
        val angles =
            engine.update(
                accelX = 0f,
                accelY = 1f,
                accelZ = 9.7f,
                gyroX = 1f,
                gyroY = 0f,
                timestampNs = 1_100_000_000L,
            )

        // Pitch should have increased smoothly
        org.junit.Assert.assertTrue(angles.pitch > 0f)
    }
}
