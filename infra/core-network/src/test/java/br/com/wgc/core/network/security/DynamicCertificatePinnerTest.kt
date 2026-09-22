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
}
