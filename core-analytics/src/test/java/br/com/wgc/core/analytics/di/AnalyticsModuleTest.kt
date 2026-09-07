package br.com.wgc.core.analytics.di

import org.junit.Assert.assertNotNull
import org.junit.Test

class AnalyticsModuleTest {

    @Test
    fun `provideAnalyticsTracker should return initialized instance`() {
        assertNotNull(AnalyticsModule.provideAnalyticsTracker())
    }
}
