package br.com.wgc.core.dataStorePreferences

import androidx.datastore.preferences.core.Preferences
import br.com.wgc.core.exceptions.StorageException
import kotlinx.coroutines.flow.Flow

/**
 * Interface que abstrai o acesso e persistência reativa de pares chave-valor
 * baseada em Jetpack Preferences DataStore.
 *
 * Todas as operações de escrita são suspensas (`suspend`) e executadas fora da Main Thread.
 * Todas as operações de leitura retornam um [Flow] assíncrono que emite novas atualizações
 * sempre que o valor no DataStore for modificado.
 */
interface KeyValueDataStore {

    /**
     * Salva uma [String] de forma assíncrona no DataStore.
     *
     * @param key Identificador único da chave.
     * @param value O texto a ser persistido.
     */
    suspend fun saveString(key: String, value: String)

    /**
     * Retorna um [Flow] com o valor da [String] associada à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave ainda não exista no DataStore. Padrão: `null`.
     * @return [Flow] que emite a [String] atual e subsequentes alterações.
     */
    fun getStringFlow(key: String, defaultValue: String? = null): Flow<String?>

    /**
     * Salva um [Int] de forma assíncrona no DataStore.
     *
     * @param key Identificador único da chave.
     * @param value O número inteiro a ser persistido.
     */
    suspend fun saveInt(key: String, value: Int)

    /**
     * Retorna um [Flow] com o valor de [Int] associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista no DataStore. Padrão: `0`.
     * @return [Flow] que emite o valor inteiro atual e subsequentes alterações.
     */
    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int>

    /**
     * Salva um [Boolean] de forma assíncrona no DataStore.
     *
     * @param key Identificador único da chave.
     * @param value O valor booleano a ser persistido.
     */
    suspend fun saveBoolean(key: String, value: Boolean)

    /**
     * Retorna um [Flow] com o valor booleano associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista no DataStore. Padrão: `false`.
     * @return [Flow] que emite o [Boolean] atual e subsequentes alterações.
     */
    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean>

    /**
     * Salva um [Float] de forma assíncrona no DataStore.
     *
     * @param key Identificador único da chave.
     * @param value O número de ponto flutuante a ser persistido.
     */
    suspend fun saveFloat(key: String, value: Float)

    /**
     * Retorna um [Flow] com o valor de [Float] associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista no DataStore. Padrão: `0f`.
     * @return [Flow] que emite o valor de ponto flutuante atual e alterações subsequentes.
     */
    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float>

    /**
     * Salva um [Long] de forma assíncrona no DataStore.
     *
     * @param key Identificador único da chave.
     * @param value O valor inteiro longo a ser persistido.
     */
    suspend fun saveLong(key: String, value: Long)

    /**
     * Retorna um [Flow] com o valor de [Long] associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Valor retornado caso a chave não exista no DataStore. Padrão: `0L`.
     * @return [Flow] que emite o [Long] atual e alterações subsequentes.
     */
    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long>

    /**
     * Salva um conjunto de Strings ([Set]<[String]>) de forma assíncrona no DataStore.
     *
     * @param key Identificador único da chave.
     * @param value O conjunto de textos a ser persistido.
     */
    suspend fun saveStringSet(key: String, value: Set<String>)

    /**
     * Retorna um [Flow] com o conjunto de Strings associado à chave fornecida.
     *
     * @param key Identificador único da chave.
     * @param defaultValue Conjunto retornado caso a chave não exista. Padrão: conjunto vazio.
     * @return [Flow] que emite o [Set]<[String]> atual e alterações subsequentes.
     */
    fun getStringSetFlow(key: String, defaultValue: Set<String> = emptySet()): Flow<Set<String>>

    /**
     * Salva um valor de tipo genérico suportado no DataStore.
     * Tipos aceitos: [String], [Int], [Boolean], [Float], [Long] e [Set]<[String]>.
     *
     * @param key Identificador único da chave.
     * @param value O valor polimórfico a ser persistido.
     * @throws StorageException.UnsupportedTypeException Caso o tipo fornecido não seja compatível.
     */
    suspend fun saveAny(key: String, value: Any)

    /**
     * Retorna um mapa contendo todas as chaves e valores atualmente persistidos no DataStore.
     *
     * @return [Map] contendo todas as entradas armazenadas.
     */
    suspend fun getAll(): Map<Preferences.Key<*>, Any>

    /**
     * Verifica de forma assíncrona se existe um registro correspondente ao nome da chave informada.
     *
     * @param key Nome textual da chave.
     * @return `true` se a chave existir no DataStore, `false` caso contrário.
     */
    suspend fun containsKey(key: String): Boolean

    /**
     * Verifica de forma assíncrona se existe um registro correspondente a uma [Preferences.Key] tipada.
     *
     * @param key Chave tipada do Preferences DataStore.
     * @return `true` se a chave existir, `false` caso contrário.
     */
    suspend fun <T> contains(key: Preferences.Key<T>): Boolean

    /**
     * Remove o registro correspondente a uma [Preferences.Key] tipada do DataStore.
     *
     * @param key Chave tipada a ser removida.
     */
    suspend fun <T> remove(key: Preferences.Key<T>)

    /**
     * Remove o registro correspondente ao nome textual da chave informada.
     *
     * @param key Nome textual da chave a ser removida.
     */
    suspend fun removeKey(key: String)

    /**
     * Limpa completamente todos os dados persistidos nesta instância do DataStore.
     */
    suspend fun clear()
}
