package br.com.wgc.core.sharedPreferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryKeyValueStorageTest {
    private val storage = InMemoryKeyValueStorage()

    @Test
    fun should_store_and_retrieve_primitive_types() {
        storage.saveString("username", "gabriel")
        storage.saveInt("user_age", 27)
        storage.saveBoolean("is_admin", true)
        storage.saveLong("created_at", 1700000000L)
        storage.saveFloat("score", 9.8f)

        assertEquals("gabriel", storage.getString("username"))
        assertEquals(27, storage.getInt("user_age"))
        assertTrue(storage.getBoolean("is_admin"))
        assertEquals(1700000000L, storage.getLong("created_at"))
        assertEquals(9.8f, storage.getFloat("score"), 0.001f)
    }

    @Test
    fun should_handle_defaults_and_removal() {
        assertNull(storage.getString("unknown_key"))
        assertEquals("default_fallback", storage.getString("unknown_key", "default_fallback"))

        storage.saveString("temp", "value")
        assertTrue(storage.contains("temp"))

        storage.remove("temp")
        assertFalse(storage.contains("temp"))
        assertNull(storage.getString("temp"))
    }

    @Test
    fun should_handle_string_sets_and_clear() {
        val permissions = setOf("READ", "WRITE", "DELETE")
        storage.saveStringSet("perms", permissions)

        assertEquals(permissions, storage.getStringSet("perms"))

        storage.clear()
        assertFalse(storage.contains("perms"))
        assertEquals(0, storage.getAll().size)
    }
}
