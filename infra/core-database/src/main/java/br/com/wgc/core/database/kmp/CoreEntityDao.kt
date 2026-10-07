package br.com.wgc.core.database.kmp

import kotlinx.coroutines.flow.Flow

/**
 * Contrato abstrato de DAO genérico com operações CRUD reativas desacopladas de drivers específicos.
 * Compatível com Room KMP e SQLDelight.
 */
interface CoreEntityDao<T : Any, ID : Any> {
    suspend fun insert(entity: T): Long

    suspend fun insertAll(entities: List<T>): List<Long>

    suspend fun update(entity: T): Int

    suspend fun delete(entity: T): Int

    suspend fun findById(id: ID): T?

    fun observeById(id: ID): Flow<T?>

    fun observeAll(): Flow<List<T>>

    suspend fun clear(): Int
}
