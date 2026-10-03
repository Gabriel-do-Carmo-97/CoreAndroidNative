package br.com.wgc.core.database.kmp

/**
 * Provedor de chave de encriptação de banco de dados desacoplado do Android Keystore.
 * Permite que cada plataforma (Android Keystore / iOS Keychain / Desktop KMS) forneça
 * o segredo de encriptação para o SQLCipher de maneira segura.
 */
interface DatabaseCipherKeyProvider {
    /**
     * Retorna a passphrase para o banco de dados.
     * Deve ser mantida em memória pelo menor tempo possível.
     */
    fun getPassphrase(): ByteArray

    /**
     * Informa se a chave já foi gerada e está pronta para uso.
     */
    fun isKeyInitialized(): Boolean
}

/**
 * Implementação padrão estática para cenários de desenvolvimento ou testes unitários.
 */
class StaticDatabaseCipherKeyProvider(
    private val key: ByteArray,
) : DatabaseCipherKeyProvider {
    override fun getPassphrase(): ByteArray = key.clone()

    override fun isKeyInitialized(): Boolean = true
}
