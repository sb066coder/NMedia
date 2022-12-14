package ru.netology.nmedia.model

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.*
import java.io.IOException

class PostRepositoryServerImpl(private val postDao: PostDao) : PostRepository {

    override var data = postDao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        try {
            val response = PostApi.service.getPosts()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            return response.body()?.also {
                postDao.insert(it.toEntity())
            } ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            throw RuntimeException("unknown error")
        }
    }



    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            try {
                delay(10_000L)
                val response = PostApi.service.getNewer(id)
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                postDao.insert(body.toEntity().map { it.hide() })
                emit(body.size)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.e("Error", e.message ?: "Network error")
                emit(getInvisibleAmount())
            } catch (e: Exception) {
                throw UnknownError
            }
        }
    }
        .flowOn(Dispatchers.Default)

    override fun getInvisibleAmount(): Int {
        return postDao.getInvisibleAmount()
    }

    override suspend fun showNewPosts() {
        postDao.showAll()
    }

    override suspend fun likeById(plusLike: Boolean, id: Long) {
        try {
            postDao.likeById(id)
            val response = if (plusLike) {
                PostApi.service.likeById(id)
            } else {
                PostApi.service.disLikeById(id)
            }
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
        } catch (e: Exception) {
            postDao.likeById(id)
            throw RuntimeException("unknown error")
        }
    }

    override suspend fun deleteById(id: Long) {
        val justDeleted = postDao.getById(id).toDto()
        postDao.removeById(id)
        try {
            val response = PostApi.service.delete(id)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
        } catch (e: Exception) {
            postDao.insert(PostEntity.fromDto(justDeleted))
            throw RuntimeException("unknown error")
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            response.body()?.also {
                postDao.save(PostEntity.fromDto(it))
            } ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            throw RuntimeException("unknown error")
        }
    }
}