package br.com.wgc.core.cache.prefetch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrefetchCacheManagerTest {
    @Test
    fun `put and get stores and retrieves valid item`() {
        val manager = PrefetchCacheManager<String, String>()
        manager.put("user_1", "Gabriel", ttlMs = 10_000L)

        assertEquals("Gabriel", manager.get("user_1"))
        assertEquals(1, manager.size())

        manager.invalidate("user_1")
        assertNull(manager.get("user_1"))
        assertEquals(0, manager.size())
    }

    @Test
    fun `get returns null and removes expired item`() {
        val manager = PrefetchCacheManager<String, String>()
        manager.put("temp", "value", ttlMs = -1L) // expired immediately

        assertNull(manager.get("temp"))
        assertEquals(0, manager.size())
    }

    @Test
    fun `shouldPrefetch evaluates conditions based on policy`() {
        val manager = PrefetchCacheManager<String, String>()
        // If not in cache, shouldPrefetch returns true
        assertTrue(manager.shouldPrefetch("missing_key"))

        // With fresh item (0% elapsed), should not prefetch
        manager.put("item", "data", ttlMs = 100_000L)
        assertFalse(manager.shouldPrefetch("item", isMeteredNetwork = false, batteryPercent = 1.0f))

        // If battery is low (< 20%), should not prefetch
        assertFalse(manager.shouldPrefetch("item", isMeteredNetwork = false, batteryPercent = 0.10f))

        // If metered network, should not prefetch
        assertFalse(manager.shouldPrefetch("item", isMeteredNetwork = true, batteryPercent = 1.0f))
    }
}
