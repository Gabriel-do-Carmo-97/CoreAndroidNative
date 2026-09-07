package br.com.wgc.core.result

import app.cash.turbine.test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class ResultStateTest {

    @Test
    fun successState_helpersReturnExpectedValues() {
        val state: ResultState<String> = ResultState.Success("Hello")

        assertTrue(state.isSuccess())
        assertFalse(state.isError())
        assertFalse(state.isLoading())
        assertEquals("Hello", state.getOrNull())
    }

    @Test
    fun errorState_helpersReturnExpectedValues() {
        val exception = RuntimeException("Error occurred")
        val state: ResultState<String> = ResultState.Error(throwable = exception)

        assertFalse(state.isSuccess())
        assertTrue(state.isError())
        assertFalse(state.isLoading())
        assertNull(state.getOrNull())
        assertEquals("Error occurred", (state as ResultState.Error).message)
    }

    @Test
    fun loadingState_helpersReturnExpectedValues() {
        val state: ResultState<String> = ResultState.Loading

        assertFalse(state.isSuccess())
        assertFalse(state.isError())
        assertTrue(state.isLoading())
        assertNull(state.getOrNull())
    }

    @Test
    fun map_transformsSuccessData() {
        val initial: ResultState<Int> = ResultState.Success(10)
        val mapped = initial.map { it * 2 }

        assertTrue(mapped is ResultState.Success)
        assertEquals(20, (mapped as ResultState.Success).data)
    }

    @Test
    fun asResultState_whenFlowEmitsValue_emitsLoadingThenSuccess() = runTest {
        val sourceFlow = flowOf("payload").asResultState()
        sourceFlow.test {
            val first = awaitItem()
            assertTrue(first.isLoading())

            val second = awaitItem()
            assertTrue(second.isSuccess())
            assertEquals("payload", second.getOrNull())

            awaitComplete()
        }
    }

    @Test
    fun asResultState_whenFlowThrows_emitsLoadingThenError() = runTest {
        val failingFlow = flow<String> {
            throw IOException("Stream failure")
        }.asResultState()

        failingFlow.test {
            val first = awaitItem()
            assertTrue(first.isLoading())

            val second = awaitItem()
            assertTrue(second.isError())
            assertEquals("Stream failure", (second as ResultState.Error).message)

            awaitComplete()
        }
    }
}
