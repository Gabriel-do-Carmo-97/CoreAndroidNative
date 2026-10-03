package br.com.wgc.core.ai.feedback

/**
 * On-device feedback event collected to evaluate AI inference quality.
 *
 * @property promptId Unique identifier of the generated answer or prompt.
 * @property isHelpful True if the user upvoted or accepted the AI output.
 * @property userComments Optional textual feedback from the user.
 * @property timestamp Epoch millis timestamp of the feedback action.
 */
data class AiFeedbackEvent(
    val promptId: String,
    val isHelpful: Boolean,
    val userComments: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Collector buffering on-device user feedback to guide model distillation or fine-tuning.
 */
class EdgeFeedbackCollector {
    private val buffer = mutableListOf<AiFeedbackEvent>()

    /**
     * Enqueues a feedback event.
     */
    @Synchronized
    fun recordFeedback(event: AiFeedbackEvent) {
        buffer.add(event)
    }

    /**
     * Drains all collected feedback events for telemetry sync.
     */
    @Synchronized
    fun drainFeedback(): List<AiFeedbackEvent> {
        val copy = buffer.toList()
        buffer.clear()
        return copy
    }

    /**
     * Returns total pending feedback count.
     */
    @Synchronized
    fun pendingCount(): Int = buffer.size
}
