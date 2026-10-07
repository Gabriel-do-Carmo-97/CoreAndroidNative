package br.com.wgc.core.qa.reproducible

/**
 * Enterprise auditor ensuring build artifact determinism and byte-for-byte reproducibility.
 */
class ReproducibleBuildVerifier {
    /**
     * Compares byte contents of two compiled AAR or JAR archives.
     */
    fun areArtifactsIdentical(
        artifactOneBytes: ByteArray,
        artifactTwoBytes: ByteArray,
    ): Boolean {
        return artifactOneBytes.contentEquals(artifactTwoBytes)
    }
}
