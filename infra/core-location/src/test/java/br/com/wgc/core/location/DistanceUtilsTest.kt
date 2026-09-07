package br.com.wgc.core.location

import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceUtilsTest {

    @Test
    fun calculateDistanceMeters_returnsAccurateDistanceBetweenCoordinates() {
        val distance = DistanceUtils.calculateDistanceMeters(
            startLat = -23.5615,
            startLng = -46.6559,
            endLat = -22.9711,
            endLng = -43.1822
        )

        assertEquals(357000.0, distance, 10000.0)
    }
}
