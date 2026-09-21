package br.com.wgc.core.storage.cache

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TwoLevelCacheTest {
    private lateinit var cache: TwoLevelCache<String, String>

    @Before
    fun setUp() {
        cache = TwoLevelCache(maxMemoryEntries = 5)
    }

    @Test
    fun `put and get should return stored value`() =
        runTest {
            cache.put("user_1", "Gabriel")

            val result = cache.get("user_1")
            assertNotNull(result)
            assertEquals("Gabriel", result)
        }

    @Test
    fun `evict and clear should remove items properly`() =
        runTest {
            cache.put("item_a", "val_a")
            cache.put("item_b", "val_b")
            assertEquals(2, cache.size())

            cache.evict("item_a")
            assertNull(cache.get("item_a"))
            assertEquals("val_b", cache.get("item_b"))

            cache.clear()
            assertEquals(0, cache.size())
            assertNull(cache.get("item_b"))
        }

    @Test
    fun `entry with expired TTL should return null`() =
        runTest {
            // TTL de -1ms já expirado no momento da leitura
            cache.put("session_token", "secret", ttlMs = -100L)

            val result = cache.get("session_token")
            assertNull(result)
        }
}
