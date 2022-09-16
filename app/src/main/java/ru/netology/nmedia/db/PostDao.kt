package ru.netology.nmedia.db

import ru.netology.nmedia.model.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun likeById(id: Long)
    fun deleteById(id: Long)
    fun shareById(id: Long)
}