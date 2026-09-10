package br.com.wgc.core.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contrato para fornecimento reativo de dados de localização geográfica do dispositivo.
 */
interface LocationClient {

    /**
     * Retorna um [Flow] contínuo emitindo novas instâncias de [Location] conforme o dispositivo se desloca.
     *
     * O fluxo é reativo e resiliente: quando o coletor do Flow é cancelado (ex: o ViewModel é destruído),
     * os listeners do sensor GPS são imediatamente desregistrados para economizar a bateria do aparelho.
     *
     * @param intervalMs Intervalo desejado em milissegundos entre as leituras de localização (padrão: 10000ms / 10s).
     * @return [Flow] emitindo objetos [Location] com latitude, longitude, precisão e velocidade.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocationUpdates(intervalMs: Long = 10000L): Flow<Location>
}

/**
 * Implementação padrão de [LocationClient] baseada na API moderna [FusedLocationProviderClient] do Google Play Services.
 *
 * Utiliza alta precisão ([Priority.PRIORITY_HIGH_ACCURACY]) e gerencia o ciclo de vida do callback
 * de localização automaticamente através de um [callbackFlow].
 *
 * @property context Contexto de aplicação injetado pelo Hilt.
 * @property client Cliente do Google Play Services para provedor unificado de localização.
 */
@Singleton
class DefaultLocationClient @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val client: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) : LocationClient {

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun getLocationUpdates(intervalMs: Long): Flow<Location> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    trySend(location)
                }
            }
        }

        client.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }
}
