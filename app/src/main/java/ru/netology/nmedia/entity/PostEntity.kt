package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.Attachment
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
    val shown: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable? = null
) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        content,
        published,
        likes,
        0,
        0,
        likedByMe,
        attachment = attachment?.toDto()
    )
    fun hide() = this.copy(shown = false)

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            dto.id,
            dto.author,
            dto.authorAvatar,
            dto.content,
            dto.published,
            dto.likes,
            dto.likedByMe,
            attachment = AttachmentEmbeddable.fromDto(dto.attachment)
        )
    }

}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)