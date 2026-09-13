package br.com.wgc.core.logging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCoreLoggerTest {
    private data class LogEntry(
        val priority: Int,
        val tag: String,
        val message: String,
        val throwable: Throwable?,
    )

    @Test
    fun log_whenLevelIsBelowMinLevel_shouldNotEmitLog() {
        val emittedLogs = mutableListOf<LogEntry>()
        val logger =
            DefaultCoreLogger(
                minLogLevel = LogLevel.INFO,
                logWriter = { priority, tag, msg, throwable ->
                    emittedLogs.add(LogEntry(priority, tag, msg, throwable))
                },
            )

        logger.v("TAG", "Verbose log")
        logger.d("TAG", "Debug log")

        assertTrue(emittedLogs.isEmpty())
    }

    @Test
    fun log_whenLevelIsAtOrAboveMinLevel_shouldEmitLog() {
        val emittedLogs = mutableListOf<LogEntry>()
        val logger =
            DefaultCoreLogger(
                minLogLevel = LogLevel.INFO,
                logWriter = { priority, tag, msg, throwable ->
                    emittedLogs.add(LogEntry(priority, tag, msg, throwable))
                },
            )

        logger.i("TAG", "Info log")
        logger.w("TAG", "Warn log")
        logger.e("TAG", "Error log")

        assertEquals(3, emittedLogs.size)
        assertEquals(LogLevel.INFO.priority, emittedLogs[0].priority)
        assertEquals(LogLevel.WARN.priority, emittedLogs[1].priority)
        assertEquals(LogLevel.ERROR.priority, emittedLogs[2].priority)
    }

    @Test
    fun maskPii_shouldMaskCpfCreditCardAndBearerToken() {
        val emittedLogs = mutableListOf<LogEntry>()
        val logger =
            DefaultCoreLogger(
                minLogLevel = LogLevel.DEBUG,
                enablePiiMasking = true,
                logWriter = { priority, tag, msg, throwable ->
                    emittedLogs.add(LogEntry(priority, tag, msg, throwable))
                },
            )

        logger.d("AUTH", "User with CPF 123.456.789-01 logged in with Bearer token_secret_12345")
        logger.d("PAYMENT", "Charged card 4111 2222 3333 4444")

        assertEquals(2, emittedLogs.size)
        assertEquals("User with CPF ***.***.***-** logged in with Bearer [MASKED_TOKEN]", emittedLogs[0].message)
        assertEquals("Charged card ****-****-****-****", emittedLogs[1].message)
    }

    @Test
    fun maskPii_whenDisabled_shouldNotAlterMessage() {
        val logger =
            DefaultCoreLogger(
                minLogLevel = LogLevel.DEBUG,
                enablePiiMasking = false,
            )
        val rawMessage = "User 123.456.789-01"
        assertEquals(rawMessage, logger.maskPii(rawMessage))
    }
}
