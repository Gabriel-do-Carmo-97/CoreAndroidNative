package br.com.wgc.core.dataStorePreferences

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DataStorePreferencesCoreTest {
    private lateinit var context: Context
    private lateinit var dataStoreCore: DataStorePreferencesCore

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dataStoreCore =
            DataStorePreferencesCore(
                context = context,
                dataStoreName = "test_datastore_${System.currentTimeMillis()}",
            )
    }

    @Test
    fun saveAndGetString_returnsSavedValue() =
        runTest {
            dataStoreCore.saveString("username", "Gabriel")

            dataStoreCore.getStringFlow("username").test {
                assertEquals("Gabriel", awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveAndGetInt_returnsSavedValue() =
        runTest {
            dataStoreCore.saveInt("user_id", 42)

            dataStoreCore.getIntFlow("user_id").test {
                assertEquals(42, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveAndGetBoolean_returnsSavedValue() =
        runTest {
            dataStoreCore.saveBoolean("is_admin", true)

            dataStoreCore.getBooleanFlow("is_admin").test {
                assertTrue(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveAndGetFloat_returnsSavedValue() =
        runTest {
            dataStoreCore.saveFloat("score", 99.5f)

            dataStoreCore.getFloatFlow("score").test {
                assertEquals(99.5f, awaitItem(), 0.001f)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveAndGetLong_returnsSavedValue() =
        runTest {
            dataStoreCore.saveLong("timestamp", 1234567890L)

            dataStoreCore.getLongFlow("timestamp").test {
                assertEquals(1234567890L, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveAndGetStringSet_returnsSavedSetWithCommasCorrectly() =
        runTest {
            val originalSet = setOf("apple,banana", "orange", "grape,berry")
            dataStoreCore.saveStringSet("fruits", originalSet)

            dataStoreCore.getStringSetFlow("fruits").test {
                assertEquals(originalSet, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun containsKey_returnsTrueWhenKeyExists() =
        runTest {
            dataStoreCore.saveString("existing_key", "value")

            assertTrue(dataStoreCore.containsKey("existing_key"))
            assertFalse(dataStoreCore.containsKey("non_existing_key"))
        }

    @Test
    fun removeKey_removesKeyFromDataStore() =
        runTest {
            dataStoreCore.saveInt("points", 100)
            assertTrue(dataStoreCore.containsKey("points"))

            dataStoreCore.removeKey("points")

            assertFalse(dataStoreCore.containsKey("points"))
        }

    @Test
    fun clear_removesAllKeys() =
        runTest {
            dataStoreCore.saveString("key1", "val1")
            dataStoreCore.saveInt("key2", 2)

            dataStoreCore.clear()

            assertFalse(dataStoreCore.containsKey("key1"))
            assertFalse(dataStoreCore.containsKey("key2"))
        }
}
