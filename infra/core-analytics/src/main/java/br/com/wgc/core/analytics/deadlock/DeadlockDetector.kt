package br.com.wgc.core.analytics.deadlock

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
     * Inspects JVM thread MXBean for deadlocked threads via reflection if supported.
     */
    @Suppress("TooGenericExceptionCaught")
    fun detectDeadlocks(): DeadlockReport {
        return try {
            val clazz = Class.forName("java.lang.management.ManagementFactory")
            val getBeanMethod = clazz.getMethod("getThreadMXBean")
            val bean = getBeanMethod.invoke(null) ?: return DeadlockReport(hasDeadlocks = false)
            val findMethod = bean.javaClass.getMethod("findDeadlockedThreads")
            val deadlocked = findMethod.invoke(bean) as? LongArray
            if (deadlocked != null && deadlocked.isNotEmpty()) {
                DeadlockReport(
                    hasDeadlocks = true,
                    deadlockedThreadIds = deadlocked.toList(),
                )
            } else {
                DeadlockReport(hasDeadlocks = false)
            }
        } catch (_: Throwable) {
            DeadlockReport(hasDeadlocks = false)
        }
    }
}
