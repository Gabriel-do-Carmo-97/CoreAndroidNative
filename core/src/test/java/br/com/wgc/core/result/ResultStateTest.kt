package br.com.wgc.core.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
