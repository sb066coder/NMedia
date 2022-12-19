package ru.netology.nmedia.model

import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll(): List<Post>
    suspend fun likeById(plusLike: Boolean, id: Long)
    suspend fun deleteById(id: Long)
    suspend fun save(post: Post)
    suspend fun showNewPosts()
    fun getNewerCount(id: Long): Flow<Int>
    fun getInvisibleAmount(): Int

}