package br.com.wgc.core.security.postquantum

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PostQuantumHybridKdfTest {
    private val kdf = PostQuantumHybridKdf()

    @Test
    fun `deriveHybridKey outputs expected length`() {
        val classical = "classical-secret-ikm".toByteArray()
        val pqSecret = kdf.generateQuantumEntropy(32)

        val key = kdf.deriveHybridKey(classical, pqSecret, outputLength = 32)
        assertEquals(32, key.size)
    }

    @Test
    fun `deriveHybridKey is deterministic given identical inputs`() {
        val classical = "same-classical".toByteArray()
        val pqSecret = ByteArray(32) { 42 }
        val salt = ByteArray(32) { 7 }

        val key1 = kdf.deriveHybridKey(classical, pqSecret, salt)
        val key2 = kdf.deriveHybridKey(classical, pqSecret, salt)

        assertEquals(key1.toList(), key2.toList())
    }

    @Test
    fun `deriveHybridKey produces different outputs with different quantum secrets`() {
        val classical = "same-classical".toByteArray()
        val pqSecret1 = ByteArray(32) { 1 }
        val pqSecret2 = ByteArray(32) { 2 }

        val key1 = kdf.deriveHybridKey(classical, pqSecret1)
        val key2 = kdf.deriveHybridKey(classical, pqSecret2)

        assertNotEquals(key1.toList(), key2.toList())
    }

    @Test
    fun `deriveHybridKey throws on empty secrets`() {
        assertThrows(IllegalArgumentException::class.java) {
            kdf.deriveHybridKey(ByteArray(0), ByteArray(32))
        }
        assertThrows(IllegalArgumentException::class.java) {
            kdf.deriveHybridKey(ByteArray(32), ByteArray(0))
        }
    }
}
