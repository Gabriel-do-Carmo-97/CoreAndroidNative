package br.com.wgc.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NetworkMonitorTest {

    private lateinit var context: Context
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkMonitor = NetworkMonitor(context)
    }

    @Test
    fun isCurrentlyConnected_returnsFalseWhenNoActiveNetwork() {
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(null)

        val connected = networkMonitor.isCurrentlyConnected()
        assertFalse(connected)
    }

    @Test
    fun isConnectedFlow_emitsInitialState() = runTest {
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(null)

        networkMonitor.isConnected.test {
            val initial = awaitItem()
            assertFalse(initial)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun statusFlow_emitsInitialStateWhenDisconnected() = runTest {
        val shadowConnectivityManager = Shadows.shadowOf(connectivityManager)
        shadowConnectivityManager.setActiveNetworkInfo(null)

        networkMonitor.status.test {
            val initial = awaitItem()
            assertEquals(NetworkStatus.Unavailable, initial)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
