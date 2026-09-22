package br.com.wgc.core.device.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SecureClipboardManagerTest {
    private lateinit var context: Context
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var manager: SecureClipboardManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        manager = SecureClipboardManager(context, testScope)
    }

    @Test
    fun copySensitive_and_clearImmediate() {
        manager.copySensitive("PixKey", "user@example.com", autoClearDelayMs = 0)
        assertTrue(manager.hasClipboardData())

        manager.clearClipboard()
        assertFalse(manager.hasClipboardData())
    }

    @Test
    fun copySensitive_autoClearsAfterTimeout() =
        runTest(testDispatcher) {
            val delayMs = 5_000L
            manager.copySensitive("Password", "secret123", autoClearDelayMs = delayMs)
            assertTrue(manager.hasClipboardData())

            testScope.advanceTimeBy(delayMs + 100)
            assertFalse(manager.hasClipboardData())
        }
}
