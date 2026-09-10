package br.com.wgc.core.location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Utilitário matemático para cálculos geodésicos e distâncias entre coordenadas GPS.
 */
object DistanceUtils {
    /** Raio médio aproximado do planeta Terra em metros (WGS 84). */
    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * Calcula a distância aproximada em metros entre dois pontos na superfície da Terra
     * utilizando a fórmula de grande círculo de **Haversine**.
     *
     * ### Exemplo de Uso:
     * ```kotlin
     * val distancia = DistanceUtils.calculateDistanceMeters(
     *     startLat = -23.550520, startLng = -46.633308, // Praça da Sé, SP
     *     endLat = -22.906847, endLng = -43.172896     // Rio de Janeiro, RJ
     * )
     * println("Distância: ${distancia / 1000} km")
     * ```
     *
     * @param startLat Latitude do ponto de partida em graus decimais (entre -90.0 e 90.0).
     * @param startLng Longitude do ponto de partida em graus decimais (entre -180.0 e 180.0).
     * @param endLat Latitude do ponto de destino em graus decimais (entre -90.0 e 90.0).
     * @param endLng Longitude do ponto de destino em graus decimais (entre -180.0 e 180.0).
     * @return A distância em metros entre as duas coordenadas.
     */
    fun calculateDistanceMeters(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val dLat = Math.toRadians(endLat - startLat)
        val dLng = Math.toRadians(endLng - startLng)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(startLat)) * cos(Math.toRadians(endLat)) *
                sin(dLng / 2) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }
}
