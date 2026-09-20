package br.com.wgc.core.startup

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CoreInitializerTest {
    @Test
    fun `create should initialize successfully without throwing`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val initializer = CoreInitializer()

        initializer.create(context)
    }

    @Test
    fun `dependencies should return an empty list for immediate startup`() {
        val initializer = CoreInitializer()

        assertTrue(initializer.dependencies().isEmpty())
    }
}
