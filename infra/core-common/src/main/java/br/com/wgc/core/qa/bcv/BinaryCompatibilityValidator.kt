package br.com.wgc.core.qa.bcv

/**
 * Representation of a public ABI declaration element.
 */
data class PublicAbiMember(
    val signature: String,
    val isBinaryCompatible: Boolean = true,
)

/**
 * Enterprise validator evaluating breaking changes in Kotlin public binary API surface.
 */
class BinaryCompatibilityValidator {
    /**
     * Compares previous public ABI declarations against current ABI surface.
     * Returns list of removed or modified incompatible declarations.
     */
    fun findBreakingChanges(
        previousSurface: Set<String>,
        currentSurface: Set<String>,
    ): Set<String> {
        // Any declaration present in previous surface that is missing in current surface constitutes a break
        return previousSurface - currentSurface
    }
}
