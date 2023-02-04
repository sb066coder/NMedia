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

    override fun getChangePayload(oldItem: FeedItem, newItem: FeedItem): Any? {
        return if ((oldItem as Post).likedByMe != (newItem as Post).likedByMe) true else null
    }
}
