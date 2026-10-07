package br.com.wgc.core.sync.crdt

/**
 * Enterprise Two-Phase Set (2P-Set) CRDT.
 * Elements can be added and removed, but once removed, cannot be re-added.
 */
data class TwoPhaseSet<T>(
    val addSet: Set<T> = emptySet(),
    val removeSet: Set<T> = emptySet(),
) {
    /**
     * Returns the active elements present in addSet and not in removeSet.
     */
    val elements: Set<T>
        get() = addSet - removeSet

    /**
     * Adds an element into the set.
     */
    fun add(element: T): TwoPhaseSet<T> {
        return copy(addSet = addSet + element)
    }

    /**
     * Removes an element by adding it to the tombstone set.
     */
    fun remove(element: T): TwoPhaseSet<T> {
        return copy(removeSet = removeSet + element)
    }

    /**
     * Merges two 2P-Sets via set union of addSet and removeSet.
     */
    fun merge(other: TwoPhaseSet<T>): TwoPhaseSet<T> {
        return TwoPhaseSet(
            addSet = this.addSet + other.addSet,
            removeSet = this.removeSet + other.removeSet,
        )
    }
}
