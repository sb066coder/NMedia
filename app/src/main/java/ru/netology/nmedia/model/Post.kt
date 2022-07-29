package ru.netology.nmedia.model

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int,
    val shares: Int,
    val watches: Int,
    val likedByMe: Boolean = false
)
