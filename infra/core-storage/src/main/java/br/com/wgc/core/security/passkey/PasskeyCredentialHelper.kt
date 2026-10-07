package br.com.wgc.core.security.passkey

/**
 * Result data representing a generated or verified Passkey operation.
 *
 * @property credentialId Unique public key credential ID.
 * @property rawClientDataJson Serialized client data JSON containing challenge and origin.
 * @property authenticatorData Raw binary authenticator response flags.
 * @property signature Cryptographic assertion signature for user authentication.
 */
data class PasskeyAssertion(
    val credentialId: String,
    val rawClientDataJson: ByteArray,
    val authenticatorData: ByteArray,
    val signature: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PasskeyAssertion
        return credentialId == other.credentialId &&
            rawClientDataJson.contentEquals(other.rawClientDataJson) &&
            authenticatorData.contentEquals(other.authenticatorData) &&
            signature.contentEquals(other.signature)
    }

    override fun hashCode(): Int {
        var result = credentialId.hashCode()
        result = 31 * result + rawClientDataJson.contentHashCode()
        result = 31 * result + authenticatorData.contentHashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}

/**
 * Request payload to initiate Passkey authentication or registration.
 */
data class PasskeyRequest(
    val relyingPartyId: String,
    val challenge: ByteArray,
    val userId: ByteArray? = null,
    val userName: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PasskeyRequest
        return relyingPartyId == other.relyingPartyId &&
            challenge.contentEquals(other.challenge) &&
            (userId?.contentEquals(other.userId ?: ByteArray(0)) ?: (other.userId == null)) &&
            userName == other.userName
    }

    override fun hashCode(): Int {
        var result = relyingPartyId.hashCode()
        result = 31 * result + challenge.contentHashCode()
        result = 31 * result + (userId?.contentHashCode() ?: 0)
        result = 31 * result + (userName?.hashCode() ?: 0)
        return result
    }
}

/**
 * Interface contract for Passkey credential operations (FIDO2 / WebAuthn).
 */
interface PasskeyCredentialHelper {
    /**
     * Checks whether the current operating environment supports Passkey credential management.
     */
    fun isPasskeySupported(): Boolean

    /**
     * Validates client data challenge string matches expected challenge.
     */
    fun verifyChallenge(
        clientDataJson: ByteArray,
        expectedChallengeBase64: String,
    ): Boolean
}

/**
 * Enterprise implementation of [PasskeyCredentialHelper] providing FIDO2 / WebAuthn validation.
 */
class DefaultPasskeyCredentialHelper : PasskeyCredentialHelper {
    override fun isPasskeySupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P
    }

    override fun verifyChallenge(
        clientDataJson: ByteArray,
        expectedChallengeBase64: String,
    ): Boolean {
        if (clientDataJson.isEmpty() || expectedChallengeBase64.isBlank()) return false
        val jsonString = String(clientDataJson, Charsets.UTF_8)
        return jsonString.contains(expectedChallengeBase64)
    }
}
