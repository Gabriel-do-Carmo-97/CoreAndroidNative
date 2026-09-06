package br.com.wgc.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiResultTest {

    @Test
    fun apiResult_success_shouldHoldDataAndStatusCode() {
        val result: ApiResult<String> = ApiResult.Success("OK", 200)
        assertTrue(result is ApiResult.Success)
        assertEquals("OK", (result as ApiResult.Success).data)
        assertEquals(200, result.statusCode)
    }

    @Test
    fun apiResult_failure_shouldHoldTypedNetworkException() {
        val exception = NetworkException.UnauthorizedException()
        val result: ApiResult<String> = ApiResult.Failure(exception)

        assertTrue(result is ApiResult.Failure)
        assertEquals(401, ((result as ApiResult.Failure).exception as NetworkException.UnauthorizedException).code)
    }
}
