package br.com.wgc.core.security.identity

import java.security.PublicKey

/**
 * Presentation request for identity attributes according to ISO/IEC 18013-5 mDL standard.
 *
 * @property docType ISO/IEC 18013-5 doc type (e.g., "org.iso.18013.5.mDL").
 * @property requestedNamespaces Mapping of namespace to list of element identifiers requested.
 * @property readerAuthPublicKey Reader's authenticated public key for verification.
 */
data class IdentityPresentationRequest(
    val docType: String,
    val requestedNamespaces: Map<String, List<String>>,
    val readerAuthPublicKey: PublicKey? = null,
)

/**
 * Result of credential presentation exchange.
 *
 * @property docType The document type presented.
 * @property issuerSignedData Raw signed payload from credential issuer.
 * @property deviceSignedData Device authentication signature data.
 */
data class IdentityPresentationResponse(
    val docType: String,
    val issuerSignedData: ByteArray,
    val deviceSignedData: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdentityPresentationResponse
        return docType == other.docType &&
            issuerSignedData.contentEquals(other.issuerSignedData) &&
            deviceSignedData.contentEquals(other.deviceSignedData)
    }

    override fun hashCode(): Int {
        var result = docType.hashCode()
        result = 31 * result + issuerSignedData.contentHashCode()
        result = 31 * result + deviceSignedData.contentHashCode()
        return result
    }
}

/**
 * Interface for ISO/IEC 18013-5 / ISO/IEC 23220 Digital Credentials presentation.
 */
interface IdentityCredentialHelper {
    /**
     * Checks if hardware-backed digital identity credentials storage is supported on this device.
     */
    fun isIdentityCredentialSupported(): Boolean

    /**
     * Verifies whether requested docType matches standard ISO/IEC 18013-5 mDL specification.
     */
    fun isValidMdlDocType(docType: String): Boolean
}

/**
 * Default implementation of [IdentityCredentialHelper].
 */
class DefaultIdentityCredentialHelper : IdentityCredentialHelper {
    override fun isIdentityCredentialSupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R
    }

    override fun isValidMdlDocType(docType: String): Boolean {
        return docType == MDL_DOC_TYPE
    }

    companion object {
        const val MDL_DOC_TYPE = "org.iso.18013.5.mDL"
    }
}
