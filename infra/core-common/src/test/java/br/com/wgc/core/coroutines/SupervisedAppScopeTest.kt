package br.com.wgc.core.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupervisedAppScopeTest {
    @Test
    fun `launchSupervised catches unhandled exception without cancelling siblings`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val dispatchers =
                object : CoroutineDispatchers {
                    override val default = testDispatcher
                    override val main = testDispatcher
                    override val io = testDispatcher
                    override val unconfined = testDispatcher
                }

            var capturedError: Throwable? = null
            val scope =
                SupervisedAppScope(dispatchers) { error ->
                    capturedError = error
                }

            var siblingExecuted = false

            scope.launchSupervised {
                throw IllegalStateException("Failing background task")
            }

            scope.launchSupervised {
                siblingExecuted = true
            }

            advanceUntilIdle()

            assertTrue(siblingExecuted)
            assertEquals("Failing background task", capturedError?.message)
        }

    @Test
    fun `asyncSupervised returns computed value`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val dispatchers =
                object : CoroutineDispatchers {
                    override val default = testDispatcher
                    override val main = testDispatcher
                    override val io = testDispatcher
                    override val unconfined = testDispatcher
                }

            val scope = SupervisedAppScope(dispatchers)

            val deferred =
                scope.asyncSupervised {
                    42
                }

            advanceUntilIdle()
            assertEquals(42, deferred.await())
        }
}
