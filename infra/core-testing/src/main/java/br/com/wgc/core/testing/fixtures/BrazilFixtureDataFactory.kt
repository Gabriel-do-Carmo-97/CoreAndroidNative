package br.com.wgc.core.testing.fixtures

import java.util.Random

/**
 * Fábrica declarativa de massas de dados brasileiras válidas para testes automatizados.
 * Gera CPFs, CNPJs, CEPs e Placas de veículos matematicamente válidos.
 */
object BrazilFixtureDataFactory {
    private val random = Random()

    /**
     * Gera um CPF matematicamente válido com cálculo real de dígitos verificadores (módulo 11).
     */
    fun generateCpf(formatted: Boolean = true): String {
        val n = IntArray(9) { random.nextInt(10) }

        // Primeiro dígito verificador
        var sum1 = 0
        for (i in 0 until 9) {
            sum1 += n[i] * (10 - i)
        }
        val remainder1 = sum1 % 11
        val d1 = if (remainder1 < 2) 0 else 11 - remainder1

        // Segundo dígito verificador
        var sum2 = 0
        for (i in 0 until 9) {
            sum2 += n[i] * (11 - i)
        }
        sum2 += d1 * 2
        val remainder2 = sum2 % 11
        val d2 = if (remainder2 < 2) 0 else 11 - remainder2

        val raw = "${n.joinToString("")}$d1$d2"
        return if (formatted) {
            "${raw.substring(0, 3)}.${raw.substring(3, 6)}.${raw.substring(6, 9)}-${raw.substring(9, 11)}"
        } else {
            raw
        }
    }

    /**
     * Gera um CNPJ matematicamente válido com dígitos verificadores reais.
     */
    fun generateCnpj(formatted: Boolean = true): String {
        val n = IntArray(12)
        for (i in 0 until 8) n[i] = random.nextInt(10)
        n[8] = 0
        n[9] = 0
        n[10] = 0
        n[11] = 1 // Matriz padrão 0001

        val weights1 = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        var sum1 = 0
        for (i in 0 until 12) sum1 += n[i] * weights1[i]
        val rem1 = sum1 % 11
        val d1 = if (rem1 < 2) 0 else 11 - rem1

        val weights2 = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        var sum2 = 0
        for (i in 0 until 12) sum2 += n[i] * weights2[i]
        sum2 += d1 * 2
        val rem2 = sum2 % 11
        val d2 = if (rem2 < 2) 0 else 11 - rem2

        val raw = "${n.joinToString("")}$d1$d2"
        return if (formatted) {
            "${raw.substring(
                0,
                2,
            )}.${raw.substring(2, 5)}.${raw.substring(5, 8)}/${raw.substring(8, 12)}-${raw.substring(12, 14)}"
        } else {
            raw
        }
    }

    /**
     * Gera uma placa de trânsito no padrão Mercosul (ex: ABC1D23).
     */
    fun generateMercosulPlate(): String {
        fun randLetter(): Char = ('A'.code + random.nextInt(26)).toChar()

        fun randDigit(): Int = random.nextInt(10)

        return "${randLetter()}${randLetter()}${randLetter()}${randDigit()}${randLetter()}${randDigit()}${randDigit()}"
    }

    /**
     * Gera um CEP brasileiro válido (ex: 01310-100).
     */
    fun generateCep(formatted: Boolean = true): String {
        val prefix = "%05d".format(random.nextInt(100000))
        val suffix = "%03d".format(random.nextInt(1000))
        return if (formatted) "$prefix-$suffix" else "$prefix$suffix"
    }
}
