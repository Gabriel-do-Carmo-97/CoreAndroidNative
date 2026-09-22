package br.com.wgc.core.security.keyrotation

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyRotationHelperTest {
    private val helper = KeyRotationHelper()

    @Test
    fun `rotate copies data to target storage and removes from source when wipeOldEntries is true`() {
        val sourceStorage = mockk<KeyValueStorage>(relaxed = true)
        val targetStorage = mockk<KeyValueStorage>(relaxed = true)

        every { sourceStorage.getString("auth_token") } returns "old_jwt_token"
        every { sourceStorage.getBoolean("is_biometric_enabled", false) } returns true
        every { sourceStorage.getInt("login_attempts", 0) } returns 3
        every { sourceStorage.getLong("last_login_time", 0L) } returns 1670000000L

        val report =
            helper.rotate(
                sourceStorage = sourceStorage,
                targetStorage = targetStorage,
                stringKeys = listOf("auth_token"),
                booleanKeys = listOf("is_biometric_enabled"),
                intKeys = listOf("login_attempts"),
                longKeys = listOf("last_login_time"),
                wipeOldEntries = true,
            )

        assertTrue(report.isSuccessful)
        assertEquals(4, report.keysMigrated)

        verify { targetStorage.saveString("auth_token", "old_jwt_token") }
        verify { targetStorage.saveBoolean("is_biometric_enabled", true) }
        verify { targetStorage.saveInt("login_attempts", 3) }
        verify { targetStorage.saveLong("last_login_time", 1670000000L) }

        verify { sourceStorage.remove("auth_token") }
        verify { sourceStorage.remove("is_biometric_enabled") }
        verify { sourceStorage.remove("login_attempts") }
        verify { sourceStorage.remove("last_login_time") }
    }

    @Test
    fun `rotate handles exceptions gracefully and reports failure`() {
        val sourceStorage = mockk<KeyValueStorage>()
        val targetStorage = mockk<KeyValueStorage>()

        every { sourceStorage.getString("secret") } throws IllegalStateException("Corrupted storage")

        val report =
            helper.rotate(
                sourceStorage = sourceStorage,
                targetStorage = targetStorage,
                stringKeys = listOf("secret"),
            )

        assertFalse(report.isSuccessful)
        assertEquals(0, report.keysMigrated)
        assertEquals("Corrupted storage", report.error?.message)
    }
}
