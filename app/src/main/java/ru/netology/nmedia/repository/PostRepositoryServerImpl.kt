package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.*
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.Post
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryServerImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService
    ) : PostRepository {

    override val data: Flow<List<Post>> = postDao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getAll(): List<Post> {
        try {
            val response = apiService.getPosts()
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
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity().map { it.hide() })
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override fun getInvisibleAmount(): Int {
        return postDao.getInvisibleAmount()
    }

    override suspend fun saveWithAttachment(post: Post, uploadItem: MediaUpload) {
        try {
            val media = upload(uploadItem)
            // TODO: add support for other types
            val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(uploadItem: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", uploadItem.file.name, uploadItem.file.asRequestBody()
            )

            val response = apiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun showNewPosts() {
        postDao.showAll()
    }

    override suspend fun likeById(plusLike: Boolean, id: Long) {
        try {
            postDao.likeById(id)
            val response = if (plusLike) {
                apiService.likeById(id)
            } else {
                apiService.disLikeById(id)
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
            val response = apiService.delete(id)
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
            val response = apiService.save(post)
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