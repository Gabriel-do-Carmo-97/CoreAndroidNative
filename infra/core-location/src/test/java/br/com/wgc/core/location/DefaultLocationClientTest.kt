package br.com.wgc.core.location

import android.content.Context
import android.location.Location
import android.os.Looper
import app.cash.turbine.test
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DefaultLocationClientTest {
    private val context: Context = mockk(relaxed = true)
    private val fusedClient: FusedLocationProviderClient = mockk(relaxed = true)
    private val mockLooper: Looper = mockk(relaxed = true)
    private lateinit var locationClient: DefaultLocationClient

    @Before
    fun setup() {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns mockLooper
        locationClient = DefaultLocationClient(context, fusedClient)
    }

    @After
    fun tearDown() {
        unmockkStatic(Looper::class)
    }

    @Test
    fun getLocationUpdates_registersAndEmitsLocationAndUnregistersOnClose() =
        runTest {
            val callbackSlot = slot<LocationCallback>()
            val mockTask: Task<Void> = mockk(relaxed = true)

            every {
                fusedClient.requestLocationUpdates(any(), capture(callbackSlot), any<Looper>())
            } returns mockTask

            every {
                fusedClient.removeLocationUpdates(any<LocationCallback>())
            } returns mockTask

            val fakeLocation: Location =
                mockk(relaxed = true) {
                    every { latitude } returns -23.55052
                    every { longitude } returns -46.633308
                }
            val fakeResult: LocationResult =
                mockk(relaxed = true) {
                    every { locations } returns listOf(fakeLocation)
                }

            locationClient.getLocationUpdates(5000L).test {
                callbackSlot.captured.onLocationResult(fakeResult)

                val emitted = awaitItem()
                assertEquals(-23.55052, emitted.latitude, 0.0001)
                assertEquals(-46.633308, emitted.longitude, 0.0001)

                cancelAndIgnoreRemainingEvents()
            }

            verify(exactly = 1) {
                fusedClient.removeLocationUpdates(callbackSlot.captured)
            }
        }
}
