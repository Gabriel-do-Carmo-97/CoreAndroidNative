package br.com.wgc.core.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultWrapperTest {
    @Test
    fun should_handle_success_properly() {
        val result = ResultWrapper.Success("data_value")

        assertTrue(result.isSuccess)
        assertFalse(result.isFailure)
        assertEquals("data_value", result.getOrNull())
        assertEquals("data_value", result.getOrThrow())

        val mapped = result.map { it.length }
        assertTrue(mapped.isSuccess)
        assertEquals(10, mapped.getOrNull())
    }

    @Test
    fun should_handle_failure_properly() {
        val error = CoreDomainError.Network("Connection timed out", statusCode = 408)
        val result = ResultWrapper.Failure(error)

        assertFalse(result.isSuccess)
        assertTrue(result.isFailure)
        assertNull(result.getOrNull())

        val folded =
            result.fold(
                onSuccess = { "success" },
                onFailure = { it.message },
            )
        assertEquals("Connection timed out", folded)
    }

    @Test
    fun should_catch_exception_in_of_builder() {
        val successResult = ResultWrapper.of { 10 / 2 }
        assertTrue(successResult.isSuccess)
        assertEquals(5, successResult.getOrNull())

        val errorResult = ResultWrapper.of { throw IllegalStateException("Boom") }
        assertTrue(errorResult.isFailure)
    }
}
