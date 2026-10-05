package br.com.wgc.core.qa.security

/**
 * Enterprise auditor inspecting security headers returned by backend API responses.
 */
class SecurityHeadersAuditor {
    private val mandatoryHeaders =
        setOf(
            "Strict-Transport-Security",
            "X-Content-Type-Options",
            "X-Frame-Options",
            "Content-Security-Policy",
        )

    /**
     * Identifies missing security headers from the given response headers map.
     */
    fun findMissingHeaders(responseHeaders: Map<String, String>): Set<String> {
        val lowerCaseKeys = responseHeaders.keys.map { it.lowercase() }.toSet()
        return mandatoryHeaders.filter { it.lowercase() !in lowerCaseKeys }.toSet()
    }
}
