package br.com.wgc.core.sync.lamport

import java.util.concurrent.atomic.AtomicLong

/**
 * Enterprise thread-safe Lamport Logical Clock for ordering distributed events without wall-clock drift.
 */
class LamportClock(
    initialTime: Long = 0L,
) {
    private val clock = AtomicLong(initialTime)

    /**
     * Returns current logical time without ticking.
     */
    fun getTime(): Long = clock.get()

    /**
     * Increments local clock upon a local mutation event.
     */
    fun tick(): Long = clock.incrementAndGet()

    /**
     * Advances local clock upon receiving a remote event timestamp: max(local, remote) + 1.
     */
    fun update(receivedTime: Long): Long {
        return clock.updateAndGet { current ->
            maxOf(current, receivedTime) + 1
        }
    }
}
