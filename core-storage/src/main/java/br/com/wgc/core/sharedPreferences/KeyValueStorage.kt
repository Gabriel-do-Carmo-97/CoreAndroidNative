package br.com.wgc.core.sharedPreferences

/**
 * Interface que abstrai o acesso síncrono a dados chave-valor
 * (utilizada por implementações baseadas em SharedPreferences e EncryptedSharedPreferences).
 *
 * Fornece métodos padronizados para leitura e gravação imediata de tipos primitivos e conjuntos de Strings.
 */
interface KeyValueStorage {

    /**
     * Salva uma [String] de forma síncrona.
     *
     * @param key Identificador único da chave.
     * @param value O texto a ser persistido.
     */
    fun saveString(key: String, value: String)

    /**
     * Recupera a [String] associada à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista. Padrão: `null`.
     * @return O texto persistido ou [defaultValue] caso inexistente.
     */
    fun getString(key: String, defaultValue: String? = null): String?

    /**
     * Salva um número inteiro ([Int]) de forma síncrona.
     *
     * @param key Identificador único da chave.
     * @param value O valor inteiro a ser persistido.
     */
    fun saveInt(key: String, value: Int)

    /**
     * Recupera o número inteiro ([Int]) associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista. Padrão: `0`.
     * @return O número inteiro persistido ou [defaultValue].
     */
    fun getInt(key: String, defaultValue: Int = 0): Int

    /**
     * Salva um valor booleano ([Boolean]) de forma síncrona.
     *
     * @param key Identificador único da chave.
     * @param value O valor booleano a ser persistido.
     */
    fun saveBoolean(key: String, value: Boolean)

    /**
     * Recupera o valor booleano ([Boolean]) associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista. Padrão: `false`.
     * @return O valor booleano persistido ou [defaultValue].
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    /**
     * Salva um número de ponto flutuante ([Float]) de forma síncrona.
     *
     * @param key Identificador único da chave.
     * @param value O valor de ponto flutuante a ser persistido.
     */
    fun saveFloat(key: String, value: Float)

    /**
     * Recupera o número de ponto flutuante ([Float]) associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista. Padrão: `0f`.
     * @return O valor de ponto flutuante persistido ou [defaultValue].
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    /**
     * Salva um número inteiro longo ([Long]) de forma síncrona.
     *
     * @param key Identificador único da chave.
     * @param value O valor inteiro longo a ser persistido.
     */
    fun saveLong(key: String, value: Long)

    /**
     * Recupera o número inteiro longo ([Long]) associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista. Padrão: `0L`.
     * @return O número inteiro longo persistido ou [defaultValue].
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long

    /**
     * Salva um conjunto de Strings ([Set]<[String]>) de forma síncrona.
     *
     * @param key Identificador único da chave.
     * @param value O conjunto de textos a ser persistido.
     */
    fun saveStringSet(key: String, value: Set<String>)

    /**
     * Recupera o conjunto de Strings ([Set]<[String]>) associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista. Padrão: conjunto vazio.
     * @return O conjunto de textos persistido ou [defaultValue].
     */
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String>

    /**
     * Remove o registro associado à chave informada.
     *
     * @param key Identificador único da chave a ser removida.
     */
    fun remove(key: String)

    /**
     * Limpa todos os dados armazenados nesta partição de preferências.
     */
    fun clear()

    /**
     * Verifica se existe um registro correspondente à chave informada.
     *
     * @param key Identificador único da chave.
     * @return `true` caso a chave exista, `false` caso contrário.
     */
    fun contains(key: String): Boolean

    /**
     * Retorna um mapa contendo todas as chaves e valores armazenados.
     *
     * @return [Map] com todos os pares chave-valor.
     */
    fun getAll(): Map<String, *>
}
