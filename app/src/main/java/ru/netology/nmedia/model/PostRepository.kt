package ru.netology.nmedia.model

import androidx.lifecycle.LiveData

interface PostRepository {
    fun getAll(): LiveData<List<Post>>
    fun likeById(id: Long)
    fun shareById(id: Long)
    fun deleteById(id: Long)
    fun save(post: Post)
}