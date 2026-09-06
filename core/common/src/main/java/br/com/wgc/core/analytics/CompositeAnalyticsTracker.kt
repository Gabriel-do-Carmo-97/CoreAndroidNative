package br.com.wgc.core.analytics

/**
 * Composite analytics tracker that fans out events, user properties, and user IDs
 * to multiple underlying providers (e.g. Firebase Analytics, Mixpanel, AppsFlyer)
 * with automatic PII sanitization.
 *
 * @property trackers List of destination [AnalyticsTracker] instances.
 * @property enablePiiMasking When true, sanitizes sensitive data (CPF, cards, tokens, emails) before dispatching.
 */
class CompositeAnalyticsTracker(
    private val trackers: List<AnalyticsTracker>,
    private val enablePiiMasking: Boolean = true
) : AnalyticsTracker {

    companion object {
        private val CPF_REGEX = Regex("""\b\d{3}\.?\d{3}\.?\d{3}-?\d{2}\b""")
        private val CARD_REGEX = Regex("""\b(?:\d{4}[ -]?){3}\d{4}\b""")
        private val BEARER_REGEX = Regex("""(?i)(bearer\s+)[A-Za-z0-9\-._~+/]+=*""")
        private val EMAIL_REGEX = Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b""")

        private const val CPF_MASK = "***.***.***-**"
        private const val CARD_MASK = "****-****-****-****"
        private const val BEARER_REPLACEMENT = "$1[MASKED_TOKEN]"
        private const val EMAIL_MASK = "***@***.***"
    }

    override fun logEvent(name: String, params: Map<String, Any>) {
        val sanitizedParams = if (enablePiiMasking) {
            params.mapValues { (_, value) -> sanitizeValue(value) }
        } else {
            params
        }

        trackers.forEach { tracker ->
            tracker.logEvent(name, sanitizedParams)
        }
    }

    override fun setUserProperty(name: String, value: String) {
        val sanitizedValue = if (enablePiiMasking) maskPii(value) else value
        trackers.forEach { tracker ->
            tracker.setUserProperty(name, sanitizedValue)
        }
    }

    override fun setUserId(userId: String?) {
        trackers.forEach { tracker ->
            tracker.setUserId(userId)
        }
    }

    /**
     * Sanitizes sensitive information such as CPF, credit cards, bearer tokens, and emails from text.
     *
     * @param text Raw input text.
     * @return Sanitized text with sensitive values masked.
     */
    fun maskPii(text: String): String {
        if (!enablePiiMasking) return text
        return text
            .replace(CPF_REGEX, CPF_MASK)
            .replace(CARD_REGEX, CARD_MASK)
            .replace(BEARER_REGEX, BEARER_REPLACEMENT)
            .replace(EMAIL_REGEX, EMAIL_MASK)
    }

    private fun sanitizeValue(value: Any): Any {
        return when (value) {
            is String -> maskPii(value)
            is Map<*, *> -> value.entries.associate { (k, v) ->
                (k?.toString() ?: "") to (v?.let { sanitizeValue(it) } ?: "")
            }
            is Iterable<*> -> value.map { it?.let { elem -> sanitizeValue(elem) } ?: "" }
            else -> value
        }
    }
}
