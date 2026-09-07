package br.com.wgc.core.device

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultHapticFeedbackHelperTest {

    private lateinit var context: Context
    private lateinit var hapticHelper: DefaultHapticFeedbackHelper

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        hapticHelper = DefaultHapticFeedbackHelper(context)
    }

    @Test
    fun hapticFeedback_shouldExecuteWithoutThrowingExceptions() {
        // Safe execution tests: ensure no unhandled exceptions even in simulated environments
        hapticHelper.vibrateClick()
        hapticHelper.vibrateSuccess()
        hapticHelper.vibrateError()
    }
}
