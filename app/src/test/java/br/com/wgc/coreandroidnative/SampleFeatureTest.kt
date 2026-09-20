package br.com.wgc.coreandroidnative

import br.com.wgc.core.featureflag.DefaultFeatureToggle
import br.com.wgc.core.featureflag.FeatureToggleManager
import br.com.wgc.core.testing.fakes.FakeTokenProvider
import br.com.wgc.core.testing.rules.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SampleFeatureTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `feature should respect feature toggle and fake token provider`() =
        runTest {
            val fakeTokenProvider = FakeTokenProvider(initialToken = "user-active-jwt")
            val toggleManager = FeatureToggleManager()

            // Verifica estado inicial da flag
            assertFalse(toggleManager.isEnabled(DefaultFeatureToggle.SAMPLE_NEW_EXPERIENCE))

            // Habilita a flag via override em teste
            toggleManager.setOverride(DefaultFeatureToggle.SAMPLE_NEW_EXPERIENCE, true)
            assertTrue(toggleManager.isEnabled(DefaultFeatureToggle.SAMPLE_NEW_EXPERIENCE))

            // Valida consumo do fake token provider
            assertEquals("user-active-jwt", fakeTokenProvider.getCachedAccessToken())
        }
}
