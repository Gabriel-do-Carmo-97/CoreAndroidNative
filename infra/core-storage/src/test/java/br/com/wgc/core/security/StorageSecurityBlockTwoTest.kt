package br.com.wgc.core.security

import br.com.wgc.core.security.attestation.KeyAttestationVerifier
import br.com.wgc.core.security.identity.DefaultIdentityCredentialHelper
import br.com.wgc.core.security.memory.NativeMemoryWiper
import br.com.wgc.core.security.passkey.DefaultPasskeyCredentialHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer

class StorageSecurityBlockTwoTest {
    @Test
    fun testPasskeyChallengeVerification() {
        val helper = DefaultPasskeyCredentialHelper()
        val clientData = "{\"type\":\"webauthn.get\",\"challenge\":\"dGVzdC1jaGFsbGVuZ2U\"}".toByteArray(Charsets.UTF_8)
        assertTrue(helper.verifyChallenge(clientData, "dGVzdC1jaGFsbGVuZ2U"))
        assertFalse(helper.verifyChallenge(clientData, "wrong-challenge"))
    }

    @Test
    fun testIdentityCredentialHelperDocType() {
        val helper = DefaultIdentityCredentialHelper()
        assertTrue(helper.isValidMdlDocType("org.iso.18013.5.mDL"))
        assertFalse(helper.isValidMdlDocType("org.iso.unknown"))
    }

    @Test
    fun testKeyAttestationEmptyCertificates() {
        val verifier = KeyAttestationVerifier()
        val result = verifier.verifyChain(emptyArray())
        assertFalse(result.isValidChain)
        assertFalse(result.isHardwareBacked)
        assertEquals(KeyAttestationVerifier.SECURITY_LEVEL_UNKNOWN, result.attestationSecurityLevel)
    }

    @Test
    fun testNativeMemoryWiperByteBuffer() {
        val buffer = ByteBuffer.allocateDirect(16)
        buffer.put(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16))
        buffer.flip()

        NativeMemoryWiper.wipe(buffer)

        buffer.flip()
        while (buffer.hasRemaining()) {
            assertEquals(0.toByte(), buffer.get())
        }
    }
}
