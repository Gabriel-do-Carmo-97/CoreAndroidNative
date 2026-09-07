package br.com.wgc.core.formatters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.ZoneId
import java.util.Locale

class FormattersTest {

    @Test
    fun unmask_shouldRemoveNonDigits() {
        assertEquals("12345678901", "123.456.789-01".unmask())
        assertEquals("11987654321", "(11) 98765-4321".unmask())
        assertEquals("01001000", "01001-000".unmask())
    }

    @Test
    fun toCurrencyFormatted_whenDouble_shouldFormatWithCurrencySymbol() {
        val amount = 1250.50
        val ptBrLocale = Locale.forLanguageTag("pt-BR")
        val formatted = amount.toCurrencyFormatted(ptBrLocale)
        assertTrue(formatted.contains("1.250,50") || formatted.contains("1250,50"))
        assertTrue(formatted.contains("R$"))
    }

    @Test
    fun toCurrencyFormatted_whenBigDecimal_shouldFormatWithCurrencySymbol() {
        val amount = BigDecimal("99.90")
        val ptBrLocale = Locale.forLanguageTag("pt-BR")
        val formatted = amount.toCurrencyFormatted(ptBrLocale)
        assertTrue(formatted.contains("99,90"))
        assertTrue(formatted.contains("R$"))
    }

    @Test
    fun toFormattedDate_shouldFormatTimestampAccordingToPattern() {
        // Timestamp for 2026-01-15T12:00:00Z = 1768478400000L
        val timestamp = 1768478400000L
        val zoneUtc = ZoneId.of("UTC")
        val formatted = timestamp.toFormattedDate(pattern = "yyyy-MM-dd", zoneId = zoneUtc)
        assertEquals("2026-01-15", formatted)
    }

    @Test
    fun formatCpf_shouldMask11DigitsCorrectly() {
        assertEquals("123.456.789-01", "12345678901".formatCpf())
        assertEquals("123.456.789-01", "123.456.789-01".formatCpf())
        assertEquals("12345", "12345".formatCpf())
    }

    @Test
    fun formatCnpj_shouldMask14DigitsCorrectly() {
        assertEquals("11.222.333/0001-81", "11222333000181".formatCnpj())
        assertEquals("11.222.333/0001-81", "11.222.333/0001-81".formatCnpj())
        assertEquals("123", "123".formatCnpj())
    }

    @Test
    fun formatCep_shouldMask8DigitsCorrectly() {
        assertEquals("01001-000", "01001000".formatCep())
        assertEquals("01001-000", "01001-000".formatCep())
        assertEquals("123", "123".formatCep())
    }

    @Test
    fun formatPhone_shouldFormatFixedAndMobileNumbers() {
        assertEquals("(11) 98765-4321", "11987654321".formatPhone())
        assertEquals("(11) 3322-4455", "1133224455".formatPhone())
        assertEquals("123", "123".formatPhone())
    }
}
