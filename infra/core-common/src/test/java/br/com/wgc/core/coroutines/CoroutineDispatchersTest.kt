package br.com.wgc.core.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineDispatchersTest {
    @Test
    fun defaultCoroutineDispatchers_shouldProvideStandardDispatchers() {
        val dispatchers = DefaultCoroutineDispatchers()
        assertEquals(kotlinx.coroutines.Dispatchers.Main, dispatchers.main)
        assertEquals(kotlinx.coroutines.Dispatchers.IO, dispatchers.io)
        assertEquals(kotlinx.coroutines.Dispatchers.Default, dispatchers.default)
        assertEquals(kotlinx.coroutines.Dispatchers.Unconfined, dispatchers.unconfined)
    }

    @Test
    fun retryWithBackoff_whenSucceedsOnFirstAttempt_shouldReturnResultImmediately() =
        runTest {
            var attempts = 0
            val result =
                retryWithBackoff<String>(
                    times = 3,
                    initialDelayMs = 10L,
                ) {
                    attempts++
                    "success"
                }

            assertEquals(1, attempts)
            assertEquals("success", result)
        }

    @Test
    fun retryWithBackoff_whenFailsOnceThenSucceeds_shouldRetryAndReturn() =
        runTest {
            var attempts = 0
            val result =
                retryWithBackoff<String>(
                    times = 3,
                    initialDelayMs = 10L,
                ) {
                    attempts++
                    if (attempts < 2) {
                        throw IOException("Network glitch")
                    }
                    "recovered"
                }

            assertEquals(2, attempts)
            assertEquals("recovered", result)
        }

    @Test
    fun retryWithBackoff_whenAllAttemptsExhausted_shouldThrowLastException() =
        runTest {
            var attempts = 0
            try {
                retryWithBackoff<String>(
                    times = 3,
                    initialDelayMs = 10L,
                ) {
                    attempts++
                    error("Persistent failure")
                }
                fail("Expected IllegalStateException to be thrown")
            } catch (e: IllegalStateException) {
                assertEquals("Persistent failure", e.message)
                assertEquals(3, attempts)
            }
        }
}
