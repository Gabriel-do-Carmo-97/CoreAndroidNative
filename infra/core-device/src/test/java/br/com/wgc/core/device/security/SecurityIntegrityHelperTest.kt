package br.com.wgc.core.device.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SecurityIntegrityHelperTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun isFridaDetected_whenLibPresent_returnsTrue() {
        val mockMapsFile = tempFolder.newFile("maps")
        mockMapsFile.writeText("7f8a1000-7f8a2000 r-xp 00000000 /data/local/tmp/frida-agent-64.so\n")

        val helper = SecurityIntegrityHelper(procMapsPath = mockMapsFile.absolutePath)
        assertTrue(helper.isFridaDetected())
    }

    @Test
    fun isFridaDetected_whenClean_returnsFalse() {
        val mockMapsFile = tempFolder.newFile("clean_maps")
        mockMapsFile.writeText("7f8a1000-7f8a2000 r-xp 00000000 /system/lib64/libc.so\n")

        val helper = SecurityIntegrityHelper(procMapsPath = mockMapsFile.absolutePath)
        assertFalse(helper.isFridaDetected())
    }

    @Test
    fun isHookingDetected_whenXposedPresent_returnsTrue() {
        val mockMapsFile = tempFolder.newFile("xposed_maps")
        mockMapsFile.writeText("7f8a1000-7f8a2000 r-xp 00000000 /system/framework/edxposed.jar\n")

        val helper = SecurityIntegrityHelper(procMapsPath = mockMapsFile.absolutePath)
        assertTrue(helper.isHookingDetected())
    }

    @Test
    fun checkSecurityIntegrity_whenClean_returnsNotCompromised() {
        val mockMapsFile = tempFolder.newFile("clean_all")
        mockMapsFile.writeText("7f8a1000-7f8a2000 r-xp 00000000 /system/lib64/libm.so\n")

        val helper = SecurityIntegrityHelper(procMapsPath = mockMapsFile.absolutePath)
        val report = helper.checkSecurityIntegrity()

        assertFalse(report.isFridaDetected)
        assertFalse(report.isHookingDetected)
    }
}
