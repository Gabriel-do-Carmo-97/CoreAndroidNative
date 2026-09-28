package br.com.wgc.core.security.postquantum

import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Hybrid Post-Quantum Key Derivation Function (HKDF + Lattice entropy combination).
 * Follows the NIST PQC hybrid recommendation: combines classical input keying material (IKM)
 * with quantum-resistant entropy to derive forward-secure 256-bit symmetric keys.
 */
class PostQuantumHybridKdf {
    companion object {
        private const val HMAC_ALGORITHM = "HmacSHA512"
        private const val DEFAULT_OUTPUT_KEY_SIZE = 32 // 256 bits
        private const val MIN_OUTPUT_KEY_SIZE = 16
        private const val MAX_OUTPUT_KEY_SIZE = 64
    }

    /**
     * Derives a 256-bit key from classical key material and post-quantum shared secret.
     */
    fun deriveHybridKey(
        classicalIkm: ByteArray,
        postQuantumSecret: ByteArray,
        salt: ByteArray? = null,
        info: ByteArray? = null,
        outputLength: Int = DEFAULT_OUTPUT_KEY_SIZE,
    ): ByteArray {
        require(classicalIkm.isNotEmpty()) { "Classical IKM cannot be empty" }
        require(postQuantumSecret.isNotEmpty()) { "Post-quantum secret cannot be empty" }
        require(outputLength in MIN_OUTPUT_KEY_SIZE..MAX_OUTPUT_KEY_SIZE) {
            "Output key size must be between $MIN_OUTPUT_KEY_SIZE and $MAX_OUTPUT_KEY_SIZE bytes"
        }

        // Step 1: Combine classical + post-quantum secrets into combined IKM
        val combinedIkm = ByteArray(classicalIkm.size + postQuantumSecret.size)
        System.arraycopy(classicalIkm, 0, combinedIkm, 0, classicalIkm.size)
        System.arraycopy(postQuantumSecret, 0, combinedIkm, classicalIkm.size, postQuantumSecret.size)

        // Step 2: HKDF-Extract
        val effectiveSalt = salt ?: ByteArray(64) { 0 }
        val prk = hkdfExtract(effectiveSalt, combinedIkm)

        // Step 3: HKDF-Expand
        val effectiveInfo = info ?: "WGC_CORE_POST_QUANTUM_HYBRID_KDF_V1".toByteArray(Charsets.UTF_8)
        return hkdfExpand(prk, effectiveInfo, outputLength)
    }

    /**
     * Generates cryptographically secure random entropy suitable for PQC ephemeral secrets.
     */
    fun generateQuantumEntropy(sizeBytes: Int = 32): ByteArray {
        val bytes = ByteArray(sizeBytes)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    private fun hkdfExtract(
        salt: ByteArray,
        ikm: ByteArray,
    ): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(salt, HMAC_ALGORITHM))
        return mac.doFinal(ikm)
    }

    private fun hkdfExpand(
        prk: ByteArray,
        info: ByteArray,
        length: Int,
    ): ByteArray {
        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(prk, HMAC_ALGORITHM))

        val output = ByteArray(length)
        var t = ByteArray(0)
        var offset = 0
        var iteration: Byte = 1

        while (offset < length) {
            mac.reset()
            mac.update(t)
            mac.update(info)
            mac.update(iteration)
            t = mac.doFinal()

            val toCopy = minOf(t.size, length - offset)
            System.arraycopy(t, 0, output, offset, toCopy)
            offset += toCopy
            iteration++
        }

        return output
    }
}
