package br.com.wgc.core.database.security

import android.util.Base64
import br.com.wgc.core.security.EncryptedSharedPreferencesCore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.util.Base64 as JavaBase64

class DatabaseEncrypterTest {
    private val encryptedStorage: EncryptedSharedPreferencesCore = mockk(relaxed = true)
    private lateinit var encrypter: DatabaseEncrypter

    @Before
    fun setup() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } answers {
            JavaBase64.getEncoder().encodeToString(firstArg())
        }
        every { Base64.decode(any<String>(), any()) } answers {
            JavaBase64.getDecoder().decode(firstArg<String>())
        }
        encrypter = DatabaseEncrypter(encryptedStorage)
    }

    @After
    fun tearDown() {
        unmockkStatic(Base64::class)
    }

    @Test
    fun getOrCreatePassphrase_whenKeyAlreadyExists_shouldReturnDecoded32Bytes() {
        val expected32Bytes = ByteArray(32) { (it + 1).toByte() }
        val base64Key = JavaBase64.getEncoder().encodeToString(expected32Bytes)

        every {
            encryptedStorage.getString(DatabaseEncrypter.KEY_PASSPHRASE, any())
        } returns base64Key

        val result = encrypter.getOrCreatePassphrase()

        assertEquals(32, result.size)
        assertArrayEquals(expected32Bytes, result)
        verify(exactly = 0) { encryptedStorage.saveString(any(), any()) }
    }

    @Test
    fun getOrCreatePassphrase_whenNoKeyExists_shouldGenerateAndSave32Bytes() {
        var savedKey: String? = null
        every {
            encryptedStorage.getString(DatabaseEncrypter.KEY_PASSPHRASE, any())
        } returns null
        every {
            encryptedStorage.saveString(DatabaseEncrypter.KEY_PASSPHRASE, any())
        } answers {
            savedKey = secondArg()
        }

        val result = encrypter.getOrCreatePassphrase()

        assertEquals(32, result.size)
        assertNotNull(savedKey)
        val decodedSaved = JavaBase64.getDecoder().decode(savedKey)
        assertArrayEquals(result, decodedSaved)
        verify(exactly = 1) { encryptedStorage.saveString(DatabaseEncrypter.KEY_PASSPHRASE, any()) }
    }

    @Test
    fun getSupportFactory_shouldReturnConfiguredSupportFactory() {
        every {
            encryptedStorage.getString(DatabaseEncrypter.KEY_PASSPHRASE, any())
        } returns JavaBase64.getEncoder().encodeToString(ByteArray(32) { 7 })

        val factory = encrypter.getSupportFactory()
        assertNotNull(factory)
    }
}
