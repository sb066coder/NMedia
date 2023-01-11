package ru.netology.nmedia.model

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int,
    val shares: Int = 0,
    val watches: Int = 0,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val type: AttachmentType
)
