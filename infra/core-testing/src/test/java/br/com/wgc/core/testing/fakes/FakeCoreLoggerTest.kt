package br.com.wgc.core.testing.fakes

import br.com.wgc.core.logging.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeCoreLoggerTest {
    @Test
    fun `captures logs across different levels`() {
        val logger = FakeCoreLogger()

        logger.d("TAG", "Debug message")
        logger.e("TAG", "Error message", RuntimeException("Boom"))

        assertEquals(2, logger.logs.size)
        assertTrue(logger.hasMessageWithTag("TAG"))
        assertTrue(logger.hasMessageContaining("Debug message"))
        assertEquals(LogLevel.ERROR, logger.logs[1].level)
        assertEquals("Boom", logger.logs[1].throwable?.message)

        logger.clear()
        assertTrue(logger.logs.isEmpty())
    }
}
