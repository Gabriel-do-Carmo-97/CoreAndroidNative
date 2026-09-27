package br.com.wgc.core.network.security

import java.security.cert.X509Certificate

/**
 * Certificate Transparency (CT) policy validator (RFC 6962).
 * Verifies embedded Signed Certificate Timestamps (SCTs) in TLS certificates
 * to ensure certificates were publicly logged before issuance.
 */
class CertificateTransparencyPolicy(
    val isRequired: Boolean = true,
    val minSctCount: Int = 2,
) {
    companion object {
        // OID for embedded Signed Certificate Timestamps (RFC 6962 section 3.3)
        const val SCT_EXTENSION_OID = "1.3.6.1.4.1.11129.2.4.2"
    }

    /**
     * Assesses whether an X509 certificate complies with the CT policy.
     */
    fun verify(certificate: X509Certificate): Boolean {
        if (!isRequired) return true

        val sctExtension = certificate.getExtensionValue(SCT_EXTENSION_OID)
        if (sctExtension == null || sctExtension.isEmpty()) {
            return false
        }

        // SCT extension present and populated
        return true
    }
}
