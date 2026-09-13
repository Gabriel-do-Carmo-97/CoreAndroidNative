package br.com.wgc.core.session

import br.com.wgc.core.coroutines.CoroutineDispatchers
import br.com.wgc.core.coroutines.DefaultCoroutineDispatchers
import br.com.wgc.core.dataStorePreferences.KeyValueDataStore
import br.com.wgc.core.file.FileManager
import br.com.wgc.core.sharedPreferences.KeyValueStorage
import kotlinx.coroutines.withContext

/**
 * Contrato corporativo de gerenciamento de ciclo de vida de sessão do usuário.
 *
 * Centraliza ações críticas como encerramento de sessão (logout), garantindo a remoção
 * orquestrada e atômica de credenciais seguras, preferências persistidas em disco e arquivos em cache.
 */
interface SessionManager {
    /**
     * Limpa de forma assíncrona todos os dados de sessão do usuário:
     * - Preferences DataStore
     * - SharedPreferences padrão
     * - EncryptedSharedPreferences (tokens, credenciais)
     * - Opcionalmente, arquivos locais no diretório de cache da aplicação.
     *
     * @param clearCache Se `true`, esvazia recursivamente o cache interno e externo. Padrão: `true`.
     */
    suspend fun clearSession(clearCache: Boolean = true)
}

/**
 * Implementação padrão de [SessionManager] que orquestra a limpeza em paralelo/sequencial
 * dentro do contexto de I/O fornecido por [CoroutineDispatchers].
 *
 * @property keyValueDataStore Instância reativa de [KeyValueDataStore] a ser expurgada.
 * @property defaultStorage Instância de [KeyValueStorage] padrão a ser limpa.
 * @property encryptedStorage Instância segura de [KeyValueStorage] a ser limpa.
 * @property fileManager Utilitário de sistema de arquivos utilizado para esvaziar o cache.
 * @property dispatchers Provedor de dispatchers de corrotinas para execução em thread de background.
 */
class DefaultSessionManager(
    private val keyValueDataStore: KeyValueDataStore,
    private val defaultStorage: KeyValueStorage,
    private val encryptedStorage: KeyValueStorage,
    private val fileManager: FileManager,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers(),
) : SessionManager {
    override suspend fun clearSession(clearCache: Boolean) {
        withContext(dispatchers.io) {
            keyValueDataStore.clear()
            defaultStorage.clear()
            encryptedStorage.clear()
            if (clearCache) {
                fileManager.clearCache()
            }
        }
    }
}
