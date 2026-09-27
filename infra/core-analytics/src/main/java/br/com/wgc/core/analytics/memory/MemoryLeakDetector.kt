package br.com.wgc.core.analytics.memory

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/**
 * Detector leve de retenção indevida de memória e vazamento de Context em produção.
 * Opera sem bibliotecas pesadas de debug (Zero-Production Overhead).
 */
class MemoryLeakDetector {
    data class WatchedReference(
        val weakRef: WeakReference<Any>,
        val description: String,
        val watchedAtMs: Long = System.currentTimeMillis(),
    )

    private val watchedObjects = ConcurrentHashMap<String, WatchedReference>()

    /**
     * Registra um objeto (Activity, Fragment, View, Listener) que deveria ser destruído e coletado pelo GC.
     */
    fun watch(
        target: Any,
        description: String,
    ) {
        val key = "${target.javaClass.simpleName}@${System.identityHashCode(target)}"
        watchedObjects[key] =
            WatchedReference(
                weakRef = WeakReference(target),
                description = description,
            )
    }

    /**
     * Avalia as referências sob monitoramento e retorna a lista de objetos que ainda não foram coletados pelo GC.
     */
    fun detectRetainedObjects(): List<String> {
        val leaked = mutableListOf<String>()
        val iterator = watchedObjects.entries.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            val target = entry.value.weakRef.get()
            if (target == null) {
                // Objeto foi coletado com sucesso
                iterator.remove()
            } else {
                // Objeto ainda retido na memória
                leaked.add("${entry.value.description} (${entry.key})")
            }
        }

        return leaked
    }

    fun clear() {
        watchedObjects.clear()
    }
}
