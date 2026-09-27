package br.com.wgc.core.logging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SensitiveDataMaskerTest {
    @Test
    fun `mask replaces CPF patterns`() {
        val input = "Usuario com CPF 123.456.789-00 logou"
        val masked = SensitiveDataMasker.mask(input)
        assertEquals("Usuario com CPF ***.***.***-** logou", masked)
    }

    @Test
    fun `mask replaces Credit Card numbers`() {
        val input = "Pagamento com cartao 4111 2222 3333 4444 processado"
        val masked = SensitiveDataMasker.mask(input)
        assertEquals("Pagamento com cartao ****-****-****-**** processado", masked)
    }

    @Test
    fun `mask replaces Bearer token`() {
        val input = "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token"
        val masked = SensitiveDataMasker.mask(input)
        assertTrue(masked.contains("Bearer [MASKED_TOKEN]"))
        assertFalse(masked.contains("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"))
    }

    @Test
    fun `mask replaces JSON password and query param password`() {
        val json = """{"user": "gabriel", "password": "mypassword123"}"""
        val query = "https://api.example.com/auth?password=supersecret&user=admin"

        val maskedJson = SensitiveDataMasker.mask(json)
        val maskedQuery = SensitiveDataMasker.mask(query)

        assertTrue(maskedJson.contains(""""password": "[REDACTED]""""))
        assertFalse(maskedJson.contains("mypassword123"))

        assertTrue(maskedQuery.contains("password=[REDACTED]"))
        assertFalse(maskedQuery.contains("supersecret"))
    }

    @Test
    fun `mask masks email address`() {
        val input = "Contact: john.doe@example.com"
        val masked = SensitiveDataMasker.mask(input)
        assertEquals("Contact: j***@example.com", masked)
    }

    @Test
    fun `hashForTracing produces deterministic pseudonymous identifier`() {
        val id = "user-12345"
        val hash1 = SensitiveDataMasker.hashForTracing(id)
        val hash2 = SensitiveDataMasker.hashForTracing(id)

        assertEquals(16, hash1.length)
        assertEquals(hash1, hash2)
        assertFalse(hash1.contains("user-12345"))
    }
}
