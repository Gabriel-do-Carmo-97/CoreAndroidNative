package br.com.wgc.core.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Interface genérica de Data Access Object (DAO) que fornece operações CRUD fundamentais para o Room.
 *
 * Todas as operações são declaradas como funções de suspensão ([suspend]), garantindo execução
 * não-bloqueante fora da Main Thread da aplicação consumidora.
 *
 * ### Exemplo de Implementação:
 * ```kotlin
 * @Dao
 * interface UserDao : BaseDao<UserEntity> {
 *     @Query("SELECT * FROM users WHERE id = :id")
 *     suspend fun getById(id: String): UserEntity?
 * }
 * ```
 *
 * @param T O tipo da entidade gerenciada por este DAO.
 */
@JvmSuppressWildcards
interface BaseDao<T> {

    /**
     * Insere uma única entidade no banco de dados.
     *
     * Caso ocorra conflito de chave primária, a estratégia padrão é substituir o registro existente ([OnConflictStrategy.REPLACE]).
     *
     * @param entity O objeto da entidade a ser inserido.
     * @return O ID (`rowId`) da linha recém-inserida no SQLite.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: T): Long

    /**
     * Insere uma coleção de entidades no banco de dados.
     *
     * Em caso de conflito, registros existentes serão substituídos ([OnConflictStrategy.REPLACE]).
     *
     * @param entities A lista de entidades a ser inserida.
     * @return A lista de IDs (`rowId`) correspondentes a cada inserção.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<T>): List<Long>

    /**
     * Atualiza uma entidade existente no banco de dados com base em sua chave primária.
     *
     * @param entity O objeto da entidade contendo os dados atualizados.
     * @return A quantidade de linhas afetadas pela operação de atualização.
     */
    @Update
    suspend fun update(entity: T): Int

    /**
     * Remove uma entidade existente do banco de dados com base em sua chave primária.
     *
     * @param entity O objeto da entidade a ser excluído.
     * @return A quantidade de linhas removidas pela operação.
     */
    @Delete
    suspend fun delete(entity: T): Int
}
