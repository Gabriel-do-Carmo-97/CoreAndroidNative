package br.com.wgc.core.security.memory

import java.security.SecureRandom
import java.util.Arrays

/**
 * Utility for erasing cryptographic material and sensitive data from volatile memory (RAM).
 * Mitigates memory dump attacks and heap inspection.
 */
object SecureMemory {
    private val secureRandom = SecureRandom()

    /**
     * Overwrites byte array with random bytes and then zeros it out completely.
     */
    fun wipe(data: ByteArray?) {
        if (data == null || data.isEmpty()) return
        val randomPadding = ByteArray(data.size)
        secureRandom.nextBytes(randomPadding)
        System.arraycopy(randomPadding, 0, data, 0, data.size)
        Arrays.fill(data, 0.toByte())
        Arrays.fill(randomPadding, 0.toByte())
    }

    /**
     * Overwrites char array with zeros.
     */
    fun wipe(data: CharArray?) {
        if (data == null || data.isEmpty()) return
        Arrays.fill(data, '\u0000')
    }
}

/**
 * AutoCloseable container for sensitive byte arrays that guarantees zeroization upon close.
 */
class SecureByteArray(
    private val buffer: ByteArray,
) : AutoCloseable {
    private var closed = false

    val bytes: ByteArray
        get() {
            check(!closed) { "SecureByteArray has already been wiped and closed" }
            return buffer
        }

    val size: Int get() = buffer.size

    override fun close() {
        if (!closed) {
            SecureMemory.wipe(buffer)
            closed = true
        }
    }

    fun isClosed(): Boolean = closed
}

/**
 * AutoCloseable container for sensitive character arrays (e.g. passwords, pin codes).
 */
class SecureCharArray(
    private val buffer: CharArray,
) : AutoCloseable {
    private var closed = false

    val chars: CharArray
        get() {
            check(!closed) { "SecureCharArray has already been wiped and closed" }
            return buffer
        }

    val length: Int get() = buffer.size

    override fun close() {
        if (!closed) {
            SecureMemory.wipe(buffer)
            closed = true
        }
    }

    fun isClosed(): Boolean = closed
}
