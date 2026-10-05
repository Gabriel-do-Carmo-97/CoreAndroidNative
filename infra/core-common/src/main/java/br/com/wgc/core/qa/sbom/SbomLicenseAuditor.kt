package br.com.wgc.core.qa.sbom

/**
 * Software Bill of Materials (SBOM) component metadata conforming to CycloneDX / SPDX specifications.
 *
 * @property name Component / library name.
 * @property version Semantic version string.
 * @property purl Package URL identifier (e.g. "pkg:maven/androidx.core/core-ktx@1.19.0").
 * @property license Open-source license descriptor (e.g. "Apache-2.0").
 */
data class SbomComponent(
    val name: String,
    val version: String,
    val purl: String,
    val license: String,
)

/**
 * Enterprise auditor validating SBOM components against known vulnerable or forbidden licenses.
 */
class SbomLicenseAuditor(
    private val forbiddenLicenses: Set<String> = setOf("GPL-3.0", "AGPL-3.0"),
) {
    /**
     * Checks if any dependency violates organizational licensing policies.
     */
    fun audit(components: List<SbomComponent>): List<SbomComponent> {
        return components.filter { it.license in forbiddenLicenses }
    }
}
