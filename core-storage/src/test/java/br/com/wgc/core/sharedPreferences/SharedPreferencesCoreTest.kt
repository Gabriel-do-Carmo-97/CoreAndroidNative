package br.com.wgc.core.sharedPreferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SharedPreferencesCoreTest {

    private lateinit var context: Context
    private lateinit var sharedPreferencesCore: SharedPreferencesCore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        sharedPreferencesCore = SharedPreferencesCore(
            context = context,
            sharedPreferencesName = "test_prefs_${System.currentTimeMillis()}"
        )
    }

    @Test
    fun saveAndGetString_returnsSavedValue() {
        sharedPreferencesCore.saveString("key_string", "test_value")
        assertEquals("test_value", sharedPreferencesCore.getString("key_string"))
    }

    @Test
    fun saveAndGetInt_returnsSavedValue() {
        sharedPreferencesCore.saveInt("key_int", 100)
        assertEquals(100, sharedPreferencesCore.getInt("key_int"))
    }

    @Test
    fun saveAndGetBoolean_returnsSavedValue() {
        sharedPreferencesCore.saveBoolean("key_bool", true)
        assertTrue(sharedPreferencesCore.getBoolean("key_bool"))
    }

    @Test
    fun saveAndGetFloat_returnsSavedValue() {
        sharedPreferencesCore.saveFloat("key_float", 3.14f)
        assertEquals(3.14f, sharedPreferencesCore.getFloat("key_float"), 0.001f)
    }

    @Test
    fun saveAndGetLong_returnsSavedValue() {
        sharedPreferencesCore.saveLong("key_long", 999999999L)
        assertEquals(999999999L, sharedPreferencesCore.getLong("key_long"))
    }

    @Test
    fun saveAndGetStringSet_returnsSavedValue() {
        val set = setOf("a", "b", "c")
        sharedPreferencesCore.saveStringSet("key_set", set)
        assertEquals(set, sharedPreferencesCore.getStringSet("key_set"))
    }

    @Test
    fun containsAndRemove_worksCorrectly() {
        sharedPreferencesCore.saveString("temp_key", "data")
        assertTrue(sharedPreferencesCore.contains("temp_key"))

        sharedPreferencesCore.remove("temp_key")
        assertFalse(sharedPreferencesCore.contains("temp_key"))
        assertNull(sharedPreferencesCore.getString("temp_key"))
    }

    @Test
    fun clear_removesAllData() {
        sharedPreferencesCore.saveString("k1", "v1")
        sharedPreferencesCore.saveInt("k2", 2)

        sharedPreferencesCore.clear()

        assertFalse(sharedPreferencesCore.contains("k1"))
        assertFalse(sharedPreferencesCore.contains("k2"))
    }
}
