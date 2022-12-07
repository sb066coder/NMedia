package ru.netology.nmedia.model

import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity

class PostRepositoryServerImpl(private val postDao: PostDao) : PostRepository {

    override val data = postDao.getAll().map(List<PostEntity>::toDto)

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