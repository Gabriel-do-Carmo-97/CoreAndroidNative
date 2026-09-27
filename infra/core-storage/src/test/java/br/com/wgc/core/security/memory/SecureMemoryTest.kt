package br.com.wgc.core.security.memory

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureMemoryTest {
    @Test
    fun `wipe byte array replaces content with zeros`() {
        val original = byteArrayOf(1, 2, 3, 4, 5)
        SecureMemory.wipe(original)
        assertArrayEquals(ByteArray(5) { 0 }, original)
    }

    @Test
    fun `wipe char array replaces content with null characters`() {
        val original = charArrayOf('p', 'a', 's', 's')
        SecureMemory.wipe(original)
        assertArrayEquals(CharArray(4) { '\u0000' }, original)
    }

    @Test
    fun `SecureByteArray wipes on close`() {
        val raw = byteArrayOf(10, 20, 30)
        val secure = SecureByteArray(raw)
        assertEquals(3, secure.size)
        assertEquals(10.toByte(), secure.bytes[0])

        secure.close()
        assertTrue(secure.isClosed())
        assertArrayEquals(byteArrayOf(0, 0, 0), raw)
        assertThrows(IllegalStateException::class.java) {
            secure.bytes
        }
    }

    @Test
    fun `SecureCharArray wipes on close`() {
        val raw = "secret".toCharArray()
        val secure = SecureCharArray(raw)
        assertEquals(6, secure.length)
        assertEquals('s', secure.chars[0])

        secure.close()
        assertTrue(secure.isClosed())
        assertArrayEquals(CharArray(6) { '\u0000' }, raw)
        assertThrows(IllegalStateException::class.java) {
            secure.chars
        }
    }
}
