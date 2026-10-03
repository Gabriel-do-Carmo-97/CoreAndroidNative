package br.com.wgc.core.ai.sentiment

/**
 * Sentiment analysis classification result.
 */
enum class SentimentPolarity {
    POSITIVE,
    NEUTRAL,
    NEGATIVE,
}

/**
 * Result of on-device sentiment scoring.
 */
data class SentimentResult(
    val polarity: SentimentPolarity,
    val confidence: Float,
)

/**
 * Lightweight on-device lexica/heuristic classifier for offline sentiment scoring.
 */
class LocalSentimentClassifier {
    private val positiveWords =
        setOf("bom", "otimo", "excelente", "maravilhoso", "adoro", "rapido", "facil", "good", "great")
    private val negativeWords =
        setOf("ruim", "pessimo", "lento", "horrivel", "odeio", "erro", "falha", "bad", "terrible")

    /**
     * Scores the provided text based on local lexical matches.
     */
    fun classify(text: String): SentimentResult {
        val tokens = text.lowercase().split(Regex("""\W+"""))
        var posCount = 0
        var negCount = 0

        for (token in tokens) {
            if (token in positiveWords) posCount++
            if (token in negativeWords) negCount++
        }

        val total = (posCount + negCount).toFloat()
        return when {
            posCount > negCount ->
                SentimentResult(
                    polarity = SentimentPolarity.POSITIVE,
                    confidence = if (total > 0f) posCount / total else 0.5f,
                )
            negCount > posCount ->
                SentimentResult(
                    polarity = SentimentPolarity.NEGATIVE,
                    confidence = if (total > 0f) negCount / total else 0.5f,
                )
            else ->
                SentimentResult(
                    polarity = SentimentPolarity.NEUTRAL,
                    confidence = 1.0f,
                )
        }
    }
}
