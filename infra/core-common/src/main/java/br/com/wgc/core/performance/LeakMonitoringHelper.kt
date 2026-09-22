package br.com.wgc.core.performance

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Referência fraca com identificador único e descrição para rastreamento de retenção de memória.
 */
class KeyedWeakReference(
    referent: Any,
    val key: String,
    val description: String,
    referenceQueue: ReferenceQueue<Any>,
) : WeakReference<Any>(referent, referenceQueue)

/**
 * Utilitário leve e agnóstico para monitoramento e detecção precoce de retenções indevidas de memória (Memory Leaks).
 *
 * Utiliza [ReferenceQueue] e [WeakReference] para observar objetos após o término de seu ciclo de vida
 * (como ViewModels, observadores ou instâncias pesadas), sem adicionar sobrecarga de bibliotecas de debug em produção.
 */
class LeakMonitoringHelper(
    private val onLeakDetected: ((description: String) -> Unit)? = null,
) {
    private val referenceQueue = ReferenceQueue<Any>()
    private val watchedReferences = ConcurrentHashMap<String, KeyedWeakReference>()

    /**
     * Registra um objeto para monitoramento após o término esperado de seu ciclo de vida.
     *
     * @param target Objeto que deve ser recolhido pelo Garbage Collector.
     * @param description Identificador legível do objeto rastreado (ex: "UserProfileViewModel").
     * @return O token único associado a esta inspeção.
     */
    fun watch(
        target: Any,
        description: String = target.javaClass.simpleName,
    ): String {
        drainQueue()
        val key = UUID.randomUUID().toString()
        val reference = KeyedWeakReference(target, key, description, referenceQueue)
        watchedReferences[key] = reference
        return key
    }

    /**
     * Verifica se o objeto associado ao token informado ainda está retido na memória.
     *
     * @param key O token retornado pelo método [watch].
     * @return `true` se o objeto ainda não foi coletado pelo GC, `false` se já foi liberado ou nunca existiu.
     */
    fun isRetained(key: String): Boolean {
        drainQueue()
        val reference = watchedReferences[key] ?: return false
        val retained = reference.get() != null
        if (retained) {
            onLeakDetected?.invoke(reference.description)
        }
        return retained
    }

    /**
     * Retorna a lista descritiva de todos os objetos atualmente monitorados que ainda continuam retidos em memória.
     */
    fun getRetainedObjects(): Map<String, String> {
        drainQueue()
        return watchedReferences.filterValues { it.get() != null }.mapValues { it.value.description }
    }

    /**
     * Limpa todas as referências monitoradas.
     */
    fun clear() {
        watchedReferences.clear()
        drainQueue()
    }

    /**
     * Retorna a quantidade total de referências ativas sob monitoramento.
     */
    fun activeWatchCount(): Int {
        drainQueue()
        return watchedReferences.size
    }

    private fun drainQueue() {
        var ref = referenceQueue.poll()
        while (ref != null) {
            if (ref is KeyedWeakReference) {
                watchedReferences.remove(ref.key)
            }
            ref = referenceQueue.poll()
        }
    }
}
