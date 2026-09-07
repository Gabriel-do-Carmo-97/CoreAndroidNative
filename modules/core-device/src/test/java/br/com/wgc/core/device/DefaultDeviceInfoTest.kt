package br.com.wgc.core.device

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultDeviceInfoTest {

    private lateinit var context: Context
    private lateinit var deviceInfo: DefaultDeviceInfo

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        deviceInfo = DefaultDeviceInfo(context)
    }

    @Test
    fun deviceInfo_shouldReturnNonEmptyMetadata() {
        assertNotNull(deviceInfo.versionName)
        assertTrue(deviceInfo.versionCode >= -1L)
        assertTrue(deviceInfo.sdkInt > 0)
        assertNotNull(deviceInfo.deviceModel)
        assertNotNull(deviceInfo.manufacturer)
        // Robolectric runs in a simulated environment
        assertNotNull(deviceInfo.isEmulator)
    }
}
