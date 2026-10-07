package br.com.wgc.core.device

import br.com.wgc.core.device.battery.AppStandbyInspector
import br.com.wgc.core.device.battery.AppStartLimitsInspector
import br.com.wgc.core.device.display.EdgeToEdgeInspector
import br.com.wgc.core.device.display.HdrHeadroomInspector
import br.com.wgc.core.device.display.MemoryPageSizeInspector
import br.com.wgc.core.device.predictiveback.PredictiveBackInspector
import br.com.wgc.core.device.privatespace.PartialMediaAccessInspector
import br.com.wgc.core.device.satellite.SatelliteConnectivityInspector
import br.com.wgc.core.device.service.ForegroundServiceAuditor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModernAndroidBlockEightTest {
    @Test
    fun testPredictiveBackInspector() {
        assertNotNull(PredictiveBackInspector.isPredictiveBackSupported())
    }

    @Test
    fun testForegroundServiceAuditor() {
        assertTrue(ForegroundServiceAuditor.isValidFgsType(1))
        assertFalse(ForegroundServiceAuditor.isValidFgsType(0))
        assertNotNull(ForegroundServiceAuditor.isDataSyncTypeSupported())
    }

    @Test
    fun testSatelliteConnectivityInspector() {
        val status = SatelliteConnectivityInspector.inspect()
        assertNotNull(status)
    }

    @Test
    fun testEdgeToEdgeInspector() {
        val report = EdgeToEdgeInspector.inspect()
        assertNotNull(report)
    }

    @Test
    fun testMemoryPageSizeInspector() {
        val profile = MemoryPageSizeInspector.inspect()
        assertNotNull(profile)
    }

    @Test
    fun testHdrHeadroomInspector() {
        val report = HdrHeadroomInspector.inspect()
        assertNotNull(report)
    }

    @Test
    fun testAppStartLimitsInspector() {
        val report = AppStartLimitsInspector.inspect()
        assertNotNull(report)
    }

    @Test
    fun testAppStandbyInspector() {
        val report = AppStandbyInspector.inspect()
        assertNotNull(report)
    }

    @Test
    fun testPartialMediaAccessInspector() {
        val report = PartialMediaAccessInspector.inspect()
        assertNotNull(report)
    }
}
