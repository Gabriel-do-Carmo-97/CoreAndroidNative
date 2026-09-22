package br.com.wgc.core.security

import java.util.Arrays

/**
 * Utilitário corporativo para higienização e destruição imediata de dados sensíveis na memória RAM (Zero-Heap Leak).
 *
 * Utilizado para sobrescrever senhas, tokens de autenticação e chaves criptográficas após o uso,
 * impedindo que permaneçam legíveis em heap dumps ou inspeções de memória em caso de ataques.
 */
object MemorySanitizer {
    /**
     * Sobrescreve todos os caracteres de um [CharArray] com zeros binários (`\u0000`).
     *
     * @param array Array de caracteres a ser higienizado.
     */
    fun wipe(array: CharArray) {
        Arrays.fill(array, '\u0000')
    }

    /**
     * Sobrescreve todos os bytes de um [ByteArray] com zeros binários (`0`).
     *
     * @param array Array de bytes a ser higienizado.
     */
    fun wipe(array: ByteArray) {
        Arrays.fill(array, 0.toByte())
    }

    /**
     * Sobrescreve todos os caracteres de um [StringBuilder] com zeros e zera seu tamanho.
     *
     * @param builder Instância de [StringBuilder] a ser limpa.
     */
    fun wipe(builder: java.lang.StringBuilder) {
        for (i in 0 until builder.length) {
            builder.setCharAt(i, '\u0000')
        }
        builder.setLength(0)
    }

    /**
     * Executa [block] com o [array] fornecido e garante a higienização de seu conteúdo
     * imediatamente após a conclusão da execução, inclusive em caso de lançamento de exceções.
     */
    inline fun <R> useAndWipe(
        array: CharArray,
        block: (CharArray) -> R,
    ): R {
        try {
            return block(array)
        } finally {
            wipe(array)
        }
    }

    /**
     * Executa [block] com o [array] fornecido e garante a higienização de seu conteúdo
     * imediatamente após a conclusão da execução, inclusive em caso de lançamento de exceções.
     */
    inline fun <R> useAndWipe(
        array: ByteArray,
        block: (ByteArray) -> R,
    ): R {
        try {
            return block(array)
        } finally {
            wipe(array)
        }
    }
}
