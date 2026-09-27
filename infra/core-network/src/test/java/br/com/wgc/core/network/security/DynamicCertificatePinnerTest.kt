package br.com.wgc.core.network.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DynamicCertificatePinnerTest {
    @Test
    fun addAndGetPins_persistsCorrectly() {
        val pinner = DynamicCertificatePinner()
        val pattern = "api.example.com"
        val pin1 = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
        val pin2 = "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="

        pinner.addPins(pattern, pin1, pin2)

        val retrieved = pinner.getPins(pattern)
        assertEquals(2, retrieved.size)
        assertTrue(retrieved.contains(pin1))
        assertTrue(retrieved.contains(pin2))
    }

    @Test
    fun removePins_clearsPattern() {
        val pinner =
            DynamicCertificatePinner(
                mapOf("api.example.com" to setOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")),
            )

        pinner.removePins("api.example.com")
        assertTrue(pinner.getPins("api.example.com").isEmpty())
    }

    @Test
    fun clear_removesAll() {
        val pinner =
            DynamicCertificatePinner(
                mapOf(
                    "api.a.com" to setOf("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="),
                    "api.b.com" to setOf("sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="),
                ),
            )

        pinner.clear()
        assertTrue(pinner.getAllPins().isEmpty())
    }

    @Test
    fun buildCertificatePinner_returnsValidInstance() {
        val pinner = DynamicCertificatePinner()
        pinner.addPins("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")

        val okHttpPinner = pinner.buildCertificatePinner()
        assertNotNull(okHttpPinner)
    }

    @Test
    fun hasBackupPins_returnsTrueWhenAtLeastTwoPins() {
        val pinner = DynamicCertificatePinner()
        pinner.addPins("api.test.com", "sha256/PIN1", "sha256/PIN2")
        assertTrue(pinner.hasBackupPins("api.test.com"))
    }

    @Test
    fun rotatePinsWithSignature_updatesOnValidSignature() {
        val pinner = DynamicCertificatePinner()
        val secret = "super-secret-key-12345".toByteArray()
        val pattern = "api.test.com"
        val pins = listOf("sha256/NEWPIN1", "sha256/NEWPIN2")

        val payload = "$pattern:" + pins.sorted().joinToString(",")
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(secret, "HmacSHA256"))
        val validSig = mac.doFinal(payload.toByteArray()).joinToString("") { "%02x".format(it) }

        val rotated = pinner.rotatePinsWithSignature(pattern, pins, validSig, secret)
        assertTrue(rotated)
        assertEquals(2, pinner.getPins(pattern).size)
    }

    @Test
    fun rotatePinsWithSignature_rejectsInvalidSignature() {
        val pinner = DynamicCertificatePinner()
        val secret = "super-secret-key-12345".toByteArray()
        val pattern = "api.test.com"
        val pins = listOf("sha256/NEWPIN1")

        val rotated = pinner.rotatePinsWithSignature(pattern, pins, "invalidsig", secret)
        org.junit.Assert.assertFalse(rotated)
    }
}
