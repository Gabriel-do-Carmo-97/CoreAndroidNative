package br.com.wgc.core.qa.chaos

/**
 * Strategy defining random runtime faults injected during chaos engineering drills.
 */
sealed interface InjectedChaosFault {
    data class NetworkLatency(
        val delayMs: Long,
    ) : InjectedChaosFault

    data class HttpError(
        val statusCode: Int,
    ) : InjectedChaosFault

    object DiskFull : InjectedChaosFault
}

/**
 * Controller injecting controlled transient faults to test application resilience.
 */
class ChaosFaultInjector {
    private val activeFaults = mutableListOf<InjectedChaosFault>()

    fun injectFault(fault: InjectedChaosFault) {
        activeFaults.add(fault)
    }

    fun clearFaults() {
        activeFaults.clear()
    }

    fun hasFault(): Boolean = activeFaults.isNotEmpty()

    fun getActiveFaults(): List<InjectedChaosFault> = activeFaults.toList()
}
