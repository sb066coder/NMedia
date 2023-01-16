package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun likeById(plusLike: Boolean, id: Long)
    suspend fun deleteById(id: Long)
    suspend fun save(post: Post)
    suspend fun showNewPosts()
    fun getNewerCount(id: Long): Flow<Int>
    fun getInvisibleAmount(): Int
    suspend fun saveWithAttachment(post: Post, uploadItem: MediaUpload)
    suspend fun upload(uploadItem: MediaUpload): Media

}