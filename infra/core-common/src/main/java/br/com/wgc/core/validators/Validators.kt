package br.com.wgc.core.validators

private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private const val CPF_LENGTH = 11
private const val CNPJ_LENGTH = 14
private const val CEP_LENGTH = 8
private const val PHONE_FIXED_LENGTH = 10
private const val PHONE_MOBILE_LENGTH = 11
private const val MODULO_BASE = 11
private const val DIGIT_TOLERANCE_MIN = 2
private const val CPF_FIRST_CHECK_INDEX = 9
private const val CPF_SECOND_CHECK_INDEX = 10
private const val CNPJ_FIRST_CHECK_INDEX = 12
private const val CNPJ_SECOND_CHECK_INDEX = 13

private val CNPJ_WEIGHTS_FIRST = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
private val CNPJ_WEIGHTS_SECOND = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

/**
 * Utilitários e extensões de validação de dados comuns em aplicações corporativas brasileiras.
 */

/**
 * Valida se a string representa um Cadastro de Pessoas Físicas (CPF) matematicamente válido.
 *
 * Remove caracteres de pontuação e valida se o número possui 11 dígitos, se não é composto
 * por dígitos repetidos e se os dois dígitos verificadores atendem ao cálculo oficial do módulo 11.
 *
 * @return `true` se o CPF for válido, `false` se inválido ou nulo.
 */
@Suppress("MagicNumber")
fun String?.isValidCpf(): Boolean {
    if (this.isNullOrBlank()) return false
    val digits = this.filter { it.isDigit() }
    if (digits.length != CPF_LENGTH || digits.all { it == digits[0] }) return false

    val numbers = digits.map { it.digitToInt() }
    val firstRemainder = (0 until 9).sumOf { numbers[it] * (10 - it) } % MODULO_BASE
    val firstDigit = if (firstRemainder < DIGIT_TOLERANCE_MIN) 0 else MODULO_BASE - firstRemainder

    val secondRemainder = (0 until 10).sumOf { numbers[it] * (11 - it) } % MODULO_BASE
    val secondDigit = if (secondRemainder < DIGIT_TOLERANCE_MIN) 0 else MODULO_BASE - secondRemainder

    return numbers[CPF_FIRST_CHECK_INDEX] == firstDigit && numbers[CPF_SECOND_CHECK_INDEX] == secondDigit
}

/**
 * Valida se a string representa um Cadastro Nacional da Pessoa Jurídica (CNPJ) matematicamente válido.
 *
 * Remove caracteres de pontuação e valida se o número possui 14 dígitos, se não é composto
 * por dígitos repetidos e se os dois dígitos verificadores atendem à ponderação oficial.
 *
 * @return `true` se o CNPJ for válido, `false` se inválido ou nulo.
 */
@Suppress("MagicNumber")
fun String?.isValidCnpj(): Boolean {
    if (this.isNullOrBlank()) return false
    val digits = this.filter { it.isDigit() }
    if (digits.length != CNPJ_LENGTH || digits.all { it == digits[0] }) return false

    val numbers = digits.map { it.digitToInt() }
    val firstRemainder = (0 until 12).sumOf { numbers[it] * CNPJ_WEIGHTS_FIRST[it] } % MODULO_BASE
    val firstDigit = if (firstRemainder < DIGIT_TOLERANCE_MIN) 0 else MODULO_BASE - firstRemainder

    val secondRemainder = (0 until 13).sumOf { numbers[it] * CNPJ_WEIGHTS_SECOND[it] } % MODULO_BASE
    val secondDigit = if (secondRemainder < DIGIT_TOLERANCE_MIN) 0 else MODULO_BASE - secondRemainder

    return numbers[CNPJ_FIRST_CHECK_INDEX] == firstDigit && numbers[CNPJ_SECOND_CHECK_INDEX] == secondDigit
}

/**
 * Valida se o formato do endereço de e-mail está em conformidade com o padrão RFC padrão.
 *
 * @return `true` se for um endereço de e-mail sintaticamente válido, `false` caso contrário.
 */
fun String?.isValidEmail(): Boolean {
    if (this.isNullOrBlank()) return false
    return EMAIL_REGEX.matches(this.trim())
}

/**
 * Valida se a string representa um número de telefone brasileiro válido (com DDD), seja fixo ou celular.
 *
 * @return `true` se for um telefone válido com 10 (fixo) ou 11 (móvel iniciando com 9) dígitos.
 */
fun String?.isValidPhone(): Boolean {
    if (this.isNullOrBlank()) return false
    val digits = this.filter { it.isDigit() }
    if (digits.all { it == digits[0] }) return false

    return when (digits.length) {
        PHONE_FIXED_LENGTH -> digits[2] in '2'..'5'
        PHONE_MOBILE_LENGTH -> digits[2] == '9'
        else -> false
    }
}

/**
 * Valida se a string representa um Código de Endereçamento Postal (CEP) brasileiro contendo 8 dígitos.
 *
 * @return `true` se contiver exatamente 8 dígitos numéricos válidos.
 */
fun String?.isValidCep(): Boolean {
    if (this.isNullOrBlank()) return false
    val digits = this.filter { it.isDigit() }
    return digits.length == CEP_LENGTH && !digits.all { it == digits[0] }
}
