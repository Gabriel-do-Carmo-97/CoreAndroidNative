package br.com.wgc.core.validators

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test
    fun isValidCpf_whenGivenValidCpf_shouldReturnTrue() {
        assertTrue("52998224725".isValidCpf())
        assertTrue("529.982.247-25".isValidCpf())
        assertTrue("11144477735".isValidCpf())
    }

    @Test
    fun isValidCpf_whenGivenInvalidOrMalformedCpf_shouldReturnFalse() {
        assertFalse("11111111111".isValidCpf())
        assertFalse("00000000000".isValidCpf())
        assertFalse("12345678900".isValidCpf())
        assertFalse("12345".isValidCpf())
        assertFalse("".isValidCpf())
        val nullCpf: String? = null
        assertFalse(nullCpf.isValidCpf())
    }

    @Test
    fun isValidCnpj_whenGivenValidCnpj_shouldReturnTrue() {
        assertTrue("11222333000181".isValidCnpj())
        assertTrue("11.222.333/0001-81".isValidCnpj())
        assertTrue("00000000000191".isValidCnpj())
    }

    @Test
    fun isValidCnpj_whenGivenInvalidOrMalformedCnpj_shouldReturnFalse() {
        assertFalse("11111111111111".isValidCnpj())
        assertFalse("11222333000182".isValidCnpj())
        assertFalse("123".isValidCnpj())
        assertFalse("".isValidCnpj())
        val nullCnpj: String? = null
        assertFalse(nullCnpj.isValidCnpj())
    }

    @Test
    fun isValidEmail_whenGivenValidEmail_shouldReturnTrue() {
        assertTrue("user@example.com".isValidEmail())
        assertTrue("user.name+tag@sub.domain.com.br".isValidEmail())
    }

    @Test
    fun isValidEmail_whenGivenInvalidEmail_shouldReturnFalse() {
        assertFalse("invalid_email".isValidEmail())
        assertFalse("user@".isValidEmail())
        assertFalse("@example.com".isValidEmail())
        assertFalse("user@domain".isValidEmail())
        assertFalse("".isValidEmail())
        val nullEmail: String? = null
        assertFalse(nullEmail.isValidEmail())
    }

    @Test
    fun isValidPhone_whenGivenValidFixedOrMobile_shouldReturnTrue() {
        assertTrue("11987654321".isValidPhone())
        assertTrue("(11) 98765-4321".isValidPhone())
        assertTrue("1133224455".isValidPhone())
        assertTrue("(11) 3322-4455".isValidPhone())
    }

    @Test
    fun isValidPhone_whenGivenInvalidPhone_shouldReturnFalse() {
        assertFalse("11111111111".isValidPhone())
        assertFalse("123".isValidPhone())
        assertFalse("".isValidPhone())
        val nullPhone: String? = null
        assertFalse(nullPhone.isValidPhone())
    }

    @Test
    fun isValidCep_whenGivenValidCep_shouldReturnTrue() {
        assertTrue("01001000".isValidCep())
        assertTrue("01001-000".isValidCep())
    }

    @Test
    fun isValidCep_whenGivenInvalidCep_shouldReturnFalse() {
        assertFalse("00000000".isValidCep())
        assertFalse("123".isValidCep())
        assertFalse("".isValidCep())
        val nullCep: String? = null
        assertFalse(nullCep.isValidCep())
    }
}
