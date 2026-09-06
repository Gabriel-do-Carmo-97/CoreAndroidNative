package br.com.wgc.core.device

import android.Manifest
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultPermissionManagerTest {

    private lateinit var context: Context
    private lateinit var permissionManager: DefaultPermissionManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        permissionManager = DefaultPermissionManager(context)
    }

    @Test
    fun `isGranted should check permission state`() {
        val granted = permissionManager.isGranted(Manifest.permission.CAMERA)
        assertFalse(granted)
    }

    @Test
    fun `permission states should represent valid models`() {
        val grantedState = PermissionState.Granted
        val deniedWithRationale = PermissionState.Denied(shouldShowRationale = true)
        val deniedNoRationale = PermissionState.Denied(shouldShowRationale = false)

        assertNotNull(grantedState)
        assert(deniedWithRationale.shouldShowRationale)
        assertFalse(deniedNoRationale.shouldShowRationale)
    }
}
