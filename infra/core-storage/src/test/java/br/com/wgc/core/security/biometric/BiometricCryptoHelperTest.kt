package br.com.wgc.core.security.biometric

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BiometricCryptoHelperTest {
    @Test
    fun getOrCreateSecretKey_generatesValidKey() {
        val helper = BiometricCryptoHelper(keyAlias = "test_key_alias")
        val key = helper.getOrCreateSecretKey()

        assertNotNull(key)
    }

    @Test
    fun createEncryptCryptoObject_returnsCryptoObject() {
        val helper = BiometricCryptoHelper(keyAlias = "test_encrypt_alias")
        val cryptoObject = helper.createEncryptCryptoObject()

        assertNotNull(cryptoObject)
        assertNotNull(cryptoObject.cipher)
    }
}
