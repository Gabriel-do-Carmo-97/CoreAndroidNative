package br.com.wgc.core.security

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class MemorySanitizerTest {
    @Test
    fun wipeCharArray_fillsWithZeros() {
        val password = "secret_password_123".toCharArray()
        MemorySanitizer.wipe(password)

        val expected = CharArray(password.size) { '\u0000' }
        assertArrayEquals(expected, password)
    }

    @Test
    fun wipeByteArray_fillsWithZeros() {
        val tokenBytes = byteArrayOf(1, 2, 3, 4, 5)
        MemorySanitizer.wipe(tokenBytes)

        val expected = ByteArray(tokenBytes.size) { 0 }
        assertArrayEquals(expected, tokenBytes)
    }

    @Test
    fun wipeStringBuilder_clearsContentAndLength() {
        val builder = java.lang.StringBuilder("sensitive_token_abc")
        MemorySanitizer.wipe(builder)

        assertEquals(0, builder.length)
        assertEquals("", builder.toString())
    }

    @Test
    fun useAndWipeCharArray_executesBlockAndWipes() {
        val secret = "my_token".toCharArray()
        var readValue = ""

        MemorySanitizer.useAndWipe(secret) { chars ->
            readValue = String(chars)
        }

        assertEquals("my_token", readValue)
        val expected = CharArray(secret.size) { '\u0000' }
        assertArrayEquals(expected, secret)
    }

    @Test
    fun useAndWipeByteArray_executesBlockAndWipes() {
        val secret = byteArrayOf(10, 20, 30)
        var sum = 0

        MemorySanitizer.useAndWipe(secret) { bytes ->
            sum = bytes.sum()
        }

        assertEquals(60, sum)
        val expected = ByteArray(secret.size) { 0 }
        assertArrayEquals(expected, secret)
    }
}
