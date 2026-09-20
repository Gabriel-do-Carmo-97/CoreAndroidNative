package br.com.wgc.core.performance

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class StrictModeHelperTest {
    @Test
    fun `enableStrictModeInDebug should not throw when enabled in debug`() {
        // Valida execução sem falha de configuração de ThreadPolicy e VmPolicy sob Robolectric
        StrictModeHelper.enableStrictModeInDebug(isDebug = true)
    }

    @Test
    fun `enableStrictModeInDebug should no-op when isDebug is false`() {
        StrictModeHelper.enableStrictModeInDebug(isDebug = false)
    }
}
