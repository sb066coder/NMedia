package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.model.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar:String,
    val content: String,
    val published: String,
    val likes: Int,
    val likedByMe: Boolean = false,
) {
    fun toDto() = Post(
        id, author, authorAvatar, content, published, likes, 0, 0, likedByMe
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likes, dto.likedByMe
        )
    }

}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)