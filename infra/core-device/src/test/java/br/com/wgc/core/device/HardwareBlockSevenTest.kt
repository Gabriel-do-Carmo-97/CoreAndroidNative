package br.com.wgc.core.device

import br.com.wgc.core.device.audio.AudioProcessingInspector
import br.com.wgc.core.device.ble.BlePeripheralConfig
import br.com.wgc.core.device.display.DisplayRefreshRateInspector
import br.com.wgc.core.device.haptics.HapticActuatorInspector
import br.com.wgc.core.device.sensor.HardwareSensorsInspector
import br.com.wgc.core.device.sensor.SensorFusionCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class HardwareBlockSevenTest {
    @Test
    fun testAudioProcessingInspector() {
        val caps = AudioProcessingInspector.inspect()
        assertNotNull(caps)
    }

    @Test
    fun testBlePeripheralConfig() {
        val uuid = UUID.randomUUID()
        val config = BlePeripheralConfig(serviceUuid = uuid, deviceName = "CorePeripheral")
        assertEquals(uuid, config.serviceUuid)
        assertEquals("CorePeripheral", config.deviceName)
    }

    @Test
    fun testSensorFusionShortInput() {
        val orientation = SensorFusionCalculator.computeOrientation(floatArrayOf(1f), floatArrayOf(2f))
        assertNull(orientation)
    }

    @Test
    fun testDisplayRefreshRateInspectorNull() {
        val profile = DisplayRefreshRateInspector.inspect(null)
        assertEquals(60.0f, profile.currentRefreshRate, 0.001f)
        assertFalse(profile.isHighRefreshRate)
    }

    @Test
    fun testHapticActuatorNull() {
        val inspector = HapticActuatorInspector(null)
        val caps = inspector.inspect()
        assertFalse(caps.hasVibrator)
        assertFalse(caps.hasAmplitudeControl)
    }

    @Test
    fun testHardwareSensorsNull() {
        val inspector = HardwareSensorsInspector(null)
        assertEquals(0, inspector.listSensors().size)
        assertFalse(inspector.hasSensor(1))
    }
}
