package ru.netology.nmedia.model

import androidx.lifecycle.LiveData

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun likeById(plusLike: Boolean, id: Long)
    suspend fun deleteById(id: Long)
    suspend fun save(post: Post)

}