package br.com.wgc.core.database.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator

/**
 * Abstração corporativa genérica de [RemoteMediator] para orquestração automática entre API e Room.
 *
 * @param Key Tipo da chave de paginação (ex: [Int]).
 * @param Value Tipo da entidade de dados mapeada no Room.
 */
@OptIn(ExperimentalPagingApi::class)
abstract class BaseRemoteMediator<Key : Any, Value : Any> : RemoteMediator<Key, Value>() {
    abstract suspend fun fetchFromRemote(
        page: Int,
        pageSize: Int,
    ): List<Value>

    abstract suspend fun saveToDatabase(
        loadType: LoadType,
        items: List<Value>,
    )

    abstract suspend fun clearLocalData()

    @Suppress("TooGenericExceptionCaught")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Key, Value>,
    ): MediatorResult {
        return try {
            val page =
                when (loadType) {
                    LoadType.REFRESH -> STARTING_PAGE_INDEX
                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        val lastItem = state.lastItemOrNull()
                        if (lastItem == null) {
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                        state.pages.size + 1
                    }
                }

            val items = fetchFromRemote(page, state.config.pageSize)
            val endOfPaginationReached = items.isEmpty()

            if (loadType == LoadType.REFRESH) {
                clearLocalData()
            }
            saveToDatabase(loadType, items)

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}
