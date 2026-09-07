package br.com.wgc.core.analytics.consent

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import javax.inject.Inject
import javax.inject.Singleton

enum class ConsentType {
    ANALYTICS,
    CRASH_REPORTING,
    MARKETING,
    PERSONALIZATION
}

@Singleton
class LgpdConsentManager @Inject constructor(
    private val storage: KeyValueStorage
) {
    fun setConsent(type: ConsentType, granted: Boolean) {
        storage.saveBoolean("consent_${type.name}", granted)
    }

    fun isConsentGranted(type: ConsentType): Boolean {
        return storage.getBoolean("consent_${type.name}", false)
    }

    fun revokeAllConsents() {
        ConsentType.entries.forEach { type ->
            setConsent(type, false)
        }
    }
}
