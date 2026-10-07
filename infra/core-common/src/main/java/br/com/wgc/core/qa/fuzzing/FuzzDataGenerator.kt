package br.com.wgc.core.qa.fuzzing

import kotlin.random.Random

/**
 * Enterprise fuzzer driver generating random mutated byte streams for security boundary testing.
 */
class FuzzDataGenerator(
    private val seed: Long = DEFAULT_SEED,
) {
    private val random = Random(seed)

    /**
     * Generates a random byte array of size [length].
     */
    fun generateRandomBytes(length: Int): ByteArray {
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return bytes
    }

    /**
     * Mutates input byte array by flipping bits or inserting corrupt bytes.
     */
    fun mutate(input: ByteArray): ByteArray {
        if (input.isEmpty()) return byteArrayOf(0)
        val copy = input.copyOf()
        val indexToMutate = random.nextInt(copy.size)
        copy[indexToMutate] = (copy[indexToMutate].toInt() xor (1 shl random.nextInt(BITS_PER_BYTE))).toByte()
        return copy
    }

    companion object {
        private const val DEFAULT_SEED = 42L
        private const val BITS_PER_BYTE = 8
    }
}
