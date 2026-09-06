package br.com.wgc.core.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Representa os possíveis estados de conectividade de rede do dispositivo.
 */
sealed interface NetworkStatus {
    /** A rede está ativa e disponível para tráfego de dados. */
    data object Available : NetworkStatus

    /** A rede está prestes a ser desconectada ou seu tempo de vida está expirando. */
    data object Losing : NetworkStatus

    /** A conexão de rede foi perdida. */
    data object Lost : NetworkStatus

    /** Nenhuma rede adequada para acesso à internet está disponível. */
    data object Unavailable : NetworkStatus
}

/**
 * Utilitário reativo para monitorar o status de conectividade à internet em tempo real.
 *
 * Utiliza o [ConnectivityManager] do sistema Android registrando um [ConnectivityManager.NetworkCallback]
 * e emitindo as alterações de forma limpa via [Flow].
 *
 * Requer a permissão [Manifest.permission.ACCESS_NETWORK_STATE] no `AndroidManifest.xml`.
 *
 * Exemplo de uso:
 * ```kotlin
 * @Inject
 * lateinit var networkMonitor: NetworkMonitor
 *
 * // Observar boolean simplificado:
 * networkMonitor.isConnected.collect { online ->
 *     if (online) carregarDados() else exibirAvisoOffline()
 * }
 * ```
 *
 * @param context Contexto da aplicação Android.
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * [Flow] reativo que emite o estado detalhado da conexão ([NetworkStatus]).
     *
     * Emite o estado inicial imediatamente na subscrição e novas transições apenas quando o status muda.
     */
    @get:RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    val status: Flow<NetworkStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                trySend(NetworkStatus.Losing)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Lost)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                if (hasInternet) {
                    trySend(NetworkStatus.Available)
                } else {
                    trySend(NetworkStatus.Lost)
                }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        // Emissão do estado inicial
        val initialStatus = if (isCurrentlyConnected()) NetworkStatus.Available else NetworkStatus.Unavailable
        trySend(initialStatus)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * [Flow] reativo simplificado que emite `true` se o dispositivo estiver online
     * e `false` caso contrário.
     */
    @get:RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    val isConnected: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        trySend(isCurrentlyConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    /**
     * Verifica de forma síncrona e imediata se há uma rede com capacidade de internet ativa.
     *
     * @return `true` se a rede ativa possuir internet ativa, `false` caso contrário.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isCurrentlyConnected(): Boolean {
        val capabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    /**
     * Verifica se a conexão com a internet foi efetivamente validada pelo sistema operacional.
     *
     * Garante que não se trata de uma rede com captive portal (ex: Wi-Fi público com tela de login).
     *
     * @return `true` se a rede ativa possuir capacidade de internet e validação do sistema.
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkValidated(): Boolean {
        val capabilities = connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }
        return capabilities?.let {
            it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } == true
    }
}
