package ru.netology.nmedia.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.model.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int,
    val shares: Int,
    val watches: Int,
    val likedByMe: Boolean = false,
    val videoContent: String? = null
) {
    fun toDto() = Post(id, author, content, published, likes, shares, watches, likedByMe, videoContent)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes, dto.shares,
            dto.watches, dto.likedByMe, dto.videoContent)
    }
}