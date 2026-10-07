package br.com.wgc.core.device.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceSecurityBlockTwoTest {
    @Test
    fun testBytecodeObfuscationHelper() {
        val element = StackTraceElement("TestClass", "testMethod", null, -1)
        assertTrue(BytecodeObfuscationHelper.hasStrippedDebugInfo(element))

        val fullElement = StackTraceElement("TestClass", "testMethod", "TestClass.kt", 42)
        assertFalse(BytecodeObfuscationHelper.hasStrippedDebugInfo(fullElement))
    }

    @Test
    fun testScreenCaptureProtectionHelper() {
        val helper = ScreenCaptureProtectionHelper()
        assertNotNull(helper)
    }

    @Test
    fun testObfuscationNameCheck() {
        assertFalse(BytecodeObfuscationHelper.isObfuscated(DeviceSecurityBlockTwoTest::class.java))
    }
}
