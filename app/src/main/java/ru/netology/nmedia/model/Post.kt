package ru.netology.nmedia.model

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int,
    val shares: Int,
    val watches: Int,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val description: String,
    val type: String
)
