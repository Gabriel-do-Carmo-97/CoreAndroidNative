package br.com.wgc.core.analytics.breadcrumbs

/**
 * High-performance circular buffer storing the last N breadcrumbs without memory reallocation.
 */
class RingBufferBreadcrumbs<T>(
    val capacity: Int,
) {
    init {
        require(capacity > 0) { "Capacity must be greater than 0" }
    }

    private val buffer =
        ArrayList<T?>(capacity).apply {
            for (i in 0 until capacity) add(null)
        }
    private var head = 0
    private var size = 0

    /**
     * Adds an item to the ring buffer, overwriting the oldest entry if full.
     */
    @Synchronized
    fun add(item: T) {
        val index = (head + size) % capacity
        if (size < capacity) {
            buffer[index] = item
            size++
        } else {
            buffer[head] = item
            head = (head + 1) % capacity
        }
    }

    /**
     * Drains all elements in chronological order (oldest to newest).
     */
    @Synchronized
    fun snapshot(): List<T> {
        val list = ArrayList<T>(size)
        for (i in 0 until size) {
            val item = buffer[(head + i) % capacity]
            if (item != null) list.add(item)
        }
        return list
    }

    @Synchronized
    fun clear() {
        head = 0
        size = 0
        for (i in 0 until capacity) buffer[i] = null
    }

    @Synchronized
    fun size(): Int = size
}
