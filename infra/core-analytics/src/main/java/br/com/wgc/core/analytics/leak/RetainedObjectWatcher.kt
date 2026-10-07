package br.com.wgc.core.analytics.leak

import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.util.concurrent.ConcurrentHashMap

/**
 * Enterprise reference watcher detecting retained objects without third-party heavy dependencies.
 */
class RetainedObjectWatcher {
    private val referenceQueue = ReferenceQueue<Any>()
    private val watchedReferences = ConcurrentHashMap<String, KeyedWeakReference>()

    private class KeyedWeakReference(
        referent: Any,
        val key: String,
        val description: String,
        queue: ReferenceQueue<Any>,
    ) : PhantomReference<Any>(referent, queue)

    /**
     * Watches an object that is expected to be garbage collected shortly (e.g. Activity or Fragment).
     */
    fun watch(
        target: Any,
        description: String = target.javaClass.simpleName,
    ): String {
        reapGarbageCollectedObjects()
        val key =
            java.util.UUID
                .randomUUID()
                .toString()
        val reference = KeyedWeakReference(target, key, description, referenceQueue)
        watchedReferences[key] = reference
        return key
    }

    /**
     * Returns true if the object associated with [key] has not yet been collected.
     */
    fun isRetained(key: String): Boolean {
        reapGarbageCollectedObjects()
        return watchedReferences.containsKey(key)
    }

    /**
     * Drains references already garbage collected by the JVM.
     */
    private fun reapGarbageCollectedObjects() {
        var ref = referenceQueue.poll()
        while (ref != null) {
            val keyed = ref as? KeyedWeakReference
            if (keyed != null) {
                watchedReferences.remove(keyed.key)
            }
            ref = referenceQueue.poll()
        }
    }

    fun retainedCount(): Int {
        reapGarbageCollectedObjects()
        return watchedReferences.size
    }
}
