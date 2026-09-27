package br.com.wgc.core.location.geofence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KalmanLocationFilterTest {
    @Test
    fun `KalmanLocationFilter smooths noisy measurements`() {
        val filter = KalmanLocationFilter()

        val f1 = filter.filter(-23.5505, -46.6333, 10f, 1000L)
        assertEquals(-23.5505, f1.latitude, 0.0001)

        // Jitter measurement with bad accuracy
        val f2 = filter.filter(-23.5520, -46.6350, 50f, 2000L)
        // Kalman should resist the large sudden change due to 50m inaccuracy
        assertTrue(kotlin.math.abs(f2.latitude - (-23.5505)) < kotlin.math.abs(f2.latitude - (-23.5520)))
    }

    @Test
    fun `GeofenceEngine detects ENTER and EXIT transitions`() {
        val engine = GeofenceEngine()
        val geofence =
            GeofenceEngine.Geofence(
                id = "office",
                latitude = 0.0,
                longitude = 0.0,
                radiusMeters = 100.0,
            )
        engine.addGeofence(geofence)

        // Outside (far away)
        val eventsOutside = engine.evaluate(1.0, 1.0)
        assertTrue(eventsOutside.isEmpty())

        // Enters within 50m of (0,0)
        val eventsEnter = engine.evaluate(0.0001, 0.0001)
        assertEquals(1, eventsEnter.size)
        assertEquals(GeofenceEngine.Transition.ENTER, eventsEnter[0].transition)

        // Stays inside - no new transition event
        val eventsInside = engine.evaluate(0.0002, 0.0002)
        assertTrue(eventsInside.isEmpty())

        // Exits to far away
        val eventsExit = engine.evaluate(1.0, 1.0)
        assertEquals(1, eventsExit.size)
        assertEquals(GeofenceEngine.Transition.EXIT, eventsExit[0].transition)
    }
}
