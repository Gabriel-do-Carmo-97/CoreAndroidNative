package br.com.wgc.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Teste instrumentado para EncryptedSharedPreferencesCore.
 * Executa em ambiente Android (dispositivo ou emulador) com suporte ao AndroidKeyStore.
 */
@RunWith(AndroidJUnit4::class)
class EncryptedSharedPreferencesCoreTest {
    private lateinit var context: Context
    private lateinit var encryptedStorage: EncryptedSharedPreferencesCore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        encryptedStorage =
            EncryptedSharedPreferencesCore(
                context = context,
                fileName = "test_encrypted_prefs_${System.currentTimeMillis()}",
            )
    }

    @Test
    fun saveAndGetString_returnsEncryptedValueCorrectly() {
        encryptedStorage.saveString("auth_token", "jwt.secret.token")
        assertEquals("jwt.secret.token", encryptedStorage.getString("auth_token"))
    }

    @Test
    fun getString_returnsDefaultValueWhenKeyDoesNotExist() {
        assertEquals("default", encryptedStorage.getString("non_existing", "default"))
        assertNull(encryptedStorage.getString("non_existing"))
    }

    @Test
    fun saveAndGetInt_returnsSavedValue() {
        encryptedStorage.saveInt("user_id", 12345)
        assertEquals(12345, encryptedStorage.getInt("user_id"))
    }

    @Test
    fun saveAndGetBoolean_returnsSavedValue() {
        encryptedStorage.saveBoolean("is_authenticated", true)
        assertTrue(encryptedStorage.getBoolean("is_authenticated"))
    }

    @Test
    fun saveAndGetFloat_returnsSavedValue() {
        encryptedStorage.saveFloat("threshold", 12.34f)
        assertEquals(12.34f, encryptedStorage.getFloat("threshold"), 0.001f)
    }

    @Test
    fun saveAndGetLong_returnsSavedValue() {
        encryptedStorage.saveLong("expire_epoch", 1890000000L)
        assertEquals(1890000000L, encryptedStorage.getLong("expire_epoch"))
    }

    @Test
    fun saveAndGetStringSet_returnsSavedSet() {
        val roles = setOf("ADMIN", "DEVELOPER")
        encryptedStorage.saveStringSet("roles", roles)
        assertEquals(roles, encryptedStorage.getStringSet("roles"))
    }

    @Test
    fun containsAndRemove_worksCorrectly() {
        encryptedStorage.saveString("temp_secret", "xyz")
        assertTrue(encryptedStorage.contains("temp_secret"))

        encryptedStorage.remove("temp_secret")
        assertFalse(encryptedStorage.contains("temp_secret"))
        assertNull(encryptedStorage.getString("temp_secret"))
    }

    @Test
    fun clear_removesAllEntries() {
        encryptedStorage.saveString("key1", "val1")
        encryptedStorage.saveInt("key2", 42)

        encryptedStorage.clear()

        assertFalse(encryptedStorage.contains("key1"))
        assertFalse(encryptedStorage.contains("key2"))
    }

    @Test
    fun getAll_returnsMapWithStoredData() {
        encryptedStorage.saveString("k1", "val1")
        val all = encryptedStorage.getAll()
        assertEquals("val1", all["k1"])
    }
}
