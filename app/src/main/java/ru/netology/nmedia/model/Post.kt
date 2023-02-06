package ru.netology.nmedia.model

import android.os.Build
import androidx.annotation.RequiresApi
import ru.netology.nmedia.enumeration.AttachmentType
import java.time.Instant.now

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likes: Int,
    val shares: Int = 0,
    val watches: Int = 0,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
) : FeedItem {
    @RequiresApi(Build.VERSION_CODES.O)
    fun ageDays(): Long = (now().epochSecond - published) / 86400
}

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem

data class TimingSeparator(
    override val id: Long,
    val title: Period
) : FeedItem {
    enum class Period {
        TODAY, YESTERDAY, LAST_WEEK
    }
}

data class Attachment(
    val url: String,
    val type: AttachmentType
)
