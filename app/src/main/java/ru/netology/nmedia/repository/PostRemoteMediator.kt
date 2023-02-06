package ru.netology.nmedia.repository

import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val id: Long?
            val result = when (loadType) {
                LoadType.REFRESH -> apiService.getLatest(state.config.initialLoadSize)
                /*{
                    id = postRemoteKeyDao.max()
                    if (id == null) {
                        apiService.getLatest(state.config.initialLoadSize)
                    } else {
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }*/
                LoadType.PREPEND -> {
                    id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    apiService.getAfter(id, state.config.pageSize)
                    //return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
            }

            if (!result.isSuccessful) {
                throw ApiError(result.code(), result.message())
            }

            val body = result.body() ?: throw ApiError(result.code(), result.message())

            appDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.clear()
                        postRemoteKeyDao.insert(
                            listOf(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.firstOrNull()?.id
                            ),
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.lastOrNull()?.id
                                )
                            )
                        )
                        postDao.clear()
                    }
                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.firstOrNull()?.id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                body.lastOrNull()?.id,
                            )
                        )
                    }
                }
                postDao.insert(body.map(PostEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}