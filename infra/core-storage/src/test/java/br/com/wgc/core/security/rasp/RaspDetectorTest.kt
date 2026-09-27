package br.com.wgc.core.security.rasp

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RaspDetectorTest {
    private val detector = RaspDetector()

    @Test
    fun `assessThreats returns valid report structure`() {
        val assessment = detector.assessThreats()
        assertNotNull(assessment)
        assertNotNull(assessment.threats)
    }

    @Test
    fun `isFridaDetected returns false when no frida is running`() {
        assertFalse(detector.isFridaDetected())
    }

    @Test
    fun `isHookingFrameworkDetected returns false on standard jvm`() {
        assertFalse(detector.isHookingFrameworkDetected())
    }

    @Test
    fun `isSignatureValid with null context returns false`() {
        assertFalse(detector.isSignatureValid("DUMMY_SHA256"))
    }
}
