package br.com.wgc.core.ai.privacy

/**
 * Filter and anonymizer protecting sensitive Personally Identifiable Information (PII)
 * before ingestion by Edge AI or LLMs.
 */
object EdgePiiSanitizer {
    private val CPF_REGEX = Regex("""\b\d{3}\.?\d{3}\.?\d{3}-?\d{2}\b""")
    private val EMAIL_REGEX = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}\b""")
    private val CREDIT_CARD_REGEX = Regex("""\b(?:\d{4}[ -]?){3}\d{4}\b""")
    private val PHONE_REGEX = Regex("""\b(?:\+?55\s?)?(?:\(?\d{2}\)?\s?)?(?:9\d{4}|\d{4})-?\d{4}\b""")

    /**
     * Replaces CPF, email, card numbers, and phone numbers with redaction tokens.
     */
    fun sanitize(text: String): String {
        return text
            .replace(CPF_REGEX, "[REDACTED_CPF]")
            .replace(EMAIL_REGEX, "[REDACTED_EMAIL]")
            .replace(CREDIT_CARD_REGEX, "[REDACTED_CARD]")
            .replace(PHONE_REGEX, "[REDACTED_PHONE]")
    }

    /**
     * Checks if the text contains any identifiable personal information.
     */
    fun containsPii(text: String): Boolean {
        return CPF_REGEX.containsMatchIn(text) ||
            EMAIL_REGEX.containsMatchIn(text) ||
            CREDIT_CARD_REGEX.containsMatchIn(text) ||
            PHONE_REGEX.containsMatchIn(text)
    }
}
