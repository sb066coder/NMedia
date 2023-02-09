package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.model.FeedItem
import ru.netology.nmedia.model.Post

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>(){
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: FeedItem, newItem: FeedItem): Any =
        Payload(
            likedByMe = (newItem as Post).likedByMe.takeIf { it != (oldItem as Post).likedByMe },
            likes = newItem.likes.takeIf { it != (oldItem as Post).likes },
            content = newItem.content.takeIf { it != (oldItem as Post).content }
        )
}
