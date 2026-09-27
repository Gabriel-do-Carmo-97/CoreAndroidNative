package br.com.wgc.core.testing.fixtures

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BrazilFixtureDataFactoryTest {
    @Test
    fun `generateCpf produces valid CPF check digits`() {
        val rawCpf = BrazilFixtureDataFactory.generateCpf(formatted = false)
        assertEquals(11, rawCpf.length)

        // Verify check digits
        val digits = rawCpf.map { it.toString().toInt() }

        var sum1 = 0
        for (i in 0 until 9) sum1 += digits[i] * (10 - i)
        val rem1 = sum1 % 11
        val expectedD1 = if (rem1 < 2) 0 else 11 - rem1
        assertEquals(expectedD1, digits[9])

        var sum2 = 0
        for (i in 0 until 9) sum2 += digits[i] * (11 - i)
        sum2 += expectedD1 * 2
        val rem2 = sum2 % 11
        val expectedD2 = if (rem2 < 2) 0 else 11 - rem2
        assertEquals(expectedD2, digits[10])
    }

    @Test
    fun `generateCpf formatted matches standard mask`() {
        val cpf = BrazilFixtureDataFactory.generateCpf(formatted = true)
        assertTrue(Regex("""\d{3}\.\d{3}\.\d{3}-\d{2}""").matches(cpf))
    }

    @Test
    fun `generateCnpj formatted matches standard mask`() {
        val cnpj = BrazilFixtureDataFactory.generateCnpj(formatted = true)
        assertTrue(Regex("""\d{2}\.\d{3}\.\d{3}/\d{4}-\d{2}""").matches(cnpj))
    }

    @Test
    fun `generateMercosulPlate matches Mercosul regex`() {
        val plate = BrazilFixtureDataFactory.generateMercosulPlate()
        assertTrue(Regex("""[A-Z]{3}[0-9][A-Z][0-9]{2}""").matches(plate))
    }

    @Test
    fun `generateCep matches standard format`() {
        val cep = BrazilFixtureDataFactory.generateCep(formatted = true)
        assertTrue(Regex("""\d{5}-\d{3}""").matches(cep))
    }
}
