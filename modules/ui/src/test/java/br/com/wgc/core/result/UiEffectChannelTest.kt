package br.com.wgc.core.result

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UiEffectChannelTest {

    sealed interface NavigationEffect {
        data class NavigateToDetails(val id: String) : NavigationEffect
        object ShowToast : NavigationEffect
    }

    @Test
    fun sendEffect_shouldEmitEffectToSubscribers() = runTest {
        val effectChannel = DefaultUiEffectChannel<NavigationEffect>()

        effectChannel.effects.test {
            effectChannel.sendEffect(NavigationEffect.NavigateToDetails("123"))
            val received = awaitItem()
            assertEquals(NavigationEffect.NavigateToDetails("123"), received)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun trySendEffect_shouldReturnTrueAndEmitSuccessfully() = runTest {
        val effectChannel = DefaultUiEffectChannel<NavigationEffect>()

        effectChannel.effects.test {
            val success = effectChannel.trySendEffect(NavigationEffect.ShowToast)
            assertTrue(success)
            val received = awaitItem()
            assertEquals(NavigationEffect.ShowToast, received)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
