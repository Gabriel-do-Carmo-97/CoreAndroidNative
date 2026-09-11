package br.com.wgc.core.device

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DefaultDeviceSecurityHelperTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `root detection should be disabled by default`() {
        val helper = DefaultDeviceSecurityHelper(context)
        assertFalse(helper.isRootDetectionEnabled)
        assertFalse(helper.isRooted())
    }

    @Test
    fun `root detection can be explicitly enabled`() {
        val helper = DefaultDeviceSecurityHelper(context, isRootDetectionEnabled = true)
        assertTrue(helper.isRootDetectionEnabled)
    }

    @Test
    fun `checkSecurity produces comprehensive diagnostic report`() {
        val helper = DefaultDeviceSecurityHelper(context, isRootDetectionEnabled = false)
        val report = helper.checkSecurity()

        assertNotNull(report)
        assertFalse(report.isRootDetectionEnabled)
        assertFalse(report.isRooted)
    }
}
