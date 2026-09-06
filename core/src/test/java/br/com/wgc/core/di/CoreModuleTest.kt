package br.com.wgc.core.di

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CoreModuleTest {

    @Test
    fun `default qualifiers and names should be initialized`() {
        assertEquals("wgc_core_datastore", CoreModule.provideDataStoreName())
        assertEquals("wgc_core_preferences", CoreModule.provideSharedPreferencesName())
    }

    @Test
    fun `default non-contextual singletons should be provided`() {
        assertNotNull(CoreModule.provideCoroutineDispatchers())
        assertNotNull(CoreModule.provideCoreLogger())
        assertNotNull(CoreModule.provideAnalyticsTracker())
        assertNotNull(CoreModule.provideImageCompressor(CoreModule.provideCoroutineDispatchers()))
    }
}
