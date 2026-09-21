package br.com.wgc.core.database.paging

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para gerenciar chaves remotas de paginação e controle de cache no Room.
 *
 * @property id Identificador do registro ou chave do fluxo paginado.
 * @property prevKey Chave/página anterior para paginação reversa.
 * @property nextKey Chave/página seguinte para requisições subsequentes.
 * @property updatedAt Epoch timestamp de sincronização.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val id: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val updatedAt: Long = System.currentTimeMillis(),
)
