package br.com.wgc.core.sync.priority

/**
 * Priority levels for scheduling background offline data mutations.
 */
enum class MutationPriority(
    val weight: Int,
) {
    CRITICAL(100),
    HIGH(50),
    NORMAL(10),
    LOW(1),
}

/**
 * Prioritized mutation task queued for outbound server synchronization.
 */
data class PrioritizedMutation(
    val taskId: String,
    val priority: MutationPriority,
    val enqueuedAt: Long = System.currentTimeMillis(),
) : Comparable<PrioritizedMutation> {
    override fun compareTo(other: PrioritizedMutation): Int {
        val priorityComparison = other.priority.weight.compareTo(this.priority.weight)
        return if (priorityComparison != 0) {
            priorityComparison
        } else {
            this.enqueuedAt.compareTo(other.enqueuedAt)
        }
    }
}
