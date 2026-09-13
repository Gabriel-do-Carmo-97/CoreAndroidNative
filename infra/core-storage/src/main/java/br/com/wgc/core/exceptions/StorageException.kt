package br.com.wgc.core.exceptions

import java.io.IOException

/**
 * Hierarquia selada de exceções para operações de armazenamento no CoreAndroidNative.
 *
 * Permite que aplicações consumidoras tratem falhas de persistência de forma
 * tipada e expressiva, distinguindo erros de I/O, tipos não suportados e falhas de criptografia.
 *
 * @param message Mensagem detalhando a causa do erro.
 * @param cause Causa raiz opcional da exceção.
 */
sealed class StorageException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * Lançada quando uma operação de persistência recebe um tipo de dado não suportado.
     *
     * @param type Nome da classe do tipo incompatível recebido.
     */
    class UnsupportedTypeException(
        type: String,
    ) : StorageException(
            "Tipo de dado não suportado para armazenamento: $type. " +
                "Tipos válidos: String, Int, Boolean, Float, Long, Set<String>.",
        )

    /**
     * Lançada quando ocorre uma falha de entrada/saída durante a leitura ou escrita em disco.
     *
     * @param message Detalhes do erro de I/O.
     * @param cause A [IOException] original.
     */
    class StorageIOException(
        message: String,
        cause: IOException? = null,
    ) : StorageException(
            message,
            cause,
        )

    /**
     * Lançada quando ocorre uma falha de criptografia ou acesso à Android KeyStore.
     *
     * @param message Detalhes da falha criptográfica.
     * @param cause Causa original da exceção.
     */
    class EncryptionException(
        message: String,
        cause: Throwable? = null,
    ) : StorageException(
            message,
            cause,
        )
}
