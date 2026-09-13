package br.com.wgc.core.extensions

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ContextExtensionsTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun hasPermission_returnsFalseWhenNotGranted_andTrueWhenGranted() {
        val permission = Manifest.permission.CAMERA
        val app = ApplicationProvider.getApplicationContext<Application>()
        val shadowApp = Shadows.shadowOf(app)

        shadowApp.denyPermissions(permission)
        assertFalse(context.hasPermission(permission))

        shadowApp.grantPermissions(permission)
        assertTrue(context.hasPermission(permission))
    }

    @Test
    fun showToast_displaysToastWithGivenMessage() {
        context.showToast("Mensagem de Teste")
        assertEquals("Mensagem de Teste", ShadowToast.getTextOfLatestToast())
    }
}
