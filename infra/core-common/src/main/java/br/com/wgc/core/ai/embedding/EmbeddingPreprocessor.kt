package br.com.wgc.core.ai.embedding

/**
 * Text preprocessing and normalization pipeline before embedding inference.
 */
object EmbeddingPreprocessor {
    /**
     * Truncates text to max tokens/characters and normalizes whitespace.
     */
    fun prepareText(
        input: String,
        maxLength: Int = DEFAULT_MAX_LENGTH,
    ): String {
        val normalized = input.trim().replace(Regex("""\s+"""), " ")
        return if (normalized.length > maxLength) {
            normalized.substring(0, maxLength)
        } else {
            normalized
        }
    }

    private const val DEFAULT_MAX_LENGTH = 512
}
