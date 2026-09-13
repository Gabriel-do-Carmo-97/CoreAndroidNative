package br.com.wgc.core.analytics.consent

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.wgc.core.sharedPreferences.SharedPreferencesCore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LgpdConsentManagerTest {
    private lateinit var consentManager: LgpdConsentManager

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = SharedPreferencesCore(context, "test_consent_${System.currentTimeMillis()}")
        consentManager = LgpdConsentManager(storage)
    }

    @Test
    fun setConsent_updatesConsentStatus() {
        assertFalse(consentManager.isConsentGranted(ConsentType.ANALYTICS))

        consentManager.setConsent(ConsentType.ANALYTICS, true)
        assertTrue(consentManager.isConsentGranted(ConsentType.ANALYTICS))
    }

    @Test
    fun revokeAllConsents_revokesAllConsents() {
        consentManager.setConsent(ConsentType.ANALYTICS, true)
        consentManager.setConsent(ConsentType.MARKETING, true)

        consentManager.revokeAllConsents()

        assertFalse(consentManager.isConsentGranted(ConsentType.ANALYTICS))
        assertFalse(consentManager.isConsentGranted(ConsentType.MARKETING))
    }
}
