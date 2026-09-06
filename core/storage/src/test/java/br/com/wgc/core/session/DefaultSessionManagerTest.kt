package br.com.wgc.core.session

import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.dataStorePreferences.KeyValueDataStore
import br.com.wgc.core.file.FileManager
import br.com.wgc.core.sharedPreferences.KeyValueStorage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultSessionManagerTest {

    private val keyValueDataStore: KeyValueDataStore = mockk(relaxed = true)
    private val defaultStorage: KeyValueStorage = mockk(relaxed = true)
    private val encryptedStorage: KeyValueStorage = mockk(relaxed = true)
    private val fileManager: FileManager = mockk(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = object : CoroutineDispatchers {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
        override val unconfined = testDispatcher
    }

    private lateinit var sessionManager: DefaultSessionManager

    @Before
    fun setup() {
        sessionManager = DefaultSessionManager(
            keyValueDataStore = keyValueDataStore,
            defaultStorage = defaultStorage,
            encryptedStorage = encryptedStorage,
            fileManager = fileManager,
            dispatchers = dispatchers
        )
    }

    @Test
    fun clearSession_whenClearCacheIsTrue_shouldPurgeAllStoragesAndCache() = runTest {
        coEvery { keyValueDataStore.clear() } returns Unit
        every { defaultStorage.clear() } returns Unit
        every { encryptedStorage.clear() } returns Unit
        every { fileManager.clearCache() } returns true

        sessionManager.clearSession(clearCache = true)

        coVerify(exactly = 1) { keyValueDataStore.clear() }
        verify(exactly = 1) { defaultStorage.clear() }
        verify(exactly = 1) { encryptedStorage.clear() }
        verify(exactly = 1) { fileManager.clearCache() }
    }

    @Test
    fun clearSession_whenClearCacheIsFalse_shouldPurgeStoragesWithoutClearingCache() = runTest {
        coEvery { keyValueDataStore.clear() } returns Unit
        every { defaultStorage.clear() } returns Unit
        every { encryptedStorage.clear() } returns Unit

        sessionManager.clearSession(clearCache = false)

        coVerify(exactly = 1) { keyValueDataStore.clear() }
        verify(exactly = 1) { defaultStorage.clear() }
        verify(exactly = 1) { encryptedStorage.clear() }
        verify(exactly = 0) { fileManager.clearCache() }
    }
}
