package br.com.wgc.core.featureflag

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureToggleManagerTest {
    private enum class TestToggles(
        override val key: String,
        override val defaultValue: Boolean,
    ) : FeatureToggle {
        ENABLED_BY_DEFAULT("flag_enabled_default", true),
        DISABLED_BY_DEFAULT("flag_disabled_default", false),
    }

    @Test
    fun `isEnabled should return default value when no provider or override exists`() {
        val manager = FeatureToggleManager()

        assertTrue(manager.isEnabled(TestToggles.ENABLED_BY_DEFAULT))
        assertFalse(manager.isEnabled(TestToggles.DISABLED_BY_DEFAULT))
    }

    @Test
    fun `isEnabled should return remote provider value when available`() {
        val fakeProvider =
            FeatureToggleProvider { key ->
                if (key == TestToggles.DISABLED_BY_DEFAULT.key) true else null
            }
        val manager = FeatureToggleManager(remoteProvider = fakeProvider)

        assertTrue(manager.isEnabled(TestToggles.DISABLED_BY_DEFAULT))
        assertTrue(manager.isEnabled(TestToggles.ENABLED_BY_DEFAULT))
    }

    @Test
    fun `local override should take precedence over remote provider and default`() {
        val fakeProvider = FeatureToggleProvider { true }
        val manager = FeatureToggleManager(remoteProvider = fakeProvider)

        manager.setOverride(TestToggles.DISABLED_BY_DEFAULT, false)
        assertFalse(manager.isEnabled(TestToggles.DISABLED_BY_DEFAULT))

        manager.clearOverride(TestToggles.DISABLED_BY_DEFAULT)
        assertTrue(manager.isEnabled(TestToggles.DISABLED_BY_DEFAULT))
    }

    @Test
    fun `clearAllOverrides should remove all local overrides`() {
        val manager = FeatureToggleManager()

        manager.setOverride(TestToggles.DISABLED_BY_DEFAULT, true)
        manager.setOverride(TestToggles.ENABLED_BY_DEFAULT, false)

        manager.clearAllOverrides()

        assertFalse(manager.isEnabled(TestToggles.DISABLED_BY_DEFAULT))
        assertTrue(manager.isEnabled(TestToggles.ENABLED_BY_DEFAULT))
    }
}
