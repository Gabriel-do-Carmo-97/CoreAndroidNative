package br.com.wgc.core.device.ble

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class BleScannerEngineTest {
    @Test
    fun `scanBleDevices fails immediately when bluetooth adapter is null or disabled`() {
        val engine = BleScannerEngine(null)
        assertFalse(engine.isBluetoothEnabled)

        assertThrows(IllegalStateException::class.java) {
            runBlocking {
                engine.scanBleDevices().firstOrNull()
            }
        }
    }
}
