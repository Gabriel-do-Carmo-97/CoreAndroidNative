package br.com.wgc.core.analytics.deadlock

import java.lang.management.ManagementFactory

/**
 * Diagnostic result of JVM thread deadlock inspection.
 */
data class DeadlockReport(
    val hasDeadlocks: Boolean,
    val deadlockedThreadIds: List<Long> = emptyList(),
)

/**
 * Background watchdog detecting thread synchronization deadlocks.
 */
object DeadlockDetector {
    /**
     * Inspects JVM thread MXBean for deadlocked threads.
     */
    fun detectDeadlocks(): DeadlockReport {
        return try {
            val threadMxBean = ManagementFactory.getThreadMXBean()
            val deadlockedIds = threadMxBean.findDeadlockedThreads()
            if (deadlockedIds != null && deadlockedIds.isNotEmpty()) {
                DeadlockReport(
                    hasDeadlocks = true,
                    deadlockedThreadIds = deadlockedIds.toList(),
                )
            } else {
                DeadlockReport(hasDeadlocks = false)
            }
        } catch (ignored: Exception) {
            DeadlockReport(hasDeadlocks = false)
        }
    }
}
