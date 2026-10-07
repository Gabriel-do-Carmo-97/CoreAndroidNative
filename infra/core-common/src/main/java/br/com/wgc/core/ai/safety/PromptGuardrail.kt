package br.com.wgc.core.ai.safety

/**
 * Filter evaluation guarding against prompt injection and toxic output.
 */
data class GuardrailResult(
    val isSafe: Boolean,
    val violationReason: String? = null,
)

/**
 * Enterprise guardrail protecting edge models against jailbreaks and system prompt extraction.
 */
object PromptGuardrail {
    private val JAILBREAK_PATTERNS =
        listOf(
            "ignore previous instructions",
            "system prompt",
            "bypass safeguards",
            "developer mode enabled",
            "act as dan",
        )

    /**
     * Inspects prompt text for known jailbreak patterns.
     */
    fun evaluatePrompt(prompt: String): GuardrailResult {
        val lower = prompt.lowercase()
        for (pattern in JAILBREAK_PATTERNS) {
            if (lower.contains(pattern)) {
                return GuardrailResult(
                    isSafe = false,
                    violationReason = "Potential jailbreak detected: pattern '$pattern'",
                )
            }
        }
        return GuardrailResult(isSafe = true)
    }
}
