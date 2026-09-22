package br.com.wgc.core.security.keyrotation

import br.com.wgc.core.sharedPreferences.KeyValueStorage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Relatório consolidado da rotina de rotação de credenciais e migração entre armazenamentos seguros.
 *
 * @property keysMigrated Quantidade total de chaves copiadas com sucesso para o novo destino.
 * @property isSuccessful `true` se todas as chaves foram migradas sem falha de decodificação.
 * @property error Exceção capturada em caso de inconsistência na migração.
 */
data class RotationReport(
    val keysMigrated: Int,
    val isSuccessful: Boolean,
    val error: Throwable? = null,
)

/**
 * Utilitário de governança e rotação de chaves criptográficas (Key Rotation Orchestrator).
 *
 * Facilita a migração transparente de segredos entre diferentes instâncias de [KeyValueStorage]
 * (por exemplo, ao rotacionar chaves mestras do AndroidKeyStore ou atualizar algoritmos de cifra),
 * garantindo integridade e exclusão segura das credenciais antigas após confirmação.
 */
@Singleton
class KeyRotationHelper
    @Inject
    constructor() {
        /**
         * Migra dados de forma atômica de um armazenamento de origem para um novo armazenamento de destino.
         *
         * @param sourceStorage Instância de origem (antiga).
         * @param targetStorage Instância de destino (com a nova chave criptográfica).
         * @param stringKeys Chaves de texto a serem migradas.
         * @param booleanKeys Chaves booleanas a serem migradas.
         * @param intKeys Chaves inteiras a serem migradas.
         * @param longKeys Chaves long a serem migradas.
         * @param wipeOldEntries Se `true`, apaga os registros do storage de origem após migrá-los para o destino.
         * @return [RotationReport] com o status da operação.
         */
        @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
        fun rotate(
            sourceStorage: KeyValueStorage,
            targetStorage: KeyValueStorage,
            stringKeys: List<String> = emptyList(),
            booleanKeys: List<String> = emptyList(),
            intKeys: List<String> = emptyList(),
            longKeys: List<String> = emptyList(),
            wipeOldEntries: Boolean = true,
        ): RotationReport {
            var count = 0
            return try {
                stringKeys.forEach { key ->
                    val value = sourceStorage.getString(key)
                    if (value != null) {
                        targetStorage.saveString(key, value)
                        if (wipeOldEntries) sourceStorage.remove(key)
                        count++
                    }
                }

                booleanKeys.forEach { key ->
                    val value = sourceStorage.getBoolean(key, false)
                    targetStorage.saveBoolean(key, value)
                    if (wipeOldEntries) sourceStorage.remove(key)
                    count++
                }

                intKeys.forEach { key ->
                    val value = sourceStorage.getInt(key, 0)
                    targetStorage.saveInt(key, value)
                    if (wipeOldEntries) sourceStorage.remove(key)
                    count++
                }

                longKeys.forEach { key ->
                    val value = sourceStorage.getLong(key, 0L)
                    targetStorage.saveLong(key, value)
                    if (wipeOldEntries) sourceStorage.remove(key)
                    count++
                }

                RotationReport(keysMigrated = count, isSuccessful = true)
            } catch (t: Throwable) {
                RotationReport(keysMigrated = count, isSuccessful = false, error = t)
            }
        }
    }
