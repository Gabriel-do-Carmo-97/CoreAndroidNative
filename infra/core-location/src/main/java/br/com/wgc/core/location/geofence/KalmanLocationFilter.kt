package br.com.wgc.core.location.geofence

private const val MILLIS_PER_SECOND = 1000f

/**
 * Filtro de Kalman para suavização de ruído e jitter em coordenadas geográficas (GPS/Network).
 */
class KalmanLocationFilter(
    private val qMetresPerSecond: Float = 3f, // Variância do processo
) {
    private var timestampMs: Long = 0L
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var variance: Float = -1f // -1 indica não inicializado

    data class FilteredLocation(
        val latitude: Double,
        val longitude: Double,
        val accuracyMetres: Float,
    )

    fun filter(
        newLat: Double,
        newLng: Double,
        accuracyMetres: Float,
        timeMs: Long = System.currentTimeMillis(),
    ): FilteredLocation {
        val accuracy = accuracyMetres.coerceAtLeast(1f)

        if (variance < 0) {
            // Inicialização com a primeira medição
            timestampMs = timeMs
            latitude = newLat
            longitude = newLng
            variance = accuracy * accuracy
            return FilteredLocation(latitude, longitude, accuracy)
        }

        val timeDeltaMs = (timeMs - timestampMs).coerceAtLeast(1L)
        if (timeDeltaMs > 0) {
            // Predição de incerteza no tempo
            variance += (timeDeltaMs / MILLIS_PER_SECOND) * qMetresPerSecond * qMetresPerSecond
            timestampMs = timeMs
        }

        // Ganho de Kalman K = Variância / (Variância + Variância da Medição)
        val measurementVariance = accuracy * accuracy
        val k = variance / (variance + measurementVariance)

        latitude += k * (newLat - latitude)
        longitude += k * (newLng - longitude)
        variance = (1f - k) * variance

        return FilteredLocation(latitude, longitude, kotlin.math.sqrt(variance))
    }

    fun reset() {
        variance = -1f
    }
}

/**
 * Motor reativo de Geofencing para monitoramento de cercas virtuais.
 */
class GeofenceEngine {
    enum class Transition {
        ENTER,
        EXIT,
        INSIDE,
        OUTSIDE,
    }

    data class Geofence(
        val id: String,
        val latitude: Double,
        val longitude: Double,
        val radiusMeters: Double,
    )

    data class GeofenceEvent(
        val geofence: Geofence,
        val transition: Transition,
        val distanceMeters: Double,
    )

    private val monitoredGeofences = mutableMapOf<String, Geofence>()
    private val insideStates = mutableMapOf<String, Boolean>()

    fun addGeofence(geofence: Geofence) {
        monitoredGeofences[geofence.id] = geofence
    }

    fun removeGeofence(id: String) {
        monitoredGeofences.remove(id)
        insideStates.remove(id)
    }

    fun evaluate(
        currentLat: Double,
        currentLng: Double,
    ): List<GeofenceEvent> {
        val events = mutableListOf<GeofenceEvent>()

        for (geofence in monitoredGeofences.values) {
            val dist =
                DistanceUtils.calculateDistanceMeters(
                    currentLat,
                    currentLng,
                    geofence.latitude,
                    geofence.longitude,
                )

            val isInsideNow = dist <= geofence.radiusMeters
            val wasInside = insideStates[geofence.id] ?: false

            val transition =
                when {
                    !wasInside && isInsideNow -> Transition.ENTER
                    wasInside && !isInsideNow -> Transition.EXIT
                    isInsideNow -> Transition.INSIDE
                    else -> Transition.OUTSIDE
                }

            insideStates[geofence.id] = isInsideNow

            if (transition == Transition.ENTER || transition == Transition.EXIT) {
                events.add(GeofenceEvent(geofence, transition, dist))
            }
        }

        return events
    }
}
